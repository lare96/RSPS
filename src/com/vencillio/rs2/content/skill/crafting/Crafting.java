package com.vencillio.rs2.content.skill.crafting;

import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendEnterXInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class Crafting {
	
	private static int[][] LEATHER_ARMOR_IDS = {
		{ 33187, 1, 1129 }, { 33186, 5, 1129 }, { 33185, 10, 1129 },
		{ 33190, 1, 1059 }, { 33189, 5, 1059 }, { 33188, 10, 1059 },
		{ 33193, 1, 1061 }, { 33192, 5, 1061 }, { 33193, 10, 1061 },
		{ 33194, 1, 1063 }, { 33195, 5, 1063 }, { 33196, 10, 1063 },
		{ 33197, 1, 1095 }, { 33198, 5, 1095 }, { 33199, 10, 1095 },
		{ 33200, 1, 1169 }, { 33201, 5, 1169 }, { 33202, 10, 1169 },
		{ 33203, 1, 1167 }, { 33204, 5, 1167 }, { 33205, 10, 1167 }
		};

	public static boolean handleCraftingByButtons(Player player, int buttonId) {
		switch (buttonId) {
		case 6211:
			if (player.getAttributes().get("craftingType") != null) {
				switch (((CraftingType) player.getAttributes().get("craftingType"))) {
				case WHEEL_SPINNING:
					if (player.getAttributes().get("spinnable") != null) {
						TaskQueue.queue(new WheelSpinning(player, (short) 28, (Spinnable) player.getAttributes().get("spinnable")));
						player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
						player.getAttributes().remove("spinnable");
						player.getAttributes().remove("craftingType");
					}
					break;
				default:
					return true;
				}

				player.getAttributes().remove("craftingType");
			}
			break;
		case 10238:
			if (player.getAttributes().get("craftingType") != null) {
				switch (((CraftingType) player.getAttributes().get("craftingType"))) {
				case WHEEL_SPINNING:
					TaskQueue.queue(new WheelSpinning(player, (short) 5, (Spinnable) player.getAttributes().get("spinnable")));
					player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
					player.getAttributes().remove("spinnable");
					break;
				default:
					return true;
				}

				player.getAttributes().remove("craftingType");
			}
			break;
		case 10239:
			if (player.getAttributes().get("craftingType") != null) {
				switch (((CraftingType) player.getAttributes().get("craftingType"))) {
				case WHEEL_SPINNING:
					TaskQueue.queue(new WheelSpinning(player, (short) 1, (Spinnable) player.getAttributes().get("spinnable")));
					player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
					player.getAttributes().remove("spinnable");
					player.getAttributes().remove("craftingType");
					return true;
				default:
					player.getAttributes().remove("craftingType");
					return true;
				}
			} else {
				return false;
			}
		case 6212:
			player.getClient().queueOutgoingPacket(new SendEnterXInterface(8886, 0));
			break;
		case 34186:
			player.getClient().queueOutgoingPacket(new SendEnterXInterface(8890, 0));
			break;
		case 34190:
			player.getClient().queueOutgoingPacket(new SendEnterXInterface(8894, 0));
			return true;
		}

		for (int[] i : LEATHER_ARMOR_IDS) {
			if (i[0] == buttonId) {
				player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
				TaskQueue.queue(new ArmourCreation(player, (short) i[1], Craftable.forReward(i[2])));
				player.getAttributes().remove("craftingHide");
				player.getAttributes().remove("craftingType");
				return true;
			}
		}
		return false;
	}
}
