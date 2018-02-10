package com.vencillio.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Logger;

import com.vencillio.VencillioConstants;
import com.vencillio.rs2.content.gambling.Gambling;

public class FileHandler {
	
	/** 
	 * The logger for printing information.
	 */
	private static Logger logger = Logger.getLogger(FileHandler.class.getSimpleName());
	
	/**
	 * Loads all the saves
	 */
	public static void load() {
		loadMaxPlayers();
		loadGambling();
	}

	/**
	 * Saves the maximum amount of players
	 */
	public static void saveMaxPlayers() {
		try {
			File file = new File("./data/saves/PLAYERS.txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(VencillioConstants.MOST_ONLINE);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the maximum amount of players
	 */
	public static void loadMaxPlayers() {
		try {
			File file = new File("./data/saves/PLAYERS.txt");
			if (!file.exists()) {
				return;
			}
			BufferedReader in = new BufferedReader(new FileReader(file));
			int online = Integer.parseInt(in.readLine());
			VencillioConstants.MOST_ONLINE = online;
			logger.info("Most players loaded: "+ online);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the gambling details
	 */
	public static void saveGambling() {
		try {
			File file = new File("./data/saves/GAMBLING.txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(String.valueOf(Gambling.MONEY_TRACKER));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the gambling details
	 */
	public static void loadGambling() {
		try {
			File file = new File("./data/saves/GAMBLING.txt");
			if (!file.exists()) {
				return;
			}
			BufferedReader in = new BufferedReader(new FileReader(file));
			long money = Long.parseLong(in.readLine());
			Gambling.MONEY_TRACKER = money;
			logger.info("Gambling results " + Utility.format(money));
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

}
