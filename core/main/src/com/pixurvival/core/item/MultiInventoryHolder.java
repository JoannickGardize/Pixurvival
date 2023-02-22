package com.pixurvival.core.item;

import com.pixurvival.core.team.TeamMember;

import java.util.function.Consumer;

public interface MultiInventoryHolder extends TeamMember {
    void forEachInventory(Consumer<Inventory> action);
}
