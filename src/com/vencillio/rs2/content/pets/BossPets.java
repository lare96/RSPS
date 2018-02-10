package com.vencillio.rs2.content.pets;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles boss pets
 * 
 * @author Daniel
 */
public class BossPets {

	/**
	 * Boss Pet data
	 * @author Daniel
	 *
	 */
	public enum PetData {
		
		//TODO:
		//PENANCE QUEEN
		//CHOMPY CHICK
		
		KALPHITE_PRINCESS_FLY(12654, 6637), 
		KALPHITE_PRINCESS_BUG(12647, 6638),
		SMOKE_DEVIL(12648, 6655),
		DARK_CORE(12816, 318),
		PRINCE_BLACK_DRAGON(12653, 4000),
		GREEN_SNAKELING(12921, 2130),
		RED_SNAKELING(12939, 2131),
		BLUE_SNAKELING(12940, 2132),
		CHAOS_ELEMENT(11995, 5907),
		KREE_ARRA(12649, 4003),
		CALLISTO(13178, 497),
		SCORPIAS_OFFSPRING(13181, 5547),
		VENENATIS(13177, 495),		
		VETION_PURPLE(13179, 5559),
		VETION_ORANGE(13180, 5560),
		BABY_MOLE(12646, 6635),
		KRAKEN(12655, 6640),
		DAGANNOTH_SUPRIME(12643, 4006),
		DAGANNOTH_RIME(12644, 4007),
		DAGANNOTH_REX(12645, 4008),
		GENERAL_GRAARDOR(12650, 4001), 
		COMMANDER_ZILYANA(12651, 4009),
		KRIL_TSUTSAROTH(12652, 4004);

		private final int itemID;
		private final int npcID;

		private PetData(int itemID, int npcID) {
			this.itemID = itemID;
			this.npcID = npcID;
		}

		public int getItem() {
			return itemID;
		}

		public int getNPC() {
			return npcID;
		}

		public static PetData forItem(int id) {
			for (PetData data : PetData.values())
				if (data.itemID == id)
					return data;
			return null;
		}

		public static PetData forNPC(int id) {
			for (PetData data : PetData.values())
				if (data.npcID == id)
					return data;
			return null;
		}
	}

	/**
	 * Handles spawning the pet
	 * @param player
	 * @param itemID
	 */
	public static boolean spawnPet(Player player, int itemID, boolean loot) {
		PetData data = PetData.forItem(itemID);

		if (data == null) {
			return false;
		}

		if (player.getBossPet() != null) {
			player.send(new SendMessage("You already have a pet summoned!"));
			return true;
		}

		player.getInventory().remove(new Item(itemID, 1));

		final Mob mob = new Mob(player, data.npcID, false, false, true, player.getLocation());
		mob.getFollowing().setIgnoreDistance(true);
		mob.getFollowing().setFollow(player);

		player.setBossPet(mob);
		player.setBossID(data.npcID);
		player.getUpdateFlags().sendAnimation(new Animation(827));
		player.face(player.getBossPet());
		

		if (loot) {
			AchievementHandler.activateAchievement(player, AchievementList.OBTAIN_1_BOSS_PET, 1);
			AchievementHandler.activateAchievement(player, AchievementList.OBTAIN_10_BOSS_PET, 1);
		} else {
			player.send(new SendMessage("You summoned your " + mob.getDefinition().getName() + "."));
		}
		return true;
	}

	/**
	 * Handles picking up the pet
	 * @param player
	 * @param npcID
	 * @return
	 */
	public static boolean pickupPet(Player player, Mob mob) {
		if (mob == null || World.getNpcs()[mob.getIndex()] == null) {
			return false;
		}
		PetData data = PetData.forNPC(mob.getId());

		if (data == null) {
			return false;
		}
		
		if (player.getBossPet() == null || player.getBossPet().isDead()) {
			return false;
		}
		
		if (player.getBossPet() != mob || mob.getOwner() != player) {
			DialogueManager.sendStatement(player, "This is not your pet!");
			return true;
		}
		
		if (player.getInventory().hasSpaceFor(new Item(data.getItem()))) {
			player.getInventory().add(new Item(data.getItem()));
		} else if (player.getBank().hasSpaceFor((new Item(data.getItem())))) {
			player.getBank().add((new Item(data.getItem())));
			player.getClient().queueOutgoingPacket(new SendMessage("Your pet has been added to your bank."));
		} else {
			player.getClient().queueOutgoingPacket(new SendMessage("You must free some inventory space to pick up your pet."));
			return false;
		}
		
		player.getUpdateFlags().sendAnimation(new Animation(827));
		player.face(player.getBossPet());
		
		TaskQueue.queue(new Task(player, 1, true) {
			@Override
			public void execute() {
				player.getBossPet().remove();
				player.setBossPet(null);
				stop();
			}

			@Override
			public void onStop() {
				player.send(new SendMessage("You have picked up your pet."));
			}
		});		
		
		return true;
	}
	
	/**
	 * Handles pets on logout
	 * @param player
	 * @return
	 */
	public static void onLogout(Player player) {
		if (player.getBossPet() != null) {
			
			PetData data = PetData.forNPC(player.getBossPet().getId());
			
			if (player.getInventory().hasSpaceFor(new Item(data.getItem()))) {
				player.getInventory().add(new Item(data.getItem()));
			} else if (player.getBank().hasSpaceFor((new Item(data.getItem())))) {
				player.getBank().add((new Item(data.getItem())));
			}
		}
	}
	
	/**
	 * Handles what happens on death
	 * @param player
	 */
	public static void onDeath(Player player) {
		if (player.getBossPet() != null) {
			player.getBossPet().remove();
			player.setBossPet(null);
			player.send(new SendMessage("You have died with your pet, loosing it forever."));
		}
	}
}