package com.vencillio.rs2.content.trading;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.NameUtil;
import com.vencillio.core.util.Utility;
import com.vencillio.core.util.logger.PlayerLogger;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendInventoryInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

import java.util.HashMap;

public class Trade {

	public static enum TradeStages {
		NONE,
		STAGE_1,
		STAGE_1_ACCEPTED,
		STAGE_2,
		STAGE_2_ACCEPTED;
	}

	public static final int TRADE_CONTAINER_SIZE = 28;

	public static String getTotalAmount(int amount) {
		if ((amount >= 10000) && (amount < 10000000))
			return amount / 1000 + "K";
		if ((amount >= 10000000) && (amount <= 2147483647)) {
			return amount / 1000000 + "M";
		}
		return amount + " gp";
	}

	public static int getTradedWealth(Player Player) {
		int value = 0;
		for (Item item : Player.getTrade().getTradedItems()) {
			if (item != null)
				value += GameDefinitionLoader.getHighAlchemyValue(item.getId()) * item.getAmount();
		}
		return value;
	}

	public static void sendItemText(Player player) {
		Item[] traded = player.getTrade().getTradedItems();
		Item[] recieving = player.getTrade().getTradingWith().getTradedItems();

		StringBuilder trade = new StringBuilder();
		boolean empty = true;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			Item item = traded[i];
			String prefix = "";
			if (item != null) {
				empty = false;
				if ((item.getAmount() >= 1000) && (item.getAmount() < 1000000))
					prefix = "@cya@" + item.getAmount() / 1000 + "K @whi@(" + item.getAmount() + ")";
				else if (item.getAmount() >= 1000000)
					prefix = "@gre@" + item.getAmount() / 1000000 + " million @whi@(" + item.getAmount() + ")";
				else {
					prefix = "" + item.getAmount();
				}
				trade.append(item.getDefinition().getName());
				trade.append(" x ");
				trade.append(prefix);
				trade.append("\\n");
			}
		}
		if (empty) {
			trade.append("Absolutely nothing!");
		}
		player.getClient().queueOutgoingPacket(new SendString(trade.toString(), 3557));
		trade = new StringBuilder();
		empty = true;

