package com.vencillio.rs2.entity.mob.impl;

import java.util.Random;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.mob.Mob;

/**
 * Handles the Giant Mole boss
 * @author Daniel
 *
 */
public class GiantMole extends Mob {
	
	/**
	 * Initializing Giant Mole
	 */
	public GiantMole() {
		super(5779, true, new Location(1759, 5153, 0));
	}
	
	@Override
	public void hit(Hit hit) {
	
		if (isDead() || !isCanAttack()) {
			return;
		}
		
		int random = Utility.random(15);
		if (random == 0) {
			special();
		}
		
		super.hit(hit);
	
	}
	
	public void special() {
	
		setCanAttack(false);
	
		int[] COORDS_X = { 1760, 1781, 1753, 1738, 1746, 1741, 1738, 1779, 1754, 1738, 1751, 1779, 1770, 1779 };
		int[] COORDS_Y = { 5164, 5151, 5150, 5155, 5171, 5187, 5209, 5182, 5206, 5225, 5174, 5208, 5228, 5188 };
		
		Random r = new Random();
		int next = r.nextInt(COORDS_X.length);

		TaskQueue.queue(new Task(1) {
		
			@Override
			public void execute() {
				getUpdateFlags().sendAnimation(new Animation(3314));
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
				getUpdateFlags().isUpdateRequired();
				stop();
			}

			@Override
			public void onStop() {
			}
		});
		
		TaskQueue.queue(new Task(2) {
		
			@Override
			public void execute() {
				teleport(new Location(COORDS_X[next], COORDS_Y[next], 0));
				getUpdateFlags().isUpdateRequired();
				stop();
			}

			@Override
			public void onStop() {
				setCanAttack(true);
			}
		});
	}
	
}
