package com.vencillio.rs2.content.skill.magic;

import java.util.HashMap;
import java.util.Map;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.impl.Attack;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.magic.spells.Charge;
import com.vencillio.rs2.content.skill.magic.spells.HighAlchemy;
import com.vencillio.rs2.content.skill.magic.spells.LowAlchemy;
import com.vencillio.rs2.content.skill.magic.spells.SuperHeat;
import com.vencillio.rs2.content.skill.magic.spells.Vengeance;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendOpenTab;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendSidebarInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;

public class MagicSkill {

	public static enum SpellBookTypes {
		MODERN,
		ANCIENT,
		LUNAR;
	}

	public static enum TeleportTypes {
		SPELL_BOOK,
		TABLET,
		TELE_OTHER,
		FOUNTAIN_OF_RUNE,
		OBELISK;
	}

	public static final String MAGIC_ITEM_KEY = "magicitem";

	private final Player player;
	private SpellCasting spellCasting;
	
	private boolean teleporting = false;

	private boolean vengeanceActive = false;

	private boolean ahrimEffectActive = false;

	private byte dragonFireShieldCharges = 0;

	private int magicBook = 0;

	private long lastVengeance = 0L;
	private SpellBookTypes spellBookType = SpellBookTypes.MODERN;

	private boolean dFireShieldEffect = false;

	private long dFireShieldTime = 0L;

	public static final int[][] AUTOCAST_BUTTONS = { { 84242, 21746 }, { 50091, 12891 }, { 50129, 12929 }, { 50223, 13023 }, { 50175, 12975 }, { 84241, 21745 }, { 50071, 12871 }, { 50111, 12911 }, { 50199, 12999 }, { 50151, 12951 }, { 86152, 22168 }, { 50081, 12881 }, { 50119, 12919 }, { 50221, 13011 }, { 50163, 12963 }, { 84220, 21744 }, { 50061, 12861 }, { 50101, 12901 }, { 50187, 12987 }, { 50139, 12939 }, { 6056, 1592 }, { 4165, 1189 }, { 4164, 1188 }, { 4161, 1185 }, { 4159, 1183 }, { 4168, 1192 }, { 4167, 1191 }, { 4166, 1190 }, { 4157, 1181 }, { 4153, 1177 }, { 6046, 1582 }, { 4151, 1175 }, { 4148, 1172 }, { 4145, 1169 }, { 4142, 1166 }, { 4139, 1163 }, { 6036, 1572 }, { 4136, 1160 }, { 4134, 1158 }, { 4132, 1156 }, { 4130, 1154 }, { 4128, 1152 } };

	public MagicSkill(Player player) {
		this.player = player;
		spellCasting = new SpellCasting(player);
	}

	public void activateVengeance() {
		vengeanceActive = true;
		lastVengeance = System.currentTimeMillis();
	}

	public boolean canTeleport(TeleportTypes type) {
		if (player.isJailed()) {
			player.send(new SendMessage("You are jailed and can not do this!"));
			return false;
		}
		if (player.inWilderness() && player.getWildernessLevel() >= 20) {
			player.getClient().queueOutgoingPacket(new SendMessage("You can't teleport above level 20 Wilderness."));
			return false;
		}
		if (player.getController().equals(ControllerManager.FIGHT_PITS_CONTROLLER) || player.getController().equals(ControllerManager.FIGHT_PITS_WAITING_CONTROLLER)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You can't teleport from here."));
			return false;
		}
		if (player.getController().equals(ControllerManager.PEST_WAITING_ROOM_CONTROLLER)) {
			player.getClient().queueOutgoingPacket(new SendMessage("Please Exit the boat via the ladder."));
			return false;
		} else if (player.getController().equals(ControllerManager.PEST_CONTROLLER)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You can't teleport whilst in pest control."));
			player.getClient().queueOutgoingPacket(new SendMessage("If you wish to leave speak with the squire back at the boat."));
			return false;
		}
		if (player.inDuelArena() && player.getDueling().isDueling()) {
			return false;
		}
		if (player.isBusyNoInterfaceCheck()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You can't teleport right now."));
			return false;
		}
		if (player.isTeleblocked()) {
			player.getClient().queueOutgoingPacket(new SendMessage("A magical force blocks you from teleporting."));
			return false;
		} else if (teleporting) {
			return false;
		} else if (!player.getController().canTeleport()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You can't teleport right now."));
			return false;
		}
		return true;
	}

