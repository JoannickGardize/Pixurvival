package com.pixurvival.core.item;

import java.util.function.Consumer;

import com.pixurvival.core.team.TeamMember;

public interface MultiInventoryHolder extends TeamMember {
	void forEachInventory(Consumer<Inventory> action);
}
