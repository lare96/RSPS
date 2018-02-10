package com.vencillio.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;

public class NetworkThread extends Thread {

	public static class PacketLog {
		public final String username;
		public final String packet;

		public PacketLog(String username, String packet) {
			this.username = username;
			this.packet = packet;
		}
	}

	public static NetworkThread singleton;

	private static Queue<PacketLog> packetLog = new ConcurrentLinkedQueue<PacketLog>();
	public static int cycles = 0;

	public static final String PACKET_LOG_DIR = "./data/logs/packets/";

	public static void createLog(String username, IncomingPacket packet, int opcode) {
		packetLog.add(new PacketLog(username, packet.getClass().getSimpleName() + " : " + opcode));
	}

	public static void cycle() {
		long start = System.nanoTime();

		GameObject r;
		while ((r = ObjectManager.getSend().poll()) != null) {
			try {
				ObjectManager.send(r);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (Player k : World.getPlayers()) {
			try {
				if ((k != null) && (k.isActive())) {
					try {
						if (k.getPlayerShop().hasSearch()) {
							k.getPlayerShop().doSearch();
							k.getPlayerShop().resetSearch();
						}

						k.getGroundItems().process();
						k.getObjects().process();
						k.getClient().processOutgoingPackets();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		long elapsed = (System.nanoTime() - start) / 1000000;

		if (elapsed < 200) {
			try {
				Thread.sleep(200 - elapsed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("network thread overflow: " + elapsed);
		}
	}

	public static NetworkThread getSingleton() {
		return singleton;
	}

	public NetworkThread() {
		singleton = this;

		setName("Network Thread");

		setPriority(Thread.MAX_PRIORITY - 1);

		start();
	}

	@Override
	public void run() {
		while (!Thread.interrupted())
			cycle();
	}
}
