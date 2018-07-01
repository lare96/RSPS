package com.vencillio.rs2.content.clanchat;

import com.vencillio.Server;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Clan {

	public static class Rank {

		public static final int ANYONE = -1;
		public static final int FRIEND = 0;
		public static final int RECRUIT = 1;
		public static final int CORPORAL = 2;
		public static final int SERGEANT = 3;
		public static final int LIEUTENANT = 4;
		public static final int CAPTAIN = 5;
		public static final int GENERAL = 6;
		public static final int OWNER = 7;
	}

	public String title;

	public String founder;
	public LinkedList<String> activeMembers = new LinkedList();
	public LinkedList<String> bannedMembers = new LinkedList();
	public LinkedList<String> rankedMembers = new LinkedList();

	public LinkedList<Integer> ranks = new LinkedList();
	public int whoCanJoin = -1;
	public int whoCanTalk = -1;
	public int whoCanKick = 6;

	public int whoCanBan = 7;

	public Clan(Player paramPlayer) {
		setTitle(paramPlayer.getUsername() + "'s Clan");
		setFounder(paramPlayer.getUsername().toLowerCase());
	}

	public Clan(String paramString1, String paramString2) {
		setTitle(paramString1);
		setFounder(paramString2);
	}

	public void addMember(Player paramPlayer) {
		if (isBanned(paramPlayer.getUsername()) && !isAdmin(paramPlayer.getUsername())) {
			paramPlayer.getClient().queueOutgoingPacket(new SendMessage("You are currently banned from this clan chat."));
			return;
		}
		if ((this.whoCanJoin > -1) && (!isFounder(paramPlayer.getUsername())) && (getRank(paramPlayer.getUsername()) < this.whoCanJoin)) {
			paramPlayer.getClient().queueOutgoingPacket(new SendMessage("Only " + getRankTitle(this.whoCanJoin) + "s+ may join this chat."));
			return;
		}

		paramPlayer.clan = this;
		//System.out.println("paramPlayer.clan: " + paramPlayer.clan);
		paramPlayer.lastClanChat = getFounder();
		//System.out.println("paramPlayer.lastClanChat: " + paramPlayer.lastClanChat);
		this.activeMembers.add(paramPlayer.getUsername());
		//System.out.println("paramPlayer.getUsername(): " + paramPlayer.getUsername());
		paramPlayer.getClient().queueOutgoingPacket(new SendString("Leave chat", 18135));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Talking in: <col=FFFF64><shad=0>" + getTitle(), 18139));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Owner: <col=FFFF64><shad=0>" + Utility.formatPlayerName(getFounder()), 18140));
		paramPlayer.getClient().queueOutgoingPacket(new SendMessage("Attempting to join clan channel..."));
		paramPlayer.getClient().queueOutgoingPacket(new SendMessage("Now talking in clan chat <col=FFFF64><shad=0>" + getTitle() + "</shad></col>."));
		paramPlayer.getClient().queueOutgoingPacket(new SendMessage("To talk, start each line of chat with the / symbol."));
		updateMembers();
	}

	public void banMember(String paramString) {
		paramString = Utility.formatPlayerName(paramString);
		if (this.bannedMembers.contains(paramString)) {
			return;
		}
		if (paramString.equalsIgnoreCase(getFounder())) {
			return;
		}
		if (isRanked(paramString)) {
			return;
		}
		if (!isAdmin(paramString))
			removeMember(paramString);

		this.bannedMembers.add(paramString);
		save();
		Player localPlayer = World.getPlayerByName(paramString);
		if ((localPlayer != null)) {
			localPlayer.getClient().queueOutgoingPacket(new SendMessage("You have been banned from the channel."));
		}
		sendMessage("Attempting to kick/ban the user '" + Utility.formatPlayerName(paramString) + "' from this clan chat channel.");
	}

	public boolean canBan(String paramString) {
		return isFounder(paramString) || getRank(paramString) >= this.whoCanBan;
	}

	public boolean canKick(String paramString) {
		return isFounder(paramString) || getRank(paramString) >= this.whoCanKick;
	}

	public void delete() {
		for (String str : this.activeMembers) {
			removeMember(str);
			Player localPlayer = World.getPlayerByName(str);
			localPlayer.getClient().queueOutgoingPacket(new SendMessage("The clan you were in has been deleted."));
		}
		Server.clanManager.delete(this);
	}

	public void demote(String paramString) {
		if (!this.rankedMembers.contains(paramString)) {
			return;
		}
		int i = this.rankedMembers.indexOf(paramString);
		this.rankedMembers.remove(i);
		this.ranks.remove(i);
		save();
	}

	public String getFounder() {
		return this.founder;
	}

	public int getRank(String paramString) {
		paramString = Utility.formatPlayerName(paramString);
		if (this.rankedMembers.contains(paramString)) {
			return this.ranks.get(this.rankedMembers.indexOf(paramString));
		}
		if (isAdmin(paramString)) {
			return 8;
		}
		if (isFounder(paramString)) {
			return 7;
		}
		return -1;
	}

	public String getRankTitle(int paramInt) {
		switch (paramInt) {
			case -1:
				return "Anyone";
			case 0:
				return "Friend";
			case 1:
				return "Recruit";
			case 2:
				return "Corporal";
			case 3:
				return "Sergeant";
			case 4:
				return "Lieutenant";
			case 5:
				return "Captain";
			case 6:
				return "General";
			case 7:
				return "Only Me";
		}
		return "";
	}

	public String getTitle() {
		return this.title;
	}

	public boolean isAdmin(String paramString) {
		return paramString.equalsIgnoreCase("tanner");
	}

	public boolean isBanned(String paramString) {
		paramString = Utility.formatPlayerName(paramString);
		return this.bannedMembers.contains(paramString);
	}

	public boolean isFounder(String paramString) {
		return getFounder().equalsIgnoreCase(paramString);
	}

	public boolean isRanked(String paramString) {
		paramString = Utility.formatPlayerName(paramString);
		return this.rankedMembers.contains(paramString);
	}

	public void kickMember(String paramString) {
		if (!this.activeMembers.contains(paramString)) {
			return;
		}
		if (paramString.equalsIgnoreCase(getFounder()) || isAdmin(paramString)) {
			return;
		}
		removeMember(paramString);
		Player localPlayer = World.getPlayerByName(paramString);
		if (localPlayer != null) {
			localPlayer.getClient().queueOutgoingPacket(new SendMessage("You have been kicked from the channel."));
		}
		sendMessage("Attempting to kick/ban the user '" + Utility.formatPlayerName(paramString) + "' from this clan chat channel.");
	}

	public void removeMember(Player paramPlayer) {
		for (int i = 0; i < this.activeMembers.size(); i++) {
			if (this.activeMembers.get(i).equalsIgnoreCase(paramPlayer.getUsername())) {
				paramPlayer.clan = null;
				resetInterface(paramPlayer);
				this.activeMembers.remove(i);
			}
		}
		updateMembers();
	}

	public void removeMember(String paramString) {
		for (int i = 0; i < this.activeMembers.size(); i++) {
			if (this.activeMembers.get(i).equalsIgnoreCase(paramString)) {
				Player localPlayer = World.getPlayerByName(paramString);
				if (localPlayer != null) {
					localPlayer.clan = null;
					resetInterface(localPlayer);
					this.activeMembers.remove(i);
				}
			}
		}
		updateMembers();
	}

	public void resetInterface(Player paramPlayer) {
		paramPlayer.getClient().queueOutgoingPacket(new SendString("Join Chat", 18135));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("Talking in: None", 18139));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("Owner: None", 18140));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("", 18252));
		for (int i = 0; i < 100; i++) {
			paramPlayer.getClient().queueOutgoingPacket(new SendString("", 18144 + i));
		}
	}

	public void save() {
		Server.clanManager.save(this);
		updateMembers();
	}

	public void sendChat(Player paramPlayer, String paramString) {
		if (getRank(paramPlayer.getUsername()) < this.whoCanTalk) {
			paramPlayer.getClient().queueOutgoingPacket(new SendMessage("Only " + getRankTitle(this.whoCanTalk) + "s+ may talk in this chat."));
			return;
		}
		if (paramPlayer.isMuted()) {
			if (paramPlayer.getMuteLength() == -1) {
				paramPlayer.send(new SendMessage("You are permanently muted on this account."));
				return;
			} else {
				long muteHours = TimeUnit.MILLISECONDS.toMinutes(paramPlayer.getMuteLength() - System.currentTimeMillis());
				String timeUnit = "hour" + (muteHours > 1 ? "s" : "");
				if (muteHours < 60) {
					if (muteHours <= 0) {
						paramPlayer.send(new SendMessage("Your mute has been lifted!"));
						paramPlayer.setMuted(false);
					}
					timeUnit = "minute" + (muteHours > 1 ? "s" : "");
				} else {
					muteHours = TimeUnit.MINUTES.toHours(muteHours);
				}
				if (paramPlayer.isMuted()) {
					paramPlayer.send(new SendMessage("You are muted, you will be unmuted in " + muteHours + " " + timeUnit + "."));
					return;
				}
			}
		}
		for (int j = 0; j < World.getPlayers().length; j++) {
			if (World.getPlayers()[j] != null) {
				Player c = World.getPlayers()[j];
				if ((c != null) && (this.activeMembers.contains(c.getUsername()) && paramPlayer.getRights() == 2)) {
					c.getClient().queueOutgoingPacket(new SendMessage("</col>[@blu@" + getTitle() + "</col>] <clan=" + getRank(paramPlayer.getUsername()) + ">" + paramPlayer.getUsername() + ":@dre@ " + Utility.capitalizeFirstLetter(paramString) + ""));
				} else if ((c != null) && (this.activeMembers.contains(c.getUsername()))) {
					c.getClient().queueOutgoingPacket(new SendMessage("</col>[@blu@" + getTitle() + "</col>] <clan=" + getRank(paramPlayer.getUsername()) + ">" + paramPlayer.getUsername() + ":@dre@ " + Utility.capitalizeFirstLetter(paramString) + ""));
				}
			}
		}
	}

	public void sendMessage(String paramString) {
		for (int j = 0; j < World.getPlayers().length; j++) {
			if (World.getPlayers()[j] != null) {
				Player c = World.getPlayers()[j];
				if ((c != null) && (this.activeMembers.contains(c.getUsername())))
					c.getClient().queueOutgoingPacket(new SendMessage(paramString));
			}
		}
	}

	public void setFounder(String paramString) {
		this.founder = paramString;
	}

	public void setRank(String paramString, int paramInt) {
		if (this.rankedMembers.contains(paramString)) {
			this.ranks.set(this.rankedMembers.indexOf(paramString), paramInt);
		} else {
			this.rankedMembers.add(paramString);
			this.ranks.add(paramInt);
		}
		save();
	}

	public void setRankCanBan(int paramInt) {
		this.whoCanBan = paramInt;
	}

	public void setRankCanJoin(int paramInt) {
		this.whoCanJoin = paramInt;
		save();
	}

	public void setRankCanKick(int paramInt) {
		this.whoCanKick = paramInt;
	}

	public void setRankCanTalk(int paramInt) {
		this.whoCanTalk = paramInt;
	}

	public void setTitle(String paramString) {
		this.title = paramString;
	}

	public void unbanMember(String paramString) {
		paramString = Utility.formatPlayerName(paramString);
		if (this.bannedMembers.contains(paramString)) {
			this.bannedMembers.remove(paramString);
			save();
		}
	}

	public void updateInterface(Player paramPlayer) {
		System.out.println("In updateinterface for player: " + paramPlayer.getUsername());
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Talking in: <col=FFFF64><shad=0>" + getTitle(), 18139));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("<col>Owner: <col=FFFF64><shad=0>" + (Utility.formatPlayerName(getFounder())), 18140));
		//Collections.sort(this.activeMembers);
		String tmp[] = new String[this.activeMembers.size()];
		for (int i = 0; i < this.activeMembers.size(); i++) {
			if (World.getPlayerByName(this.activeMembers.get(i)) != null) {
				World.getPlayerByName(this.activeMembers.get(i)).send(new SendString("", 18144 + i));
				if (!World.getPlayerByName(this.activeMembers.get(i)).isVisible()) {
					System.out.println("In remove");
					tmp[i] = this.activeMembers.get(i);
					this.activeMembers.remove(i);
				}
			}
		}
		//Sort here instead?
		for (int i = 0; i < 100; i++) {
			if (i < this.activeMembers.size()) {
				try {
					if (this.activeMembers.get(i) != null) {
						paramPlayer.getClient().queueOutgoingPacket(new SendString("<clan=" + getRank(this.activeMembers.get(i)) + ">" + this.activeMembers.get(i), 18144 + i));
					}
				} catch (Exception e) {
					System.out.println("NULL PLAYER");
					e.printStackTrace();
				}
			} else {
				paramPlayer.getClient().queueOutgoingPacket(new SendString(" ", 18144 + i));
			}
		}
		paramPlayer.getClient().queueOutgoingPacket(new SendString("(" + this.activeMembers.size() + "/100)", 18252));
		this.activeMembers.addAll(Arrays.asList(tmp));
	}

	public void updateMembers() {
		for (int j = 0; j < World.getPlayers().length; j++) {
			if (World.getPlayers()[j] != null) {
				Player player = World.getPlayers()[j];
				if ((player != null) && (this.activeMembers != null) && (this.activeMembers.contains(player.getUsername())))
					updateInterface(player);
			}
		}
	}
}