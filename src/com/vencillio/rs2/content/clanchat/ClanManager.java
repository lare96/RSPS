package com.vencillio.rs2.content.clanchat;

import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

import java.io.*;
import java.util.LinkedList;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ClanManager {

	public LinkedList<Clan> clans;

	public ClanManager() {
		clans = new LinkedList();
	}

	public boolean clanExists(String paramString) {
		File localFile = new File("/home/server/Tannerscape/data/clan/" + paramString.toLowerCase() + ".cla");
		return localFile.exists();
	}

	public void create(Player paramClient) {
		Clan localClan = new Clan(paramClient);
		this.clans.add(localClan);
		localClan.addMember(paramClient);
		localClan.save();
		paramClient.setClanData();
		paramClient.getClient().queueOutgoingPacket(new SendMessage("<col=FF0000>You may change your clan settings by clicking the 'Clan Setup' button."));
	}

	public void delete(Clan paramClan) {
		if (paramClan == null) {
			return;
		}
		File localFile = new File("/home/server/Tannerscape/data/clan/" + paramClan.getFounder().toLowerCase() + ".cla");
		if (localFile.delete()) {
			Player localClient = World.getPlayerByName(paramClan.getFounder());
			if (localClient != null) {
				localClient.getClient().queueOutgoingPacket(new SendMessage("Your clan has been deleted."));
			}
			this.clans.remove(paramClan);
		}
	}

	public int getActiveClans() {
		return this.clans.size();
	}

	public Clan getClan(String paramString) {
		for (int i = 0; i < this.clans.size(); i++) {
			if (this.clans.get(i).getFounder().equalsIgnoreCase(paramString)) {
				return this.clans.get(i);
			}

		}

		Clan localClan = read(paramString);
		if (localClan != null) {
			this.clans.add(localClan);
			return localClan;
		}
		return null;
	}

	public LinkedList<Clan> getClans() {
		return this.clans;
	}

	public int getTotalClans() {
		File localFile = new File("/home/server/Tannerscape/data/clan/");
		return localFile.listFiles().length;
	}

	private Clan read(String paramString) {
		File localFile = new File("/home/server/Tannerscape/data/clan/" + paramString.toLowerCase() + ".cla");
		if (!localFile.exists()) {
			return null;
		}
		try {
			RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rwd");
			FileReader fr = new FileReader(localFile);
			int ch;
			while ((ch=fr.read())!=-1)
				System.out.print((char)ch);

			Clan localClan = new Clan(localRandomAccessFile.readUTF(), paramString);

			localClan.whoCanJoin = localRandomAccessFile.readByte();
			System.out.println("Founder: " + localClan.getFounder() + " Who can join: " + localClan.whoCanJoin);
			localClan.whoCanTalk = localRandomAccessFile.readByte();
			localClan.whoCanKick = localRandomAccessFile.readByte();
			localClan.whoCanBan = localRandomAccessFile.readByte();
			int i = localRandomAccessFile.readShort();
			if (i != 0) {
				for (int j = 0; j < i; j++) {
					localClan.rankedMembers.add(localRandomAccessFile.readUTF());
					localClan.ranks.add((int) localRandomAccessFile.readShort());
				}
			}
			localRandomAccessFile.close();

			return localClan;
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		return null;
	}

	public void save(Clan paramClan) {
		//System.out.println("paramClan: " + paramClan);
		if (paramClan == null) {
			System.out.println("paramClan was null");
			return;
		}
		//System.out.println("paramClan founder: " + paramClan.getFounder());
		File localFile = new File("/home/server/Tannerscape/data/clan/" + paramClan.getFounder().toLowerCase() + ".cla");
		try {
			RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rwd");

			localRandomAccessFile.writeUTF(paramClan.getTitle());
			//System.out.println("paramClan.getTitle(): " + paramClan.getTitle());
			localRandomAccessFile.writeByte(paramClan.whoCanJoin);
			localRandomAccessFile.writeByte(paramClan.whoCanTalk);
			localRandomAccessFile.writeByte(paramClan.whoCanKick);
			localRandomAccessFile.writeByte(paramClan.whoCanBan);
			if ((paramClan.rankedMembers != null) && (paramClan.rankedMembers.size() > 0)) {
				localRandomAccessFile.writeShort(paramClan.rankedMembers.size());
				for (int i = 0; i < paramClan.rankedMembers.size(); i++) {
					localRandomAccessFile.writeUTF(paramClan.rankedMembers.get(i));
					localRandomAccessFile.writeShort(paramClan.ranks.get(i));
				}
			} else {
				localRandomAccessFile.writeShort(0);
			}

			localRandomAccessFile.close();
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
	}
}