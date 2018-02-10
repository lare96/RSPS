package com.vencillio.rs2.content.minigames.barrows;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vencillio.core.util.Utility;
import com.vencillio.core.util.logger.PlayerLogger;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.dialogue.OptionDialogue;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.following.Following;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendPlayerHint;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the barrows minigame. Does not need to extend the minigame class
 * because it does not have the functions of a full fledged minigame.
 * 
 * @author lare96
 */
public class Barrows {

	/** The items to be considered junk or barely useful. */
	private static final Item[] JUNK_ITEM = { new Item(4740, 5), new Item(4740, 15), new Item(4740, 75), new Item(4740, 55), new Item(4740, 25), new Item(4740, 15), new Item(592), new Item(526), new Item(533, 150), new Item(554, 75), new Item(555, 75), new Item(556, 75), new Item(557, 75), new Item(558, 75), new Item(559, 75) };

	/** The items considered relatively hard to get. */
	private static final Item[] HARD_ITEM = { new Item(4740, 50), new Item(4740, 150), new Item(4740, 750), new Item(4740, 550), new Item(4740, 205), new Item(4740, 105), new Item(534, 200), new Item(562, 250), new Item(560, 250), new Item(561, 250), new Item(565, 250), new Item(566, 250) };

	/** The items considered extremely hard to get. */
//	private static final Item[] RARE_ITEM = { new Item(4708), new Item(4710), new Item(4712), new Item(4714), new Item(4716), new Item(4718), new Item(4720), new Item(4722), new Item(4724), new Item(4726), new Item(4728), new Item(4730), new Item(4732), new Item(4734), new Item(4736), new Item(4738), new Item(4745), new Item(4749), new Item(4751), new Item(4753), new Item(4755), new Item(4757), new Item(4759) };
	private static final Item[] RARE_ITEM = { new Item(4860), new Item(4866), new Item(4872), new Item(4878), new Item(4884), new Item(4890), new Item(4896), new Item(4902), new Item(4908), new Item(4914), new Item(4920), new Item(4926), new Item(4932), new Item(4938), new Item(4944), new Item(4950), new Item(4956), new Item(4962), new Item(4968), new Item(4974), new Item(4980), new Item(4986), new Item(4992), new Item(4998) };

	/** All of the broken items. */
	public static final Item[] BROKEN_ITEM = { new Item(4860), new Item(4866), new Item(4872), new Item(4878), new Item(4884), new Item(4890), new Item(4896), new Item(4902), new Item(4908), new Item(4914), new Item(4920), new Item(4926), new Item(4932), new Item(4938), new Item(4944), new Item(4950), new Item(4956), new Item(4962), new Item(4968), new Item(4974), new Item(4980), new Item(4986), new Item(4992), new Item(4998) };

	/** The amount a repair costs per item. */
	public static final int BASE_REPAIR_COST = 400000;

	/**
	 * Holds miscellaneous data for all of the barrows brothers.
	 * 
	 * @author lare96
	 */
	public enum Brother {
		AHRIM(1672, new Location(3562, 3285), new Location(3568, 3291), new Location(3565, 3288), new Location(3557, 9703, 3)),
		GUTHAN(1674, new Location(3575, 3279), new Location(3580, 3286), new Location(3577, 3282), new Location(3534, 9704, 3)),
		VERAC(1677, new Location(3554, 3293), new Location(3560, 3301), new Location(3557, 3297), new Location(3578, 9706, 3)),
		KARIL(1675, new Location(3563, 3273), new Location(3568, 3278), new Location(3566, 3275), new Location(3546, 9684, 3)),
		DHAROK(1673, new Location(3573, 3295), new Location(3578, 3300), new Location(3575, 3298), new Location(3556, 9718, 3)),
		TORAG(1676, new Location(3551, 3279), new Location(3556, 3285), new Location(3554, 3282), new Location(3568, 9683, 3));

		/** The npc id of the barrows brother. */
		private int npcId;

		/** The location that has to be dug to get into the crypt. */
		private Location southWest;

		/** The location that has to be dug to get into the crypt. */
		private Location northEast;

		/** The position of the hill. */
		private Location hillPosition;

		/** The position of the crypt. */
		private Location cryptPosition;

