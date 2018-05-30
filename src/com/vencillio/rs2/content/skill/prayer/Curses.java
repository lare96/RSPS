package com.vencillio.rs2.content.skill.prayer;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.entity.*;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;



/**
 * Created by Tanner on 2/4/2018.
 */

public class Curses {
	private static Player p;

	public Curses(Player p) {
		this.p = p;
	}

	public static int random(int range) {
		return (int) (Math.random() * (range + 1));
	}

	public void applyLeeches(final Entity entity) {
		boolean performed = false;

		try {
			/*// check if entity is dead...
			if (p.getPrayer().active(PrayerBook.Prayer.LEECHATTACK) && random(20 - (int) p.curseOdds) == 1) {
				performed = leechAttack(entity);
			} else if (p.getPrayer().active(PrayerBook.Prayer.LEECHDEFENCE) && random(23 - (int) p.curseOdds) == 1) {
				performed = leechDefence(entity);
			} else if (p.getPrayer().active(PrayerBook.Prayer.LEECHSTRENGTH) && random(26 - (int) p.curseOdds) == 1) {
				performed = leechStrength(entity);
			} else if (p.getPrayer().active(PrayerBook.Prayer.LEECHRANGED) && random(29 - (int) p.curseOdds) == 1) {
				performed = leechRanged(entity);
			} else if (p.getPrayer().active(PrayerBook.Prayer.LEECHMAGIC) && random(32 - (int) p.curseOdds) == 1) {
				performed = leechMagic(entity);
			} else if (entity instanceof Player && (p.getPrayer().active(PrayerBook.Prayer.LEECHSPECIAL) && random(32) == 1)) {
				performed = leechSpecial(entity.getPlayer());
			}

			if (p.getPrayer().active(PrayerBook.Prayer.SAPWARRIOR) && random(10 - (int) p.curseOdds) == 1) {
				performed = sapWarrior(entity);
			} else if (p.getPrayer().active(PrayerBook.Prayer.SAPRANGE) && random(13 - (int) p.curseOdds) == 1) {
				performed = sapRanged(entity);
			} else if (p.getPrayer().active(PrayerBook.Prayer.SAPMAGE) && random(16 - (int) p.curseOdds) == 1) {
				performed = sapMagic(entity);
			} else if (entity instanceof Player && (p.getPrayer().active(PrayerBook.Prayer.SAPSPIRIT) && random(20) == 1)) {
				performed = sapSpirit((Player) entity);
			}*/
		} catch (Exception ex) {
			p.curseOdds = 0.0;
		}

		if (performed) {
			p.curseOdds = 0.0;
		} else {
			p.curseOdds += 0.3;
		}
	}

	private boolean sapWarrior(final Entity entity) {
		if (entity.attackMultiplier <= 0.90 && entity.defenceMultiplier <= 0.90 && entity.strengthMultiplier <= 0.90) {
			return false;
		}
		final int pX = p.getX();
		final int pY = p.getY();
		final int oX = entity.getX();
		final int oY = entity.getY();
		final int offX = (pY - oY) * -1;
		final int offY = (pX - oX) * -1;
		p.send(new SendMessage("You drain your opponents Attack, Defence and Strength."));
		p.getUpdateFlags().sendAnimation(new Animation(12569));
		p.getUpdateFlags().sendGraphic(new Graphic(2214));

		TaskQueue.queue(new Task(0) {
			int delay = 0;

			public void execute() {
				if (delay == 0) {
					//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 40, 2215, 25, 25, 0, 0);
					World.sendProjectile(new Projectile(2215, 1, 0, 40, 25, 25, 50), new Location(pX, pY), 0, (byte) oX, (byte) oY);
				} else if (delay == 2) {
					entity.getUpdateFlags().sendGraphic(new Graphic(2216));
					if (entity.attackMultiplier > 0.90) {
						entity.attackMultiplier -= 0.2;
						if (entity.attackMultiplier < 0.90) {
							entity.attackMultiplier = 0.90;
						}
					}

					if (entity.defenceMultiplier > 0.90) {
						entity.defenceMultiplier -= 0.2;
						if (entity.defenceMultiplier < 0.90) {
							entity.defenceMultiplier = 0.90;
						}
					}

					if (entity.strengthMultiplier > 0.90) {
						entity.strengthMultiplier -= 0.2;
						if (entity.strengthMultiplier < 0.90) {
							entity.strengthMultiplier = 0.90;
						}
					}
					stop();
				}
				delay++;
			}

			@Override
			public void onStop() {

			}
		});
		return true;
	}

