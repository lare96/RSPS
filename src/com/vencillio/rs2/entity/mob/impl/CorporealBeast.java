package com.vencillio.rs2.entity.mob.impl;

import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CorporealBeast extends Mob {

	private Mob[] darkEnergyCores = null;
	private long TIME;

	public List<Player> allCombatants = new ArrayList<>();


	public CorporealBeast() {
		super(319, true, new Location(2993, 4382, 2));
		TIME = System.currentTimeMillis();
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
	public void doAliveMobProcessing() {
		if (getCombatants().size() > 0) {
			for (int i = 0; i < getCombatants().size(); i++) {
				if(!allCombatants.contains(getCombatants().get(i))) {
					System.out.println(getCombatants().get(i).getUsername() + " added to allCombatants");
					allCombatants.add(getCombatants().get(i));
				}
				if (!getCombatants().get(i).getAttributes().isSet("CORP_DAMAGE"))
					getCombatants().get(i).getAttributes().set("CORP_DAMAGE", 0);
			}
		}
	}

	@Override
	public void onDeath() {
		darkEnergyCores = null;
		for (int i = 0; i < allCombatants.size(); i++) {
			allCombatants.get(i).send(new SendMessage("Fight duration: @red@" + new SimpleDateFormat("m:ss").format(System.currentTimeMillis() - TIME) + "</col>."));
			allCombatants.get(i).send(new SendMessage("Damage dealt: " + allCombatants.get(i).getAttributes().getInt("CORP_DAMAGE")));
		}

		for (Player p : allCombatants) {
			p.getAttributes().remove("CORP_DAMAGE");
		}
		allCombatants.clear();
	}

	public void spawn() {
		new CorporealBeast();
	}
}
