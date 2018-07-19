package com.vencillio.rs2.entity.mob.impl;

import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

import java.text.SimpleDateFormat;

public class CorporealBeast extends Mob {
	
	private Mob[] darkEnergyCores = null;
	private long TIME;

	public CorporealBeast() {
		super(319, true, new Location(2946, 4386));
		TIME = System.currentTimeMillis();
		System.out.println("Combatant size(In constructor): " + getCombatants().size());
		for(int i=0; i<getCombatants().size(); i++)
			getCombatants().get(i).getAttributes().set("CORP_DAMAGE", 0);
	}

	public boolean areCoresDead() {
		if (darkEnergyCores == null) {
			return true;
		}

		for (Mob mob : darkEnergyCores) {
			if (!mob.isDead()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void doPostHitProcessing(Hit hit) {
		if ((getCombatants().size() != 0) && (getLevels()[3] != 0) && (getLevels()[3] <= 150) && (areCoresDead()))
			darkEnergyCores = DarkEnergyCore.spawn();
	}

	@Override
	public void onDeath() {
		darkEnergyCores = null;
		System.out.println("Combatant size(In on death): " + getCombatants().size());
		for(int i=0; i<getCombatants().size(); i++) {
			System.out.println("combatant " + i + ": " + getCombatants().get(i).getUsername());
			getCombatants().get(i).send(new SendMessage("Fight duration: @red@" + new SimpleDateFormat("m:ss").format(System.currentTimeMillis() - TIME) + "</col>."));
			getCombatants().get(i).send(new SendMessage("Damage dealt: " + getCombatants().get(i).getAttributes().getInt("CORP_DAMAGE")));
			getCombatants().get(i).getAttributes().remove("CORP_DAMAGE");
		}
	}

	public void spawn() {
		new CorporealBeast();
	}
}