	private boolean sapRanged(final Entity entity) {
		if (entity.rangedMultiplier <= 0.90 && entity.defenceMultiplier <= 0.90) {
			return false;
		}
		final int pX = p.getX();
		final int pY = p.getY();
		final int oX = entity.getX();
		final int oY = entity.getY();
		final int offX = (pY - oY) * -1;
		final int offY = (pX - oX) * -1;
		p.send(new SendMessage("You drain your opponents Ranged and Defence."));
		p.getUpdateFlags().sendAnimation(new Animation(12569));
		p.getUpdateFlags().sendGraphic(new Graphic(2217));
		TaskQueue.queue(new Task(0) {
			int delay = 0;

			public void execute() {
				if (delay == 0) {
					//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 40, 2218, 25, 25, 0, 0);
					World.sendProjectile(new Projectile(2218, 1, 0, 40, 25, 25, 50), new Location(pX,pY), 0, (byte)oX, (byte)oY);
				} else if (delay == 2) {
					entity.getUpdateFlags().sendGraphic(new Graphic(2219));
					if (entity.rangedMultiplier > 0.90) {
						entity.rangedMultiplier -= 0.2;
						if (entity.rangedMultiplier < 0.90) {
							entity.rangedMultiplier = 0.90;
						}
					}

					if (entity.defenceMultiplier > 0.90) {
						entity.defenceMultiplier -= 0.2;
						if (entity.defenceMultiplier < 0.90) {
							entity.defenceMultiplier = 0.90;
						}
					}
					stop();
				}
				delay++;
			}

			@Override
			public void onStop() {

			}
		});
		return true;
	}

	private boolean sapMagic(final Entity entity) {
		if (entity.attackMultiplier <= 0.90 && entity.defenceMultiplier <= 0.90) {
			return false;
		}
		final int pX = p.getX();
		final int pY = p.getY();
		final int oX = entity.getX();
		final int oY = entity.getY();
		final int offX = (pY - oY) * -1;
		final int offY = (pX - oX) * -1;
		p.send(new SendMessage("You drain your opponents Magic and Defence."));
		p.getUpdateFlags().sendAnimation(new Animation(12569));
		p.getUpdateFlags().sendGraphic(new Graphic(2220));
		TaskQueue.queue(new Task(0) {
			int delay = 0;

			public void execute() {
				if (delay == 0) {
					//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 40, 2221, 25, 25, 0, 0);
					World.sendProjectile(new Projectile(2221, 1, 0, 40, 25, 25, 50), new Location(pX, pY), 0, (byte) oX, (byte) oY);
				} else if (delay == 2) {
					entity.getUpdateFlags().sendGraphic(new Graphic(2222));
					if (entity.attackMultiplier > 0.90) {
						entity.attackMultiplier -= 0.2;
						if (entity.attackMultiplier < 0.90) {
							entity.attackMultiplier = 0.90;
						}
					}

					if (entity.defenceMultiplier > 0.90) {
						entity.defenceMultiplier -= 0.2;
						if (entity.defenceMultiplier < 0.90) {
							entity.defenceMultiplier = 0.90;
						}
					}
					stop();
				}
				delay++;
			}

			@Override
			public void onStop() {

			}
		});
		return true;
	}

