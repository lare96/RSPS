package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.clanchat.Clan;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class InterfaceAction extends IncomingPacket {

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		int id = in.readShort(false);
		int action = in.readShort(false);
		switch (id) {
		case 43704:
			if (action == 1) {
				player.getClan().delete();
				player.setClanData();
			}
			break;
		case 43707:
		case 43710:
		case 43713:
		case 43716:
			Clan clan = player.getClan();
			if (clan != null) {
				if (id == 43707) {
					clan.setRankCanJoin(action == 0 ? -1 : action);
				} else if (id == 43710) {
					clan.setRankCanTalk(action == 0 ? -1 : action);
				} else if (id == 43713) {
					clan.setRankCanKick(action == 0 ? -1 : action);
				} else if (id == 43716) {
					clan.setRankCanBan(action == 0 ? -1 : action);
				}
				String title = "";
				if (id == 43707) {
					title = clan.getRankTitle(clan.whoCanJoin) + (clan.whoCanJoin > Clan.Rank.ANYONE && clan.whoCanJoin < Clan.Rank.OWNER ? "+" : "");
				} else if (id == 43710) {
					title = clan.getRankTitle(clan.whoCanTalk) + (clan.whoCanTalk > Clan.Rank.ANYONE && clan.whoCanTalk < Clan.Rank.OWNER ? "+" : "");
				} else if (id == 43713) {
					title = clan.getRankTitle(clan.whoCanKick) + (clan.whoCanKick > Clan.Rank.ANYONE && clan.whoCanKick < Clan.Rank.OWNER ? "+" : "");
				} else if (id == 43716) {
					title = clan.getRankTitle(clan.whoCanBan) + (clan.whoCanBan > Clan.Rank.ANYONE && clan.whoCanBan < Clan.Rank.OWNER ? "+" : "");
				}
				player.send(new SendString(title, id + 2));
			}
			break;

		default:

			break;
		}
		if (id >= 43723 && id < 43823) {
			Clan clan = player.getClan();
			if (clan != null && clan.rankedMembers != null && !clan.rankedMembers.isEmpty()) {
				String member = clan.rankedMembers.get(id - 43723);
				switch (action) {
				case 0:
					clan.demote(member);
					break;
				default:
					if(clan.isAdmin(member))
						player.send(new SendMessage("You don't have the permission to do this"));
					else
						clan.setRank(member, action);
					break;
				}
				player.setClanData();
			}
		}
		if (id >= 43824 && id < 43924) {
			Clan clan = player.getClan();
			if (clan != null && clan.bannedMembers != null && !clan.bannedMembers.isEmpty()) {
				String member = clan.bannedMembers.get(id - 43824);
				switch (action) {
				case 0:
					clan.unbanMember(member);
					break;
				}
				player.setClanData();
			}
		}
		if (id >= 18144 && id < 18244) {
			for (int index = 0; index < 100; index++) {
				if (id == index + 18144) {
					String member = player.clan.activeMembers.get(id - 18144);
					if (member == null) {
						return;
					}
					switch (action) {

					case 0:
						if (player.clan.isFounder(player.getUsername()) && !player.getCombat().inCombat()) {
							player.send(new SendInterface(43700)); //Clan setup screen
						}
						break;
					case 1:
						if (member.equalsIgnoreCase(player.getUsername())) {
							player.send(new SendMessage("You can't kick yourself!"));
						} else {
							if (player.clan.canKick(player.getUsername()) && !player.clan.isAdmin(member)) {
								player.clan.kickMember(member);
							} else {
								player.send(new SendMessage("You do not have sufficient privileges to do this."));
							}
						}
						break;
					case 2:
						if (member.length() == 0) {
							break;
						} else if (member.length() > 12) {
							member = member.substring(0, 12);
						}
						if (member.equalsIgnoreCase(player.getUsername())) {
							break;
						}
						Clan clan = player.getClan();
						if (clan.isRanked(member) || clan.isAdmin(member)) {
							player.send(new SendMessage("You can't ban a ranked member of this channel."));
							break;
						}
						if (clan != null) {
							clan.banMember(Utility.formatPlayerName(member));
							player.setClanData();
							clan.save();
						}
						break;
					}
					break;
				}
			}
		}
	}

	@Override
	public int getMaxDuplicates() {
		return 1;
	}
}