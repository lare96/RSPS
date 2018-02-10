package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.core.network.StreamBuffer.ByteOrder;
import com.vencillio.core.network.StreamBuffer.InBuffer;
import com.vencillio.core.network.StreamBuffer.ValueType;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.FollowToEntityTask;
import com.vencillio.rs2.content.wilderness.TargetSystem;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.following.Following.FollowType;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

@SuppressWarnings("all")
public class PlayerOptionPacket extends IncomingPacket {

	public static final int TRADE = 153;
	public static final int FOLLOW = 128;
	public static final int ATTACK = 73;
	public static final int OPTION_4 = 139;
	public static final int MAGIC_ON_PLAYER = 249;
	public static final int USE_ITEM_ON_PLAYER = 14;
	public static final int TRADE_ANSWER2 = 39;

	@Override
	public int getMaxDuplicates() {
		return 2;
	}

	@Override
	public void handle(final Player player, InBuffer in, int opcode, int length) {
		if ((player.isDead()) || (!player.getController().canClick())) {
			return;
		}

		int playerSlot = -1;

		int itemSlot = -1;
		TaskQueue.onMovement(player);

		Player other = null;

		if (player.getDueling().getInteracting() != null) {
			if (player.getDueling().isScreen()) {
				player.getDueling().decline();
			}
		}

		player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		player.getMagic().getSpellCasting().resetOnAttack();

		switch (opcode) {
		case 153:
		case 128:
			final int slot = in.readShort(true, ByteOrder.LITTLE);

			if ((!World.isPlayerWithinRange(slot)) || (World.getPlayers()[slot] == null) || (slot == player.getIndex())) {
				return;
			}

			TaskQueue.queue(new FollowToEntityTask(player, World.getPlayers()[slot]) {
				@Override
				public void onDestination() {
					Player other = World.getPlayers()[slot];

					if (other == null) {
						player.getMovementHandler().reset();
						return;
					}

					if (player.getController().equals(ControllerManager.DUEL_ARENA_CONTROLLER)) {
						if (TargetSystem.getInstance().playerHasTarget(player)) {
							player.getClient().queueOutgoingPacket(new SendMessage("You can't duel whilst having an active wilderness target."));
							return;
						}
						player.face(other);
						player.getDueling().request(other);
					}
				}
			});
			break;
		case 39:
			final int tradeSlot = in.readShort(true, ByteOrder.LITTLE);

			if ((!World.isPlayerWithinRange(tradeSlot)) || (World.getPlayers()[tradeSlot] == null) || (tradeSlot == player.getIndex())) {
				return;
			}

			TaskQueue.queue(new FollowToEntityTask(player, World.getPlayers()[tradeSlot]) {
				@Override
				public void onDestination() {
					Player other = World.getPlayers()[tradeSlot];

					if (other == null) {
						stop();
						player.getMovementHandler().reset();
						return;
					}

					player.face(other);

					player.getTrade().request(other);
					stop();
				}
			});
			break;
		case 139:
			player.getMovementHandler().reset();
			playerSlot = in.readShort(true, ByteOrder.LITTLE);

			if ((!World.isPlayerWithinRange(playerSlot)) || (playerSlot == player.getIndex())) {
				player.send(new SendMessage((!World.isPlayerWithinRange(playerSlot)) + " " + (playerSlot == player.getIndex())));
				return;
			}

			other = World.getPlayers()[playerSlot];

			if (other == null) {
				return;
			}
			if (player.getDueling().getInteracting() != null) {
				if (player.getDueling().isScreen()) {
					return;
				}
			}
			player.getFollowing().setFollow(other);
			break;
		case 73:
			playerSlot = in.readShort(true, ByteOrder.LITTLE);
			player.getMovementHandler().reset();

			if ((playerSlot == player.getIndex()) || (!World.isPlayerWithinRange(playerSlot))) {
				return;
			}

			other = World.getPlayers()[playerSlot];

			if (other == null) {
				return;
			}

			if (player.getController().equals(ControllerManager.DUEL_ARENA_CONTROLLER)) {
				final Player o = other;
				TaskQueue.queue(new FollowToEntityTask(player, o) {
					@Override
					public void onDestination() {
						if (o == null) {
							player.getMovementHandler().reset();
							return;
						}

						if (player.getController().equals(ControllerManager.DUEL_ARENA_CONTROLLER)) {
							if (TargetSystem.getInstance().playerHasTarget(player)) {
								player.getClient().queueOutgoingPacket(new SendMessage("You can't duel whilst having an active wilderness target."));
								return;
							}
							player.face(o);
							player.getDueling().request(o);
						}
					}
				});
				return;
			}

			if (player.getController().canMove(player)) {
				player.getFollowing().setFollow(other, FollowType.COMBAT);
			}
			player.getCombat().setAttacking(other);

			player.getMagic().getSpellCasting().disableClickCast();

			break;
		case 249:
			playerSlot = in.readShort(true, ValueType.A);
			int magicId = in.readShort(true, ByteOrder.LITTLE);

			player.getMovementHandler().reset();

			if ((!World.isPlayerWithinRange(playerSlot)) || (World.getPlayers()[playerSlot] == null) || (playerSlot == player.getIndex())) {
				return;
			}

			other = World.getPlayers()[playerSlot];

			player.getMagic().getSpellCasting().castCombatSpell(magicId, other);
			break;
		case 14:
			int interfaceId = in.readShort(ValueType.A);
			playerSlot = in.readShort();
			int item = in.readShort();
			itemSlot = in.readShort(ByteOrder.LITTLE);
			
			if ((!World.isPlayerWithinRange(playerSlot)) || (playerSlot == player.getIndex())) {
				return;
			}
			
			break;
		}
	}
}