	private boolean sapSpirit(final Player otherPlayer) {
		if (otherPlayer.getSpecialAttack().getAmount() <= 1.0) { //Is this right?
			return false;
		}
		final int pX = p.getX();
		final int pY = p.getY();
		final int oX = otherPlayer.getX();
		final int oY = otherPlayer.getY();
		final int offX = (pY - oY) * -1;
		final int offY = (pX - oX) * -1;
		p.send(new SendMessage("You drain your opponents Special Attack by 10%."));
		p.getUpdateFlags().sendAnimation(new Animation(12569));
		p.getUpdateFlags().sendGraphic(new Graphic(2223));
		TaskQueue.queue(new Task(0) {
			int delay = 0;

			public void execute() {
				if (delay == 0) {
					//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 40, 2224, 25, 25, 0, 0);
					World.sendProjectile(new Projectile(2224, 1, 0, 40, 25, 25, 50), new Location(pX, pY), 0, (byte) oX, (byte) oY);
				} else if (delay == 2) {
					otherPlayer.getUpdateFlags().sendGraphic(new Graphic(2225));
					if (otherPlayer.getSpecialAttack().getAmount() > 0.0) {
						otherPlayer.getSpecialAttack().deduct(1);
						if (otherPlayer.getSpecialAttack().getAmount() <= 0.0) {
							otherPlayer.getSpecialAttack().setSpecialAmount(0);
						}
					}
					stop();
				}
				delay++;
			}

			@Override
			public void onStop() {

			}
		});
		return true;
	}

	public void deflectAttack(Entity entity, int damage) {
		/*if (!p.getPrayer().active(PrayerBook.Prayer.DEFLECTMELEE) && !p.getPrayer().active(PrayerBook.Prayer.DEFLECTMISSILES) && !p.getPrayer().active(PrayerBook.Prayer.DEFLECTMAGIC)) {
			return;
		}
		if (random(2) > 0) {
			return;
		}
		int deflectedDamage = (int) (damage * .10);
		if (deflectedDamage <= 0) {
			return;
		}
		if (p.getPrayer().active(PrayerBook.Prayer.DEFLECTMELEE)) {
			p.getUpdateFlags().sendGraphic(new Graphic(2227)); // melee deflect
		} else if (p.getPrayer().active(PrayerBook.Prayer.DEFLECTMISSILES)) {
			p.getUpdateFlags().sendGraphic(new Graphic(2229)); // range deflect
		} else if (p.getPrayer().active(PrayerBook.Prayer.DEFLECTMAGIC)) {
			p.getUpdateFlags().sendGraphic(new Graphic(2230)); // mage deflect
		}
		p.getUpdateFlags().sendAnimation(new Animation(12573));
		Hit h = new Hit(deflectedDamage);
		entity.hit(h);*/
		//entity.setHitUpdateRequired(true);
		//entity.setUpdateRequired(true);
	}

	public void applySoulSplit(final Player otherPlayer, int damage) {//PvP
		int maxHp = p.getMaxLevels()[3];

		if (p.getSkill().getLevels()[3] < maxHp) {
			int healAmount = (int) (damage * 0.4);
			if (p.getSkill().getLevels()[3] + healAmount > maxHp) {
				p.getLevels()[3] = p.getMaxLevels()[3];
			} else {
				p.getLevels()[3] += healAmount;
			}
			p.getSkill().update(3);

			if (otherPlayer.getLevels()[5] >= 1) {
				otherPlayer.getLevels()[5]--;
				otherPlayer.getSkill().update(5);
			}

			final int pX = p.getX();
			final int pY = p.getY();
			final int oX = otherPlayer.getX();
			final int oY = otherPlayer.getY();
			int offX = (pY - oY) * -1;
			int offY = (pX - oX) * -1;
			//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 20, 2263, 15, 10, -p.oldPlayerIndex - 1, 0); //-pOldPlayerIndex-1 = lock-on ?
			World.sendProjectile(new Projectile(2263, 1, 0, 20, 15, 10, 50), new Location(pX,pY), 0, (byte)oX, (byte)oY);
			TaskQueue.queue(new Task(0) {
				public void execute() {
					otherPlayer.getUpdateFlags().sendGraphic(new Graphic(2264));
					int offX2 = (oY - pY) * -1;
					int offY2 = (oX - pX) * -1;
					//p.getPA().createPlayersProjectile(oX, oY, offX2, offY2, 50, 20, 2263, 15, 10, -p.playerId - 1, 0);
					World.sendProjectile(new Projectile(2263, 1, 0, 20, 15, 10, 50), new Location(oX,oY), 0, (byte)offX2, (byte)offY2);
					stop();
				}

				@Override
				public void onStop() {

				}
			});
		}
	}