	public boolean clickMagicButtons(int buttonId) {
		if (buttonId == 26010) {
			spellCasting.disableAutocast();
			Autocast.resetAutoCastInterface(player);
			player.updateCombatType();
			return true;
		}

		for (int i = 0; i < AUTOCAST_BUTTONS.length; i++) {
			if (buttonId == AUTOCAST_BUTTONS[i][0]) {
				Autocast.setAutocast(player, AUTOCAST_BUTTONS[i][1]);
				return true;
			}
		}
		switch (buttonId) {
		case 75008:
			player.getClient().queueOutgoingPacket(new SendMessage("You don't have a house to teleport to!"));
			return true;
		case 4169:
			player.getMagic().getSpellCasting().cast(new Charge());
			return true;
		case 118098:
			player.getMagic().getSpellCasting().cast(new Vengeance());
			return true;

		}
		return false;
	}

	public boolean clickMagicItems(int id) {
		switch (id) {
		case 8007:
			if (canTeleport(TeleportTypes.TABLET)) {
				player.getInventory().remove(id, 1);
				teleport(3212, 3424, 0, TeleportTypes.TABLET);
				return true;
			}
			break;
		case 8008:
			if (canTeleport(TeleportTypes.TABLET)) {
				player.getInventory().remove(id, 1);
				teleport(3222, 3218, 0, TeleportTypes.TABLET);
				return true;
			}
			break;
		case 8009:
			if (canTeleport(TeleportTypes.TABLET)) {
				player.getInventory().remove(id, 1);
				teleport(2964, 3378, 0, TeleportTypes.TABLET);
				return true;
			}
			break;	
		case 8010:
			if (canTeleport(TeleportTypes.TABLET)) {
				player.getInventory().remove(id, 1);
				teleport(2757, 3477, 0, TeleportTypes.TABLET);
				return true;
			}
			break;
		case 8011:
			if (canTeleport(TeleportTypes.TABLET)) {
				player.getInventory().remove(id, 1);
				teleport(2662, 3305, 0, TeleportTypes.TABLET);
				return true;
			}
			break;
		case 8012:
			if (canTeleport(TeleportTypes.TABLET)) {
				player.getInventory().remove(id, 1);
				teleport(2549, 3112, 0, TeleportTypes.TABLET);
				return true;
			}
			break;
		case 8013:
			if (canTeleport(TeleportTypes.TABLET)) {
				player.getInventory().remove(id, 1);
				teleport(MagicConstants.Teleports.HOME, TeleportTypes.TABLET);
				return true;
			}
			break;
		case 8014:
		case 8015:
			
			int[] bones = {
				526, 528, 530
			};
			
			int bone = 0;
			
			for (int index = 0; index < bones.length; index++) {
				if (player.getInventory().hasItemId(bones[index])) {
					bone = bones[index];
					continue;
				}
			}
			
			int amount = player.getInventory().getItemAmount(bone);
			
			if (amount == 0) {
				player.send(new SendMessage("You have no bones to do this!"));
				return false;
			}
			
			player.getInventory().remove(id, 1);
			player.getInventory().remove(bone, amount);
			player.getInventory().add(id == 8014 ? 1963 : 6883, amount);
			player.getSkill().addExperience(Skills.MAGIC, id == 8014 ? 25 : 35.5);
			
			player.send(new SendMessage("You have converted " + amount + " bones to " + (id == 8014 ? "bananas" : "peaches") + " ."));
			
			return true;
		}
		return false;
	}

	public void deactivateVengeance() {
		vengeanceActive = false;
	}

	public void decrDragonFireShieldCharges() {
		dragonFireShieldCharges = ((byte) (dragonFireShieldCharges - 1));

		if (dragonFireShieldCharges == 0)
			player.getClient().queueOutgoingPacket(new SendMessage("Your Dragonfire shield is now empty."));
	}

