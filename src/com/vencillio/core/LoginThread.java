package com.vencillio.core;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.content.io.PlayerSave;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.Client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoginThread extends Thread {
	
	private static final Queue<Player> login = new ConcurrentLinkedQueue<Player>();

	public static void cycle() {
		long start = System.currentTimeMillis();

		Player player = null;

		if ((player = login.poll()) != null) {
			System.out.println("Logging in: " + player.getUsername());

			boolean starter = false;
			boolean wasLoaded = false;
			try {
				starter = !PlayerSave.load(player);
				System.out.println("Starter: " + starter);
				wasLoaded = true;
			} catch (Exception e) {
				if (player != null) {
					StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(3);
					resp.writeByte(11);
					resp.writeByte(0);
					resp.writeByte(0);
					player.getClient().send(resp.getBuffer());
				}

				e.printStackTrace();
			}

			if (wasLoaded) {
				try {
					boolean login = player.login(starter);

					if (login) {
						player.getClient().setStage(Client.Stages.LOGGED_IN);
					}
				} catch (Exception e) {
					e.printStackTrace();
					player.logout(true);
				}
			}

		}

		long elapsed = System.currentTimeMillis() - start;

		if (elapsed < 700L) {
			try {
				Thread.sleep(700L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Login thread overflow: " + elapsed);
		}
	}

	public static void queueLogin(Player player) {
		login.add(player);
	}

	public LoginThread() {
		setName("Login Thread");

		setPriority(Thread.MAX_PRIORITY - 2);

		start();
	}

	@Override
	public void run() {
		while (!Thread.interrupted())
			cycle();
	}
}