		/**
		 * Create a new {@link Brother}.
		 * 
		 * @param npcId
		 *            the npc id of the barrows brother.
		 * @param digLocation
		 *            the location that has to be dug to get into the crypt.
		 * @param hillPosition
		 *            the position of the hill.
		 * @param cryptPosition
		 *            the position of the crypt.
		 */
		private Brother(int npcId, Location southWest, Location northEast, Location hillPosition, Location cryptPosition) {
			this.npcId = npcId;
			this.southWest = southWest;
			this.northEast = northEast;
			this.hillPosition = hillPosition;
			this.cryptPosition = cryptPosition;
		}

		/**
		 * Gets the npc id of the barrows brother.
		 * 
		 * @return the npc id of the barrows brother.
		 */
		public int getNpcId() {
			return npcId;
		}

		/**
		 * Gets the location that has to be dug to get into the crypt.
		 * 
		 * @return the location that has to be dug to get into the crypt.
		 */
		public Location getSouthWest() {
			return southWest;
		}

		/**
		 * Gets the location that has to be dug to get into the crypt.
		 * 
		 * @return the location that has to be dug to get into the crypt.
		 */
		public Location getNorthEast() {
			return northEast;
		}

		/**
		 * Gets the position of the hill.
		 * 
		 * @return the position of the hill.
		 */
		public Location getHillPosition() {
			return hillPosition;
		}

		/**
		 * Gets the position of the crypt.
		 * 
		 * @return the position of the crypt.
		 */
		public Location getCryptPosition() {
			return cryptPosition;
		}

		public static boolean isBarrowsBrother(Mob mob) {
			for (Brother brother : values()) {
				if (brother.getNpcId() == mob.getId()) {
					return true;
				}
			}
			return false;
		}

		public static Brother getBarrowsBrother(Mob mob) {
			for (Brother brother : values()) {
				if (brother.getNpcId() == mob.getId()) {
					return brother;
				}
			}
			return null;
		}
	}

	/**
	 * Teleports the player to the crypt based on the position they dug on.
	 * 
	 * @param player
	 *            the player to teleport to the crypt.
	 */
	public static boolean teleportPlayer(Player player) {
		for (Brother brother : Brother.values()) {
			if (player.getLocation().inLocation(brother.getSouthWest(), brother.getNorthEast(), true)) {
				player.teleport(brother.getCryptPosition());
				player.send(new SendMessage("You dig a hole and fall into " + brother.name().toLowerCase() + "s crypt!"));
				updateInterface(player);
				return true;
			}
		}
		return false;
	}

	/**
	 * Summons the brother to the player.
	 * 
	 * @param brother
	 *            the brother to summon.
	 * @param player
	 *            the player to summon the brother for.
	 */
	public static void summon(final Brother brother, final Player player) {

		if (player.getKillRecord()[brother.ordinal()]) {
			DialogueManager.sendPlayerChat(player, Emotion.CALM, "I have already killed this brother.");
			return;
		}

		if (player.getBrotherNpc() != null) {
			DialogueManager.sendPlayerChat(player, Emotion.CALM, "Maybe I should finish killing the other one first.");
			return;
		}

		if (player.getHiddenBrother() == brother && player.getBarrowsKC() != 5) {
			DialogueManager.sendPlayerChat(player, Emotion.CALM, "I should return when I've killed the others.");
			return;
		} else if (player.getHiddenBrother() == brother && player.getBarrowsKC() == 5) {

			player.start(new OptionDialogue("Enter the tunnel!", p -> p.teleport(new Location(3551, 9691, 0)), "No, I'm not ready yet!", p -> p.send(new SendRemoveInterfaces())));
			return;
		}

		player.setBrotherNpc(new Mob(player, brother.getNpcId(), false, false, false, player.getLocation()));
		player.getBrotherNpc().getFollowing().setFollow(player, Following.FollowType.COMBAT);
		player.getBrotherNpc().getCombat().setAttacking(player);
		player.send(new SendPlayerHint(false, player.getBrotherNpc().getIndex()));
		player.getBrotherNpc().getUpdateFlags().sendForceMessage("How dare you disturb my rest!");
	}

	/**
	 * Updates the interface
	 * 
	 * @param player
	 */
	public static void updateInterface(Player player) {
		player.send(new SendString(player.getKillRecord()[Brother.AHRIM.ordinal()] == true ? "@red@Ahrim the Blighted" : "Ahrim the Blighted", 59003));
		player.send(new SendString(player.getKillRecord()[Brother.DHAROK.ordinal()] == true ? "@red@Dharok the Wretched" : "Dharok the Wretched", 59004));
		player.send(new SendString(player.getKillRecord()[Brother.GUTHAN.ordinal()] == true ? "@red@Guthan the Infested" : "Guthan the Infested", 59005));
		player.send(new SendString(player.getKillRecord()[Brother.KARIL.ordinal()] == true ? "@red@Karil the Tainted" : "Karil the Tainted", 59006));
		player.send(new SendString(player.getKillRecord()[Brother.TORAG.ordinal()] == true ? "@red@Torag the Corrupted" : "Torag the Corrupted", 59007));
		player.send(new SendString(player.getKillRecord()[Brother.VERAC.ordinal()] == true ? "@red@Verac the Defiled" : "Verac the Defiled", 59008));
	}
	