	public void doWildernessTeleport(final int x, final int y, final int z, final TeleportTypes type) {
		teleporting = true;
		player.setTakeDamage(false);
		player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		player.getController().onTeleport(player);
		int delay = 3;
		switch (type) {
		case OBELISK:
			player.getUpdateFlags().sendGraphic(Graphic.highGraphic(342, 0));
			player.getUpdateFlags().sendAnimation(new Animation(1816));
			TaskQueue.queue(new Task(player, 1, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
				@Override
				public void execute() {
					stop();
				}

				@Override
				public void onStop() {
					player.getClient().queueOutgoingPacket(new SendMessage("Ancient magic teleports you somewhere in the wilderness"));
				}
			});
			break;
		case TABLET:
			player.getUpdateFlags().sendAnimation(MagicConstants.TABLET_BREAK_ANIMATION);
			TaskQueue.queue(new Task(player, 1, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
				@Override
				public void execute() {
					player.getUpdateFlags().sendAnimation(MagicConstants.TABLET_TELEPORT_ANIMATION);
					player.getUpdateFlags().sendGraphic(MagicConstants.TABLET_TELEPORT_GRAPHIC);
					stop();
				}

				@Override
				public void onStop() {
				}
			});
			break;
		case TELE_OTHER:
			player.getUpdateFlags().sendAnimation(1816, 0);
			player.getUpdateFlags().sendGraphic(new Graphic(342, 0, false));
			break;
		case FOUNTAIN_OF_RUNE:
			player.getUpdateFlags().sendAnimation(1816, 0);
			player.getUpdateFlags().sendGraphic(new Graphic(342, 0, false));
			break;
		default:
			switch (spellBookType) {
			case ANCIENT:
				player.getUpdateFlags().sendAnimation(MagicConstants.ANCIENT_TELEPORT_ANIMATION);
				player.getUpdateFlags().sendGraphic(MagicConstants.ANCIENT_TELEPORT_GRAPHIC);
				delay = 4;
				break;
			case LUNAR:
				player.getUpdateFlags().sendAnimation(9606, 0);
				player.getUpdateFlags().sendGraphic(new Graphic(1685, 0, false));
				delay = 4;
				break;
			default:
				player.getClient().queueOutgoingPacket(new SendSound(202, 1, 0));
				player.getUpdateFlags().sendAnimation(MagicConstants.MODERN_TELEPORT_ANIMATION);
				player.getUpdateFlags().sendGraphic(MagicConstants.MODERN_TELEPORT_GRAPHIC);
				delay = 4;
			}

			break;
		}

		TaskQueue.queue(new Task(player, delay, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
			@Override
			public void execute() {
				if (!player.getController().canTeleport()) {
					player.setTakeDamage(true);
					teleporting = false;
					return;
				}

				TaskQueue.onMovement(player);

				player.teleport(new Location(x, y, z));
				player.setTakeDamage(true);
				teleporting = false;

				switch (type) {
				case SPELL_BOOK:
					player.getUpdateFlags().sendAnimation(MagicConstants.MODERN_TELEPORT_END_ANIMATION);
					switch (spellBookType) {
					case MODERN:
						player.getUpdateFlags().sendGraphic(MagicConstants.MODERN_TELEPORT_END_GRAPHIC);
						break;
					default:
						break;
					}
					break;
				case TABLET:
					player.getUpdateFlags().sendAnimation(MagicConstants.TABLET_TELEPORT_END_ANIMATION);
					break;
				default:
					break;
				}

				stop();
			}

			@Override
			public void onStop() {
			}
		});
	}

	public byte getDragonFireShieldCharges() {
		return dragonFireShieldCharges;
	}

	public long getLastVengeance() {
		return lastVengeance;
	}

	public int getMagicBook() {
		return magicBook;
	}

	public SpellBookTypes getSpellBookType() {
		return spellBookType;
	}

	public SpellCasting getSpellCasting() {
		return spellCasting;
	}

	public int hasRunes(Item[] runes) {
		Item[] items = player.getInventory().getItems();
		int k;
		for (int i = 0; i < runes.length; i++) {
			if (runes[i] != null) {
				if (!needsRune(runes[i].getId())) {
					runes[i] = null;
				} else {
					for (k = 0; k < items.length; k++)
						if (items[k] != null) {
							if (items[k].getId() == runes[i].getId()) {
								if (items[k].getAmount() < runes[i].getAmount()) {
									return items[k].getId();
								}
								runes[i] = null;
								break;
							}
						}
				}
			}
		}
		for (Item item : runes) {
			if (item != null) {
				return item.getId();
			}
		}

		return -1;
	}

