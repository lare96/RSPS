package com.vencillio.rs2.content.minigames.plunder.tasks;

import com.vencillio.core.cache.map.RSObject;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.content.minigames.plunder.PlunderConstants;
import com.vencillio.rs2.content.minigames.plunder.PlunderConstants.DoorBitPosition;
import com.vencillio.rs2.content.minigames.plunder.PyramidPlunder;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendAnimateObject;
import com.vencillio.rs2.entity.player.net.out.impl.SendConfig;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class PicklockDoorTask extends Task {

	private final RSObject doorObject;
	private final DoorBitPosition door;
	
	private int ticks;

	public PicklockDoorTask(Player player, RSObject doorObject, DoorBitPosition door) {
		super(player, 1, true, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.doorObject = doorObject;
		this.door = door;

		ticks = 0;
		player.getSkill().lock(4);
		player.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
		player.getMovementHandler().setForceMove(true);
	}

	@Override
	public void execute() {
		switch (ticks++) {
		case 1:
			getEntity().getPlayer().getAttributes().set(PlunderConstants.DOORS_CHEST_SARCOPHAGUS_CONFIG_KEY, getEntity().getPlayer().getAttributes().getInt(PlunderConstants.DOORS_CHEST_SARCOPHAGUS_CONFIG_KEY) | door.getConfig());
			break;
		
		case 2:
			getEntity().getPlayer().send(new SendConfig(PlunderConstants.DOORS_CHEST_SARCOPHAGUS_CONFIG, getEntity().getPlayer().getAttributes().getInt(PlunderConstants.DOORS_CHEST_SARCOPHAGUS_CONFIG_KEY)));
			getEntity().getPlayer().send(new SendAnimateObject(doorObject, PlunderConstants.DOOR_ANIMATION));
			break;

		case 3:
			int floor = getEntity().getPlayer().getAttributes().getInt("PLUNDER_FLOOR");
			
			if (PyramidPlunder.SINGLETON.isExitDoor(door, floor)) {
				if (PyramidPlunder.SINGLETON.changeFloor(getEntity().getPlayer(), floor + 1)) {
					getEntity().getPlayer().send(new SendMessage("Floor @dre@" + (floor + 2) + "</col>."));
				} else {
					((Task) getEntity().getPlayer().getAttributes().get("PLUNDER_TASK")).stop();
					getEntity().getPlayer().send(new SendMessage("You have completed your run."));
				}
			}
			stop();
			break;
		}
		
		getEntity().getPlayer().getUpdateFlags().sendAnimation(PlunderConstants.SPEAR_TRAP);
	}

	@Override
	public void onStop() {
		getEntity().getPlayer().setController(ControllerManager.PLUNDER_CONTROLLER);
		getEntity().getPlayer().getMovementHandler().setForceMove(false);
	}
}