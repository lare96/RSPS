package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.VencillioConstants;
import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.DigTask;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.DropTable;
import com.vencillio.rs2.content.ItemInteraction;
import com.vencillio.rs2.content.ItemOpening;
import com.vencillio.rs2.content.MysteryBox;
import com.vencillio.rs2.content.bank.Bank.RearrangeTypes;
import com.vencillio.rs2.content.cluescroll.ClueScrollManager;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.consumables.ConsumableType;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.OptionDialogue;
import com.vencillio.rs2.content.dialogue.impl.teleport.GloryDialogue;
import com.vencillio.rs2.content.dialogue.impl.teleport.RingOfDuelingDialogue;
import com.vencillio.rs2.content.dwarfcannon.DwarfMultiCannon;
import com.vencillio.rs2.content.membership.MembershipBonds;
import com.vencillio.rs2.content.minigames.weapongame.WeaponGameStore;
import com.vencillio.rs2.content.pets.BossPets;
import com.vencillio.rs2.content.skill.crafting.AmuletStringing;
import com.vencillio.rs2.content.skill.crafting.JewelryCreationTask;
import com.vencillio.rs2.content.skill.craftingnew.Crafting;
import com.vencillio.rs2.content.skill.firemaking.Firemaking;
import com.vencillio.rs2.content.skill.fletching.Fletching;
import com.vencillio.rs2.content.skill.herblore.CleanHerbTask;
import com.vencillio.rs2.content.skill.herblore.HerbloreFinishedPotionTask;
import com.vencillio.rs2.content.skill.herblore.HerbloreGrindingTask;
import com.vencillio.rs2.content.skill.herblore.HerbloreUnfinishedPotionTask;
import com.vencillio.rs2.content.skill.herblore.PotionDecanting;
import com.vencillio.rs2.content.skill.herblore.SuperCombatPotion;
import com.vencillio.rs2.content.skill.hunter.Impling.ImplingRewards;
import com.vencillio.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.vencillio.rs2.content.skill.magic.spells.BoltEnchanting;
import com.vencillio.rs2.content.skill.magic.weapons.TridentOfTheSeas;
import com.vencillio.rs2.content.skill.magic.weapons.TridentOfTheSwamp;
import com.vencillio.rs2.content.skill.magic.TabCreation;
import com.vencillio.rs2.content.skill.melee.SerpentineHelmet;
import com.vencillio.rs2.content.skill.prayer.BoneBurying;
import com.vencillio.rs2.content.skill.ranged.ToxicBlowpipe;
import com.vencillio.rs2.content.skill.smithing.SmithingTask;
import com.vencillio.rs2.content.wilderness.TargetSystem;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemCreating;
import com.vencillio.rs2.entity.mob.impl.Zulrah;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Handles the item packet
 * 
 * @author Daniel
 * 
 *         ITEM OPERATE - 75 DROP ITEM - 87 PICKUP ITEM - 236 EQUIP ITEM - 42
 *         USE ITEM ON ITEM - 53 FIRST CLICK ITEM - 122 SECOND CLICK ITEM 16
 *
 */
public class ItemPackets extends IncomingPacket {

	@Override
	public int getMaxDuplicates() {
		return 40;
	}

	@SuppressWarnings("unused")
	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		if (player.isStunned() || player.isDead() || !player.getController().canClick()) {
			return;
		}
		int x;
		int magicId;
		int z;

