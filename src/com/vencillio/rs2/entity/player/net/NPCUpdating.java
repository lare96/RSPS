package com.vencillio.rs2.entity.player.net;

import java.util.Iterator;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.GameConstants;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.MobUpdateFlags;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerUpdateFlags;

/**
 * Provides static utility methods for updating NPCs.
 * 
 * @author blakeman8192
 */
public class NPCUpdating {

	public static void addNewNpc(StreamBuffer.OutBuffer out, Player player, Mob mob) {
		out.writeBits(14, mob.getIndex());
		Location delta = Utility.delta(player.getLocation(), mob.getLocation());
		out.writeBits(5, delta.getY());
		out.writeBits(5, delta.getX());
		out.writeBits(1, 0);
		out.writeBits(16, mob.getId());
		out.writeBit(true);
	}

	public static void appendState(StreamBuffer.OutBuffer block, MobUpdateFlags npc, int faceDir) {
		int mask = 0x0;
		if (npc.isAnimationUpdateRequired()) {
			mask |= 0x10;
		}
		if (npc.isHitUpdate()) {
			mask |= 0x8;
		}
		if (npc.isGraphicsUpdateRequired()) {
			mask |= 0x80;
		}
		if (npc.isEntityFaceUpdate()) {
			mask |= 0x20;
		}
		if (npc.isForceChatUpdate()) {
			mask |= 0x1;
		}
		if (npc.isHitUpdate2()) {
			mask |= 0x40;
		}
		if (npc.isTransformUpdate()) {
			mask |= 0x2;
		}
		if (npc.isFaceToDirection() || faceDir != -1) {
			mask |= 0x4;
		}
		block.writeByte(mask);
		if (npc.isAnimationUpdateRequired()) {
			block.writeShort(npc.getAnimationId(), StreamBuffer.ByteOrder.LITTLE);
			block.writeByte(npc.getAnimationDelay());
		}
		if (npc.isHitUpdate()) {
			block.writeByte(npc.getDamage(), StreamBuffer.ValueType.A);
			block.writeByte(npc.getHitType(), StreamBuffer.ValueType.C);
			block.writeByte(npc.getHitUpdateType());

			block.writeByte(getCurrentHP(npc.getHp(), npc.getMaxHP(), 100), StreamBuffer.ValueType.A);
			block.writeByte(100);
		}
		if (npc.isGraphicsUpdateRequired()) {
			block.writeShort(npc.getGraphicId());
			block.writeInt(npc.getGraphicDelay());
		}
		if (npc.isEntityFaceUpdate()) {
			block.writeShort(npc.getEntityFaceIndex());
		}
		if (npc.isForceChatUpdate()) {
			block.writeString(npc.getForceChatMessage());
		}
		if (npc.isHitUpdate2()) {
			block.writeByte(npc.getDamage2(), StreamBuffer.ValueType.C);
			block.writeByte(npc.getHitType2(), StreamBuffer.ValueType.S);
			block.writeByte(npc.getHitUpdateType2());

			block.writeByte(getCurrentHP(npc.getHp(), npc.getMaxHP(), 100), StreamBuffer.ValueType.S);
			block.writeByte(100, StreamBuffer.ValueType.C);
		}
		if (npc.isTransformUpdate()) {
			if (npc.getTransformId() != -1) {
				block.writeShort(npc.getTransformId(), StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			}
		}
		if (npc.isFaceToDirection() || faceDir != -1) {
			Location pos = null;
			if (npc.isFaceToDirection()) {
				pos = npc.getFaceLocation();
			} else {
				pos = new Location(npc.getLocation().getX() + GameConstants.DIR[faceDir][0], npc.getLocation().getY() + GameConstants.DIR[faceDir][1]);
			}
			if (pos == null) {
				block.writeShort(0, StreamBuffer.ByteOrder.LITTLE);
				block.writeShort(0, StreamBuffer.ByteOrder.LITTLE);
			} else {
				block.writeShort(pos.getX() * 2 + 1, StreamBuffer.ByteOrder.LITTLE);
				block.writeShort(pos.getY() * 2 + 1, StreamBuffer.ByteOrder.LITTLE);
			}
		}
	}

	public static int getCurrentHP(int hp, int maxHp, int totalHp) {
		double x = hp / (double) maxHp;
		return (int) Math.round(x * totalHp);
	}

	public static void update(Client client, PlayerUpdateFlags pFlags, MobUpdateFlags[] nFlags) {
		// XXX: The buffer sizes may need to be tuned.
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(4096);
		StreamBuffer.OutBuffer block = StreamBuffer.newOutBuffer(2048);

		// Initialize the update packet.
		out.writeVariableShortPacketHeader(client.getEncryptor(), 65);
		out.setAccessType(StreamBuffer.AccessType.BIT_ACCESS);

		// Update the NPCs in the local list.
		out.writeBits(8, client.getNpcs().size());
		for (Iterator<Mob> i = client.getNpcs().iterator(); i.hasNext();) {
			Mob mob = i.next();
			MobUpdateFlags flags = nFlags[mob.getIndex()];

			if (mob.getLocation().isViewableFrom(pFlags.getLocation()) && mob.isVisible() && flags != null && !flags.isPlacement()) {
				updateNpcMovement(out, flags);
				appendState(block, flags, -1);
			} else {
				// Remove the NPC from the local list.
				out.writeBit(true);
				out.writeBits(2, 3);
				i.remove();
				// World.getMobUpdateList().decr(mob);
			}
		}

		int added = 0;
		int size = client.getNpcs().size();
		// Update the local NPC list itself.
		for (int i = 0; i < World.getNpcs().length; i++) {
			if (size >= 255) {
				break;
			}

			Mob mob = World.getNpcs()[i];
			MobUpdateFlags flags = nFlags[i];

			if (flags == null || mob == null || !mob.isVisible() || client.getNpcs().contains(mob)) {
				continue;
			}

			if (mob.getLocation().isViewableFrom(pFlags.getLocation()) && mob.isActive()) {
				addNewNpc(out, client.getPlayer(), mob);
				client.getNpcs().add(mob);

				appendState(block, flags, flags.getFaceDirection());

				if (added++ == 15) {
					break;
				}
			}
		}

		// Append the update block to the packet if need be.
		if (block.getBuffer().writerIndex() > 0) {
			out.writeBits(14, 16383);
			out.setAccessType(StreamBuffer.AccessType.BYTE_ACCESS);
			out.writeBytes(block.getBuffer());
		} else {
			out.setAccessType(StreamBuffer.AccessType.BYTE_ACCESS);
		}

		// Ship the packet out to the client.
		out.finishVariableShortPacketHeader();
		client.send(out.getBuffer());
	}

	public static void updateNpcMovement(StreamBuffer.OutBuffer out, MobUpdateFlags npc) {
		if (npc.getPrimaryDirection() == -1) {
			if (npc.isUpdateRequired()) {
				out.writeBit(true);
				out.writeBits(2, 0);
			} else {
				out.writeBit(false);
			}
		} else {
			out.writeBit(true);
			out.writeBits(2, 1);
			out.writeBits(3, npc.getPrimaryDirection());
			out.writeBit(true);
		}
	}
}
