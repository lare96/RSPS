package com.vencillio.rs2.content.skill.magic;

import com.vencillio.core.definitions.CombatSpellDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.PlayerConstants;

import java.util.HashMap;
import java.util.Map;

public class MagicConstants {

	public static enum SpellType {
		NONE,
		EARTH,
		WATER,
		WIND,
		FIRE;
	}

	public static enum Teleports {
		HOME(new Location(PlayerConstants.HOME));

		private final Location location;

		private Teleports(Location location) {
			this.location = location;
		}

		public Location getLocation() {
			return location;
		}
	}

	public static final int SET_AUTOCAST_BUTTON_ID = 1093;
	public static final int HOME_TELEPORT_BUTTON = 4171;
	public static final int TELEPORT_VARROCK_BUTTON = 4140;
	public static final int TELEPORT_LUMBRIDGE_BUTTON = 4143;
	public static final int TELEPORT_FALADOR_BUTTON = 4146;
	public static final int TELEPORT_CAMELOT_BUTTON = 4150;
	public static final int VENGEANCE_BUTTON = 118098;
	public static final int VENGEANCE_ANIMATION = 4410;
	public static final int VENGEANCE_GRAPHIC = 726;
	public static final int VENGEANCE_DELAY = 30000;
	public static final double VENGEANCE_HIT = 0.75D;
	public static final double VENGEANCE_EXPERIENCE = 112.0D;
	public static final String VENGEANCE_MESSAGE = "Taste Vengeance!";
	public static final Item[] VENGEANCE_RUNES = { new Item(9075, 4), new Item(557, 10), new Item(560, 2) };
	public static final int STAFF_OF_LIGHT_ID = 15486;
	public static final int TELE_BLOCK_SPELL_ID = 12445;
	public static final long TELE_BLOCK_TIME = 180000L;
	public static final int ICE_SPELL_IMMUNITY = 5;
	public static final int ICE_RUSH_SPELL_ID = 12861;
	public static final int ICE_RUSH_FREEZE_TIME = 5;
	public static final int ICE_BURST_SPELL_ID = 12881;
	public static final int ICE_BURST_FREEZE_TIME = 10;
	public static final int ICE_BLITZ_SPELL_ID = 12871;
	public static final int ICE_BLITZ_FREEZE_TIME = 15;
	public static final int ICE_BARRAGE_SPELL_ID = 12891;
	public static final int ICE_BARRAGE_FREEZE_TIME = 20;

	/** Modern teleporting */
	public static final Animation MODERN_TELEPORT_ANIMATION = new Animation(714, 0);

	public static final Graphic MODERN_TELEPORT_GRAPHIC = Graphic.lowGraphic(308, 0);

	public static final Graphic MODERN_TELEPORT_END_GRAPHIC = Graphic.lowGraphic(308, 0);

	public static final Animation MODERN_TELEPORT_END_ANIMATION = new Animation(715, 0);

	/** Ancient teleporting */
	public static final Animation ANCIENT_TELEPORT_ANIMATION = new Animation(1979, 0);

	public static final Graphic ANCIENT_TELEPORT_GRAPHIC = Graphic.lowGraphic(392, 0);

	public static final Animation ANCIENT_TELEPORT_END_ANIMATION = new Animation(65535, 0);

	public static final Graphic ANCIENT_TELEPORT_END_GRAPHIC = Graphic.lowGraphic(-1, 0);

	/** Teleporting tab teleporting */
	public static final Animation TABLET_BREAK_ANIMATION = new Animation(4069, 0);
	public static final Animation TABLET_TELEPORT_ANIMATION = new Animation(4731, 0);

	public static final Graphic TABLET_TELEPORT_GRAPHIC = Graphic.lowGraphic(678, 0);

	public static final Animation TABLET_TELEPORT_END_ANIMATION = new Animation(65535, 0);

	private static final Map<Integer, SpellType> spellTypes = new HashMap<Integer, SpellType>();

