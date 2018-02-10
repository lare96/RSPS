package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.VencillioConstants;
import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.content.DropTable;
import com.vencillio.rs2.content.moderation.StaffTab;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class InputFieldPacket extends IncomingPacket {

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		int id = in.readShort();
		String text = in.readString();

		if (id < 0 || text == null || text.length() <= 0) {
			return;
		}

		if (PlayerConstants.isOwner(player) && VencillioConstants.DEV_MODE) {
			player.send(new SendMessage("ID: " + id + " | Text: " + text));
		}
		
		if (StaffTab.inputField(player, id, text)) {
			return;
		}

		switch (id) {

		/* Drop Table Search Item */
		case 59814:
			DropTable.searchItem(player, text);
			break;

		/* Drop Table Search NPC */
		case 59815:
			DropTable.searchNpc(player, text);
			break;

		/* Bank Pins Setting/Removing */
		case 43755:
			if (player.getPin() != null) {
				player.getSecurity().removePin(text);
			} else {
				player.getSecurity().setPin(text);
			}
			break;

		/* Bank Pins Enter */
		case 48755:
			player.getSecurity().enterPin(text);
			break;

		/* Set Name */
		case 56013:
			player.getSecurity().setName(text);
			break;

		/* Set Email */
		case 56014:
			player.getSecurity().setEmail(text);
			break;

		/* Set Recovery */
		case 56015:
			player.getSecurity().setRecovery(text);
			break;

		/* Set IP */
		case 56016:
			player.getSecurity().setIP(text);
			break;

		}
	}

	@Override
	public int getMaxDuplicates() {
		return 1;
	}
}