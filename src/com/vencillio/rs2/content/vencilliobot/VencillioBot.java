package com.vencillio.rs2.content.vencilliobot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.vencillio.VencillioConstants;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendBanner;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Vencillio Bot - Asks random questions which players can race to answer
 * @author Daniel
 *
 */
public class VencillioBot {
	
	/**
	 * The logger for the class
	 */
	private static Logger logger = Logger.getLogger(VencillioBot.class.getSimpleName());
	
	/**
	 * Holds all the bot data
	 */
	private final static Set<VencillioBotData> BOT_DATA = new HashSet<>();
	
	/**
	 * The current question/answer set
	 */
	private static VencillioBotData current = null;
	
	/*
	 * Holds all the VencillioBot attempted answers
	 */
	public final static ArrayList<String> attempts = new ArrayList<String>();
	
	/**
	 * Color of the VencillioBot messages
	 */
	private static final String COLOR = "<col=8814B3>";
	
	/**
	 * Declares the Vencillio data
	 */
	public static void declare() {
		for (VencillioBotData data : VencillioBotData.values()) {
			BOT_DATA.add(data);
		}
		logger.info("Loaded " + BOT_DATA.size() + " TannerBot questions.");
	}
	
	/**
	 * Initializes the VencillioBot task
	 */
	public static void initialize() {
		TaskQueue.queue(new Task(650, false) {
			@Override
			public void execute() {
				if (current == null) {
					assign();	
					return;
				}
				sendMessage("[" + COLOR + "TannerBot</col>] " + current.getQuestion());
				sendNotification("[" + COLOR + "TannerBot</col>] " + current.getQuestion());
			}
			@Override
			public void onStop() {
			}
		});	
	}
	
	/**
	 * Assigns a new question
	 */
	private static void assign() {
		current = Utility.randomElement(BOT_DATA);
		sendMessage("[" + COLOR + "TannerBot</col>] " + current.getQuestion());
	}
	
	/**
	 * Handles player answering the question
	 * @param player
	 * @param answer
	 */
	public static void answer(Player player, String answer) {
		if (current == null) {
			return;
		}
		for (int i = 0; i < VencillioConstants.BAD_STRINGS.length; i++) {
			if (answer.contains(VencillioConstants.BAD_STRINGS[i])) {
				player.send(new SendMessage("[" + COLOR + "TannerBot</col>] That was an offensive answer! Contain yourself or be punished."));
				return;
			}
		}
		for (int i = 0; i < current.getAnswers().length; i++) {
			if (current.getAnswers()[i].equalsIgnoreCase(answer)) {
				answered(player, answer);
				return;
			}
		}
		player.send(new SendMessage("[" + COLOR + "TannerBot</col>] Sorry, the answer you have entered is incorrect! Try again!"));
		attempts.add(answer);
	}
	
	/**
	 * Handles player answering the question successfully 
	 * @param player
	 * @param answer
	 */
	private static void answered(Player player, String answer) {
		sendMessage("[" + COLOR + "TannerBot</col>] " + COLOR + player.determineIcon(player) + " " + player.getUsername() + "</col> has answered the question correctly! Answer:" + COLOR + " " + Utility.capitalizeFirstLetter(answer) + "</col>.");
		if (attempts.size() > 0) {
			sendMessage("[" + COLOR + "TannerBot</col>] Attempted answers: " + COLOR + "" + attempts.toString() + "</col>!");
		}
		int REWARD = Utility.random(150_000);
		player.getInventory().addOrCreateGroundItem(995, REWARD, true);
		AchievementHandler.activateAchievement(player, AchievementList.ANSWER_15_TRIVIABOTS_CORRECTLY, 1);
		AchievementHandler.activateAchievement(player, AchievementList.ANSWER_80_TRIVIABOTS_CORRECTLY, 1);
		reset();
	}
	
	/**
	 * Resets the VencillioBot
	 */
	private static final void reset() {
		current = null;
		attempts.clear();
	}
	
	/**
	 * Sends message to server
	 * @param message
	 */
	public static void sendMessage(String message) {
		for (Player players : World.getPlayers()) {
			if (players != null && players.isWantTrivia()) {
				players.send(new SendMessage(message));
			}
		}
	}
	
	/**
	 * Sends notification to server
	 * @param message
	 */
	public static void sendNotification(String message) {
		for (Player players : World.getPlayers()) {
			if (players != null && players.isWantTrivia() && players.isTriviaNotification()) {
				players.send(new SendBanner(message, 0x8E9CA3));
			}
		}
	}

}