	/**
	 * Gets the reward for completing the minigame.
	 * 
	 * @return the item reward for completing the minigame.
	 */
	public static Item[] getReward() {
		Item[] barrowsReward = null;

		switch (Utility.RANDOM.nextInt(6)) {
		case 0:
			barrowsReward = new Item[5];
			break;
		case 1:
		case 2:
			barrowsReward = new Item[4];
			break;
		default:
			barrowsReward = new Item[3];
			break;
		}

		for (int i = 0; i < barrowsReward.length; i++) {
			switch (Utility.RANDOM.nextInt(35)) {
			case 0:
			case 1:
			case 2:
				barrowsReward[i] = Utility.randomElement(HARD_ITEM);
				break;
			case 8:
			case 9:
			case 10:
				barrowsReward[i] = Utility.randomElement(RARE_ITEM);
				break;
			default:
				barrowsReward[i] = Utility.randomElement(JUNK_ITEM);
				break;
			}
		}
		return barrowsReward;
	}
	
	public static void main(String[] args) {
		int junk = 0;
		int hard = 0;
		int rare = 0;
		double trials = 1_000_000.0;
		for (int i = 0; i < trials; i++) {
			Item[] barrowsReward = null;
			switch (Utility.RANDOM.nextInt(6)) {
			case 0:
				barrowsReward = new Item[5];
				break;
			case 1:
			case 2:
				barrowsReward = new Item[4];
				break;
			default:
				barrowsReward = new Item[3];
				break;
			}

			for (int ii = 0; ii < barrowsReward.length; ii++) {
				switch (Utility.RANDOM.nextInt(35)) {
				case 0:
				case 1:
				case 2:
					hard++;
					break;
				case 8:
				case 9:
				case 10:
					rare++;
					break;
				default:
					junk++;
					break;
				}
			}
		}
		DecimalFormat formatter = new DecimalFormat("#.##");
		formatter.setRoundingMode(RoundingMode.DOWN);
		System.out.println("runs: " + trials);
		trials = (junk + hard + rare);
		System.out.println(formatter.format(junk*100/trials) + "% - junk: " + Utility.format(junk));
		System.out.println(formatter.format(hard*100/trials) + "% - hard: " + Utility.format(hard));
		System.out.println(formatter.format(rare*100/trials) + "% - rares: " + Utility.format(rare));
	}

	/**
	 * Replaces a broken barrows piece with a fresh one.
	 * 
	 * @param itemId
	 *            the broken barrows piece.
	 * @return the fresh barrows piece.
	 */
	public static int replaceBrokenItem(int itemId) {
		switch (itemId) {
		case 4860:
			return 4708;
		case 4866:
			return 4710;
		case 4872:
			return 4712;
		case 4878:
			return 4714;
		case 4884:
			return 4716;
		case 4890:
			return 4718;
		case 4896:
			return 4720;
		case 4902:
			return 4722;
		case 4908:
			return 4724;
		case 4914:
			return 4726;
		case 4920:
			return 4728;
		case 4926:
			return 4730;
		case 4932:
			return 4732;
		case 4938:
			return 4734;
		case 4944:
			return 4736;
		case 4950:
			return 4738;
		case 4956:
			return 4745;
		case 4962:
			return 4747;
		case 4968:
			return 4749;
		case 4974:
			return 4751;
		case 4980:
			return 4753;
		case 4986:
			return 4755;
		case 4992:
			return 4757;
		case 4998:
			return 4759;
		}
		return -1;
	}

	/**
	 * Gets the hidden brother.
	 * 
	 * @return the hidden brother.
	 */
	public static Brother getHiddenBrother(Player player) {
		List<Brother> brother = new ArrayList<Brother>();

		for (int i = 0; i < player.getKillRecord().length; i++) {
			if (!player.getKillRecord()[i]) {
				brother.add(Brother.values()[i]);
			}
		}
		return Utility.randomElement(brother);
	}

