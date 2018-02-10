package com.vencillio.rs2.entity.item;

public class BasicItemContainer extends ItemContainer {
	public BasicItemContainer(int size) {
		super(size, ItemContainer.ContainerTypes.ALWAYS_STACK, false, false);
	}

	@Override
	public boolean allowZero(int id) {
		return false;
	}

	@Override
	public void onAdd(Item item) {
	}

	@Override
	public void onFillContainer() {
	}

	@Override
	public void onMaxStack() {
	}

	@Override
	public void onRemove(Item item) {
	}

	@Override
	public void update() {
	}
}
