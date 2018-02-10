package com.vencillio.rs2.entity.player;

import com.vencillio.GameDataLoader;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

import java.util.TimerTask;

/**
 * Created by Tanner on 5/31/2017.
 */
public class PlayTime {
	private transient Player player;

	public PlayTime(Player player) {
		this.player = player;
	}
	public void startTimer() {

		GameDataLoader.time.schedule(new TimerTask() {
			int timer = 60;
			@Override
			public void run() {
				if (timer == 0) {
					player.setPlayPoints(player.getPlayPoints() + 1);
					timer = 60;
				}
				if (player.getPlayPoints() == 18000 && !player.veteran) {
					player.getBank().add(20763,1, true);
					player.getBank().add(20764,1, true);
					player.veteran = true;
					player.send(new SendMessage("Well done, you've been awarded the veteran rank for playing 300 hours ingame time."));
				}
				if (timer > 0) {
					timer--;
				}
			}
		}, 0L, 1000L);
	}
}
