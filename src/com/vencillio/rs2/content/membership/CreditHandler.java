package com.vencillio.rs2.content.membership;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.CreditTab;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

import java.util.HashMap;

/**
 * Handles the credit system
 * @author Daniel
 *
 */
public enum CreditHandler {
	
	SPECIAL_ATTACK(205052, 1, new Handle() {
		@Override
		public void handle(Player player) {
			if (!allowed(player, null, 1)) {
				return;
			}
			player.setCredits(player.getCredits() - 1);
			player.getSpecialAttack().setSpecialAmount(100);
			player.getSpecialAttack().update();
			spent(player, 1);
		}
	}),
	RESTORE_PRAYER(205053, 1, new Handle() {
		@Override
		public void handle(Player player) {
			if (!allowed(player, null, 1)) {
				return;
			}
			player.setCredits(player.getCredits() - 1);
			player.getSkill().setLevel(Skills.PRAYER, player.getMaxLevels()[Skills.PRAYER]);
			spent(player, 1);
		}
	}),
	OPEN_BANK(205054, 3, new Handle() {
		@Override
		public void handle(Player player) {
			if (!allowed(player, null, 3)) {
				return;
			}
			player.setCredits(player.getCredits() - 3);
			player.getBank().openBank();
			spent(player, 3);
		}
	}),
	UNLOCK_FREE_TELEPORTS(205055, 35, new Handle() {
		@Override
		public void handle(Player player) {
			if (!allowed(player, CreditPurchase.FREE_TELEPORTS, 35)) {
				return;
			}
			player.setCredits(player.getCredits() - 35);
			player.unlockCredit(CreditPurchase.FREE_TELEPORTS);
			spent(player, 35);
			player.send(new SendMessage("You no longer have to pay for teleports!"));
		}
	}),
	UNLOCK_DISEASE_IMUNITY(205056, 27, new Handle() {
		@Override
		public void handle(Player player) {
			if (!allowed(player, CreditPurchase.DISEASE_IMUNITY, 27)) {
				return;
			}
			player.setCredits(player.getCredits() - 27);
			player.unlockCredit(CreditPurchase.DISEASE_IMUNITY);
			spent(player, 27);
			player.send(new SendMessage("You no longer have to deal with crops catching disease!"));
		}
	}),
	REMOVE_TELEBLOCK(205057, 2, new Handle() {
		@Override
		public void handle(Player player) {
			if (!allowed(player, null, 2)) {
				return;
			}
			player.setCredits(player.getCredits() - 2);
			player.teleblock(0);
			spent(player, 2);
		}
	}),
	UNLOCK_HIDDEN_WILDERNESS_KILLS(205058, 30, new Handle() {
		@Override
		public void handle(Player player) {
			if (!allowed(player, CreditPurchase.HIDE_WILDERNESS_KILLS, 30)) {
				return;
			}
			player.setCredits(player.getCredits() - 30);
			player.unlockCredit(CreditPurchase.HIDE_WILDERNESS_KILLS);
			spent(player, 30);
			player.send(new SendMessage("Players may not see your wilderness kills anymore!"));
		}
	}),PURCHASE_MORE_CREDITS(205025, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.send(new SendString("http://tannerscape.everythingrs.com/services/store", 12000));
		}
	}),;

	private int button;
	private int creditCost;
	private Handle handle;

	private CreditHandler(int button, int creditCost, Handle handle) {
		this.button = button;
		this.creditCost = creditCost;
		this.handle = handle;
	}

	public int getButton() {
		return button;
	}

	public int getCost() {
		return creditCost;
	}

	public Handle getHandle() {
		return handle;
	}

	public static HashMap<Integer, CreditHandler> credits = new HashMap<Integer, CreditHandler>();

	static {
		for (final CreditHandler credits : CreditHandler.values()) {
			CreditHandler.credits.put(credits.button, credits);
		}
	}

	
	/**
	 * Checks if player is allowed to access feature
	 * @param player
	 * @param amount
	 * @return
	 */
	public static boolean allowed(Player player, CreditPurchase credit, int amount) {
		if (player.isCreditUnlocked(credit)) {
			DialogueManager.sendStatement(player, "@red@You have already this unlocked.");
			return false;
		}
		if (player.getCredits() < amount) {
			DialogueManager.sendStatement(player, "@red@You do not have enough credits to do this!");
			player.send(new SendMessage("Please visit @red@http://rennatscape.proboards.com/ </col>to purchase more credits!"));
			return false; //Not implemented on site
		}
		if (player.inWilderness()) {
			DialogueManager.sendStatement(player, "You can not do this in the wilderness!");
			return false;
		}
		if (player.getCombat().inCombat()) {
			DialogueManager.sendStatement(player, "You can not do this while in combat!");
			return false;
		}
		return true;
	}

	/**
	 * Handles what happens when player has spent credits
	 * @param player
	 * @param amount
	 */
	public static void spent(Player player, int amount) {
		player.send(new SendMessage("@blu@You have spent " + amount + " credits; Remaining: " + player.getCredits() + "."));
		player.getClient().queueOutgoingPacket(new SendString("</col>Credits: @gre@" + Utility.format(player.getCredits()), 52504));	
		InterfaceHandler.writeText(new CreditTab(player));
		InterfaceHandler.writeText(new QuestTab(player));
	}

	/**
	 * Handles clicking buttons
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean handleClicking(Player player, int buttonId) {
		CreditHandler credits = CreditHandler.credits.get(buttonId);

		if (credits == null) {
			return false;
		}

		credits.getHandle().handle(player);
		return false;
	}
	
}
