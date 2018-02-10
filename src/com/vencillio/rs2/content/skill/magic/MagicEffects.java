package com.vencillio.rs2.content.skill.magic;

import java.util.HashMap;
import java.util.Map;

import com.vencillio.rs2.content.combat.impl.CombatEffect;
import com.vencillio.rs2.content.skill.magic.effects.BindEffect;
import com.vencillio.rs2.content.skill.magic.effects.BloodBarrageEffect;
import com.vencillio.rs2.content.skill.magic.effects.BloodBlitzEffect;
import com.vencillio.rs2.content.skill.magic.effects.BloodBurstEffect;
import com.vencillio.rs2.content.skill.magic.effects.BloodRushEffect;
import com.vencillio.rs2.content.skill.magic.effects.ClawsOfGuthixEffect;
import com.vencillio.rs2.content.skill.magic.effects.EntangleEffect;
import com.vencillio.rs2.content.skill.magic.effects.FlamesOfZamorakEffect;
import com.vencillio.rs2.content.skill.magic.effects.IceBarrageEffect;
import com.vencillio.rs2.content.skill.magic.effects.IceBlitzEffect;
import com.vencillio.rs2.content.skill.magic.effects.IceBurstEffect;
import com.vencillio.rs2.content.skill.magic.effects.IceRushEffect;
import com.vencillio.rs2.content.skill.magic.effects.SaradominStrikeEffect;
import com.vencillio.rs2.content.skill.magic.effects.ShadowBarrageEffect;
import com.vencillio.rs2.content.skill.magic.effects.ShadowBlitzEffect;
import com.vencillio.rs2.content.skill.magic.effects.ShadowBurstEffect;
import com.vencillio.rs2.content.skill.magic.effects.ShadowRushEffect;
import com.vencillio.rs2.content.skill.magic.effects.SmokeBarrageEffect;
import com.vencillio.rs2.content.skill.magic.effects.SmokeBlitzEffect;
import com.vencillio.rs2.content.skill.magic.effects.SmokeBurstEffect;
import com.vencillio.rs2.content.skill.magic.effects.SmokeRushEffect;
import com.vencillio.rs2.content.skill.magic.effects.SnareEffect;
import com.vencillio.rs2.content.skill.magic.effects.TeleBlockEffect;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;

public class MagicEffects {
	private static final Map<Integer, CombatEffect> effects = new HashMap<Integer, CombatEffect>();

	public static final void declare() {
		effects.put(Integer.valueOf(12939), new SmokeRushEffect());
		effects.put(Integer.valueOf(12987), new ShadowRushEffect());
		effects.put(Integer.valueOf(12901), new BloodRushEffect());
		effects.put(Integer.valueOf(12861), new IceRushEffect());
		effects.put(Integer.valueOf(12963), new SmokeBurstEffect());
		effects.put(Integer.valueOf(13011), new ShadowBurstEffect());
		effects.put(Integer.valueOf(12919), new BloodBurstEffect());
		effects.put(Integer.valueOf(12881), new IceBurstEffect());
		effects.put(Integer.valueOf(12951), new SmokeBlitzEffect());
		effects.put(Integer.valueOf(12999), new ShadowBlitzEffect());
		effects.put(Integer.valueOf(12911), new BloodBlitzEffect());
		effects.put(Integer.valueOf(12871), new IceBlitzEffect());
		effects.put(Integer.valueOf(12975), new SmokeBarrageEffect());
		effects.put(Integer.valueOf(13023), new ShadowBarrageEffect());
		effects.put(Integer.valueOf(12929), new BloodBarrageEffect());
		effects.put(Integer.valueOf(12891), new IceBarrageEffect());

		effects.put(Integer.valueOf(1190), new SaradominStrikeEffect());
		effects.put(Integer.valueOf(1191), new ClawsOfGuthixEffect());
		effects.put(Integer.valueOf(1192), new FlamesOfZamorakEffect());

		effects.put(Integer.valueOf(1572), new BindEffect());
		effects.put(Integer.valueOf(1582), new SnareEffect());
		effects.put(Integer.valueOf(1592), new EntangleEffect());

		effects.put(Integer.valueOf(12445), new TeleBlockEffect());
	}

	public static void doMagicEffects(Player attacker, Entity attacked,
			int spellId) {
		CombatEffect effect = effects.get(Integer.valueOf(spellId));

		if (effect == null) {
			return;
		}

		effect.execute(attacker, attacked);
	}
}
