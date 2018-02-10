package com.vencillio.rs2.content.minigames.pestcontrol;

import java.util.List;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.minigames.pestcontrol.monsters.Portal;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.VirtualMobRegion;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the Pest Control game
 * @author Daniel
 *
 */
public class PestControlGame {

	/**
	 * List of players
	 */
	private final List<Player> players;
	
	/**
	 * Void knight
	 */
	private Mob voidKnight;
	
	/**
	 * Height
	 */
	private final int z;
	
	/**
	 * Region
	 */
	private final VirtualMobRegion region;

	/**
	 * Array of Portals
	 */
	private final Portal[] portals;
	
	/**
	 * Pest damage key
	 */
	public static final String PEST_DAMAGE_KEY = "pestdamagekey";
	
	/** 
	 * Pest game key
	 */
	public static final String PEST_GAME_KEY = "pestgamekey";

	/**
	 * Game time
	 */
	private int time = 300;

	/**
	 * Check if game has ended
	 */
	private boolean ended = false;

	/**
	 * Pest Control game
	 * @param players
	 * @param count
	 */
	public PestControlGame(List<Player> players, int count) {
		this.players = players;
		z = (count << 2);
		region = new VirtualMobRegion();

		portals = new Portal[] { new Portal(this, PestControlConstants.PORTAL_IDS[0], PestControlConstants.PORTAL_SPAWN_LOCATIONS[0], z), new Portal(this, PestControlConstants.PORTAL_IDS[1], PestControlConstants.PORTAL_SPAWN_LOCATIONS[1], z), new Portal(this, PestControlConstants.PORTAL_IDS[2], PestControlConstants.PORTAL_SPAWN_LOCATIONS[2], z), new Portal(this, PestControlConstants.PORTAL_IDS[3], PestControlConstants.PORTAL_SPAWN_LOCATIONS[3], z), };

		init();
	}

	/**
	 * Ends the game
	 * @param success
	 */
	public void end(boolean success) {
		ended = true;
		
		if (success) {
			World.sendGlobalMessage("<img=8> <col=C42BAD>" + players.size() + " players have completed a Pest Control minigame.");
		} else {
			World.sendGlobalMessage("<img=8> <col=C42BAD>" + players.size() + " players have failed a Pest Control minigame.");
		}

		for (Portal i : portals) {
			i.remove();
		}

		voidKnight.remove();

		for (Player p : players) {
			p.teleport(new Location(2657, 2639));

			p.setController(ControllerManager.DEFAULT_CONTROLLER);

			p.getCombat().reset();
			p.getMagic().setVengeanceActive(false);
			p.resetLevels();
			p.curePoison(0);

			if (success) {
				if (p.getAttributes().get(PEST_DAMAGE_KEY) != null && p.getAttributes().getInt(PEST_DAMAGE_KEY) >= 80) {
					DialogueManager.sendNpcChat(p, 1756, Emotion.HAPPY_TALK, "You have managed to destroy all the portals!", "We've awarded you with Void Knight Commendation", "points and some coins to show our appreciation.");
					p.getInventory().addOrCreateGroundItem(995, p.getAttributes().getInt(PEST_DAMAGE_KEY) * 6, true);
					p.setPestPoints(p.getPestPoints() + (10));
					AchievementHandler.activateAchievement(p, AchievementList.WIN_30_PEST_CONTROL_GAMES, 1);
				} else {
					DialogueManager.sendNpcChat(p, 1756, Emotion.CALM, "You were successful but did not contribute enough", "to the void knights. Try harder next time!");
				}
			} else {
				DialogueManager.sendNpcChat(p, 1756, Emotion.SAD, "The Void Knight has fallen!", "All hope is lost..");
			}

			p.getAttributes().remove(PEST_DAMAGE_KEY);
			p.getAttributes().remove(PEST_GAME_KEY);
		}

		for (Portal i : portals) {
			i.cleanup();
		}

		voidKnight.remove();

		PestControl.onGameEnd(this);
	}

