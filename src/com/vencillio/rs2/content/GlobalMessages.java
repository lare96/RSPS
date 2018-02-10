package com.vencillio.rs2.content;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.World;

/**
 * Handles the global messages
 * @author Daniel
 *
 */
public class GlobalMessages {
	
	/**
	 * The logger for the class
	 */
	private static Logger logger = Logger.getLogger(GlobalMessages.class.getSimpleName());

	/**
	 * The news color text
	 */
	private static String newsColor = "<col=013B4F>";

	/**
	 * The news icon
	 */
	private static String iconNumber = "<img=8>";
	
	/**
	 * Holds all the announcements in a arraylist
	 */
	public final static ArrayList<String> announcements = new ArrayList<String>();

	/**
	 * The random messages that news will send
	 */
	public static final String[] ANNOUNCEMENTS = { 
		"Want to hide your wilderness kills from players? Purchase the ablity in credit tab!",
		"Talk to the guard at Edgeville bank to add extra security to your account!",
		"Read up on the rules of Tannerscape to ensure safe and enjoyable game play!",
		"Do you love Tannerscape? Please show us some love by purchasing Membership!",
		"Want to disable TriviaBot? Or add a notification bar? ::triviasettings",
		"Want to help us grow? Vote every 12 hours, you will also get a reward!",
		"Considering membership? Members have access to many custom content!",
		"Members can change their title by speaking to Dunce at Memberzone.",
		"Check out our forums for the latest news & updates at ::forums!",
		"Having an issue with your account? Tell us about it on forums.",
		"Do you have an interesting idea? Suggest it to us on forums!",
		"Found a bug? Report it on forums and we will fix it for you!",
		"Did you know you can talk to Big Mo at home for a title?",
		"Our website is located at http://www.rennatscape.proboards.com/!",
	};
	
	/**
	 * Declares all the announcements
	 */
	public static void declare() {
		for (int i = 0; i < ANNOUNCEMENTS.length; i++) {
			announcements.add(ANNOUNCEMENTS[i]);
		}
		logger.info(Utility.format(announcements.size()) + " Announcements have been loaded successfully.");
	}

	/**
	 * Initializes the task
	 */
	public static void initialize() {
		TaskQueue.queue(new Task(250, false) {
			@Override
			public void execute() {
				final String announcement = Utility.randomElement(announcements);
				World.sendGlobalMessage(iconNumber + newsColor + " " + announcement);
			}

			@Override
			public void onStop() {
			}
		});
	}
	
}
