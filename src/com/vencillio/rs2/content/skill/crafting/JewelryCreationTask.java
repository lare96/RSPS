package com.vencillio.rs2.content.skill.crafting;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.skill.smithing.Smelting;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterfaceConfig;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItemsAlt;

public class JewelryCreationTask extends Task {
	
	public static void sendInterface(Player p) {
		for (int k = 0; k < 3; k++) {
			int interfaceId = 4233;

			if (k == 1)
				interfaceId = 4239;
			else if (k == 2) {
				interfaceId = 4245;
			}
			for (int i = 0; i < JEWELRY_INTERFACE_ITEMS[k].length; i++) {
				p.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(interfaceId, JEWELRY_INTERFACE_ITEMS[k][i], 1, i));
			}
		}

		p.getClient().queueOutgoingPacket(new SendInterfaceConfig(4229, 0, -1));
		p.getClient().queueOutgoingPacket(new SendInterfaceConfig(4235, 0, -1));
		p.getClient().queueOutgoingPacket(new SendInterfaceConfig(4241, 0, -1));

		p.getClient().queueOutgoingPacket(new SendString("", 4230));
		p.getClient().queueOutgoingPacket(new SendString("", 4236));
		p.getClient().queueOutgoingPacket(new SendString("", 4242));

		p.getClient().queueOutgoingPacket(new SendInterface(4161));
	}

	private final Player player;
	private final Jewelry data;
	private byte amount;

	public static final int[][] JEWELRY_INTERFACE_ITEMS = { 
		{ 1635, 1637, 1639, 1641, 1643, 1645, 6575 }, 
		
		{ 1654, 1656, 1658, 1660, 1662, 1664, 6577 }, 
		
		{ 1673, 1675, 1677, 1679, 1681, 1683, 6579 } 
	};

	public static boolean clickButton(Player p, int id) {
		return false;
	}

	public static void start(Player p, int item, int amount) {
		if (Jewelry.forReward(item) != null) {
			TaskQueue.queue(new JewelryCreationTask(p, Jewelry.forReward(item), amount));
			p.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		}
	}

	public JewelryCreationTask(Player player, Jewelry data, int amount) {
		super(player, 2, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.data = data;
		this.player = player;
		this.amount = ((byte) amount);

		if (player.getMaxLevels()[12] < data.getRequiredLevel()) {
			player.send(new SendMessage("This requires a crafting level of " + data.getRequiredLevel() + "!"));
			stop();
			return;
		}

		for (int i : data.getMaterialsRequired())
			if (!player.getInventory().hasItemId(i)) {
				String req = Item.getDefinition(i).getName();
				player.send(new SendMessage("You need " + Utility.getAOrAn(req) + " " + req + " to make this!"));
				stop();
				return;
			}
	}

	@Override
	public void execute() {
		for (int i : data.getMaterialsRequired()) {
			if (!player.getInventory().hasItemId(i)) {
				player.send(new SendMessage("You have run out of " + Item.getDefinition(i).getName() + "s."));
				stop();
				return;
			}
		}

		for (int i : data.getMaterialsRequired()) {
			if (i != 0)
				player.getInventory().remove(i, 1, true);
		}

		if ((this.amount = (byte) (amount - 1)) == 0) {
			stop();
		}

		player.getUpdateFlags().sendAnimation(Smelting.SMELTING_ANIMATION);

		player.getClient().queueOutgoingPacket(new SendSound(469, 0, 0));

		player.getInventory().add(data.getReward(), true);

		player.getSkill().addExperience(12, data.getExperience());
		
		String name = Item.getDefinition(data.getReward().getId()).getName();
		
		player.send(new SendMessage("You have crafted " + Utility.getAOrAn(name) + " " + name + "."));
	}

	@Override
	public void onStop() {
	}
}