	//First is good item, second is broken dumb bitch
	public static final int[][] BROKEN_BARROWS = { { 4751, 4974 }, { 4708, 4860 }, { 4710, 4866 }, { 4712, 4872 }, { 4714, 4878 }, { 4716, 4884 }, { 4720, 4896 }, { 4718, 4890 }, { 4720, 4896 }, { 4722, 4902 }, { 4732, 4932 }, { 4734, 4938 }, { 4736, 4944 }, { 4738, 4950 }, { 4724, 4908 }, { 4726, 4914 }, { 4728, 4920 }, { 4730, 4926 }, { 4745, 4956 }, { 4747, 4962 }, { 4749, 4968 }, { 4751, 4794 }, { 4753, 4980 }, { 4755, 4986 }, { 4757, 4992 }, { 4759, 4998 } };

	public static boolean clickObject(Player player, int id, int x, int y, int z) {
		switch (id) {
		case 20973:
			if (player.getBarrowsKC() == 6) {
				if (player.getInventory().getFreeSlots() < 5) {
					DialogueManager.sendPlayerChat(player, Emotion.HAPPY, "I should free up some inventory slots first.");
					return true;
				}
				Item[] rewards = getReward();
				
				HashMap<Integer, Integer> recieved = new HashMap<>();
				for (Item reward : rewards) {
					if (recieved.get(reward.getId()) != null) {
						recieved.put(reward.getId(), reward.getAmount() + recieved.get(reward.getId()));
					} else {
						recieved.put(reward.getId(), reward.getAmount());
					}
				}

				for (int item : recieved.keySet()) {
					Item reward = new Item(item, recieved.get(item));
					PlayerLogger.BARROWS_LOGGER.log(player.getUsername(), String.format("%s has recieved %s %s.", Utility.formatPlayerName(player.getUsername()), reward.getAmount(), reward.getDefinition().getName()));
				}
				
				player.getInventory().addItems(rewards);
				player.send(new SendMessage("You have completed the barrows minigame, well done!"));
				player.setBarrowsKC(0);
				player.setKillRecord(new boolean[Brother.values().length]);
				player.setBrotherNpc(null);
				player.setHiddenBrother(null);
				player.setChestClicked(false);
				player.getProperties().addProperty("BARROWS", "BARROWS_CHESTS", 1);
			} else if (player.getBarrowsKC() == 5) {
				if (player.isChestClicked()) {
					return true;
				}
				player.setChestClicked(true);
				player.setBrotherNpc(new Mob(player, player.getHiddenBrother().getNpcId(), false, false, false, player.getLocation()));
				player.getBrotherNpc().getFollowing().setFollow(player, Following.FollowType.COMBAT);
				player.getBrotherNpc().getCombat().setAttacking(player);
				player.send(new SendPlayerHint(false, player.getBrotherNpc().getIndex()));
				player.getBrotherNpc().getUpdateFlags().sendForceMessage("How dare you disturb my slumber!");
			}
			break;
		case 20667:
			player.teleport(Brother.AHRIM.getHillPosition());
			return true;
		case 20770:
			Barrows.summon(Brother.AHRIM, player);
			return true;
		case 20672:
			player.teleport(Brother.VERAC.getHillPosition());
			return true;
		case 20772:
			Barrows.summon(Brother.VERAC, player);
			return true;
		case 20668:
			player.teleport(Brother.DHAROK.getHillPosition());
			return true;
		case 20720:
			Barrows.summon(Brother.DHAROK, player);
			return true;
		case 20671:
			player.teleport(Brother.TORAG.getHillPosition());
			return true;
		case 20721:
			Barrows.summon(Brother.TORAG, player);
			return true;
		case 20669:
			player.teleport(Brother.GUTHAN.getHillPosition());
			return true;
		case 20722:
			Barrows.summon(Brother.GUTHAN, player);
			return true;
		case 20670:
			player.teleport(Brother.KARIL.getHillPosition());
			return true;
		case 20771:
			Barrows.summon(Brother.KARIL, player);
			return true;
		}
		return false;
	}

	public static void onBarrowsDeath(Player p, Mob mob) {
		Brother bro = Brother.getBarrowsBrother(mob);

		if (bro == null) {
			return;
		}

		p.setBrotherNpc(null);
		p.getKillRecord()[bro.ordinal()] = true;
		p.setBarrowsKC(p.getBarrowsKC() + 1);
		Barrows.updateInterface(p);
		if (p.getBarrowsKC() == 1) {
			p.setHiddenBrother(Barrows.getHiddenBrother(p));
		}
	}
}