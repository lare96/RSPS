package com.vencillio.core;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.vencillio.GameDataLoader;
import com.vencillio.VencillioConstants;
import com.vencillio.core.network.PipelineFactory;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.LineCounter;
import com.vencillio.core.util.Stopwatch;
import com.vencillio.core.util.SystemLogger;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.object.ObjectManager;

public class GameThread extends Thread {
	
	/** The logger for printing information. */
	private static Logger logger = Logger.getLogger(GameThread.class.getSimpleName());

	public static void init() {
		try {
			/**
			 * Load data, bind ports, init connections, launch worker threads
			 */
			try {
				startup();
			} catch (Exception e) {
				e.printStackTrace();
			}

			/**
			 * Begin game thread
			 */
			new GameThread();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the server up.
	 * 
	 * @throws Exception
	 */
	private static void startup() throws Exception {
		// The stopwatch for timing how long all this takes.
		Stopwatch timer = new Stopwatch().reset();

		logger.info("Launching Vencillio..");

		if (!VencillioConstants.DEV_MODE) {
			System.setErr(new SystemLogger(System.err, new File("./data/logs/err")));
		}

		if (VencillioConstants.DEV_MODE) {
			LineCounter.run();
		}

		logger.info("Loading game data..");

		GameDataLoader.load();

//		logger.info("Loading character backup strategy..");
//		TaskQueue.queue(new PlayerBackupTask());

		while (!GameDataLoader.loaded()) {
			Thread.sleep(200);
		}

		logger.info("Binding port and initializing threads..");

		ServerBootstrap serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		serverBootstrap.setPipelineFactory(new PipelineFactory());

		/**
		 * Initialize worker threads
		 */
		new LoginThread();
		new NetworkThread();

		while (true) {
			try {
				serverBootstrap.bind(new InetSocketAddress(43594));
				break;
			} catch (ChannelException e2) {
				logger.info("Server could not bind port - sleeping..");
				Thread.sleep(2000);
			}
		}

		logger.info("Server successfully launched. [Took " + timer.elapsed() / 1000 + " seconds]");
	}

	/**
	 * Create the game thread
	 */
	private GameThread() {
		setName("Main Thread");
		setPriority(Thread.MAX_PRIORITY);
		start();
	}

	/**
	 * Performs a server cycle.
	 */
	private void cycle() {
		try {
			TaskQueue.process();
			GroundItemHandler.process();
			ObjectManager.process();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			World.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				long s = System.nanoTime();

				/**
				 * Cycle game
				 */
				cycle();
				

				/**
				 * Sleep
				 */
				long e = (System.nanoTime() - s) / 1_000_000;

				/**
				 * Process incoming packets consecutively throughout the
				 * sleeping cycle *The key to instant switching of equipment
				 */
				if (e < 600) {

					if (e < 400) {
						for (int i = 0; i < 30; i++) {
							long sleep = (600 - e) / 30;
							Thread.sleep(sleep);
						}
					} else {
						Thread.sleep(600 - e);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
