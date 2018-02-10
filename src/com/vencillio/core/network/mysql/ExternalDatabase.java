package com.vencillio.core.network.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles a database connection.
 *
 * @author Rene Roosen
 */
public class ExternalDatabase implements Runnable {

	/**
	 * Represents a connection in the pool.
	 *
	 * @author Rene Roosen
	 */
	private class DatabaseConnection {

		/**
		 * The database connection.
		 */
		private Connection connection;

		/**
		 * The last time the connection was pinged.
		 */
		private long lastPing = System.currentTimeMillis();

		/**
		 * The time of the last query
		 */
		private long lastQuery;

		/**
		 * The last reconnection, we periodically delete the connections, to
		 * release their resources
		 */
		private long lastReconnect = System.currentTimeMillis() + new Random().nextInt(120000); // Initial
		// Time
		// setting,
		// because otherwise
		// they close/reopen all
		// at the same time

		/**
		 * The database password.
		 */
		private final String password;

		/**
		 * The sate of the connection.
		 */
		private State state = State.INACTIVE;

		/**
		 * The url of the database.
		 */
		private final String url;

		/**
		 * The database username.
		 */
		private final String username;

		/**
		 * Initialises the MySQL driver and database settings.
		 *
		 * @param username
		 * @param password
		 * @param url
		 */
		private DatabaseConnection(String username, String password, String url) {
			this.username = username;
			this.password = password;
			this.url = "jdbc:mysql://" + url;
		}

		/**
		 * Closes the datbase connection.
		 */
		private void close() {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.log(Level.WARNING, "Error closing database connection", e);
				}

