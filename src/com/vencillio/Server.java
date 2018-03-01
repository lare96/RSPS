package com.vencillio;

import com.vencillio.core.GameThread;
import com.vencillio.core.network.mysql.HiscoreUpdater;
import com.vencillio.core.network.mysql.MembershipRewards;
import com.vencillio.core.network.mysql.VoteUpdater;
import com.vencillio.core.util.logger.PlayerLogger;
import com.vencillio.rs2.content.clanchat.ClanManager;
import com.vencillio.rs2.content.io.PlayerSave;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.tools.ControlPanel;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;


/**
 * Initializes the server
 * 
 * @author Michael Sasse
 * 
 */
public class Server {

	/** 
	 * The logger for printing information.
	 */
	private static Logger logger = Logger.getLogger(Server.class.getSimpleName());

	/**
	 * Handles the clan chat.
	 */
	public static ClanManager clanManager = new ClanManager();

	/**
	 * Gets the Vencillio time
	 */
	public static String vencillioTime() {
		return new SimpleDateFormat("HH:mm aa").format(new Date());
	}

	/**
	 * Gets the server date
	 */
	public static String vencillioDate() {
		return new SimpleDateFormat("EEEE MMM dd yyyy ").format(new Date());
	}

	private static Instant start = Instant.now();

	/**
	 * Gets the server uptime
	 * 
	 * @return
	 */
	public static String getUptime() {
		Instant temp = Instant.now();
		Duration elapsed = Duration.between(start, temp);
		return elapsed.toString().substring(2)
				.replaceAll("(\\d[HMS])(?!$)", "$1 ")
				.toLowerCase();
		/*RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
		DateFormat df = new SimpleDateFormat("DD 'D', HH 'H', mm 'M'");
		df.setTimeZone(TimeZone.getTimeZone("GMT+0")); //In-game time?
		return "" + df.format(new Date(mx.getUptime()));*/
	}

	/**
	 * The main method of the server that initializes everything
	 * 
	 * @param args
	 *            The startup arguments
	 */
	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			VencillioConstants.DEV_MODE = Boolean.valueOf(args[0]);
		}

		
		logger.info("Development mode: " + (VencillioConstants.DEV_MODE ? "Online" : "Offline") + ".");
		logger.info("Staff mode: " + (VencillioConstants.STAFF_ONLY ? "Online" : "Offline") + ".");

		if (!VencillioConstants.DEV_MODE) {
			try {
				MembershipRewards.prepare();
				HiscoreUpdater.prepare();
				VoteUpdater.prepare();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				for (Player players : World.getPlayers()) {
					if (players != null && players.isActive()) {
						PlayerSave.save(players);
					}
				}

				MembershipRewards.shutdown();
				HiscoreUpdater.shutdown();
				VoteUpdater.shutdown();

				PlayerLogger.SHUTDOWN_LOGGER.log("Logs", String.format("Server shutdown with %s online.", World.getActivePlayers()));
			}));
		}

		GameThread.init();
		ControlPanel.init();
	}
}
