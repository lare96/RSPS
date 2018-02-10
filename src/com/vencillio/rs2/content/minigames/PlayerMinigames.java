package com.vencillio.rs2.content.minigames;

import java.util.Arrays;

import com.vencillio.rs2.content.minigames.godwars.GodWarsData.Allegiance;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class PlayerMinigames {

	private Player player;
	private short[] kc = new short[Allegiance.values().length];

	public PlayerMinigames(Player player) {
		this.player = player;
	}

	public short[] getGWKC() {
		return kc;
	}

	public void setGWKC(short[] kc) {
		this.kc = kc;
	}

	public void changeGWDKills(int delta, Allegiance allegiance) {
		kc[allegiance.ordinal()] += delta;
		updateGWKC(allegiance);
	}
	
	public int getGWDKills(Allegiance allegiance) {
		return kc[allegiance.ordinal()];
	}

	public void updateGWKC(Allegiance allegiance) {
		player.send(new SendString(String.valueOf(getGWDKills(allegiance)), 61756 + allegiance.ordinal()));
	}

	public void resetGWD() {
		Arrays.fill(kc, (short) 0);
		updateGWKC(Allegiance.ARMADYL);
		updateGWKC(Allegiance.BANDOS);
		updateGWKC(Allegiance.SARADOMIN);
		updateGWKC(Allegiance.ZAMORAK);
	}
}
