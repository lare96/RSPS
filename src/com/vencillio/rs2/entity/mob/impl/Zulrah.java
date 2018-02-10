package com.vencillio.rs2.entity.mob.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendProjectile;

/**
 * Handles Zulrah Boss
 * @author Daniel
 *
 */
public class Zulrah extends Mob {
	
	/**
	 * Start of Zulrah
	 * @param player
	 * @param location
	 */
	public Zulrah(Player player, Location location) {
		super(player, 2042, false, false, false, location);
		TIME = System.currentTimeMillis();
		if (getOwner() != null) {
			getCombat().setAttack(getOwner());			
			getOwner().setHitZulrah(true);
		}
	}
	
	/**
	 * Zulrah kill time
	 */
	private long TIME;
	
	/**
	 * Snakeling list
	 */
	private List<Mob> SNAKELING = new ArrayList<>();
	
	/**
	 * Locations that zulrah can spawn too
	 */
	private Location[] LOCATIONS = { new Location(2266, 3072, getOwner().getZ()), new Location(2276, 3074, getOwner().getZ()), new Location(2273, 3064, getOwner().getZ()) };
	
	@Override
	public void onHit(Entity entity, Hit hit) {
		if (entity == null) {
			return;
		}
		
		int random = Utility.random(11);
		if (random == 0 && getOwner().isHitZulrah()) {
			move();
		} else if (random == 5) {
			snakes();
		}
	
	}
	
	@Override
	public void hit(Hit hit) {
		if (getOwner() == null) {
			return;
		}
		
		if (isDead()) {
			return;
		}
		
		super.hit(hit);
	}
	
	@Override
	public void onDeath() {
		for (Mob snakes : SNAKELING) {
			if (!snakes.isDead()) {
				snakes.remove();
			}
		}
		SNAKELING.clear();
		getOwner().send(new SendMessage("Fight duration: @red@" + new SimpleDateFormat("m:ss").format(System.currentTimeMillis() - TIME) + "</col>."));
		getOwner().getProperties().addProperty(this, 1);
		AchievementHandler.activateAchievement(getOwner(), AchievementList.KILL_100_ZULRAHS, 1);
	}
	
	/**
	 * Grabs the next valid Zulrha form
	 * @return
	 */
	private int getNextForm() {
		List<Integer> possible = new ArrayList<>();
		possible.addAll(Arrays.asList(2042, 2043, 2044));
		possible.remove(Integer.valueOf(getId()));
		return Utility.randomElement(possible);
	}
	
	/**
	 * Handles Zulrah moving
	 */
	private void move() {
		setCanAttack(false);
		getOwner().setHitZulrah(false);
		getOwner().getCombat().reset();
		TaskQueue.queue(new Task(1) {
			@Override
			public void execute() {
				getUpdateFlags().sendAnimation(new Animation(5072));
				getOwner().send(new SendMessage("Zulrah dives into the swamp..."));
				getUpdateFlags().isUpdateRequired();
				stop();
			}
			@Override
			public void onStop() {
			}
		});		
		TaskQueue.queue(new Task(3) {
			@Override
			public void execute() {
				transform(getNextForm());
				teleport(Utility.randomElement(LOCATIONS));
				getUpdateFlags().isUpdateRequired();
				getUpdateFlags().sendAnimation(new Animation(5071));
				getUpdateFlags().faceEntity(getOwner().getIndex());
				getUpdateFlags().isUpdateRequired();
				stop();
			}
			@Override
			public void onStop() {
				getCombat().setAttack(getOwner());
				setCanAttack(true);
				getOwner().setHitZulrah(true);
			}
		});			
	}
	
	/**
	 * Handles spawning snakes
	 */
	private void snakes() {
		List<Location> possibleLocations = new ArrayList<>();
		possibleLocations.addAll(Arrays.asList(new Location(2263, 3075), new Location(2263, 3071), new Location(2268, 3069), new Location(2273, 3071), new Location(2273, 3077)));
		TaskQueue.queue(new Task(3) {
		int cycles = -1;
			@Override
			public void execute() {
				if (++cycles == 3) {
					stop();
					return;
				}		
				Location next = Utility.randomElement(possibleLocations);
				possibleLocations.remove(next);
				getUpdateFlags().sendFaceToDirection(next);
				getUpdateFlags().sendAnimation(new Animation(5068));
				final int offsetX = next.getX() - getX();
				final int offsetY = next.getY() - getY();
				Projectile projectile = new Projectile(1047, 1, 10, 85, 40, 10, 20);
				getOwner().send(new SendProjectile(getOwner(), projectile, getLocation(), -1, (byte) offsetX, (byte) offsetY));
				spawn(next.getX(), next.getY());
				stop();
			}
			@Override
			public void onStop() {
			}
		});	
	}
	
	/**
	 * Spawns snakes
	 * @param x
	 * @param y
	 */
	private void spawn(int x, int y) {
		TaskQueue.queue(new Task(1) {
			@Override
			public void execute() {
				Mob m = new Mob(getOwner(), 2045, false, false, false, new Location(x, y, getOwner().getZ()));
				m.getFollowing().setIgnoreDistance(true);
				m.getCombat().setAttack(getOwner());
				SNAKELING.add(m);
				stop();
			}
			@Override
			public void onStop() {
			}
		});	
	}

}
