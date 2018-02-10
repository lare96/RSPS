package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.cooking.CookingTask;
import com.vencillio.rs2.content.skill.crafting.ArmourCreation;
import com.vencillio.rs2.content.skill.crafting.Craftable;
import com.vencillio.rs2.content.skill.crafting.CraftingType;
import com.vencillio.rs2.content.skill.craftingnew.craftable.impl.Gem;
import com.vencillio.rs2.content.skill.herblore.HerbloreFinishedPotionTask;
import com.vencillio.rs2.content.skill.herblore.HerbloreUnfinishedPotionTask;
import com.vencillio.rs2.content.skill.prayer.BoneBurying;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendEnterXInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMoveComponent;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendSkillGoal;

@SuppressWarnings("all")
public class ChatInterfacePacket extends IncomingPacket {
	@Override
	public int getMaxDuplicates() {
		return 10;
	}

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		switch (opcode) {
		case 40:
			handleDialogue(player);
			break;
		case 135:
			showEnterX(player, in);
			break;
		case 208:
			handleEnterX(player, in);
		}
	}

	public void handleDialogue(Player player) {
		if ((player.getDialogue() == null) || (player.getDialogue().getNext() == -1))
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		else if (player.getDialogue().getNext() > -1)
			player.getDialogue().execute();
	}

	public void handleEnterX(Player player, StreamBuffer.InBuffer in) {
		int amount = in.readInt();

		int slot = player.getEnterXSlot();
		int id = player.getEnterXItemId();

		if (amount < 1) {
			return;
		}

		switch (player.getEnterXInterfaceId()) {
		case 3917:
			double init = 0;
			int skillId = player.getEnterXSlot();
			int type = player.getEnterXItemId();

			if (skillId == Skills.SKILL_COUNT) {
				if (type == 2) {
					if (amount >= Integer.MAX_VALUE) {
						amount = Integer.MAX_VALUE - 1;
					}
					
					long totalExp = player.getSkill().getTotalExperience();
					
					if (totalExp >= Integer.MAX_VALUE) {
						return;
					}

					init = totalExp;
				} else {
					return;
				}
			} else {
				if (type == 1) {
					if (amount > 99) {
						amount = 99;
					}
					
					if (player.getSkill().getLevelForExperience(skillId, player.getSkill().getExperience()[skillId]) >= 99) {
						return;
					}

					init = player.getSkill().getLevelForExperience(skillId, player.getSkill().getExperience()[skillId]);
				} else if (type == 2) {
					if (amount > 200_000_000) {
						amount = 200_000_000;
					}
					
					if (player.getSkill().getExperience()[skillId] >= 200_000_000) {
						return;
					}

					init = player.getSkill().getExperience()[skillId];
				} else {
					return;
				}
			}

			if (amount <= init) {
				return;
			}

			player.send(new SendSkillGoal(skillId, (int) player.getSkill().getExperience()[skillId], amount, type - 1));
			break;
		case 2700:
			if (player.getSummoning().isFamilarBOB()) {
				player.getSummoning().getContainer().withdraw(slot, amount);
			}
			break;
		case 55678:
			BoneBurying.finishOnAltar(player, amount);
			break;
		case 3823:
			player.getShopping().sell(id, amount, slot);
			break;
		case 3900:
			player.getShopping().buy(id, amount, slot);
			break;
		case 15460:
			player.getPlayerShop().onSetPrice(player, amount);
			break;
		case 1743:
			CookingTask.attemptCooking(player, player.getAttributes().getInt("cookingitem"), player.getAttributes().getInt("cookingobject"), amount);
			break;
		case 4429:
			HerbloreUnfinishedPotionTask.attemptToCreateUnfinishedPotion(player, amount, (Item) player.getAttributes().get("herbloreitem1"), (Item) player.getAttributes().get("herbloreitem2"));
			break;
		case 4430:
			HerbloreFinishedPotionTask.attemptPotionMaking(player, amount);
			break;
		case 5064:
			if (!player.getInventory().slotContainsItem(slot, id)) {
				return;
			}

			if (player.getInterfaceManager().hasBankOpen())
				player.getBank().deposit(id, amount, slot);
			else if (player.getSummoning().isFamilarBOB()) {
				player.getSummoning().getContainer().store(id, amount, slot);
			}

			break;
		case 5382:
			if (player.getBank().hasItemId(id)) {
				if (amount == -1) {
					amount = player.getBank().getItemAmount(id);
				}
				
				player.getBank().withdraw(id, amount);
			}
			break;
		case 6669:
			if (player.getDueling().isStaking()) {
				player.getDueling().getContainer().withdraw(slot, amount);
			}
			break;
		case 3322:
			if (player.getTrade().trading())
				player.getTrade().getContainer().offer(id, amount, slot);
			else if (player.getDueling().isStaking()) {
				player.getDueling().getContainer().offer(id, amount, slot);
			}
			break;
		case 3415:
			if (player.getTrade().trading()) {
				player.getTrade().getContainer().withdraw(slot, amount);
			}
			break;
		}
	}

	public void showEnterX(Player player, StreamBuffer.InBuffer in) {
		player.setEnterXSlot(in.readShort(StreamBuffer.ByteOrder.LITTLE));
		player.setEnterXInterfaceId(in.readShort(StreamBuffer.ValueType.A));
		player.setEnterXItemId(in.readShort(StreamBuffer.ByteOrder.LITTLE));
		player.getClient().queueOutgoingPacket(new SendEnterXInterface());
	}
}