	public void incrDragonFireShieldCharges(Mob mob) {
		if (dragonFireShieldCharges == 50 || player.isDead() || player.getMagic().isTeleporting()) {
			return;
		} else if (dragonFireShieldCharges > 50) {
			dragonFireShieldCharges = 50;
			return;
		}
		player.face(mob);
		player.getUpdateFlags().sendGraphic(new Graphic(1164));
		player.getUpdateFlags().sendAnimation(new Animation(6695));
		dragonFireShieldCharges = ((byte) (dragonFireShieldCharges + 1));

	}

	private void initTeleport(final int x, final int y, final int z, final TeleportTypes type) {
		if (Entity.inWilderness(x, y)) {
			doWildernessTeleport(x, y, z, type);
			return;
		}

		teleporting = true;
		player.setTakeDamage(false);
		player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		player.getController().onTeleport(player);
		int delay = 3;
		switch (type) {
		case TABLET:
			player.getUpdateFlags().sendAnimation(MagicConstants.TABLET_BREAK_ANIMATION);
			TaskQueue.queue(new Task(player, 1, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
				@Override
				public void execute() {
					player.getUpdateFlags().sendAnimation(MagicConstants.TABLET_TELEPORT_ANIMATION);
					player.getUpdateFlags().sendGraphic(MagicConstants.TABLET_TELEPORT_GRAPHIC);
					stop();
				}

				@Override
				public void onStop() {
				}
			});
			break;
		case TELE_OTHER:
			player.getUpdateFlags().sendAnimation(1816, 0);
			player.getUpdateFlags().sendGraphic(new Graphic(342, 0, false));
			break;
		case FOUNTAIN_OF_RUNE:
			player.getUpdateFlags().sendAnimation(1816, 0);
			player.getUpdateFlags().sendGraphic(new Graphic(283, 0, false));
			break;
		default:
			switch (spellBookType) {
			case ANCIENT:
				player.getUpdateFlags().sendAnimation(MagicConstants.ANCIENT_TELEPORT_ANIMATION);
				player.getUpdateFlags().sendGraphic(MagicConstants.ANCIENT_TELEPORT_GRAPHIC);
				delay = 4;
				break;
			case LUNAR:
				player.getUpdateFlags().sendAnimation(MagicConstants.MODERN_TELEPORT_ANIMATION);
				player.getUpdateFlags().sendGraphic(MagicConstants.MODERN_TELEPORT_GRAPHIC);
				delay = 4;
				break;
			default:
				player.getClient().queueOutgoingPacket(new SendSound(202, 1, 0));
				player.getUpdateFlags().sendAnimation(MagicConstants.MODERN_TELEPORT_ANIMATION);
				player.getUpdateFlags().sendGraphic(MagicConstants.MODERN_TELEPORT_GRAPHIC);
				delay = 4;
			}

			break;
		}

		TaskQueue.queue(new Task(player, delay, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
			@Override
			public void execute() {
				if (!player.getController().canTeleport()) {
					player.setTakeDamage(true);
					teleporting = false;
					return;
				}

				TaskQueue.onMovement(player);

				player.teleport(new Location(x, y, z));
				player.setTakeDamage(true);
				teleporting = false;

				switch (type) {
				case SPELL_BOOK:
					player.getUpdateFlags().sendAnimation(MagicConstants.MODERN_TELEPORT_END_ANIMATION);
					switch (spellBookType) {
					case MODERN:
						player.getUpdateFlags().sendGraphic(MagicConstants.MODERN_TELEPORT_END_GRAPHIC);
						break;
					default:
						break;
					}
					break;
				case TABLET:
					player.getUpdateFlags().sendAnimation(MagicConstants.TABLET_TELEPORT_END_ANIMATION);
					break;
				default:
					break;
				}

				stop();
			}

			@Override
			public void onStop() {
				if (player.getBossPet() != null) {
					player.getBossPet().remove();
					final Mob mob = new Mob(player, player.getBossID(), false, false, true, player.getLocation());
					mob.getFollowing().setIgnoreDistance(true);
					mob.getFollowing().setFollow(player);
					player.setBossPet(mob);
				}
			}
		});
	}

	public boolean isAhrimEffectActive() {
		return ahrimEffectActive;
	}

	public boolean isDFireShieldEffect() {
		return dFireShieldEffect;
	}

	public boolean isTeleporting() {
		return teleporting;
	}
	