		switch (opcode) {
		case 145:
			int interfaceId = in.readShort(StreamBuffer.ValueType.A);
			int slot = in.readShort(StreamBuffer.ValueType.A);
			int itemId = in.readShort(StreamBuffer.ValueType.A);
			
			if (VencillioConstants.DEV_MODE) {
				player.send(new SendMessage("InterfaceId: " + interfaceId + " | Interface Manager: " + player.getInterfaceManager().getMain()));
			}

			if ((interfaceId != 1688 && interfaceId != 59813 && interfaceId != 56503) && (!player.getInterfaceManager().verify(interfaceId))) {
				return;
			}
			if (player.getMagic().isTeleporting()) {
				return;
			}
			
			switch (interfaceId) {
			case 56503:
				if (player.getInterfaceManager().main == 56500) {
					WeaponGameStore.select(player, itemId);
				}
				break;
			case 59813:
				if (player.getInterfaceManager().main == 59800) {
					DropTable.itemDetails(player, itemId);
				}
				break;
			case 4393:
				if (player.getInterfaceManager().main == 48500) {
					player.getPriceChecker().withdraw(itemId, slot, 1);					
				} else if (player.getInterfaceManager().main == 26700) {
					TabCreation.handle(player, itemId);
				} else if (player.getInterfaceManager().main == 42750) {
					BoltEnchanting.handle(player, itemId);
				} else if (player.getInterfaceManager().main == 59750) {
					String aName = Utility.getAOrAn(GameDefinitionLoader.getItemDef(itemId).getName()) + " " + GameDefinitionLoader.getItemDef(itemId).getName();				
					player.getUpdateFlags().sendForceMessage(Utility.randomElement(VencillioConstants.ITEM_IDENTIFICATION_MESSAGES).replaceAll("/s/", "" + aName));
				}
				break;

			case 1119:// Smithing
			case 1120:
			case 1121:
			case 1122:
			case 1123:
				SmithingTask.start(player, itemId, 1, interfaceId, slot);
				break;

			case 1688:// Unequip item
				if (!player.getEquipment().slotHasItem(slot)) {
					return;
				}
				player.getEquipment().unequip(slot);
				break;

			case 4233:// Crafting jewlery
			case 4239:
			case 4245:
				JewelryCreationTask.start(player, itemId, 1);
				break;

			case 5064:// Bank & price checker

				if (!player.getInventory().slotContainsItem(slot, itemId)) {
					return;
				}

				if (player.getInterfaceManager().getMain() == 48500) {
					player.getPriceChecker().store(itemId, 1);
					return;
				}

				if (player.getInterfaceManager().hasBankOpen()) {
					bankItem(player, slot, itemId, 1);
					return;
				}
				break;

			case 5382:// Bank
				withdrawBankItem(player, slot, itemId, 1);
				break;

			case 3322:// Trade
				if (player.getTrade().trading())
					handleTradeOffer(player, slot, itemId, 1);
				else if (player.getDueling().isStaking()) {
					player.getDueling().getContainer().offer(itemId, 1, slot);
				}
				break;

			case 3415:// Trade
				if (player.getTrade().trading()) {
					handleTradeRemove(player, slot, itemId, 1);
				}
				break;

			case 6669:// Dueling
				if (player.getDueling().isStaking()) {
					player.getDueling().getContainer().withdraw(slot, 1);
				}
				break;

			case 3900: // Shopping
				player.getShopping().sendSellPrice(itemId);
				break;

			case 3823:// Shopping
				player.getShopping().sendBuyPrice(itemId);
			}

			break;

		case 117:
			interfaceId = in.readShort(true, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			itemId = in.readShort(true, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			slot = in.readShort(true, StreamBuffer.ByteOrder.LITTLE);

			if (player.getMagic().isTeleporting()) {
				return;
			}

			if (VencillioConstants.DEV_MODE) {
				player.send(new SendMessage("Item packet 117 | interface " + interfaceId));
			}

			if ((interfaceId != 1688 && interfaceId != 56503) && (!player.getInterfaceManager().verify(interfaceId)))
				return;

			if (ToxicBlowpipe.itemOption(player, 2, itemId)) {
				return;
			}
			
			if (TridentOfTheSeas.itemOption(player, 2, itemId)) {
				return;
			}
			
			if (TridentOfTheSwamp.itemOption(player, 2, itemId)) {
				return;
			}
			
			if (SerpentineHelmet.itemOption(player, 2, itemId)) {
				return;
			}

			switch (interfaceId) {
			case 56503:
				if (player.getInterfaceManager().main == 56500) {
					WeaponGameStore.purchase(player, itemId);
				}
				break;
			case 2700:
				if (player.getSummoning().isFamilarBOB()) {
					player.getSummoning().getContainer().withdraw(slot, 5);
				}
				break;
			case 1688:
				if (itemId == 1712 || itemId == 1710 || itemId == 1708 || itemId == 1706) {
					player.start(new GloryDialogue(player, true, itemId));
					return;
				}
				if (itemId == 2552 || itemId == 2554 || itemId == 2556 || itemId == 2558 || itemId == 2560 || itemId == 2562 || itemId == 2564 || itemId == 2566) {
					player.start(new RingOfDuelingDialogue(player, true, itemId));
					return;
				}
				if (itemId == 1704) {
					player.getClient().queueOutgoingPacket(new SendMessage("<col=C60DDE>This amulet is all out of charges."));
					return;
				}

				if (itemId == 11283) {
					player.getMagic().onOperateDragonFireShield();
					return;
				}

				// if (itemId == 10499 || itemId == 10498) {
				// player.getRanged().getFromAvasAccumulator();
				// return;
				// }
				break;
			case 1119:
			case 1120:
			case 1121:
			case 1122:
			case 1123:
				SmithingTask.start(player, itemId, 5, interfaceId, slot);
				break;
			case 4233:
			case 4239:
			case 4245:
				JewelryCreationTask.start(player, itemId, 5);
				break;
			case 5064:// Bank & Price checker
				if (!player.getInventory().slotContainsItem(slot, itemId)) {
					return;
				}

				if (player.getInterfaceManager().getMain() == 48500) {
					player.getPriceChecker().store(itemId, 5);
					return;
				}

				if (player.getInterfaceManager().hasBankOpen()) {
					bankItem(player, slot, itemId, 5);
				}
				break;

			case 4393:// Price checker
				if (player.getInterfaceManager().main == 48500) {
					player.getPriceChecker().withdraw(itemId, slot, 5);				
				} else if (player.getInterfaceManager().main == 26700) {
					TabCreation.getInfo(player, itemId);
				}
				break;

			case 5382:
				withdrawBankItem(player, slot, itemId, 5);
				break;
			case 3322:
				if (player.getTrade().trading())
					handleTradeOffer(player, slot, itemId, 5);
				else if (player.getDueling().isStaking()) {
					player.getDueling().getContainer().offer(itemId, 5, slot);
				}
				break;
			case 6669:
				if (player.getDueling().isStaking()) {
					player.getDueling().getContainer().withdraw(slot, 5);
				}
				break;
			case 3415:
				if (player.getTrade().trading()) {
					handleTradeRemove(player, slot, itemId, 5);
				}
				break;
			case 3900:
				player.getShopping().buy(itemId, 1, slot);
				break;
			case 3823:
				player.getShopping().sell(itemId, 1, slot);
			}

			break;
		case 43:
			interfaceId = in.readShort(StreamBuffer.ByteOrder.LITTLE);
			itemId = in.readShort(StreamBuffer.ValueType.A);
			slot = in.readShort(StreamBuffer.ValueType.A);

			if (player.getMagic().isTeleporting()) {
				return;
			}

			if (VencillioConstants.DEV_MODE) {
				player.send(new SendMessage("Item packet 43 | interface " + interfaceId));
			}
			if (!player.getInterfaceManager().verify(interfaceId)) {
				return;
			}

			switch (interfaceId) {
			case 4393:// Price checker
				player.getPriceChecker().withdraw(itemId, slot, 10);
				break;
			case 2700:
				if (player.getSummoning().isFamilarBOB()) {
					player.getSummoning().getContainer().withdraw(slot, 10);
				}
				break;
			case 1119:
			case 1120:
			case 1121:
			case 1122:
			case 1123:
				SmithingTask.start(player, itemId, 10, interfaceId, slot);
				break;
			case 4233:
			case 4239:
			case 4245:
				JewelryCreationTask.start(player, itemId, 10);
				break;
			case 5064:
				if (!player.getInventory().slotContainsItem(slot, itemId)) {
					return;
				}

				if (player.getInterfaceManager().getMain() == 48500) {
					player.getPriceChecker().store(itemId, 10);
					return;
				}

				if (player.getInterfaceManager().hasBankOpen())
					bankItem(player, slot, itemId, 10);
				else if (player.getSummoning().isFamilarBOB()) {
					player.getSummoning().getContainer().store(itemId, 10, slot);
				}

				break;
			case 5382:
				withdrawBankItem(player, slot, itemId, 10);
				break;
			case 3322:
				if (player.getTrade().trading())
					handleTradeOffer(player, slot, itemId, 10);
				else if (player.getDueling().isStaking()) {
					player.getDueling().getContainer().offer(itemId, 10, slot);
				}
				break;
			case 6669:
				if (player.getDueling().isStaking()) {
					player.getDueling().getContainer().withdraw(slot, 10);
				}
				break;
			case 3415:
				if (player.getTrade().trading()) {
					handleTradeRemove(player, slot, itemId, 10);
				}
				break;
			case 3900:
				player.getShopping().buy(itemId, 5, slot);
				break;
			case 3823:
				player.getShopping().sell(itemId, 5, slot);
			}

			break;
		case 129:
			slot = in.readShort(StreamBuffer.ValueType.A);
			interfaceId = in.readShort();
			itemId = in.readShort(StreamBuffer.ValueType.A);

			if (player.getMagic().isTeleporting()) {
				return;
			}

			if (VencillioConstants.DEV_MODE) {
				player.send(new SendMessage("Item packet 129 | interface " + interfaceId));
			}

			if (!player.getInterfaceManager().verify(interfaceId)) {
				return;
			}
			switch (interfaceId) {
			case 4393:// Price checker
				player.getPriceChecker().withdraw(itemId, slot, player.getPriceChecker().getItemAmount(itemId));
				break;
			case 2700:
				if (player.getSummoning().isFamilarBOB()) {
					player.getSummoning().getContainer().withdraw(slot, 2147483647);
				}
				break;
			case 5064:
				if (!player.getInventory().slotContainsItem(slot, itemId)) {
					return;
				}

				if (player.getInterfaceManager().getMain() == 48500) {
					player.getPriceChecker().store(itemId, player.getInventory().getItemAmount(itemId));
					return;
				}

				if (player.getInterfaceManager().hasBankOpen())
					bankItem(player, slot, itemId, 2147483647);
				else if (player.getSummoning().isFamilarBOB()) {
					player.getSummoning().getContainer().store(itemId, 2147483647, slot);
				}
				break;
			case 5382:
				withdrawBankItem(player, slot, itemId, 2147483647);
				break;
			case 3322:
				if (player.getTrade().trading())
					handleTradeOffer(player, slot, itemId, 2147483647);
				else if (player.getDueling().isStaking()) {
					player.getDueling().getContainer().offer(itemId, 2147483647, slot);
				}
				break;
			case 6669:
				if (player.getDueling().isStaking()) {
					player.getDueling().getContainer().withdraw(slot, 2147483647);
				}
				break;
			case 3415:
				if (player.getTrade().trading()) {
					handleTradeRemove(player, slot, itemId, 2147483647);
				}
				break;
			case 3900:
				player.getShopping().buy(itemId, 10, slot);
				break;
			case 3823:
				player.getShopping().sell(itemId, 10, slot);
			}

			break;
		case 41:
			itemId = in.readShort();
			slot = in.readShort(StreamBuffer.ValueType.A);
			in.readShort();

			if (player.getMagic().isTeleporting()) {
				return;
			}

			if (VencillioConstants.DEV_MODE) {
				player.send(new SendMessage("Item packet 41"));
			}

			if (!player.getInventory().slotContainsItem(slot, itemId)) {
				return;
			}

			if (ItemInteraction.clickPouch(player, itemId, 2)) {
				return;
			}

			switch (itemId) {
				
			case 4079:// YOYO
				player.getUpdateFlags().sendAnimation(1458, 0);
				return;
				
			case 12810://Iron Man Armour
			case 12811:
			case 12812:
			case 12813:
			case 12814:
			case 12815:
				if (player.ironPlayer()) {
					player.getEquipment().equip(player.getInventory().get(slot), slot);
				} else {
					DialogueManager.sendStatement(player, "Only Iron Man may wear this!");
				}
				return;

			}

			player.getEquipment().equip(player.getInventory().get(slot), slot);
			break;
		case 214:
			interfaceId = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			int transfer = in.readByte(StreamBuffer.ValueType.C);
			int fromSlot = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			int toSlot = in.readShort(StreamBuffer.ByteOrder.LITTLE);

			if (VencillioConstants.DEV_MODE) {
				player.send(new SendMessage("Item packet 214"));
			}

			switch (interfaceId) {
			case 5382:
				if (player.getTrade().trading()) {
					player.send(new SendMessage("You can not do that right now!"));
					return;
				}

				if (!player.getBank().isSearching()) {
					if (transfer == 2) {
						player.getBank().itemToTab(fromSlot, toSlot, true);
					} else {
						if (transfer == 1) {
							int fromTab = player.getBank().getData(fromSlot, 0);
							int toTab = player.getBank().getData(toSlot, 0);
							player.getBank().changeTabAmount(toTab, 1, false);
							player.getBank().changeTabAmount(fromTab, -1, true);
							RearrangeTypes temp = player.getBank().rearrangeType;
							player.getBank().rearrangeType = RearrangeTypes.INSERT;
							player.getBank().swap(toSlot - (toTab > fromTab ? 1 : 0), fromSlot);
							player.getBank().rearrangeType = temp;
							player.getBank().update();
						} else {
							RearrangeTypes temp = player.getBank().rearrangeType;
							player.getBank().rearrangeType = RearrangeTypes.SWAP;
							player.getBank().swap(toSlot, fromSlot);
							player.getBank().rearrangeType = temp;
						}
					}
				}
				break;
			case 3214:
			case 5064:
				player.getInventory().swap(toSlot, fromSlot, false);
				break;
			}

			break;
		case 87:
			itemId = in.readShort(StreamBuffer.ValueType.A);
			in.readShort();
			slot = in.readShort(StreamBuffer.ValueType.A);

			if (!player.getInventory().slotContainsItem(slot, itemId)) {
				return;
			}

			if (player.getMagic().isTeleporting() || !player.getController().canDrop(player)) {
				return;
			}

			if (VencillioConstants.DEV_MODE) {
				player.send(new SendMessage("Item packet 87"));
			}

			if (ToxicBlowpipe.itemOption(player, 4, itemId)) {
				return;
			}
			
			if (TridentOfTheSeas.itemOption(player, 4, itemId)) {
				return;
			}
			
			if (TridentOfTheSwamp.itemOption(player, 4, itemId)) {
				return;
			}
			
			if (SerpentineHelmet.itemOption(player, 4, itemId)) {
				return;
			}

			if (itemId == 4045) {
				player.getUpdateFlags().sendAnimation(new Animation(827));
				player.getInventory().remove(new Item(4045, 1));
				player.hit(new Hit(15));
				player.getUpdateFlags().sendForceMessage("Ow! That really hurt my soul!");
				return;
			}

			if (BossPets.spawnPet(player, itemId, false)) {
				return;
			}

			if (player.getRights() == 2) {
				player.send(new SendMessage("You may not do this since you are an Administrator!"));
				return;
			}
			
			for (int index = 0; index < VencillioConstants.ITEM_DISMANTLE_DATA.length; index++) {
				if (itemId == VencillioConstants.ITEM_DISMANTLE_DATA[index][0]) {
					player.getInventory().remove(itemId, 1);
					player.getInventory().addOrCreateGroundItem(VencillioConstants.ITEM_DISMANTLE_DATA[index][1], 1, true);
					player.getInventory().addOrCreateGroundItem(VencillioConstants.ITEM_DISMANTLE_DATA[index][1], 1, true);
					player.send(new SendMessage("You have dismantled your " + GameDefinitionLoader.getItemDef(itemId).getName() + "."));
					player.send(new SendRemoveInterfaces());
					return;
				}
			}

			if (!Item.getDefinition(itemId).isTradable() || Item.getDefinition(itemId).getName().contains("Clue scroll")) {
				player.start(new OptionDialogue("</col>Drop and loose forever", p -> {
					player.getInventory().remove(itemId, 1);
					player.send(new SendMessage("Your " + GameDefinitionLoader.getItemDef(itemId).getName() + " has been dropped and lost forever."));
					player.send(new SendRemoveInterfaces());
				} , "Keep " + GameDefinitionLoader.getItemDef(itemId).getName(), p -> {
					player.send(new SendRemoveInterfaces());
				}));
				return;
			}

			player.getGroundItems().drop(itemId, slot);
			break;
		case 236:
			int y = in.readShort(StreamBuffer.ByteOrder.LITTLE);
			itemId = in.readShort();
			x = in.readShort(StreamBuffer.ByteOrder.LITTLE);

			if (player.getMagic().isTeleporting()) {
				return;
			}

			player.getCombat().reset();

			player.getGroundItems().pickup(x, y, itemId);
			break;
		case 53:
			int firstSlot = in.readShort();
			int secondSlot = in.readShort(StreamBuffer.ValueType.A);

			if ((!player.getInventory().slotHasItem(firstSlot)) || (!player.getInventory().slotHasItem(secondSlot))) {
				return;
			}

			if (player.getMagic().isTeleporting()) {
				return;
			}

			Item usedWith = player.getInventory().get(firstSlot);
			Item itemUsed = player.getInventory().get(secondSlot);

			if ((usedWith == null) || (itemUsed == null)) {
				return;
			}

			if ((usedWith.getId() == 985 && itemUsed.getId() == 987) || (usedWith.getId() == 987 && itemUsed.getId() == 985)) {
				player.getInventory().remove(985, 1);
				player.getInventory().remove(987, 1);
				player.getInventory().add(989, 1);
				return;
			}
			
			if (Firemaking.attemptFiremaking(player, itemUsed, usedWith)) {
				return;
			}
			
			if (Fletching.SINGLETON.itemOnItem(player, usedWith, itemUsed)) {
				return;
			}
			
			if (Crafting.SINGLETON.itemOnItem(player, usedWith, itemUsed)) {
				return;
			}

			if (ItemCreating.handle(player, itemUsed.getId(), usedWith.getId())) {
				return;
			}

			if (ToxicBlowpipe.itemOnItem(player, itemUsed, usedWith)) {
				return;
			}

			if (TridentOfTheSeas.itemOnItem(player, itemUsed, usedWith)) {
				return;
			}
			
			if (TridentOfTheSwamp.itemOnItem(player, itemUsed, usedWith)) {
				return;
			}
			
			if (SuperCombatPotion.itemOnItem(player, itemUsed, usedWith)) {
				return;
			}
			
			if (SerpentineHelmet.itemOnItem(player, itemUsed, usedWith)) {
				return;
			}
			
			if (itemUsed.getId() == 1759 || usedWith.getId() == 1759) {
				AmuletStringing.stringAmulet(player, itemUsed.getId(), usedWith.getId());
				return;
			}

			if ((usedWith.getId() == 227) || (itemUsed.getId() == 227)) {
				HerbloreUnfinishedPotionTask.displayInterface(player, itemUsed, usedWith);
			} else if (!HerbloreFinishedPotionTask.displayInterface(player, itemUsed, usedWith)) {
				if ((usedWith.getId() == 233) || (itemUsed.getId() == 233)) {
					HerbloreGrindingTask.handleGrindingIngredients(player, itemUsed, usedWith);
				} else if (!Firemaking.attemptFiremaking(player, itemUsed, usedWith)) {
					if ((usedWith.getId() == 1785) || (itemUsed.getId() == 1785)) {
						if ((usedWith.getId() == 1785) && (itemUsed.getId() == 1775))
							player.getClient().queueOutgoingPacket(new SendInterface(11462));
						else if ((itemUsed.getId() == 1785) && (usedWith.getId() == 1775)) {
							player.getClient().queueOutgoingPacket(new SendInterface(11462));
						}

					}

					if (PotionDecanting.decant(player, firstSlot, secondSlot)) {
						return;
					}

				}
			}

			break;
		case 25:
			in.readShort();
			int itemInInven = in.readShort(StreamBuffer.ValueType.A);
			int groundItem = in.readShort();
			y = in.readShort(StreamBuffer.ValueType.A);
			z = player.getLocation().getZ();
			in.readShort();
			x = in.readShort();

		case 237:
			slot = in.readShort();
			itemId = in.readShort(StreamBuffer.ValueType.A);
			interfaceId = in.readShort();
			magicId = in.readShort(StreamBuffer.ValueType.A);

			if (!player.getInterfaceManager().verify(interfaceId)) {
				return;
			}

			if (!player.getInventory().slotContainsItem(slot, itemId)) {
				return;
			}

			if (player.getMagic().isTeleporting()) {
				return;
			}

			player.getAttributes().set("magicitem", Integer.valueOf(itemId));
			player.getMagic().useMagicOnItem(itemId, magicId);
			break;
		case 181:
			y = in.readShort(StreamBuffer.ByteOrder.LITTLE);
			itemId = in.readShort();
			x = in.readShort(StreamBuffer.ByteOrder.LITTLE);
			magicId = in.readShort(StreamBuffer.ValueType.A);
			break;
		case 253:// second click ground item
			x = in.readShort(StreamBuffer.ByteOrder.LITTLE);
			y = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			itemId = in.readShort(StreamBuffer.ValueType.A);
			z = player.getLocation().getZ();

			break;
		case 122:
			if (VencillioConstants.DEV_MODE) {
				player.send(new SendMessage("Item packet 122"));
			}

			interfaceId = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			slot = in.readShort(StreamBuffer.ValueType.A);
			itemId = in.readShort(StreamBuffer.ByteOrder.LITTLE);

			if (!player.getInventory().slotContainsItem(slot, itemId)) {
				return;
			}

			if (player.getMagic().isTeleporting()) {
				return;
			}
			
			if (ClueScrollManager.SINGLETON.clickItem(player, itemId)) {
				return;
			}

			if (ItemOpening.openSet(player, itemId)) {
				return;
			}

			if (ItemInteraction.clickPouch(player, itemId, 1)) {
				return;
			}

			if (itemId == 4079) {
				player.getUpdateFlags().sendAnimation(1457, 0);
				return;
			}

			if (DwarfMultiCannon.setCannonBase(player, itemId)) {
				return;
			}
			
			if (MembershipBonds.handle(player, itemId)) {
				return;
			}

			if (BoneBurying.bury(player, itemId, slot)) {
				return;
			}
			
			if ((player.getConsumables().consume(itemId, slot, ConsumableType.FOOD)) || (player.getConsumables().consume(itemId, slot, ConsumableType.POTION))) {
				return;
			}
			
			if (player.getMagic().clickMagicItems(itemId)) {
				return;
			}
			switch (itemId) {
			
			case 6199://Mystery Box
				MysteryBox.open(player);
				break;
				
			case 12846:
				if (TargetSystem.getInstance().playerHasTarget(player)) {
					Player target = World.getPlayers()[player.targetIndex];	
					if (target != null) {
						player.getMagic().teleport(target.getLocation(), TeleportTypes.SPELL_BOOK);
						player.getInventory().remove(12846, 1);
						player.send(new SendMessage("You have teleported to your target."));
					}
				} else {
					player.send(new SendMessage("You do not have a target to teleport to!"));
				}
				break;
			
			case 405://Casket
				player.getInventory().remove(itemId, 1);
				int random = Utility.random(10000) + Utility.random(2500) + Utility.random(666);
				player.getInventory().add(995, random);
				player.send(new SendMessage("You have found " + random + " coins inside the casket"));
				break;
			case 12938:// Zulrah teleport
				player.getInventory().remove(12938, 1);
				player.getMagic().teleport(2268, 3070, player.getIndex() << 2, TeleportTypes.SPELL_BOOK);
				TaskQueue.queue(new Task(5) {
					@Override
					public void execute() {
						Zulrah mob = new Zulrah(player, new Location(2266, 3073, player.getIndex() << 2));
						mob.face(player);
						mob.getUpdateFlags().sendAnimation(new Animation(5071));
						player.face(mob);
						player.send(new SendMessage("Welcome to Zulrah's shrine."));
						DialogueManager.sendStatement(player, "Welcome to Zulrah's shrine.");
						stop();
					}

					@Override
					public void onStop() {
					}
				});
				break;
			case 2528:// Lamp
				player.send(new SendInterface(2808));
				break;
			case 952:// Spade
				TaskQueue.queue(new DigTask(player));
				return;
			case 4155:// Slayer gem
				if (!player.getSlayer().hasTask()) {
					DialogueManager.sendStatement(player, "You currently do not have a task!");
					return;
				}
				DialogueManager.sendStatement(player, "You have been tasked to kill:", player.getSlayer().getAmount() + " " + player.getSlayer().getTask());
				return;

			case 13188:
				player.getEquipment().equip(player.getInventory().get(slot), slot);
				break;
			}

			CleanHerbTask.attemptHerbCleaning(player, slot);
			break;
		case 16:
			itemId = in.readShort(StreamBuffer.ValueType.A);
			slot = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			interfaceId = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);

			if (VencillioConstants.DEV_MODE) {
				player.send(new SendMessage("Item packet 16"));
			}
			
			if (!player.getInventory().slotContainsItem(slot, itemId)) {
				return;
			}

			if (player.getMagic().isTeleporting()) {
				return;
			}

			if (ItemInteraction.clickPouch(player, itemId, 3)) {
				return;
			}

			if (ImplingRewards.impReward.containsKey(itemId)) {
				ImplingRewards.lootImpling(player, itemId);
				return;
			}

			if (ToxicBlowpipe.itemOption(player, 1, itemId)) {
				return;
			}
			
			if (TridentOfTheSeas.itemOption(player, 1, itemId)) {
				return;
			}
			
			if (TridentOfTheSwamp.itemOption(player, 1, itemId)) {
				return;
			}
			
			if (SerpentineHelmet.itemOption(player, 1, itemId)) {
				return;
			}

			if (itemId == 4079) {
				player.getUpdateFlags().sendAnimation(1459, 0);
				return;
			}

			if (itemId == 11283) {
				player.getClient().queueOutgoingPacket(new SendMessage("Your shield has " + player.getMagic().getDragonFireShieldCharges() + " charges."));
				return;
			}

			switch (itemId) {

			case 11802:// ags
			case 11804:// bgs
			case 11806:// sgs
			case 11808:// zgs
				int[][] items = { { 11802, 11810 }, { 11804, 11812 }, { 11806, 11814 }, { 11808, 11816 } };
				if (player.getInventory().getFreeSlots() < 1) {
					DialogueManager.sendItem1(player, "You need at least one free slot to dismantle your godsword.", itemId);
					return;
				}
				for (int i = 0; i < items.length; i++) {
					if (itemId == items[i][0] && player.getInventory().hasItemAmount(items[i][0], 1)) {
						player.getInventory().remove(items[i][0], 1);
						player.getInventory().add(items[i][1], 1);
						player.getInventory().add(11798, 1);
						DialogueManager.sendItem2zoom(player, "You carefully attempt to dismantly your godsword...", "@dre@You were successful!", items[i][1], 11798);
						break;
					}
				}
				break;
			
			}
			
			
			break;
		case 75:
			if (VencillioConstants.DEV_MODE) {
				player.send(new SendMessage("Item packet 75"));
			}

			interfaceId = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			slot = in.readShort(StreamBuffer.ByteOrder.LITTLE);
			itemId = in.readShort(true, StreamBuffer.ValueType.A);

			if (!player.getInventory().slotContainsItem(slot, itemId)) {
				return;
			}

			if (player.getMagic().isTeleporting()) {
				return;
			}

			if (ToxicBlowpipe.itemOption(player, 3, itemId)) {
				return;
			}
			
			if (TridentOfTheSeas.itemOption(player, 3, itemId)) {
				return;
			}
			
			if (TridentOfTheSwamp.itemOption(player, 3, itemId)) {
				return;
			}

			if (itemId == 1712 || itemId == 1710 || itemId == 1708 || itemId == 1706) {
				player.start(new GloryDialogue(player, false, itemId));
				return;
			}
			if (itemId == 2552 || itemId == 2554 || itemId == 2556 || itemId == 2558 || itemId == 2560 || itemId == 2562 || itemId == 2564 || itemId == 2566) {
				player.start(new RingOfDuelingDialogue(player, false, itemId));
				return;
			}
			if (itemId == 1704) {
				player.getClient().queueOutgoingPacket(new SendMessage("<col=C60DDE>This amulet is all out of charges."));
				return;
			}

			if (itemId == 4079) {
				player.getUpdateFlags().sendAnimation(1460, 0);
				return;
			}
			
			if (itemId == 995) {
				player.getPouch().addPouch();
				return;
			}

			break;
		}
	}

	/**
	 * Handle add item to trade
	 * 
	 * @param player
	 * @param slot
	 * @param itemId
	 * @param amount
	 */
	public void handleTradeOffer(Player player, int slot, int itemId, int amount) {
		player.getTrade().getContainer().offer(itemId, amount, slot);
	}

	/**
	 * Handle removing item from trade
	 * 
	 * @param player
	 * @param slot
	 * @param itemId
	 * @param amount
	 */
	public void handleTradeRemove(Player player, int slot, int itemId, int amount) {
		player.getTrade().getContainer().withdraw(slot, amount);
	}

	/**
	 * Withdraw bank item
	 * 
	 * @param player
	 * @param slot
	 * @param itemId
	 * @param amount
	 */
	public void withdrawBankItem(Player player, int slot, int itemId, int amount) {
		player.getBank().withdraw(itemId, amount);
//		player.getBank().itemToTab(slot, 0, true);
	}

	/**
	 * Bank item
	 * 
	 * @param player
	 * @param slot
	 * @param itemId
	 * @param amount
	 */
	public void bankItem(Player player, int slot, int itemId, int amount) {
		player.getBank().deposit(itemId, amount, slot);
	}

}