	public static void applySoulSplit(final Mob npc, int damage, Player player) {//PvE
		int maxHp = player.getMaxLevels()[3];

		if (player.getSkill().getLevels()[3] < maxHp) {
			int healAmount = (int) (damage * 0.2);
			if (player.getSkill().getLevels()[3] + healAmount > maxHp) {
				player.getLevels()[3] = player.getMaxLevels()[3];
			} else {
				player.getLevels()[3] += healAmount;
			}
			player.getSkill().update(3);

			final int pX = player.getX();
			final int pY = player.getY();
			final int oX = npc.getX();
			final int oY = npc.getY();
			System.out.println("px: " + pX + " py: " + pY + " ox: " + oX + "oy: " + oY);
			int offX = (pY - oY) * -1;
			int offY = (pX - oX) * -1;
			//player.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 25, 2263, 15, 10, -player.oldNpcIndex - 1, 0);
			World.sendProjectile(new Projectile(2263, 1, 0, 25, 15, 10, 50), new Location(pX,pY), 0, (byte)oX, (byte)oY);
			TaskQueue.queue(new Task(0) {
				public void execute() {
					npc.getUpdateFlags().sendGraphic(new Graphic(2264));
					int offX2 = (oY - pY) * -1;
					int offY2 = (oX - pX) * -1;
					//player.getPA().createPlayersProjectile(oX, oY, offX2, offY2, 50, 25, 2263, 15, 10, -player.playerId - 1, 0);
					World.sendProjectile(new Projectile(2263, 1, 0, 25, 15, 10, 50), new Location(oX,oY), 0, (byte)offX2, (byte)offY2);
					stop();
				}

				@Override
				public void onStop() {

				}
			});
		}
	}

	private boolean leechAttack(final Entity entity) {
		if (checkLeechMultiplier(p.attackMultiplier, entity.attackMultiplier)) {
			return false;
		}
		final int pX = p.getX();
		final int pY = p.getY();
		final int oX = entity.getX();
		final int oY = entity.getY();
		final int offX = (pY - oY) * -1;
		final int offY = (pX - oX) * -1;
		p.send(new SendMessage("You leech your opponent's attack."));
		p.getUpdateFlags().sendAnimation(new Animation(12575));
		TaskQueue.queue(new Task(0) {
			int delay = 0;

			public void execute() {
				if (delay == 0) {
					//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 40, 2231, 25, 35, 0, 0);
					World.sendProjectile(new Projectile(2231, 1, 0, 40, 25, 35, 50), new Location(pX, pY), 0, (byte) oX, (byte) oY);
				} else if (delay == 2) {
					entity.getUpdateFlags().sendGraphic(new Graphic(2232));
					if (p.attackMultiplier < 1.10) {
						p.attackMultiplier += 0.2;
						if (p.attackMultiplier > 1.10) {
							p.attackMultiplier = 1.10;
						}
					}
					if (entity.attackMultiplier > 0.85) {
						entity.attackMultiplier -= 0.2;
						if (entity.attackMultiplier < 0.85) {
							entity.attackMultiplier = 0.85;
						}
					}
					stop();
				}
				delay++;
			}

			@Override
			public void onStop() {

			}
		});
		return true;
	}

	private boolean leechRanged(final Entity entity) {
		if (checkLeechMultiplier(p.rangedMultiplier, entity.rangedMultiplier)) {
			return false;
		}
		final int pX = p.getX();
		final int pY = p.getY();
		final int oX = entity.getX();
		final int oY = entity.getY();
		final int offX = (pY - oY) * -1;
		final int offY = (pX - oX) * -1;
		p.send(new SendMessage("You leech your opponent's range."));
		p.getUpdateFlags().sendAnimation(new Animation(12575));
		TaskQueue.queue(new Task(0) {
			int delay = 0;

			public void execute() {
				if (delay == 1) {
					//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 40, 2236, 25, 35, 0, 0);
					World.sendProjectile(new Projectile(2236, 1, 0, 40, 25, 35, 50), new Location(pX,pY), 0, (byte)oX, (byte)oY);
				} else if (delay == 2) {
					entity.getUpdateFlags().sendGraphic(new Graphic(2238));
					if (p.rangedMultiplier < 1.10) {
						p.rangedMultiplier += 0.2;
						if (p.rangedMultiplier > 1.10) {
							p.rangedMultiplier = 1.10;
						}
					}
					if (entity.rangedMultiplier > 0.85) {
						entity.rangedMultiplier -= 0.2;
						if (entity.rangedMultiplier < 0.85) {
							entity.rangedMultiplier = 0.85;
						}
					}
					stop();
				}
				delay++;
			}

			@Override
			public void onStop() {

			}
		});
		return true;
	}

