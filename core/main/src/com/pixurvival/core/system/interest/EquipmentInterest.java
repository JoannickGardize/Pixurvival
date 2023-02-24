package com.pixurvival.core.system.interest;

import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.livingEntity.PlayerEntity;

public interface EquipmentInterest extends Interest {

    void equipped(PlayerEntity player, int equipmentSlot, EquipableItem item);

    void unequipped(PlayerEntity player, int equipmentSlot, EquipableItem item);
}
