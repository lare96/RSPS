package com.vencillio.rs2.content.membership;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

import java.util.HashMap;

/**
 * Handles Membership Bonds
 * @author Daniel
 *
 */
public class MembershipBonds {
	
	/**
	 * Bond Data
	 * @author Daniel
	 *
	 */
	public enum BondData {
		
		ONE("10 credit", 13190, 1, 10, 0),
		TWO("30 credit", 13191, 3, 30, 0),
		THREE("50 credit", 13192, 5, 50, 5),
		FOUR("80 credit", 13193, 8, 80, 10),
		FIVE("100 credit", 13194, 10, 100, 15),
		SIX("200 credit", 13195, 20, 200, 25),
		SEVEN("500 credit", 13196, 50, 500, 50),
		EIGHT("1,000 credit", 13197, 100, 1000, 150),
		NINE("2,000 credit", 13198, 200, 2000, 500);
		
		private final String name;
		private final int item;
		private final int moneySpent;
		private final int credits;
		private final int complimentary;
		
		private BondData(String name, int item, int moneySpent, int credits, int complimentary) {
			this.name = name;
			this.item = item;
			this.moneySpent = moneySpent;
			this.credits = credits;
			this.complimentary = complimentary;
		}
		
		public String getName() {
			return name;
		}
		
		public int getItem() {
			return item;
		}
		
		public int getSpent() {
			return moneySpent;
		}
		
		public int getCredits() {
			return credits;
		}
		
		public int getComplimentary() {
			return complimentary;
		}
		
		private static HashMap<Integer, BondData> bonds = new HashMap<Integer, BondData>();

		static {
			for (final BondData item : BondData.values()) {
				BondData.bonds.put(item.item, item);
			}
		}
	}

	/**
	 * Handles opening bond
	 * @param player
	 * @param itemId
	 * @return
	 */
	public static boolean handle(Player player, int itemId) {
		
		BondData data = BondData.bonds.get(itemId);

		if (data == null) {
			return false;
		}
		
		if (player.getInventory().getFreeSlots() == 0) {
			player.send(new SendMessage("Please clear up some inventory spaces before doing this!"));
			return false;
		}
		
		player.setMember(true);
		player.getInventory().remove(data.getItem(), 1);
		player.setCredits(player.getCredits() + data.getCredits());
		player.setMoneySpent(player.getMoneySpent() + data.getSpent());
		player.send(new SendMessage("@dre@Thank you for your purchase!"));
		RankHandler.upgrade(player);		
		if (data.getComplimentary() != 0) {
			player.setCredits(player.getCredits() + data.getComplimentary());
			player.send(new SendMessage("@dre@You have been complimentated " + Utility.format(data.getComplimentary()) + " credits!"));
		}
		World.sendGlobalMessage("</col>[ @dre@Tannerscape </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + "</col> has just reedemed a @dre@" + Utility.format(data.getCredits()) + "</col> credit voucher!");
		InterfaceHandler.writeText(new QuestTab(player));
		return true;
	}

}
