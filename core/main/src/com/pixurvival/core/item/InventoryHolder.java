package com.pixurvival.core.item;

import java.util.function.Consumer;

import com.pixurvival.core.team.TeamMember;

public interface InventoryHolder extends TeamMember, MultiInventoryHolder {
	Inventory getInventory();

	@Override
	default void forEachInventory(Consumer<Inventory> action) {
		action.accept(getInventory());
	}
}
