package com.pixurvival.core.item;

import com.pixurvival.core.team.TeamMember;

import java.util.function.Consumer;

public interface InventoryHolder extends TeamMember, MultiInventoryHolder {
    Inventory getInventory();

    @Override
    default void forEachInventory(Consumer<Inventory> action) {
        action.accept(getInventory());
    }
}
