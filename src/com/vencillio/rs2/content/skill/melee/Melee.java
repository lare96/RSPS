package com.vencillio.rs2.content.skill.melee;

public class Melee {
	private boolean veracEffectActive = false;
	private boolean guthanEffectActive = false;
	private boolean toragEffectActive = false;

	public void afterCombat() {
		if (veracEffectActive)
			veracEffectActive = false;
	}

	public boolean isGuthanEffectActive() {
		return guthanEffectActive;
	}

	public boolean isToragEffectActive() {
		return toragEffectActive;
	}

	public boolean isVeracEffectActive() {
		return veracEffectActive;
	}

	public void setGuthanEffectActive(boolean guthanEffectActive) {
		this.guthanEffectActive = guthanEffectActive;
	}

	public void setToragEffectActive(boolean toragEffectActive) {
		this.toragEffectActive = toragEffectActive;
	}

	public void setVeracEffectActive(boolean veracEffectActive) {
		this.veracEffectActive = veracEffectActive;
	}
}
