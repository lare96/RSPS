package com.vencillio.rs2.content.combat.impl;

import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class RingOfRecoil implements CombatEffect {

	public static final String RECOIL_STAGE_KEY = "recoilhits";
	public static final String RECOIL_DAMAGE_KEY = "rordamage";

	public static void doRecoil(Player p, Entity e, int damage) {
		Item ring = p.getEquipment().getItems()[12];

		if ((ring != null) && (ring.getId() == 2550)) {
			int dmg = (int) Math.ceil(damage * 0.1D);
			if (dmg > 0) {
				p.getAttributes().set("rordamage", Integer.valueOf(dmg));
				new RingOfRecoil().execute(p, e);
				onRecoil(p, dmg);
			}
		}
	}

	public static void onRecoil(Player p, int dmg) {
		if (p.getAttributes().get("recoilhits") == null) {
			p.getAttributes().set("recoilhits", Integer.valueOf(dmg));
		} else {
			byte damage = (byte) (p.getAttributes().getByte("recoilhits") + dmg);

			if (damage >= 40) {
				p.getAttributes().remove("recoilhits");

				p.getEquipment().getItems()[12] = null;
				p.getEquipment().update(12);

				p.getClient().queueOutgoingPacket(new SendMessage("@blu@Your ring of recoil crumbles to dust."));
			} else {
				p.getAttributes().set("recoilhits", Byte.valueOf(damage));
			}
		}
	}

	@Override
	public void execute(Player p, Entity e) {
		if (e.isDead()) {
			return;
		}
		e.hit(new Hit(p.getAttributes().getInt("rordamage")));
		e.getAttributes().remove("rordamage");
	}
}