				setState(State.INACTIVE);
			}
		}

		/**
		 * Attempts to connect to the database.
		 *
		 * @return
		 */
		private boolean connect() {
			setState(State.INACTIVE);

			try {
				connection = DriverManager.getConnection(url, username, password);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error connecting to MySQL database (" + url + ") !", e);
				return false;
			}

			return true;
		}

		/**
		 * @return the connection.
		 */
		public Connection getConnection() {
			return connection;
		}

		/**
		 * @return the state of the connection.
		 */
		public State getState() {
			synchronized (this) {
				return state;
			}
		}

		/**
		 * @return true if the ping was successful.
		 */
		private boolean ping() {
			lastPing = System.currentTimeMillis();

			if ((connection != null) && ((System.currentTimeMillis() - lastReconnect) > 5000)) {
				try {
					connection.prepareStatement("SELECT 1").executeQuery();
				} catch (SQLException e) {
					return false;
				}

				return true;
			}

			return false;
		}

		/**
		 * Sets the state of the connection.
		 *
		 * @param state
		 */
		public void setState(State state) {
			synchronized (this) {
				this.state = state;
			}
		}

	}

	/**
	 * Represents a database query.
	 *
	 * @author Rene Roosen
	 */
	private class InteralQuery implements Callable<ResultSet> {

		/**
		 * The query to be executed.
		 */
		private final String query;

		/**
		 * Initialise the query.
		 *
		 * @param query
		 */
		private InteralQuery(String query) {
			this.query = query;
		}

		/**
		 * Executes the query.
		 *
		 * @return
		 */
		@Override
		public ResultSet call() {

			PreparedStatement statement = null;
			boolean isUpdating = !query.toLowerCase().startsWith("select");
			DatabaseConnection pooledConnection = getPooledConnection();

			if (pooledConnection == null) {
				if (isUpdating) {
					addFailedQuery(this);
				}

				System.out.println("Unexpected: pooled connection returned null..");

				return null;
			}

			pooledConnection.setState(State.BUSY);
			pooledConnection.lastQuery = System.currentTimeMillis();

			try {
				statement = pooledConnection.getConnection().prepareStatement(query);

				if (isUpdating) {
					statement.executeUpdate();
				} else {
					return statement.executeQuery();
				}
			} catch (SQLException e) {
				if (isUpdating) {
					addFailedQuery(this);
				}
				e.printStackTrace();
			} finally {
				if ((statement != null) && isUpdating) {
					try {
						statement.close();
					} catch (SQLException ex) {
						logger.log(Level.WARNING, "Error closing statement", ex);
					}
				}

				pooledConnection.setState(State.IDLE);
			}

			return null;
		}

		@Override
		public String toString() {
			return query;
		}

	}

	/**
	 * The state of the connection.
	 *
	 * @author Rene Roosen
	 */
	private static enum State {

		BUSY,
		IDLE,
		INACTIVE

	}

	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(ExternalDatabase.class.getName());

	/**
	 * Queries that failed execution.
	 */
	private final Queue<InteralQuery> failedQueries = new LinkedList<>();

	/**
	 * Determines if the <code>DatabaseManager</code> is running.
	 */
	private boolean isRunning = true;

	/**
	 * The interval at which connections are pinged.
	 */
	private long pingInterval = 60000;

	/**
	 * The database connection pool.
	 */
	private final DatabaseConnection[] pool;

	/**
	 * The work service.
	 */
	private final ExecutorService workService;

	/**
	 * Initialise the database manager.
	 *
	 * @param username
	 * @param password
	 * @param url
	 * @param poolSize
	 */
	public ExternalDatabase(String username, String password, String url, int poolSize) {
		pool = new DatabaseConnection[poolSize];
		workService = Executors.newFixedThreadPool(pool.length);

		for (int i = 0; i < pool.length; i++) {
			pool[i] = new DatabaseConnection(username, password, url);
		}

		new Thread(this, "DatabaseConnection").start();
	}

	/**
	 * Adds a failed query to the list.
	 *
	 * @param query
	 */
	private void addFailedQuery(InteralQuery query) {
		synchronized (this) {
			failedQueries.add(query);
			System.out.println(String.format("SQL query failed [failCount: %d]: %s", failedQueries.size(), query.query));
		}
	}

	/**
	 * Executes a database query.
	 *
	 * @param query
	 */
	public ResultSet executeQuery(String query) {
		ResultSet result = null;

		if (!query.toLowerCase().startsWith("select")) {
			workService.submit(new InteralQuery(query));
		} else {
			Future<?> future = workService.submit(new InteralQuery(query));

			try {
				result = (ResultSet) future.get();
			} catch (Exception e) {
				logger.log(Level.WARNING, "Error executing query!", e);
			}
		}

		return result;
	}

	/**
	 * Gets the first available connection from the pool.
	 *
	 * @return
	 */
	public DatabaseConnection getPooledConnection() {
		for (DatabaseConnection connection : pool) {
			if (connection.getState().equals(State.IDLE)) {
				return connection;
			}
		}

		return null;
	}

	/**
	 * Connect all the connections.
	 */
	public void initialise() {
		int connectionCount = 0;

		for (DatabaseConnection poolConnection : pool) {
			if (!poolConnection.connect()) {
				continue;
			}

			connectionCount++;
			poolConnection.setState(State.IDLE);
		}

		System.out.println("Succesfully opened " + connectionCount + " database connection.");
	}

	@Override
	public void run() {
		while (isRunning) {

			// Sleep a little..
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				continue;
			}

			// Ping the connections in the pool.
			for (DatabaseConnection connection : pool) {
				if ((System.currentTimeMillis() - connection.lastPing) <= pingInterval) {
					continue;
				}

				synchronized (connection) {
					if (connection.getState() == State.IDLE) {
						if (((System.currentTimeMillis() - connection.lastReconnect) > 120000) && ((System.currentTimeMillis() - connection.lastQuery) > 1000)) {
							connection.close();
							connection.lastReconnect = System.currentTimeMillis();
						}
					}
				}

				if (!connection.ping()) {
					boolean reconnected = connection.connect();

					if (reconnected) {
						connection.setState(State.IDLE);
					}
				}
			}

			// Execute queries that previously failed.
			synchronized (this) {
				if (!failedQueries.isEmpty()) {
					InteralQuery query;

					while ((query = failedQueries.poll()) != null) {
						workService.submit(query);
					}
				}
			}
		}

	}

	/**
	 * Sets the interval at which connections are pinged in minutes.
	 *
	 * @param pingInterval
	 */
	public void setPingInterval(int pingInterval) {
		this.pingInterval = pingInterval * 60000;
	}

	/**
	 * Shuts down the database manager.
	 */
	public void shutdown() {
		isRunning = false;
		failedQueries.clear();

		if (workService != null) {
			workService.shutdown();
		}

		for (DatabaseConnection connection : pool) {
			connection.close();
		}
	}

}
