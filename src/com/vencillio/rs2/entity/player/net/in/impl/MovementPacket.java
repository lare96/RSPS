package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.content.minigames.weapongame.WeaponGame;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.pathfinding.RS317PathFinder;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class MovementPacket extends IncomingPacket {
	@Override
	public int getMaxDuplicates() {
		return 2;
	}

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		player.getBank().setSearching(false);
		if ((player.isDead()) || (player.getMagic().isTeleporting()) || (!player.getController().canMove(player)) || (PlayerConstants.isSettingAppearance(player))) {
			player.getCombat().reset();
			return;
		}
	
		if (WeaponGame.gamePlayers.contains(player) && !player.inWGGame()) {
			WeaponGame.leaveGame(player, false);
			return;
		}
		
		if (WeaponGame.lobbyPlayers.contains(player) && !player.inWGLobby()) {
			WeaponGame.leaveLobby(player, false);
			return;
		}

		if (player.isStunned()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You are stunned!"));
			player.getCombat().reset();
			return;
		}

		if (player.isFrozen()) {
			player.getClient().queueOutgoingPacket(new SendMessage("A magical force stops you from moving."));
			player.getCombat().reset();
			return;
		}
		
		if (player.isJailed() && !player.inJailed()) {
			player.teleport(new Location(PlayerConstants.JAILED_AREA));
			player.send(new SendMessage("You were jailed!"));
		}

		if (player.getInterfaceManager().main == 48500) {
			player.getPriceChecker().withdrawAll();
		}

		if (opcode == 248) {
			length -= 14;
		}

		if (opcode != 98) {
			player.getMovementHandler().setForced(false);

			player.getMagic().getSpellCasting().disableClickCast();
			player.getFollowing().reset();
			player.getCombat().reset();
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());

			if (player.getTrade().trading()) {
				player.getTrade().end(false);
			}

			if (player.getDueling().isStaking()) {
				player.getDueling().decline();
			}

			player.start(null);
			player.getShopping().reset();
			player.getInterfaceManager().reset();
			TaskQueue.onMovement(player);
			player.setEnterXInterfaceId(0);
		} else {
			player.getMovementHandler().setForced(true);
		}

		int steps = (length - 5) / 2;
		int[][] path = new int[steps][2];

		int firstStepX = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);

		for (int i = 0; i < steps; i++) {
			path[i][0] = in.readByte();
			path[i][1] = in.readByte();
		}

		int firstStepY = in.readShort(StreamBuffer.ByteOrder.LITTLE);
		in.readByte(StreamBuffer.ValueType.C);

		player.getMovementHandler().reset();

		for (int i = 0; i < steps; i++) {
			path[i][0] += firstStepX;
			path[i][1] += firstStepY;
		}

		if (steps > 0) {
			if ((Math.abs(path[(steps - 1)][0] - player.getLocation().getX()) > 21) || (Math.abs(path[(steps - 1)][1] - player.getLocation().getY()) > 21)) {
				player.getMovementHandler().reset();
			}

		} else if ((Math.abs(firstStepX - player.getLocation().getX()) > 21) || (Math.abs(firstStepY - player.getLocation().getY()) > 21)) {
			player.getMovementHandler().reset();
			return;
		}
		if (steps > 0)
			RS317PathFinder.findRoute(player, path[(steps - 1)][0], path[(steps - 1)][1], true, 16, 16);
		else {
			RS317PathFinder.findRoute(player, firstStepX, firstStepY, true, 16, 16);
		}

	}
}
