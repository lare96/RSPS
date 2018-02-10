package com.vencillio.rs2.entity.mob;

import java.util.HashMap;
import java.util.Map;

import com.vencillio.rs2.content.combat.CombatEffect;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.mob.abilities.BarrelchestAbility;
import com.vencillio.rs2.entity.mob.abilities.BorkAbility;
import com.vencillio.rs2.entity.mob.abilities.CorporealBeastAbility;
import com.vencillio.rs2.entity.mob.abilities.JadAbility;

public class MobAbilities {

	private static final Map<Integer, CombatEffect> abilities = new HashMap<Integer, CombatEffect>();

	public static final void declare() {
		abilities.put(Integer.valueOf(6342), new BarrelchestAbility());
		abilities.put(Integer.valueOf(319), new CorporealBeastAbility());
		abilities.put(Integer.valueOf(2745), new JadAbility());
		abilities.put(Integer.valueOf(7133), new BorkAbility());
		
//		Zulrah zulrah = new Zulrah();
//		abilities.put(Integer.valueOf(2042), zulrah);
//		abilities.put(Integer.valueOf(2043), zulrah);
//		abilities.put(Integer.valueOf(2044), zulrah);
		
		//abilities.put(Integer.valueOf(10057), new IcyBonesAbility());
		//abilities.put(Integer.valueOf(10072), new HobgoblinGeomancerAbility());

//		for (int i = 0; i < 6538; i++) {
//			NpcDefinition def = GameDefinitionLoader.getNpcDefinition(i);
//			if (def != null) {
//				//String check = def.getName().toLowerCase();
//			}
//		}
	}

	public static final void executeAbility(int id, Mob mob, Entity a) {
		CombatEffect e = abilities.get(Integer.valueOf(id));
		
		if (e != null)
			e.execute(mob, a);
	}
}