		for (int i = 0; i < player.getInventory().getSize(); i++) {
			Item item = recieving[i];
			String prefix = "";
			if (item != null) {
				empty = false;
				if ((item.getAmount() >= 1000) && (item.getAmount() < 1000000))
					prefix = "@cya@" + item.getAmount() / 1000 + "K @whi@(" + item.getAmount() + ")";
				else if (item.getAmount() >= 1000000)
					prefix = "@gre@" + item.getAmount() / 1000000 + " million @whi@(" + item.getAmount() + ")";
				else {
					prefix = "" + item.getAmount();
				}
				trade.append(item.getDefinition().getName());
				trade.append(" x ");
				trade.append(prefix);
				trade.append("\\n");
			}
		}
		if (empty) {
			trade.append("Absolutely nothing!");
		}
		player.getClient().queueOutgoingPacket(new SendString(trade.toString(), 3558));
	}

	protected final Player player;

	protected TradeStages stage = TradeStages.NONE;

	protected Trade tradingWith = null;

	protected TradeContainer container = new TradeContainer(this);

	protected String lastRequest = null;

	public Trade(Player player) {
		this.player = player;
	}

	@SuppressWarnings("incomplete-switch")
	public void accept() {
	
		if (System.currentTimeMillis() - player.tradeDelay < 3500) {
			player.send(new SendMessage("@red@The trade screen has been modified! Please wait..."));
			return;
		}

		if (stage == TradeStages.STAGE_1) {
			if (!player.getInventory().hasSpaceFor(tradingWith.getTradedItems())) {
				player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to make this " + getAction() + "."));
				return;
			}
			player.getClient().queueOutgoingPacket(new SendString("Waiting for other player...", 3431));
			tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString("Other player has accepted.", 3431));
			stage = TradeStages.STAGE_1_ACCEPTED;
		} else if (stage == TradeStages.STAGE_2) {
			stage = TradeStages.STAGE_2_ACCEPTED;
			player.getClient().queueOutgoingPacket(new SendString("Waiting for other player...", 3535));
			tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString("Other player has accepted.", 3535));
		}

		if ((tradingWith != null) && (tradingWith.accepted()))
			switch (stage) {
			case STAGE_1_ACCEPTED:
				stage = TradeStages.STAGE_2;
				tradingWith.setStage(TradeStages.STAGE_2);

				sendItemText(player);
				sendItemText(tradingWith.getPlayer());

				container.update();
				player.getClient().queueOutgoingPacket(new SendInventoryInterface(3443, 3213));
				tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendInventoryInterface(3443, 3213));
				break;
			case STAGE_2_ACCEPTED:
				end(true);

				stage = TradeStages.NONE;
				tradingWith.setStage(TradeStages.NONE);

				tradingWith.reset();
				reset();
				break;
			}
	}

	public boolean accepted() {
		return (stage == TradeStages.STAGE_1_ACCEPTED) || (stage == TradeStages.STAGE_2_ACCEPTED);
	}

	public void begin(Trade tradingWith) {
		player.getClient().queueOutgoingPacket(new SendString(getStatus() + " with: " + NameUtil.uppercaseFirstLetter(tradingWith.getPlayer().getUsername()), 3417));
		tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString(getStatus() + " with: " + NameUtil.uppercaseFirstLetter(player.getUsername()), 3417));

		player.getClient().queueOutgoingPacket(new SendString("", 3431));
		tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString("", 3431));

		player.getClient().queueOutgoingPacket(new SendString("Are you sure you want to make this " + getAction() + "?", 3535));
		tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString("Are you sure you want to make this " + getAction() + "?", 3535));

		player.getClient().queueOutgoingPacket(new SendInventoryInterface(3323, 3321));
		tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendInventoryInterface(3323, 3321));

		reset();
		tradingWith.reset();

		this.tradingWith = tradingWith;
		tradingWith.setTradingWith(this);

		stage = TradeStages.STAGE_1;
		tradingWith.setStage(TradeStages.STAGE_1);

		container.update();
		tradingWith.container.update();
	}

	public boolean canAppendTrade() {
		return (stage == TradeStages.STAGE_1) || (stage == TradeStages.STAGE_1_ACCEPTED);
	}

	public boolean clickTradeButton(int buttonId) {
		switch (buttonId) {
		case 13218:
			accept();
			return true;
		case 13092:
			accept();
			return true;
		}

		return false;
	}

	public void end(boolean success) {
		Item[] traded = getTradedItems();
		Item[] recieving = tradingWith.getTradedItems();
		
		HashMap<Integer, Integer> trade = new HashMap<>();
		HashMap<Integer, Integer> recieved = new HashMap<>();

		for (int i = 0; i < 28; i++) {
			if (success) {
				if (traded[i] != null) {
					tradingWith.getPlayer().getInventory().insert(traded[i]);

					if (trade.get(traded[i].getId()) != null) {
						trade.put(traded[i].getId(), traded[i].getAmount() + trade.get(traded[i].getId()));
					} else {
						trade.put(traded[i].getId(), traded[i].getAmount());
					}
				}

				if (recieving[i] != null) {
					player.getInventory().insert(recieving[i]);

					if (recieved.get(recieving[i].getId()) != null) {
						recieved.put(recieving[i].getId(), recieving[i].getAmount() + recieved.get(recieving[i].getId()));
					} else {
						recieved.put(recieving[i].getId(), recieving[i].getAmount());
					}
				}
			} else {
				if (traded[i] != null) {
					player.getInventory().insert(traded[i]);
				}

				if (recieving[i] != null) {
					tradingWith.getPlayer().getInventory().insert(recieving[i]);
				}
			}
		}

		com.everythingrs.marketplace.Trade market = new com.everythingrs.marketplace.Trade();
		System.out.println("player username: " + player.getUsername() + " tradingWith username: " + tradingWith.getPlayer().getUsername() + " Trade: " + trade.toString() + " Received: " + recieved.toString());
		market.setUsername(player.getUsername());
		market.setTradeWith(tradingWith.getPlayer().getUsername());
		for (int item : recieved.keySet()) {
			System.out.println("RECEIVED: " + recieved.size());
			if (item > 0) {
				String itemName = Item.getDefinition(item).getName();
				market.push(new com.everythingrs.marketplace.Item(item, recieved.get(item), itemName));
			}
		}

		for (int item : trade.keySet()) {
			System.out.println("TRADE: " + trade.size());
			if (item > 0) {
				String itemName = Item.getDefinition(item).getName();
				market.push(new com.everythingrs.marketplace.Item(item, trade.get(item), itemName));
			}
		}

		market.update("mjijehoz8vrj046m7remte29z1x6ynyo7mc3vh4wfqpbke29btmpjp8709loo4b348svcs1yvi");

		/*market.setUsername(tradingWith.getPlayer().getUsername());
		market.setTradeWith(player.getPlayer().getUsername());

		market.update("mjijehoz8vrj046m7remte29z1x6ynyo7mc3vh4wfqpbke29btmpjp8709loo4b348svcs1yvi");*/
		
		String[][][] strings = new String[2][trade.size()][4];
		int index = 0;
		for (int item : trade.keySet()) {
			Item tradedItem = new Item(item, trade.get(item));
			strings[0][index] = new String[] { Utility.formatPlayerName(player.getUsername()), "" + tradedItem.getAmount(), Utility.formatPlayerName(tradedItem.getDefinition().getName()), Utility.formatPlayerName(tradingWith.player.getUsername()) };
			strings[1][index] = new String[] { Utility.formatPlayerName(tradingWith.player.getUsername()), "" + tradedItem.getAmount(), Utility.formatPlayerName(tradedItem.getDefinition().getName()), Utility.formatPlayerName(player.getUsername()) };
			index++;
		}
		
		PlayerLogger.TRADE_LOGGER.multiLog(player.getUsername(), "%s has given %s %s to %s", strings[0]);
		PlayerLogger.TRADE_LOGGER.multiLog(tradingWith.player.getUsername(), "%s has received %s %s from %s", strings[1]);
		strings = new String[2][recieved.size()][4];
		index = 0;
		
		for (int item : recieved.keySet()) {
			Item tradedItem = new Item(item, recieved.get(item));
			strings[0][index] = new String[] { Utility.formatPlayerName(tradingWith.player.getUsername()), "" + tradedItem.getAmount(), Utility.formatPlayerName(tradedItem.getDefinition().getName()), Utility.formatPlayerName(player.getUsername()) };
			strings[1][index] = new String[] { Utility.formatPlayerName(player.getUsername()), "" + tradedItem.getAmount(), Utility.formatPlayerName(tradedItem.getDefinition().getName()), Utility.formatPlayerName(tradingWith.player.getUsername()) };
			index++;
		}

		PlayerLogger.TRADE_LOGGER.multiLog(tradingWith.player.getUsername(), "%s has given %s %s to %s", strings[0]);
		PlayerLogger.TRADE_LOGGER.multiLog(player.getUsername(), "%s has received %s %s from %s", strings[1]);

		if (success) {
			player.getClient().queueOutgoingPacket(new SendMessage("The trade has been accepted."));
			tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendMessage("The trade has been accepted."));
		} else {
			player.getClient().queueOutgoingPacket(new SendMessage("You decline the " + getAction() + "."));
			tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendMessage("The other player declined the " + getAction() + "."));
		}

		player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendRemoveInterfaces());

		player.getInventory().update();
		tradingWith.getPlayer().getInventory().update();

		stage = TradeStages.NONE;
		tradingWith.setStage(TradeStages.NONE);
	}

	public String getAction() {
		return "trade";
	}

	public TradeContainer getContainer() {
		return container;
	}

	public Player getPlayer() {
		return player;
	}

	public String getRequestString() {
		return "trade";
	}

	public TradeStages getStage() {
		return stage;
	}

	public String getStatus() {
		return "Trading";
	}

	public Item[] getTradedItems() {
		return container.getItems();
	}

	public Trade getTradingWith() {
		return tradingWith;
	}

	public void request(Player requested) {
		if ((requested.isBusy()) || (player.isBusy())) {
			player.getClient().queueOutgoingPacket(new SendMessage("The other player is busy at the moment."));
			return;
		}

		if (!player.getController().canTrade()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You can't trade right now."));
			return;
		}

		if ((requested.getTrade().trading())) {
			player.getClient().queueOutgoingPacket(new SendMessage("The other player is busy at the moment."));
			return;
		}
		
		if (player.ironPlayer()) {
			player.send(new SendMessage("You are an Ironman player and cannot trade!"));
			return;
		}
		
		if (requested.ironPlayer()) {
			player.send(new SendMessage(requested.getUsername() + " is an Ironman player and cannot trade!"));
			return;
		}
		
		if (requested.getRights() == 2 && !PlayerConstants.isOwner(player)) {
			player.send(new SendMessage("You may not trade Administrators!"));
			return;
		}
		
		if (player.getRights() == 2) {
			player.send(new SendMessage("You may not trade since you are an Administrator!"));
			return;
		}

		player.getClient().queueOutgoingPacket(new SendMessage("Sending " + getRequestString() + " offer.."));
		lastRequest = requested.getUsername();

		if (requested.getTrade().requested(player))
			begin(requested.getTrade());
		else if (!requested.getPrivateMessaging().ignored(player.getUsername()))
			requested.getClient().queueOutgoingPacket(new SendMessage(NameUtil.uppercaseFirstLetter(player.getUsername()) + ":" + getRequestString() + "req:"));
	}

	public boolean requested(Player other) {
		if (lastRequest == null) {
			return false;
		}

		return lastRequest.equalsIgnoreCase(other.getUsername());
	}

	public void reset() {
		container = new TradeContainer(this);
		stage = TradeStages.NONE;
		tradingWith = null;
		lastRequest = null;
	}

	public void setStage(TradeStages stage) {
		this.stage = stage;
	}

	public void setTradingWith(Trade tradingWith) {
		this.tradingWith = tradingWith;
	}

	public boolean trading() {
		return stage != TradeStages.NONE;
	}
}
