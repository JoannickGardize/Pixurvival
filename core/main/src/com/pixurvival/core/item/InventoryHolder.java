package com.pixurvival.core.item;

import com.pixurvival.core.team.TeamMember;

public interface InventoryHolder extends TeamMember {
	Inventory getInventory();
}
