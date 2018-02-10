package com.vencillio.rs2.content.skill.firemaking;

import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles changing fire colors
 * 
 * @author Daniel
 *
 */
public class FireColor {

	/* Current fire ID */
	public static int FIRE = 5249, FIRE_TIME = 10;

	/* Current fire starter and their clan */
	public static String FIRE_STARTER = "None", FIRE_STARTER_CLAN = "None";

	/**
	 * Fire Data enum
	 * 
	 * @author Daniel
	 */
	public enum FireData {
		ORANGE(194098, "Orange", 5249, new String[] { "@or1@", "<col=ffb000>" }),
		GREEN(194101, "Green", 11405, new String[] { "@gre@", "<col=65280>" }),
		BLUE(194104, "Blue", 11406, new String[] { "@pt1@", "<col=005eff>" }),
		RED(194107, "Red", 11404, new String[] { "@red@", "<col=ff0000>" });

		private String fireName, color[];
		private int buttonId, fireId;

		private FireData(int buttonId, String fireName, int fireId, String[] color) {
			this.buttonId = buttonId;
			this.fireName = fireName;
			this.fireId = fireId;
			this.color = color;
		}

		public int getButton() {
			return buttonId;
		}

		public String getName() {
			return fireName;
		}

		public int getFire() {
			return fireId;
		}

		public String[] getColor() {
			return color;
		}

		public static FireData forId(int id) {
			for (FireData data : FireData.values())
				if (data.buttonId == id)
					return data;
			return null;
		}

		public static FireData forFire(int id) {
			for (FireData data : FireData.values())
				if (data.fireId == id)
					return data;
			return null;
		}
	}

	/**
	 * Opens the interface
	 * 
	 * @param player
	 */
	public static final void open(Player player) {
		FireData fires = FireData.forFire(FIRE);

		if (fires == null) {
			return;
		}

		player.send(new SendInterface(49750));
		player.send(new SendString("Current color: " + fires.getColor()[1] + "" + fires.getName(), 49758));
		player.send(new SendString("Changed by: " + fires.getColor()[1] + "" + FIRE_STARTER, 49759));
		player.send(new SendString("Tanner's Clan: " + fires.getColor()[1] + "" + FIRE_STARTER_CLAN, 49760));
		player.send(new SendString("Timer: " + fires.getColor()[1] + FIRE_TIME + " minutes", 49761));
	}

	/**
	 * Checks if player can use change fire color
	 * 
	 * @param player
	 * @return
	 */
	public static final boolean can(Player player) {
		if (FIRE_TIME != 0 && !PlayerConstants.isOwner(player)) {
			DialogueManager.sendStatement(player, "Please wait " + FIRE_TIME + " minutes before doing this!");
			return false;
		}
		if (player.getCredits() < 10) {
			DialogueManager.sendStatement(player, "You need 10 credits to do this!");
			return false;
		}
		return true;
	}

    /**
     * All fire positions
     */
	private static final Location FIRE_POSITIONS[] = { new Location(2916, 5467, 0), };

	/**
	 * Handles fire color changing
	 * 
	 * @param player
	 * @param buttonId
	 */
	public static final void main(Player player, int buttonId) {
		FireData fires = FireData.forId(buttonId);

		if (!can(player) || fires == null) {
			return;
		}

		FIRE = fires.getFire();
		FIRE_STARTER = player.getUsername();
		FIRE_STARTER_CLAN = player.getClan() == null ? "None" : player.clan.getTitle();
		FIRE_TIME = 10;

		for (Location position : FIRE_POSITIONS) {
			ObjectManager.remove(new GameObject(position.getX(), position.getY(), position.getZ()));
			ObjectManager.add(new GameObject(FIRE, position.getX(), position.getY(), position.getZ(), 10, 1));
		}

		player.setCredits(player.getCredits() - 10);
		World.sendGlobalMessage(fires.getColor()[1] + "<img=8>" + player.getUsername() + " has just changed all fire colors to " + fires.getName() + "!");
		//AchievementHandler.activateAchievement(player, AchievementList.CHANGE_FIRE_COLOR_5_TIMES, 1);
		player.send(new SendString("Current color: " + fires.getColor()[0] + "" + fires.getName(), 49758));
		player.send(new SendString("Changed by: " + fires.getColor()[0] + "" + FIRE_STARTER, 49759));
		player.send(new SendString("Clan: " + fires.getColor()[0] + "" + FIRE_STARTER_CLAN, 49760));
		player.send(new SendString("Timer: " + fires.getColor()[0] + FIRE_TIME + " minutes", 49761));

	}

}