	/**
	 * Gets the attackers
	 * @param p
	 * @return
	 */
	public int getAttackers(Player p) {
		int i = 0;

		for (Portal k : portals) {
			for (Mob j : k.getPests()) {
				if (j.getCombat().getAttacking() != null && j.getCombat().getAttacking().equals(p)) {
					i++;
				}
			}
		}

		return i;
	}

	/**
	 * Gets the players
	 * @return
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Gets the region
	 * @return
	 */
	public VirtualMobRegion getVirtualRegion() {
		return region;
	}

	/**
	 * Gets the void knight
	 * @return
	 */
	public Mob getVoidKnight() {
		return voidKnight;
	}

	/**
	 * Gets the height
	 * @return
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Gets if ended
	 * @return
	 */
	public boolean hasEnded() {
		return ended;
	}

	/**
	 * Initialize
	 */
	public void init() {
		for (Player p : players) {
			p.teleport(new Location(2656 + Utility.randomNumber(4), 2609 + Utility.randomNumber(6), z));
			p.getAttributes().set(PEST_DAMAGE_KEY, 0);
			p.getAttributes().set(PEST_GAME_KEY, this);
			p.setController(ControllerManager.PEST_CONTROLLER);

			p.getSpecialAttack().setSpecialAmount(100);

			DialogueManager.sendNpcChat(p, 1756, Emotion.CALM, "Go with strength!", "Defend the void knight and destroy the portals!", "You are our only hope!");
		}
		
		time = 300;

		voidKnight = new Mob(region, 1756, false, false, new Location(2656, 2592, z));

		voidKnight.getLevels()[Skills.HITPOINTS] = 200;
		voidKnight.getMaxLevels()[Skills.HITPOINTS] = 200;
		voidKnight.getLevels()[Skills.DEFENCE] = 400;

		voidKnight.setRespawnable(false);

		voidKnight.getAttributes().set(PEST_GAME_KEY, this);
	}
	
	/**
	 * Void knight messages
	 */
	public String[] VOID_KNIGHT_MESSAGES = {
		"We must not fail!", 
		"Take down the portals", 
		"The Void Knights will not fall!", 
		"Hail the Void Knights!", 
		"We are beating these scum!"
	};

	/**
	 * Game process
	 */
	public void process() {
		time--;

		if (time <= 0) {
			end(false);
			return;
		}

		if (voidKnight.isDead()) {
			end(false);
			return;
		}

		if (!portals[0].isActive() && !portals[1].isActive() && !portals[2].isActive() && !portals[3].isActive()) {
			end(true);
		}
		
		int random = 5;
		if (random == 3) {
			voidKnight.getUpdateFlags().sendForceMessage(Utility.randomElement(VOID_KNIGHT_MESSAGES));
		}
		
		for (Player p : players) {
			p.getClient().queueOutgoingPacket(new SendString(Utility.getFormattedTime(time) + "", 21117));
			p.getClient().queueOutgoingPacket(new SendString("" + voidKnight.getLevels()[Skills.HITPOINTS], 21115));

			for (int i = 0; i < 4; i++) {
				boolean dead = portals[i].isDead();
				p.getClient().queueOutgoingPacket(new SendString((dead ? "@red@Dead" : "" + portals[i].getLevels()[Skills.HITPOINTS]), 21111 + i));
			}

			if (p.getAttributes().get(PEST_DAMAGE_KEY) != null) {
				int damage = p.getAttributes().getInt(PEST_DAMAGE_KEY);
				p.getClient().queueOutgoingPacket(new SendString((damage >= 80 ? "" : "@red@") + p.getAttributes().getInt(PEST_DAMAGE_KEY), 21116));
			}
		}
	}

	/**
	 * Removes player from game
	 * @param p
	 */
	public void remove(Player p) {
		players.remove(p);

		if (players.size() == 0) {
			for (Portal i : portals) {
				i.cleanup();
			}

			voidKnight.remove();

			PestControl.onGameEnd(this);
		}
	}
	
}
