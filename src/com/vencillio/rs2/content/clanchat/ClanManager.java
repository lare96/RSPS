package com.vencillio.rs2.content.clanchat;

import com.vencillio.rs2.content.io.PlayerSave;
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
		File localFile = new File("/home/server/Tannerscape/data/clan/" + paramString.toLowerCase() + ".json");
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
			System.out.println(this.clans.get(i).getFounder());
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
		File localFile = new File("/home/server/Tannerscape/data/clan/" + paramString.toLowerCase() + ".json");
		if (!localFile.exists()) {
			return null;
		}

		try {
			/*RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rwd");
			Clan localClan = new Clan(localRandomAccessFile.readUTF(), paramString);*/
			BufferedReader reader = new BufferedReader(new FileReader(localFile));
			Clan details = PlayerSave.GSON.fromJson(reader, Clan.class);
			Player player = World.getPlayerByName(paramString);

			if(details.whoCanJoin != -1)
				player.getClan().whoCanJoin = details.whoCanJoin;

			if(details.whoCanTalk != -1)
				player.getClan().whoCanTalk = details.whoCanTalk;

			if(details.whoCanKick != 6)
				player.getClan().whoCanKick = details.whoCanKick;

			if(details.whoCanBan != 7)
				player.getClan().whoCanBan = details.whoCanBan;
			System.out.println("Founder: " + details.getFounder() + " Who can join: " + details.whoCanJoin);

			int rankedAmount = reader.read();
			if(rankedAmount != 0) {
				for (int i = 0; i < rankedAmount; i++) {
					details.rankedMembers.add(reader.readLine());
					details.ranks.add(reader.read());
				}
			}
			/*localClan.whoCanJoin = localRandomAccessFile.readByte();
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
			localRandomAccessFile.close();*/

			reader.close();
			return details;
			//return localClan;
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}

		return null;
	}

	public void save(Clan paramClan) {
		System.out.println("paramClan: " + paramClan);
		if (paramClan == null) {
			System.out.println("paramClan was null");
			return;
		}
		//System.out.println("paramClan founder: " + paramClan.getFounder());
		File localFile = new File("/home/server/Tannerscape/data/clan/" + paramClan.getFounder().toLowerCase() + ".json");

		try {
			//RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rwd");
			BufferedWriter writer = new BufferedWriter(new FileWriter(localFile, false));

			writer.write(PlayerSave.GSON.toJson(paramClan.getTitle()));
			writer.newLine();
			writer.write(PlayerSave.GSON.toJson(paramClan.whoCanJoin));
			writer.newLine();
			writer.write(PlayerSave.GSON.toJson(paramClan.whoCanTalk));
			writer.newLine();
			writer.write(PlayerSave.GSON.toJson(paramClan.whoCanKick));
			writer.newLine();
			writer.write(PlayerSave.GSON.toJson(paramClan.whoCanBan));
			writer.newLine();

			if ((paramClan.rankedMembers != null) && (paramClan.rankedMembers.size() > 0)) {
				writer.write(PlayerSave.GSON.toJson(paramClan.rankedMembers.size()));
				writer.newLine();
				for(int i=0; i< paramClan.rankedMembers.size(); i++) {
					writer.write(PlayerSave.GSON.toJson(paramClan.rankedMembers.get(i)));
					writer.write(PlayerSave.GSON.toJson(paramClan.ranks.get(i)));
				}
			}
			else {
				writer.write(0);
			}

			writer.flush();
			writer.close();

			/*localRandomAccessFile.writeUTF(paramClan.getTitle());
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

			localRandomAccessFile.close();*/
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
	}
}