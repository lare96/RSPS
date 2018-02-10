package com.vencillio.rs2.content.combat.impl;

import java.util.HashMap;
import java.util.Map;

import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;

public class PoisonWeapons {
	private static final Map<Integer, PoisonData> poison = new HashMap<Integer, PoisonData>();

	public static void checkForPoison(Player player, Entity attack) {
		if (Utility.randomNumber(3) != 0) {
			return;
		}

		if ((attack != null) && (!attack.isNpc())) {
			Player o = com.vencillio.rs2.entity.World.getPlayers()[attack.getIndex()];

			if (o != null) {
				Item shield = o.getEquipment().getItems()[5];

				if ((shield != null) && (shield.getId() == 18340)) {
					return;
				}
			}

		}

		Item weapon = player.getEquipment().getItems()[3];
		Item ammo = player.getEquipment().getItems()[13];

		CombatTypes type = player.getCombat().getCombatType();

		if (type == CombatTypes.MELEE) {
			if ((weapon == null) || (poison.get(Integer.valueOf(weapon.getId())) == null)) {
				return;
			}
			attack.poison(poison.get(Integer.valueOf(weapon.getId())).getStart());
		} else if (type == CombatTypes.RANGED) {
			if ((ammo == null) || (poison.get(Integer.valueOf(ammo.getId())) == null)) {
				return;
			}
			attack.poison(poison.get(Integer.valueOf(ammo.getId())).getStart());
		}
	}

	public static final void declare() {
		for (int i = 0; i < 20145; i++) {
			ItemDefinition def = GameDefinitionLoader.getItemDef(i);

			if ((def != null) && (def.getName() != null)) {
				String name = def.getName();
				
				if (name.equalsIgnoreCase("toxic blowpipe")) {
					poison.put(Integer.valueOf(i), new PoisonData(20));
				}

				if (name.contains("(p)"))
					poison.put(Integer.valueOf(i), new PoisonData(4));
				else if (name.contains("(p+)"))
					poison.put(Integer.valueOf(i), new PoisonData(5));
				else if (name.contains("(p++)"))
					poison.put(Integer.valueOf(i), new PoisonData(6));
			}
		}
	}
}
