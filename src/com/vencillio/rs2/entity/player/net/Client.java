package com.vencillio.rs2.entity.player.net;

/*
 * This file is part of RuneSource.
 *
 * RuneSource is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RuneSource is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RuneSource.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import com.vencillio.core.network.ISAACCipher;
import com.vencillio.core.network.ReceivedPacket;
import com.vencillio.core.util.Utility;
import com.vencillio.core.util.Utility.Stopwatch;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.PacketHandler;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

/**
 * The class behind a Player that handles all networking-related things.
 * 
 * @author blakeman8192
 */
public class Client {

	public enum Stages {
		CONNECTED, LOGGING_IN, LOGGED_IN, LOGGED_OUT
	}

	private Channel channel;

	private final List<Mob> mobs = new LinkedList<Mob>();
	/**
	 * Incoming packets
	 */
	private Queue<ReceivedPacket> incomingPackets = new ConcurrentLinkedQueue<ReceivedPacket>();

	/**
	 * Outgoing Packets
	 */
	private Queue<OutgoingPacket> outgoingPackets = new ConcurrentLinkedQueue<OutgoingPacket>();
	private final Utility.Stopwatch timeoutStopwatch = new Utility.Stopwatch();
	private Stages stage = Stages.LOGGING_IN;
	private ISAACCipher encryptor;
	private ISAACCipher decryptor;
	private Player player;
	private PacketHandler packetHandler;
	private String host;

	private long hostId = 0;

	private boolean logPlayer = false;

	private String enteredPassword = null;

	private String lastPlayerOption = "";

	private long lastPacketTime = World.getCycles();

	/**
	 * Creates a new Client.
	 */
	public Client(Channel channel) {
		try {
			this.channel = channel;

			// set host
			if (channel != null) {
				host = channel.getRemoteAddress().toString();
				host = host.substring(1, host.indexOf(":"));

				hostId = Utility.nameToLong(host);
			} else {
				host = "none";
				hostId = -1;
			}
			player = new Player(this);
			packetHandler = new PacketHandler(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Disconnects the client.
	 */
	public void disconnect() {
		if (outgoingPackets != null) {
			synchronized (outgoingPackets) {
				outgoingPackets = null;
			}
		}
	}

	/**
	 * Gets the decryptor.
	 * 
	 * @return the decryptor
	 */
	public synchronized ISAACCipher getDecryptor() {
		return decryptor;
	}

	/**
	 * Gets the encryptor.
	 * 
	 * @return the encryptor
	 */
	public synchronized ISAACCipher getEncryptor() {
		return encryptor;
	}

	public String getEnteredPassword() {
		return enteredPassword;
	}

	/**
	 * Gets the remote host of the client.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	public long getHostId() {
		return hostId;
	}

	public long getLastPacketTime() {
		return lastPacketTime;
	}

	public String getLastPlayerOption() {
		return lastPlayerOption;
	}

	public List<Mob> getNpcs() {
		return mobs;
	}

	/**
	 * @return the outgoing packets.
	 */
	public Queue<OutgoingPacket> getOutgoingPackets() {
		return outgoingPackets;
	}

	/**
	 * Gets the Player subclass implementation of this superclass.
	 * 
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	public Stages getStage() {
		return stage;
	}

	public Stopwatch getTimeoutStopwatch() {
		return timeoutStopwatch;
	}

	public boolean isLogPlayer() {
		return logPlayer;
	}

	/**
	 * Handles packets we have received
	 */
	public void processIncomingPackets() {
		ReceivedPacket p = null;

		try {
			if (outgoingPackets == null) {
				return;
			}

			/**
			 * Synchronize to the queue so we don't corrupt data
			 */
			synchronized (incomingPackets) {
				if (outgoingPackets == null) {
					return;
				}

				while ((p = incomingPackets.poll()) != null) {
					packetHandler.handlePacket(p);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

			/**
			 * Safely disconnect this player
			 */
			player.logout(true);
			return;
		}
	}

	/**
	 * Handles packets we are sending
	 */
	public void processOutgoingPackets() {
		if (channel == null || outgoingPackets == null) {
			return;
		}

		try {
			/**
			 * Synchronize to the channel to wait for modifications to complete
			 */
			synchronized (channel) {
				if (channel == null) {
					return;
				}

				/**
				 * Synchronize to the queue so we won't corrupt data
				 */
				synchronized (outgoingPackets) {
					if (outgoingPackets == null) {
						return;
					}
					/**
					 * Then process all the outgoing packets
					 */
					OutgoingPacket p = null;
					while ((p = outgoingPackets.poll()) != null) {
						p.execute(this);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a packet to the queue
	 * 
	 * @param packet
	 */
	public void queueIncomingPacket(ReceivedPacket packet) {
		resetLastPacketReceived();

		synchronized (incomingPackets) {
			incomingPackets.offer(packet);
		}
	}

	/**
	 * Adds a packet to the outgoing queue
	 * 
	 * @param o
	 *            the OutGoingPacket object
	 */
	public void queueOutgoingPacket(OutgoingPacket o) {
		if (outgoingPackets == null) {
			return;
		}
		
//		if (!(o instanceof SendPlayerUpdate) && !(o instanceof SendNPCUpdate)) {
//			System.err.println("Sent packet: " + o.getClass().getSimpleName());
//		}

		synchronized (outgoingPackets) {
			if (outgoingPackets == null) {
				return;
			}

			outgoingPackets.offer(o);
		}
	}

	/**
	 * Resets the packet handler
	 */
	public void reset() {
		packetHandler.reset();
	}

	public void resetLastPacketReceived() {
		lastPacketTime = World.getCycles();
	}

	/**
	 * Sends the buffer to the socket.
	 * 
	 * @param buffer
	 *            the buffer
	 */
	public void send(ChannelBuffer buffer) {
		try {

			if (channel == null || !channel.isConnected()) {
				return;
			}

			/**
			 * Synchronize to the outgoing packets just for added protection
			 * against sending packets at the exact same time
			 */
			synchronized (outgoingPackets) {
				channel.write(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the decryptor.
	 * 
	 * @param decryptor
	 *            the decryptor.
	 */
	public void setDecryptor(ISAACCipher decryptor) {
		this.decryptor = decryptor;
	}

	/**
	 * Sets the encryptor.
	 * 
	 * @param encryptor
	 *            the encryptor
	 */
	public void setEncryptor(ISAACCipher encryptor) {
		this.encryptor = encryptor;
	}

	public void setEnteredPassword(String enteredPassword) {
		this.enteredPassword = enteredPassword;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setLastPlayerOption(String lastPlayerOption) {
		this.lastPlayerOption = lastPlayerOption;
	}

	public void setLogPlayer(boolean logPlayer) {
		this.logPlayer = logPlayer;
	}

	public void setStage(Stages stage) {
		this.stage = stage;
	}
	
	private Map<Integer, TinterfaceText> interfaceText = new HashMap<Integer, TinterfaceText>();
	
	public class TinterfaceText {
		public int id;
		public String currentState;
		
		public TinterfaceText(String s, int id) {
			this.currentState = s;
			this.id = id;
		}
	}

	public boolean checkSendString(String text, int id) {
		if(!interfaceText.containsKey(id)) {
			interfaceText.put(id, new TinterfaceText(text, id));
		} else {
			TinterfaceText t = interfaceText.get(id);
			if(text.equals(t.currentState)) {
				//return false;
			}
			t.currentState = text;
		}
		return true;
	}
}
