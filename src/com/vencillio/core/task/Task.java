package com.vencillio.core.task;

import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.entity.Entity;

public abstract class Task {

	/**
	 * The break type, applies to (@Task)
	 */
	public enum BreakType {
		/**
		 * Never stop this task
		 */
		NEVER,
		/**
		 * Stop this task on movement
		 */
		ON_MOVE
	}

	/**
	 * The stacking type, applies to (@Entity)
	 */
	public enum StackType {
		/**
		 * Always duplicates
		 */
		STACK,
		/**
		 * Never allow duplicates
		 */
		NEVER_STACK
	}

	/**
	 * The delay between executions.
	 */
	private short delay;
	/**
	 * The ticks until an execution
	 */
	private short position = 0;
	/**
	 * The id that will differentiate this task from others
	 */
	private final TaskIdentifier taskId;
	/**
	 * Execute immediately
	 */
	private boolean immediate;
	/**
	 * Has the task been stopped
	 */
	private boolean stopped = false;
	/**
	 * The (@Entity) associated with this task
	 */
	private final Entity entity;

	/**
	 * The (@StackType)
	 */
	private final StackType stackType;

	/**
	 * The (@BreakType)
	 */
	private final BreakType breakType;

	public Task(Entity entity, int delay) {
		this.entity = entity;
		this.delay = (short) delay;
		this.immediate = false;
		breakType = BreakType.NEVER;
		stackType = StackType.STACK;
		taskId = TaskIdentifier.CURRENT_ACTION;
	}

	public Task(Entity entity, int delay, boolean immediate) {
		this.entity = entity;
		this.delay = (short) delay;
		this.immediate = immediate;
		breakType = BreakType.NEVER;
		stackType = StackType.STACK;
		taskId = TaskIdentifier.CURRENT_ACTION;
	}

	public Task(Entity entity, int delay, boolean immediate, StackType stackType, BreakType breakType, TaskIdentifier taskId) {
		this.delay = (short) delay;
		this.immediate = immediate;
		this.entity = entity;
		this.breakType = breakType;
		this.stackType = stackType;
		this.taskId = taskId;
	}

	public Task(int delay) {
		entity = null;
		this.delay = (short) delay;
		this.immediate = false;
		breakType = BreakType.NEVER;
		stackType = StackType.STACK;
		taskId = TaskIdentifier.CURRENT_ACTION;
	}

	public Task(int delay, boolean immediate) {
		entity = null;
		this.delay = (short) delay;
		this.immediate = immediate;
		breakType = BreakType.NEVER;
		stackType = StackType.STACK;
		taskId = TaskIdentifier.CURRENT_ACTION;
	}

	public Task(int delay, boolean immediate, StackType stackType, BreakType breakType, TaskIdentifier taskId) {
		entity = null;
		this.delay = (short) delay;
		this.immediate = immediate;
		this.breakType = breakType;
		this.stackType = stackType;
		this.taskId = taskId;
	}

	/**
	 * Execute this task
	 */
	public abstract void execute();

	public BreakType getBreakType() {
		return breakType;
	}

	public Entity getEntity() {
		return entity;
	}

	public int getPosition() {
		return position;
	}

	public StackType getStackType() {
		return stackType;
	}

	public TaskIdentifier getTaskId() {
		return taskId;
	}

	/**
	 * @return if the task is immediate
	 */
	public boolean immediate() {
		return immediate;
	}

	public boolean isAssociateActive() {
		return entity.isActive();
	}

	public boolean isAssociated() {
		return entity != null;
	}

	/**
	 * Actions on stopping this task
	 */
	public abstract void onStop();

	/**
	 * Resets the location of this (@Task)
	 */
	public void reset() {
		position = 0;
	}

	/**
	 * Ticks the task
	 */
	public void run() {
		position++;
		if (position >= delay) {
			execute();
			reset();
		}
	}

	public void setTaskDelay(int ticks) {
		if (ticks < 0) {
			throw new IllegalArgumentException("Tick amount must be positive.");
		}

		this.delay = (short) ticks;
	}

	/**
	 * Stops the (@Task)
	 */
	public void stop() {
		stopped = true;
	}

	/**
	 * @return if this task has been stopped
	 */
	public boolean stopped() {
		return stopped || entity != null && !entity.isActive() || breakType == BreakType.ON_MOVE && entity.getMovementHandler().isFlagged() && !entity.getMovementHandler().isForced();
	}

	public void onStart() {
	}
}
