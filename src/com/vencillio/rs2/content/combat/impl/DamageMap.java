package com.vencillio.rs2.content.combat.impl;

import com.vencillio.rs2.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DamageMap {

	private Map<Entity, Integer> dmg = new HashMap<Entity, Integer>();

	private long lastDamage = 0L;
	
	public DamageMap(Entity e) {
	}

	public void addDamage(Entity attacker, int damage) {
		lastDamage = System.currentTimeMillis() + 60000;

		if (damage == 0) {
			return;
		}

		if (dmg.get(attacker) == null) {
			dmg.put(attacker, Integer.valueOf(damage));
		} else {
			int total = dmg.get(attacker).intValue();
			dmg.remove(attacker);
			dmg.put(attacker, Integer.valueOf(total + damage));
		}
	}

	public void clear() {
		dmg.clear();
		lastDamage = 0L;
	}

	public Entity getKiller() {
		int highDmg = 0;
		Entity highEn = null;

		for (Entry<Entity, Integer> i : dmg.entrySet()) { //I think this is where it makes it so ironman cant be
			//determined as killer which results in them not getting drop if another player attacks that npc at any point
			if (i != null && i.getValue() > highDmg) {
				if (!i.getKey().isNpc() && i.getKey().getPlayer() != null && i.getKey().getPlayer().ironPlayer() && dmg.size() > 1) {
					continue;
				}
				highDmg = i.getValue();
				highEn = i.getKey();
			}
		}

		return highEn;
	}

	public boolean isClearHistory() {
		return lastDamage != 0 && dmg.size() > 0 && lastDamage <= System.currentTimeMillis();
	}
}