	/*public static void AssassinTeleport(final Player player,
										int i, int j, final Location tile) {
		int loop = 0;
		TaskQueue.queue(new Task(player, 1) {


			@Override
			public void execute() {
				if (loop == 0) {
					player.getUpdateFlags().sendAnimation(new Animation(17074));
					player.getUpdateFlags().sendGraphic(new Graphic(3215));
				} else if (loop == 6) {
					player.teleport(tile);
				} else if (loop == 7) {
					player.getUpdateFlags().sendAnimation(new Animation(16386));
					player.getUpdateFlags().sendGraphic(new Graphic(3019));
					player.getUpdateFlags().sendAnimation(new Animation(-1));
					stop();
				}
				loop++;
			}

			@Override
			public void onStop() {
			}

		}, 0, 1);
	}

	public static void DemonicTeleport(final Player player,
									   int i, int j, final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		//player.lock();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.getUpdateFlags().sendAnimation(new Animation(17108));
					player.getUpdateFlags().sendGraphic(new Graphic(3224));
					player.getUpdateFlags().sendGraphic(new Graphic(3225));
				} else if (loop == 8) {
					player.setNextWorldTile(tile);
					player.unlock();
				} else if (loop == 10) {
					player.getUpdateFlags().sendAnimation(new Animation(16386));
					player.getUpdateFlags().sendGraphic(new Graphic(3019));
					player.getUpdateFlags().sendAnimation(new Animation(-1));
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public static void PegasusTeleport(final Player player,
									   int i, int j, final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		player.lock();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.getUpdateFlags().sendAnimation(new Animation(17106));
					player.getUpdateFlags().sendGraphic(new Graphic(3223));
				} else if (loop == 6) {
					player.setNextWorldTile(tile);
					player.unlock();
				} else if (loop == 7) {
					player.getUpdateFlags().sendAnimation(new Animation(16386));
					player.getUpdateFlags().sendGraphic(new Graphic(3019));
					player.getUpdateFlags().sendAnimation(new Animation(-1));
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public static void GnomeTeleport(final Player player,
									 int i, int j, final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		player.lock();
		WorldTasksManager.schedule(new WorldTask() {

			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.getUpdateFlags().sendAnimation(new Animation(17191));
					player.getUpdateFlags().sendGraphic(new Graphic(3254));
				} else if (loop == 6) {
					player.setNextWorldTile(tile);
					player.unlock();
				} else if (loop == 7) {
					player.getUpdateFlags().sendAnimation(new Animation(16386));
					player.getUpdateFlags().sendGraphic(new Graphic(3019));
					player.getUpdateFlags().sendAnimation(new Animation(-1));
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public static void LightningTeleport(final Player player,
										 int i, int j, final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		player.lock();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.getUpdateFlags().sendAnimation(new Animation(17317));
					player.getUpdateFlags().sendGraphic(new Graphic(3311));
				} else if (loop == 8) {
					player.setNextWorldTile(tile);
					player.unlock();
				} else if (loop == 9) {
					player.getUpdateFlags().sendAnimation(new Animation(16386));
					player.getUpdateFlags().sendAnimation(new Animation(-1));
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}*/

	public static void declare() {
		for (int i = 0; i < 150000; i++) {
			CombatSpellDefinition def = GameDefinitionLoader.getCombatSpellDefinition(i);

			if (def != null) {
				String n = def.getName().toLowerCase();

				if (n.contains("fire"))
					spellTypes.put(Integer.valueOf(def.getId()), SpellType.FIRE);
				else if (n.contains("earth"))
					spellTypes.put(Integer.valueOf(def.getId()), SpellType.EARTH);
				else if ((n.contains("air")) || (n.contains("wind")))
					spellTypes.put(Integer.valueOf(def.getId()), SpellType.WIND);
				else if (n.contains("water"))
					spellTypes.put(Integer.valueOf(def.getId()), SpellType.WATER);
			}
		}
	}

	public static SpellType getSpellTypeForId(int id) {
		SpellType type = spellTypes.get(Integer.valueOf(id));

		if (type != null) {
			return type;
		}
		return SpellType.NONE;
	}

	public static MagicSkill.SpellBookTypes getStaffType(int id) {
		switch (id) {
		case 4675:
			return MagicSkill.SpellBookTypes.ANCIENT;
		}
		return MagicSkill.SpellBookTypes.MODERN;
	}

	public static String spellTypeToString(MagicSkill.SpellBookTypes spellBookType) {
		switch (spellBookType) {
		case ANCIENT:
			return "Modern";
		case LUNAR:
			return "Ancient";
		case MODERN:
			return "Lunar";
		}
		return "Modern";
	}
}