	private boolean leechMagic(final Entity entity) {
		if (checkLeechMultiplier(p.magicMultiplier, entity.magicMultiplier)) {
			return false;
		}
		final int pX = p.getX();
		final int pY = p.getY();
		final int oX = entity.getX();
		final int oY = entity.getY();
		final int offX = (pY - oY) * -1;
		final int offY = (pX - oX) * -1;
		p.send(new SendMessage("You leech your opponent's magic."));
		p.getUpdateFlags().sendAnimation(new Animation(12575));
		TaskQueue.queue(new Task(0) {
			int delay = 0;

			public void execute() {
				if (delay == 1) {
					//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 40, 2240, 25, 35, 0, 0);
					World.sendProjectile(new Projectile(2240, 1, 0, 40, 25, 35, 50), new Location(pX,pY), 0, (byte)oX, (byte)oY);
				} else if (delay == 2) {
					entity.getUpdateFlags().sendGraphic(new Graphic(2242));
					if (p.magicMultiplier < 1.10) {
						p.magicMultiplier += 0.2;
						if (p.magicMultiplier > 1.10) {
							p.magicMultiplier = 1.10;
						}
					}
					if (entity.magicMultiplier > 0.85) {
						entity.magicMultiplier -= 0.2;
						if (entity.magicMultiplier < 0.85) {
							entity.magicMultiplier = 0.85;
						}
					}
					stop();
				}
				delay++;
			}

			@Override
			public void onStop() {

			}
		});
		return true;
	}

	private boolean leechDefence(final Entity entity) {
		if (checkLeechMultiplier(p.defenceMultiplier, entity.defenceMultiplier)) {
			return false;
		}
		final int pX = p.getX();
		final int pY = p.getY();
		final int oX = entity.getX();
		final int oY = entity.getY();
		final int offX = (pY - oY) * -1;
		final int offY = (pX - oX) * -1;
		p.send(new SendMessage("You leech your opponent's defence."));
		p.getUpdateFlags().sendAnimation(new Animation(12575));
		TaskQueue.queue(new Task(0) {
			int delay = 0;

			public void execute() {
				if (delay == 1) {
					//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 40, 2244, 25, 35, 0, 0);
					World.sendProjectile(new Projectile(2244, 1, 0, 40, 25, 35, 50), new Location(pX,pY), 0, (byte)oX, (byte)oY);
				} else if (delay == 2) {
					entity.getUpdateFlags().sendGraphic(new Graphic(2246));
					if (p.defenceMultiplier < 1.10) {
						p.defenceMultiplier += 0.2;
						if (p.defenceMultiplier > 1.10) {
							p.defenceMultiplier = 1.10;
						}
					}
					if (entity.defenceMultiplier > 0.85) {
						entity.defenceMultiplier -= 0.2;
						if (entity.defenceMultiplier < 0.85) {
							entity.defenceMultiplier = 0.85;
						}
					}
					stop();
				}
				delay++;
			}

			@Override
			public void onStop() {

			}
		});
		return true;
	}

	private boolean leechStrength(final Entity entity) {
		if (checkLeechMultiplier(p.strengthMultiplier, entity.strengthMultiplier)) {
			return false;
		}
		final int pX = p.getX();
		final int pY = p.getY();
		final int oX = entity.getX();
		final int oY = entity.getY();
		final int offX = (pY - oY) * -1;
		final int offY = (pX - oX) * -1;
		p.send(new SendMessage("You leech your opponent's strength."));
		p.getUpdateFlags().sendAnimation(new Animation(12575));
		TaskQueue.queue(new Task(0) {
			int delay = 0;

			public void execute() {
				if (delay == 1) {
					//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 40, 2248, 25, 35, 0, 0);
					World.sendProjectile(new Projectile(2248, 1, 0, 40, 25, 35, 50), new Location(pX,pY), 0, (byte)oX, (byte)oY);
				} else if (delay == 2) {
					entity.getUpdateFlags().sendGraphic(new Graphic(2250));
					if (p.strengthMultiplier < 1.10) {
						p.strengthMultiplier += 0.2;
						if (p.strengthMultiplier > 1.10) {
							p.strengthMultiplier = 1.10;
						}
					}
					if (entity.strengthMultiplier > 0.85) {
						entity.strengthMultiplier -= 0.2;
						if (entity.strengthMultiplier < 0.85) {
							entity.strengthMultiplier = 0.85;
						}
					}
					stop();
				}
				delay++;
			}

			@Override
			public void onStop() {

			}
		});
		return true;
	}

