package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.VencillioConstants;
import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.network.StreamBuffer.ByteOrder;
import com.vencillio.rs2.entity.WalkToActions;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.following.Following;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class NPCPacket extends IncomingPacket {

	public static final int FIRST_CLICK = 155;
	public static final int SECOND_CLICK = 17;
	public static final int THIRD_CLICK = 21;
	public static final int FOURTH_CLICK = 230;
	public static final int ATTACK = 72;
	public static final int MAGIC_ON_NPC = 131;
	public static final int ITEM_ON_NPC = 57;

	@Override
	public int getMaxDuplicates() {
		return 1;
	}

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		if (player.isDead() || !player.getController().canClick() || player.isStunned()) {
			return;
		}

		player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());

		player.getCombat().reset();

		if (!player.getMagic().isDFireShieldEffect()) {
			player.getMagic().getSpellCasting().resetOnAttack();
		}
		

		switch (opcode) {
		case 155:
			int slot = in.readShort(true, StreamBuffer.ByteOrder.LITTLE);

			if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
				return;
			}

			WalkToActions.clickNpc(player, 1, slot);
			break;
		case 17:
			slot = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE) & 0xFFFF;

			if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
				return;
			}

			WalkToActions.clickNpc(player, 2, slot);
			break;
		case 21:
			slot = in.readShort();

			if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
				return;
			}

			WalkToActions.clickNpc(player, 3, slot);
			break;
		case 18:
			slot = in.readShort(true, ByteOrder.LITTLE);

			if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
				return;
			}

			WalkToActions.clickNpc(player, 4, slot);
			break;
		case 72:
			slot = in.readShort(StreamBuffer.ValueType.A);

			Mob mob = World.getNpcs()[slot];

			if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
				return;
			}

			if (mob.getId() == 5527 || mob.getId() == 606 || mob.getId() == 1603 || mob.getId() == 2130 || mob.getId() == 2131 || mob.getId() == 2132 || mob.getId() == 5860 || mob.getId() == 5523 || mob.getId() == 5860 || mob.getId() == 403 || mob.getId() == 490 || mob.getId() == 4936 || mob.getId() == 315) {
				WalkToActions.clickNpc(player, 2, slot);
				return;
			}
			
			if (!player.isHitZulrah()) {
				if (mob.getId() == 2042 || mob.getId() == 2043 || mob.getId() == 2044) {
					return;
				}
			}
			
			player.getMovementHandler().reset();

			player.getCombat().setAttacking(mob);
			player.getFollowing().setFollow(mob, Following.FollowType.COMBAT);

			if (VencillioConstants.DEV_MODE) {
				player.getClient().queueOutgoingPacket(new SendMessage("[NPCPacket] npc id " + mob.getId()));
			}
			
			break;
		case 131:
			slot = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			int magicId = in.readShort(StreamBuffer.ValueType.A);

			player.getMovementHandler().reset();
			mob = World.getNpcs()[slot];

			if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
				return;
			}

			if (VencillioConstants.DEV_MODE) {
				player.getClient().queueOutgoingPacket(new SendMessage("Magic id: " + magicId));
			}
			
			if (!player.isHitZulrah()) {
				if (mob.getId() == 2042 || mob.getId() == 2043 || mob.getId() == 2044) {
					return;
				}
			}

			player.getMagic().getSpellCasting().castCombatSpell(magicId, mob);
			break;
		case 57:
			int itemId = in.readShort(StreamBuffer.ValueType.A);
			slot = in.readShort(StreamBuffer.ValueType.A);
			int itemSlot = in.readShort(StreamBuffer.ByteOrder.LITTLE);

			if ((!World.isMobWithinRange(slot)) || (World.getNpcs()[slot] == null)) {
				return;
			}

			if (!player.getInventory().slotContainsItem(itemSlot, itemId)) {
				return;
			}

			WalkToActions.useItemOnNpc(player, itemId, slot);
		}
	}
}
