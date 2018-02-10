package com.vencillio.rs2.content.dialogue.impl.teleport;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.skill.magic.MagicSkill;
import com.vencillio.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.vencillio.rs2.entity.player.Player;

public class WildernessLever extends Dialogue {

	public WildernessLever(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		if (!player.getPlayer().getMagic()
				.canTeleport(TeleportTypes.SPELL_BOOK)) {
			player.getDialogue().end();
			return false;
		}
		switch (id) {
		case 9178:
			getPlayer().getMagic().teleport(3153, 3923, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			player.getDialogue().end();
			break;
		case 9179:
			getPlayer().getMagic().teleport(3158, 3670, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			player.getDialogue().end();
			break;
		case 9180:
			getPlayer().getMagic().teleport(3361, 3687, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			player.getDialogue().end();
			break;
		case 9181:
			getPlayer().getMagic().teleport(3091, 3476, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			player.getDialogue().end();
			break;
		}
		return false;
	}

	@Override
	public void execute() {
		DialogueManager.sendOption(player, new String[] { "Deserted Keep",
				"Graveyard", "East Dragons", "West Dragons" });
	}
}