	public void setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
	}
	

	public boolean isVengeanceActive() {
		return vengeanceActive;
	}

	public boolean needsRune(int runeId) {
		Item weapon = player.getEquipment().getItems()[3];
		Item shield = player.getEquipment().getItems()[3];

		if (weapon == null) {
			return true;
		}

		int wep = weapon.getId();

		switch (runeId) {
		case 556:
			if ((wep == 1381) || (wep == 1397) || (wep == 17293)) {
				return false;
			}
			return true;
		case 554:
			if ((wep == 1387) || (wep == 1393) || (wep == 17293)) {
				return false;
			}
			return true;
		case 555:
			if ((wep == 1383) || (wep == 1395) || (wep == 17293) || ((shield != null) && (shield.getId() == 18346))) {
				return false;
			}
			return true;
		case 557:
			if ((wep == 1385) || (wep == 1399) || (wep == 17293)) {
				return false;
			}
			return true;
		}
		return true;
	}

	public void onLogin() {

	}

	public void onOperateDragonFireShield() {
		if (dragonFireShieldCharges == 0) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have any charges on your shield."));
			return;
		}
		if ((!PlayerConstants.isOwner(player)) && (System.currentTimeMillis() - dFireShieldTime < 300000L)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You must let your shield cool down before using it again."));
			return;
		}
		dFireShieldEffect = (!dFireShieldEffect);

		if (dFireShieldEffect) {
			player.getClient().queueOutgoingPacket(new SendMessage("Your Dragonfire shield attack is now active."));

			Projectile p = new Projectile(1166);

			p.setStartHeight(25);
			p.setEndHeight(25);

			p.setDelay(40);
			p.setCurve(0);

			player.getCombat().getMagic().setpDelay((byte) 1);

			if (player.getCombat().getAttacking() == null) {
				player.getUpdateFlags().sendAnimation(6695, 0);
			}

			player.getCombat().getMagic().setAttack(new Attack(5, 5), new Animation(6696), new Graphic(1167, 45, true), new Graphic(1167, 0, true), p);
		}

		player.updateCombatType();
	}

	public void removeRunes(Item[] runes) {
		for (int i = 0; i < runes.length; i++)
			if ((runes[i] != null) && (needsRune(runes[i].getId()))) {
				if (runes[i].getId() == 561) {
					Item weapon = player.getEquipment().getItems()[3];

					if ((weapon != null) && (weapon.getId() == 18341) && (Utility.randomNumber(2) != 0))
						;
					else
						player.getInventory().remove(runes[i]);
				} else {
					player.getInventory().remove(runes[i]);
				}
			}
	}

	public void reset() {
		dFireShieldEffect = false;
		dFireShieldTime = System.currentTimeMillis();
	}

	public void setAhrimEffectActive(boolean ahrimEffectActive) {
		this.ahrimEffectActive = ahrimEffectActive;
	}

	public void setDFireShieldEffect(boolean dFireShieldEffect) {
		this.dFireShieldEffect = dFireShieldEffect;
	}

	public void setDragonFireShieldCharges(int dragonFireShieldCharges) {
		this.dragonFireShieldCharges = ((byte) dragonFireShieldCharges);
	}

	public void setMagicBook(int magicBook) {
		this.magicBook = magicBook;
		player.getClient().queueOutgoingPacket(new SendSidebarInterface(6, magicBook));

		if (player.isActive()) {
			spellCasting.disableAutocast();
			Autocast.resetAutoCastInterface(player);
			player.updateCombatType();
		}

		switch (magicBook) {
		case 1151:
			player.getMagic().setSpellBookType(SpellBookTypes.MODERN);
			break;
		case 12855:
			player.getMagic().setSpellBookType(SpellBookTypes.ANCIENT);
			break;
		case 29999:
			player.getMagic().setSpellBookType(SpellBookTypes.LUNAR);
		}
	}

	public void setSpellBookType(SpellBookTypes spellBookType) {
		this.spellBookType = spellBookType;
	}

	public void setVengeanceActive(boolean vengeanceActive) {
		this.vengeanceActive = vengeanceActive;
	}

	public void teleport(int x, int y, int z, TeleportTypes type) {
		if (!canTeleport(type) || player.isDead()) {
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return;
		}

		initTeleport(x, y, z, type);
	}
	
	public void teleport(Location location, TeleportTypes teleportType) {
		teleport(location.getX(), location.getY(), location.getZ(), teleportType);
	}

	public void teleport(MagicConstants.Teleports teleport, TeleportTypes teleportType) {
		teleport(teleport.getLocation().getX(), teleport.getLocation().getY(), teleport.getLocation().getZ(), teleportType);
	}

	public void teleportNoWildernessRequirement(int x, int y, int z, TeleportTypes type) {
		if (teleporting)
			return;
		if (player.isTeleblocked()) {
			player.getClient().queueOutgoingPacket(new SendMessage("A magical force blocks you from teleporting."));
			return;
		}
		if (!player.getController().canTeleport()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You can't teleport right now."));
			return;
		}

		if (player.isDead()) {
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return;
		}

		initTeleport(x, y, z, type);
	}
	

	public enum Enchant {

		SAPPHIRERING(1637, 2550, 7, 18, 719, 114, 1),
		SAPPHIREAMULET(1694, 1727, 7, 18, 719, 114, 1),
		SAPPHIRENECKLACE(1656, 3853, 7, 18, 719, 114, 1),

		EMERALDRING(1639, 2552, 27, 37, 719, 114, 2),
		EMERALDAMULET(1696, 1729, 27, 37, 719, 114, 2),
		EMERALDNECKLACE(1658, 5521, 27, 37, 719, 114, 2),

		RUBYRING(1641, 2568, 47, 59, 720, 115, 3),
		RUBYAMULET(1698, 1725, 47, 59, 720, 115, 3),
		RUBYNECKLACE(1660, 11194, 47, 59, 720, 115, 3),

		DIAMONDRING(1643, 2570, 57, 67, 720, 115, 4),
		DIAMONDAMULET(1700, 1731, 57, 67, 720, 115, 4),
		DIAMONDNECKLACE(1662, 11090, 57, 67, 720, 115, 4),

		DRAGONSTONERING(1645, 2572, 68, 78, 721, 116, 5),
		DRAGONSTONEAMULET(1702, 1712, 68, 78, 721, 116, 5),
		DRAGONSTONENECKLACE(1664, 11105, 68, 78, 721, 116, 5),

		ONYXRING(6575, 6583, 87, 97, 721, 452, 6),
		ONYXAMULET(6581, 6585, 87, 97, 721, 452, 6),
		ONYXNECKLACE(6577, 11128, 87, 97, 721, 452, 6);

		int unenchanted, enchanted, levelReq, xpGiven, anim, gfx, reqEnchantmentLevel;
		private Enchant(int unenchanted, int enchanted, int levelReq, int xpGiven, int anim, int gfx, int reqEnchantmentLevel) {
			this.unenchanted = unenchanted;
			this.enchanted = enchanted;
			this.levelReq = levelReq;
			this.xpGiven = xpGiven;
			this.anim = anim;
			this.gfx = gfx;
			this.reqEnchantmentLevel = reqEnchantmentLevel;
		}

		public int getUnenchanted() {
			return unenchanted;
		}

		public int getEnchanted() {
			return enchanted;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getXp() {
			return xpGiven;
		}

		public int getAnim() {
			return anim;
		}

		public int getGFX() {
			return gfx;
		}

		public int getELevel() {
			return reqEnchantmentLevel;
		}

		private static final Map <Integer,Enchant> enc = new HashMap<Integer,Enchant>();

		public static Enchant forId(int itemID) {
			return enc.get(itemID);
		}

		static {
			for (Enchant en : Enchant.values()) {
				enc.put(en.getUnenchanted(), en);
			}
		}
	}

	private enum EnchantSpell {

		SAPPHIRE(1155, 555, 1, 564, 1, -1, 0),
		EMERALD(1165, 556, 3, 564, 1, -1, 0),
		RUBY(1176, 554, 5, 564, 1, -1, 0),
		DIAMOND(1180, 557, 10, 564, 1, -1, 0), 
		DRAGONSTONE(1187, 555, 15, 557, 15, 564, 1),
		ONYX(6003, 557, 20, 554, 20, 564, 1);

		int spell, reqRune1, reqAmtRune1, reqRune2, reqAmtRune2, reqRune3, reqAmtRune3;
		private EnchantSpell(int spell, int reqRune1, int reqAmtRune1, int reqRune2, int reqAmtRune2, int reqRune3, int reqAmtRune3) {
			this.spell = spell;
			this.reqRune1 = reqRune1;
			this.reqAmtRune1 = reqAmtRune1;
			this.reqRune2 = reqRune2;
			this.reqAmtRune2 = reqAmtRune2;
			this.reqRune3 = reqRune3;
			this.reqAmtRune3 = reqAmtRune3;
		}

		public int getSpell() {
			return spell;
		}

		public int getReq1() {
			return reqRune1;
		}

		public int getReqAmt1() {
			return reqAmtRune1;
		}

		public int getReq2() {
			return reqRune2;
		}

		public int getReqAmt2() {
			return reqAmtRune2;
		}

		public int getReq3() {
			return reqRune3;
		}

		public int getReqAmt3() {
			return reqAmtRune3;
		}


		public static final Map<Integer, EnchantSpell> ens = new HashMap<Integer, EnchantSpell>();

		public static EnchantSpell forId(int id) {
			return ens.get(id);
		}

		static {
			for (EnchantSpell en : EnchantSpell.values()) {
				ens.put(en.getSpell(), en);
			}
		}

	}

	private boolean hasRunes(int spellID) {
		EnchantSpell ens = EnchantSpell.forId(spellID);
		if (ens.getReq3() == 0) {
			return player.getInventory().hasItemAmount(ens.getReq1(), ens.getReqAmt1()) && player.getInventory().hasItemAmount(ens.getReq2(), ens.getReqAmt2()) && player.getInventory().hasItemAmount(ens.getReq3(), ens.getReqAmt3());
		} else {
			return player.getInventory().hasItemAmount(ens.getReq1(), ens.getReqAmt1()) && player.getInventory().hasItemAmount(ens.getReq2(), ens.getReqAmt2());
		}
	}

	private int getEnchantmentLevel(int spellID) {
		switch (spellID) {
		case 1155: //Lvl-1 enchant sapphire
			return 1;
		case 1165: //Lvl-2 enchant emerald
			return 2;
		case 1176: //Lvl-3 enchant ruby
			return 3;
		case 1180: //Lvl-4 enchant diamond
			return 4;
		case 1187: //Lvl-5 enchant dragonstone
			return 5;
		case 6003: //Lvl-6 enchant onyx
			return 6;
		}
		return 0;
	}
	public void enchantItem(int itemID, int spellID) {
		Enchant enc = Enchant.forId(itemID);
		EnchantSpell ens = EnchantSpell.forId(spellID);
		if (enc == null || ens == null) {
			return;
		}
		if (player.getSkill().getLevels()[Skills.MAGIC] >= enc.getLevelReq()) {
			if (player.getInventory().hasItemAmount(enc.getUnenchanted(), 1)) {
				if (hasRunes(spellID)) {
					if (getEnchantmentLevel(spellID) == enc.getELevel()) {
						player.getInventory().remove(enc.getUnenchanted(), 1);
						player.getInventory().add(enc.getEnchanted(), 1);
						player.getSkill().addExperience(Skills.MAGIC, enc.getXp());
						player.getInventory().remove(ens.getReq1(), ens.getReqAmt1());
						player.getInventory().remove(ens.getReq2(),  ens.getReqAmt2());
						player.getUpdateFlags().sendAnimation(new Animation(enc.getAnim()));
						player.getUpdateFlags().sendGraphic(new Graphic(enc.getGFX(), true));
						if (ens.getReq3() != -1) {
							player.getInventory().remove(ens.getReq3(), ens.getReqAmt3());
						}
						player.send(new SendOpenTab(6));
					} else {
						player.send(new SendMessage("You can only enchant this jewelry using a level-"+enc.getELevel()+" enchantment spell!"));
					}
				} else {
					player.send(new SendMessage("You do not have enough runes to cast this spell."));
				}
			}
		} else {
			player.send(new SendMessage("You need a magic level of at least "+enc.getLevelReq()+" to cast this spell."));	
		}
	}

	public void useMagicOnItem(int itemId, int spellId) {
		switch (spellId) {
		case 1162:
			spellCasting.cast(new LowAlchemy());
			break;
		case 1178:
			spellCasting.cast(new HighAlchemy());
			break;
		case 1155: //Lvl-1 enchant sapphire
		case 1165: //Lvl-2 enchant emerald
		case 1176: //Lvl-3 enchant ruby
		case 1180: //Lvl-4 enchant diamond
		case 1187: //Lvl-5 enchant dragonstone
		case 6003: //Lvl-6 enchant onyx
			enchantItem(itemId, spellId);
			break;			
		case 1173:
			spellCasting.cast(new SuperHeat());
			break;
			
			
			
		}
	}
	
	
}
