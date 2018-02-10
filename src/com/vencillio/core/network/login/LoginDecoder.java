package com.vencillio.core.network.login;

import java.security.SecureRandom;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.vencillio.VencillioConstants;
import com.vencillio.core.network.ISAACCipher;
import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.net.Client;

/**
 * 
 * @author Stuart Murphy
 * 
 */
public class LoginDecoder extends FrameDecoder {

	/**
	 * Client version
	 */
	public static final int CLIENT_VERSION = 10;

	/**
	 * Connected login state
	 */
	private static final int CONNECTED = 0;
	/**
	 * Logging in login state
	 */
	private static final int LOGGING_IN = 1;

	/**
	 * Login
	 * @param channel
	 * @param inCipher
	 * @param outCipher
	 * @param version
	 * @param name
	 * @param pass
	 * @param uid
	 * @return
	 */
	private static Client login(Channel channel, ISAACCipher inCipher, ISAACCipher outCipher, int version, String name, String pass, String uid) {

		// Validate name
		if (!name.matches("[A-Za-z0-9 ]+") || name.length() > 12 || name.length() <= 0) {
			sendReturnCode(channel, Utility.LOGIN_RESPONSE_INVALID_CREDENTIALS);
			return null;
		}
		
		if (VencillioConstants.STAFF_ONLY) {
			boolean isStaff = false;
			for (String usernames : VencillioConstants.STAFF_MEMBERS) {
				if (name.equalsIgnoreCase(usernames)) {
					isStaff = true;
				}
			}
			if (!isStaff) {
				sendReturnCode(channel, Utility.LOGIN_RESPONSE_INVALID_USERNAME);
				return null;
			}
		}
		
		for (String retard : VencillioConstants.BAD_USERNAMES) {
			if (name.toLowerCase().contains(retard.toLowerCase())) {
				sendReturnCode(channel, Utility.LOGIN_RESPONSE_INVALID_USERNAME);
				return null;
			}
		}
		
		if (World.worldUpdating) {
			sendReturnCode(channel, Utility.LOGIN_RESPONSE_SERVER_BEING_UPDATED);
			return null;
		}

		name = name.trim();

		channel.getPipeline().remove("decoder");
		channel.getPipeline().addFirst("decoder", new Decoder(inCipher));

		Client client = new Client(channel);
		
		client.getPlayer().setUid(uid);
		client.getPlayer().setUsername(name);
		client.getPlayer().setDisplay(name);
		client.getPlayer().setPassword(pass);
		client.setEnteredPassword(pass);
		client.setEncryptor(outCipher);
		return client;
	}

	/**
	 * Send return code
	 * @param channel
	 * @param code
	 */
	public static void sendReturnCode(Channel channel, int code) {
		// Write the response.
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(1);
		out.writeByte(code); // First 8 bytes are ignored by the client.

		channel.write(out.getBuffer()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture arg0) throws Exception {
				arg0.getChannel().close();
			}
		});
	}

	/**
	 * Current login state
	 */
	private int state = CONNECTED;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer in) throws Exception {
		if (!channel.isConnected()) {
			return null;
		}
		switch (state) {
		case CONNECTED:
			if (in.readableBytes() < 2) {
				return null;
			}

			// Validate the request
			int request = in.readUnsignedByte();

			if (request == 5) {
				sendReturnCode(channel, Utility.LOGIN_RESPONSE_COULD_NOT_COMPLETE_LOGIN);// system
																						// ban
				return null;
			}

			if (request != 14) {
				System.out.println("Invalid login request: " + request);
				channel.close();
				return null;
			}

			// Name hash
			in.readUnsignedByte();

			// Write the response.
			StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(17);
			out.writeLong(0); // First 8 bytes are ignored by the client.
			out.writeByte(0); // The response opcode, 0 for logging in.
			out.writeLong((new SecureRandom().nextLong() / 2) + (new SecureRandom().nextLong() / 2)); // SSK.
			channel.write(out.getBuffer());

			state = LOGGING_IN;
			break;
		case LOGGING_IN:
			if (in.readableBytes() < 2) {
				return null;
			}

			// Validate the login type
			int loginType = in.readUnsignedByte();
			if (loginType != 16 && loginType != 18) {
				System.out.println("Invalid login type: " + loginType);
				channel.close();
				return null;
			}

			// Make sure we have the complete login block
			int blockLength = in.readUnsignedByte();
			int loginEncryptSize = blockLength - (36 + 1 + 1 + 2);
			if (loginEncryptSize <= 0) {
				System.out.println("Encrypted packet size zero or negative: " + loginEncryptSize);
				channel.close();
				return null;
			}

			in.readUnsignedByte(); // Magic id

			// Validate the client version
			int clientVersion = in.readUnsignedShort();
			int currentVersion = 217 + CLIENT_VERSION;
			if (clientVersion != currentVersion) {
				System.out.println("Invalid client version, Received: " + clientVersion + " Expected: " + currentVersion);
				StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(3);
				resp.writeByte(Utility.LOGIN_RESPONSE_UPDATED);
				resp.writeByte(0);
				resp.writeByte(0);
				channel.write(resp.getBuffer());
				channel.close();
				return null;
			}

			// High/low memory
			in.readByte();

			// Skip the CRC keys.
			for (int i = 0; i < 9; i++) {
				in.readInt();
			}

			// Skip RSA block length.
			in.readByte();

			// Validate that the RSA block was decoded properly.
			int rsaOpcode = in.readByte();
			if (rsaOpcode != 100) {
				System.err.println("Unable to decode RSA block properly!");
				channel.close();
				return null;
			}

			// Set up the ISAAC ciphers.
			long clientHalf = in.readLong();
			long serverHalf = in.readLong();
			int[] isaacSeed = { (int) (clientHalf >> 32), (int) clientHalf, (int) (serverHalf >> 32), (int) serverHalf };
			ISAACCipher inCipher = new ISAACCipher(isaacSeed);
			for (int i = 0; i < isaacSeed.length; i++) {
				isaacSeed[i] += 50;
			}
			ISAACCipher outCipher = new ISAACCipher(isaacSeed);

			int version = in.readInt();
			
			String uid = Utility.getRS2String(in);

			String username = Utility.getRS2String(in).trim();
			String password = Utility.getRS2String(in);

			return login(channel, inCipher, outCipher, version, username, password, uid);
		}
		return null;
	}
}