	private boolean leechSpecial(final Player otherPlayer) {
		if (p.getSpecialAttack().getAmount() >= 10.0 || otherPlayer.getSpecialAttack().getAmount() <= 0.0) {
			return false;
		}
		final int pX = p.getX();
		final int pY = p.getY();
		final int oX = otherPlayer.getX();
		final int oY = otherPlayer.getY();
		final int offX = (pY - oY) * -1;
		final int offY = (pX - oX) * -1;
		p.send(new SendMessage("You leech your opponent's special attack."));
		p.getUpdateFlags().sendAnimation(new Animation(12575));
		TaskQueue.queue(new Task(0) {
			int delay = 0;

			public void execute() {
				if (delay == 1) {
					//p.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 40, 2256, 25, 35, 0, 0);
					World.sendProjectile(new Projectile(2256, 1, 0, 40, 25, 35, 50), new Location(pX,pY), 0, (byte)oX, (byte)oY);
				} else if (delay == 2) {
					otherPlayer.getUpdateFlags().sendGraphic(new Graphic(2258));
					p.getSpecialAttack().add(1);
					otherPlayer.getSpecialAttack().deduct(1);
					otherPlayer.send(new SendMessage("Your special attack has been leeched."));
					//otherPlayer.getItems().addSpecialBar(otherPlayer.equipment[Player.WEAPON].getId());
					//p.getItems().addSpecialBar(p.equipment[Player.WEAPON].getId()); //What is the purpose of this
					stop();
				}
				delay++;
			}

			@Override
			public void onStop() {

			}
		});
		return true;
	}

	private boolean checkLeechMultiplier(double d, double d2) {
		if ((d >= 1.10 || d <= 0.85) && (d2 >= 1.10 || d2 <= 0.85)) {
			return true;
		}
		return false;
	}

	private double checkMultiplier(double d) {
		if (d > 1.0) {
			d -= 0.1;
			if (d < 1.0) {
				d = 1.0;
			}
		} else if (d < 1.0) {
			d += 0.1;
			if (d > 1.0) {
				d = 1.0;
			}
		}
		return d;
	}

	public void checkMultipliers() {
		/*if (!p.getPrayer().active(PrayerBook.Prayer.LEECHATTACK) && !p.getPrayer().active(PrayerBook.Prayer.SAPWARRIOR)) {
			p.attackMultiplier = checkMultiplier(p.attackMultiplier);
		}
		if (!p.getPrayer().active(PrayerBook.Prayer.LEECHRANGED) && !p.getPrayer().active(PrayerBook.Prayer.SAPRANGE)) {
			p.rangedMultiplier = checkMultiplier(p.rangedMultiplier);
		}
		if (!p.getPrayer().active(PrayerBook.Prayer.LEECHMAGIC) && !p.getPrayer().active(PrayerBook.Prayer.SAPMAGE)) {
			p.magicMultiplier = checkMultiplier(p.magicMultiplier);
		}
		if (!p.getPrayer().active(PrayerBook.Prayer.LEECHDEFENCE) && !p.getPrayer().active(PrayerBook.Prayer.SAPDEFENCE)) {
			p.defenceMultiplier = checkMultiplier(p.defenceMultiplier);
		}
		if (!p.getPrayer().active(PrayerBook.Prayer.LEECHSTRENGTH) && !p.getPrayer().active(PrayerBook.Prayer.SAPSTRENGTH)) {
			p.strengthMultiplier = checkMultiplier(p.strengthMultiplier);
		}
		//p.multiplierDelay = System.currentTimeMillis();/not sure what this is currently*/
	}
}
