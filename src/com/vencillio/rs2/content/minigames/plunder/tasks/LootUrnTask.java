package com.vencillio.rs2.content.minigames.plunder.tasks;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.content.minigames.plunder.PlunderConstants;
import com.vencillio.rs2.content.minigames.plunder.PlunderConstants.UrnBitPosition;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendConfig;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class LootUrnTask extends Task {

	private int ticks;
	private final UrnBitPosition urn;

	public LootUrnTask(Player player, UrnBitPosition urn) {
		super(player, 1, false, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.urn = urn;
		
		ticks = 0;
	}

	@Override
	public void onStart() {
		getEntity().getPlayer().getUpdateFlags().sendAnimation(PlunderConstants.ATTEMPT_LOOT);
		getEntity().getPlayer().getSkill().lock(4);
	}

	@Override
	public void execute() {
		getEntity().getPlayer().send(new SendMessage(ticks));
		switch (ticks++) {
		case 2:
			getEntity().getPlayer().getUpdateFlags().sendAnimation(PlunderConstants.SUCCESSFUL_LOOT);
			getEntity().getPlayer().getAttributes().set(PlunderConstants.URNS_CONFIG_KEY, getEntity().getPlayer().getAttributes().getInt(PlunderConstants.URNS_CONFIG_KEY) | urn.getConfig(0));
			break;
		case 3:
			stop();
			break;
		}
	}

	@Override
	public void onStop() {
		getEntity().getPlayer().send(new SendConfig(PlunderConstants.URNS_CONFIG, getEntity().getPlayer().getAttributes().getInt(PlunderConstants.URNS_CONFIG_KEY)));
	}
}