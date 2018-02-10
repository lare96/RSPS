package com.vencillio.rs2.entity.player;

public class PlayerAnimations {
	
	
	private int standEmote = 808;
	private int standTurnEmote = 823;
	private int walkEmote = 819;
	private int turn180Emote = 820;
	private int turn90CWEmote = 821;
	private int turn90CCWEmote = 822;
	private int runEmote = 824;

	public int getRunEmote() {
		return runEmote;
	}

	public int getStandEmote() {
		return standEmote;
	}

	public int getStandTurnEmote() {
		return standTurnEmote;
	}

	public int getTurn180Emote() {
		return turn180Emote;
	}

	public int getTurn90CCWEmote() {
		return turn90CCWEmote;
	}

	public int getTurn90CWEmote() {
		return turn90CWEmote;
	}

	public int getWalkEmote() {
		return walkEmote;
	}

	public void set(int standEmote, int standTurnEmote, int walkEmote, int turn180Emote, int turn90cwEmote, int turn90ccwEmote, int runEmote) {
		this.standEmote = standEmote;
		this.standTurnEmote = standTurnEmote;
		this.walkEmote = walkEmote;
		this.turn180Emote = turn180Emote;
		turn90CWEmote = turn90cwEmote;
		turn90CCWEmote = turn90ccwEmote;
		this.runEmote = runEmote;
	}

	public void set(PlayerAnimations animation) {
		this.standEmote = animation.standEmote;
		this.standTurnEmote = animation.standTurnEmote;
		this.walkEmote = animation.walkEmote;
		this.turn180Emote = animation.turn180Emote;
		this.turn90CWEmote = animation.turn90CWEmote;
		this.turn90CCWEmote = animation.turn90CCWEmote;
		this.runEmote = animation.runEmote;
	}

	public PlayerAnimations copy() {
		PlayerAnimations anims = new PlayerAnimations();
		anims.set(standEmote, standTurnEmote, walkEmote, turn180Emote, turn90CWEmote, turn90CCWEmote, runEmote);
		return anims;
	}

	public void setRunEmote(int runEmote) {
		this.runEmote = runEmote;
	}

	public void setStandEmote(int standEmote) {
		this.standEmote = standEmote;
	}

	public void setStandTurnEmote(int standTurnEmote) {
		this.standTurnEmote = standTurnEmote;
	}

	public void setTurn180Emote(int turn180Emote) {
		this.turn180Emote = turn180Emote;
	}

	public void setTurn90CCWEmote(int turn90ccwEmote) {
		turn90CCWEmote = turn90ccwEmote;
	}

	public void setTurn90CWEmote(int turn90cwEmote) {
		turn90CWEmote = turn90cwEmote;
	}

	public void setWalkEmote(int walkEmote) {
		this.walkEmote = walkEmote;
	}
}
