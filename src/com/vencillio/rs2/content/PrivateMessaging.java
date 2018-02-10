package com.vencillio.rs2.content;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.vencillio.core.util.NameUtil;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendFriendUpdate;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendPMServer;
import com.vencillio.rs2.entity.player.net.out.impl.SendPrivateMessage;

public class PrivateMessaging {
	private final Player player;
	private List<String> friends = new LinkedList<String>();
	private List<String> ignores = new LinkedList<String>();
	private int messagesReceived = 0;

	public PrivateMessaging(Player player) {
		this.player = player;
	}

	public void addFriend(long id) {
		String name = NameUtil.longToName(id).toLowerCase().replaceAll("_", " ");

		friends.add(name);

		player.getClient().queueOutgoingPacket(new SendFriendUpdate(id, World.getPlayerByName(name) == null ? 0 : 1));
	}

	public void addFriend(String name) {
		name = name.toLowerCase();

		long id = NameUtil.nameToLong(name);
		friends.add(name);

		player.getClient().queueOutgoingPacket(new SendFriendUpdate(id, World.getPlayerByName(name) == null ? 0 : 1));
	}

	public void addIgnore(long id) {
		ignores.add(NameUtil.longToName(id).replaceAll("_", " "));
	}

	public void addIgnore(String name) {
		ignores.add(name);
	}

	public void connect() {
		player.getClient().queueOutgoingPacket(new SendPMServer(2));

		for (Iterator<String> i = friends.iterator(); i.hasNext();) {
			String name = i.next();
			player.getClient().queueOutgoingPacket(new SendFriendUpdate(NameUtil.nameToLong(name), World.getPlayerByName(name) == null ? 0 : 1));
		}

	}

	public List<String> getFriends() {
		return friends;
	}

	public List<String> getIgnores() {
		return ignores;
	}

	public int getNextMessageId() {
		messagesReceived += 1;
		return messagesReceived;
	}

	public boolean ignored(String n) {
		return ignores.contains(n.toLowerCase());
	}

	public void removeFriend(long id) {
		friends.remove(NameUtil.longToName(id).toLowerCase().replaceAll("_", " "));
	}

	public void removeIgnore(long id) {
		ignores.remove(NameUtil.longToName(id).replaceAll("_", " "));
	}

	public void sendPrivateMessage(long id, int size, byte[] text) {
		String name = NameUtil.longToName(id).replaceAll("_", " ");
		Player sentTo = World.getPlayerByName(name);

		if (sentTo != null) {
			if (sentTo.getPrivateMessaging().ignored(player.getUsername())) {
				return;
			}

			if (player.isMuted()) {
				if (player.getMuteLength() == -1) {
					player.send(new SendMessage("You are permanently muted on this account."));
					return;
				} else {
					long muteHours = TimeUnit.MILLISECONDS.toMinutes(player.getMuteLength() - System.currentTimeMillis());
					String timeUnit = "hour" + (muteHours > 1 ? "s" : "");
					if (muteHours < 60) {
						if (muteHours <= 0) {
							player.send(new SendMessage("Your mute has been lifted!"));
							player.setMuted(false);
						}
						timeUnit = "minute" + (muteHours > 1 ? "s" : "");
					} else {
						muteHours = TimeUnit.MINUTES.toHours(muteHours);
					}
					if (player.isMuted()) {
						player.send(new SendMessage("You are muted, you will be unmuted in " + muteHours + " " + timeUnit + "."));
						return;
					}
				}
			}
			if (name == sentTo.getUsername()) {
				player.send(new SendMessage("You may not send a message to yourself!"));
				return;
			}
			sentTo.getClient().queueOutgoingPacket(new SendPrivateMessage(NameUtil.nameToLong(player.getUsername()), player.getRights(), text, sentTo.getPrivateMessaging().getNextMessageId()));
		} else {
			player.getClient().queueOutgoingPacket(new SendMessage("Your private message could not be delivered."));
		}
	}

	public void updateOnlineStatus(Player connectedPlayer, boolean connected) {
		String name = connectedPlayer.getUsername().toLowerCase();

		if (friends.contains(name))
			player.getClient().queueOutgoingPacket(new SendFriendUpdate(NameUtil.nameToLong(name), connected ? 1 : 0));
	}
}
