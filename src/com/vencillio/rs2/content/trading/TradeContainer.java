package com.vencillio.rs2.content.trading;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemContainer;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItems;

public class TradeContainer extends ItemContainer {

	private final Trade trade;

	public TradeContainer(Trade trade) {
		super(28, ItemContainer.ContainerTypes.STACK, true, true);
		this.trade = trade;
	}

	@Override
	public boolean allowZero(int id) {
		return false;
	}

	public void offer(int id, int amount, int slot) {
		if (!Item.getDefinition(id).isTradable() || Item.getDefinition(id).getName().contains("Clue scroll")) {
			trade.player.getClient().queueOutgoingPacket(new SendMessage("You cannot trade this item."));
			return;
		}

		if (!trade.canAppendTrade()) {
			return;
		}

		if (!trade.player.getInventory().slotContainsItem(slot, id)) {
			return;
		}

		if ((trade.getStage() == Trade.TradeStages.STAGE_2) || (trade.getStage() == Trade.TradeStages.STAGE_2_ACCEPTED)) {
			return;
		}

		int invAmount = trade.player.getInventory().getItemAmount(id);

		if (invAmount < amount) {
			amount = invAmount;
		}

		int added = add(id, amount, true);

		if (added > 0) {
			trade.getPlayer().getInventory().remove(id, added);
		}

		trade.setStage(Trade.TradeStages.STAGE_1);
		trade.tradingWith.setStage(Trade.TradeStages.STAGE_1);

		trade.player.getClient().queueOutgoingPacket(new SendString("", 3431));
		trade.tradingWith.player.getClient().queueOutgoingPacket(new SendString("", 3431));
	}

	@Override
	public void onAdd(Item item) {
	}

	@Override
	public void onFillContainer() {
	}

	@Override
	public void onMaxStack() {
	}

	@Override
	public void onRemove(Item item) {
	}

	@Override
	public void update() {
		if ((trade.stage == Trade.TradeStages.STAGE_2) || (trade.stage == Trade.TradeStages.STAGE_2_ACCEPTED)) {
			trade.player.getClient().queueOutgoingPacket(new SendUpdateItems(3322, null));
			trade.tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendUpdateItems(3322, null));
		} else {
			trade.player.getClient().queueOutgoingPacket(new SendUpdateItems(3322, trade.player.getInventory().getItems()));
			trade.tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendUpdateItems(3322, trade.tradingWith.getPlayer().getInventory().getItems()));
		}

		trade.player.getClient().queueOutgoingPacket(new SendUpdateItems(3415, trade.getTradedItems()));
		trade.player.getClient().queueOutgoingPacket(new SendUpdateItems(3416, trade.tradingWith.getTradedItems()));

		trade.tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendUpdateItems(3415, trade.tradingWith.getTradedItems()));
		trade.tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendUpdateItems(3416, trade.getTradedItems()));
		
		BigInteger toReturn = BigInteger.ZERO;
		for (Item item : trade.player.getTrade().getTradedItems()) {
			if (item == null || item.getDefinition() == null) {
				continue;
			}

			toReturn = toReturn.add(new BigInteger(String.valueOf(item.getDefinition().getGeneralPrice())).multiply(new BigInteger(String.valueOf(item.getAmount()))));
		}
		trade.tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString(NumberFormat.getNumberInstance(Locale.US).format(toReturn) + "gp", 24210));
		for (Item item : trade.tradingWith.getTradedItems()) {
			if (item == null || item.getDefinition() == null) {
				continue;
			}

			toReturn = toReturn.add(new BigInteger(String.valueOf(item.getDefinition().getGeneralPrice())).multiply(new BigInteger(String.valueOf(item.getAmount()))));
		}
		trade.player.getClient().queueOutgoingPacket(new SendString(NumberFormat.getNumberInstance(Locale.US).format(toReturn) + "gp", 24209));
	}

	public void withdraw(int slot, int amount) {
		if (!trade.canAppendTrade()) {
			return;
		}

		if ((trade.getStage() == Trade.TradeStages.STAGE_2) || (trade.getStage() == Trade.TradeStages.STAGE_2_ACCEPTED)) {
			return;
		}

		if (!slotHasItem(slot)) {
			return;
		}

		int id = getSlotId(slot);
		int tradeAmount = getItemAmount(id);

		if (tradeAmount < amount) {
			amount = tradeAmount;
		}

		int removed = remove(id, amount);

		if (removed > 0) {
			trade.player.getInventory().add(id, removed);
		}

		trade.setStage(Trade.TradeStages.STAGE_1);
		trade.tradingWith.setStage(Trade.TradeStages.STAGE_1);

		trade.player.getClient().queueOutgoingPacket(new SendString("", 3431));
		trade.tradingWith.player.getClient().queueOutgoingPacket(new SendString("", 3431));
		trade.tradingWith.player.getClient().queueOutgoingPacket(new SendMessage("@red@The trade has been modified!"));
		trade.tradingWith.player.tradeDelay = System.currentTimeMillis();
	}
}
