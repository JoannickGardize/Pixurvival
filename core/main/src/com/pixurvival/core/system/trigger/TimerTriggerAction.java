package com.pixurvival.core.system.trigger;

import com.pixurvival.core.Action;
import com.pixurvival.core.World;
import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.item.trigger.TimerTrigger;
import com.pixurvival.core.livingEntity.PlayerEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimerTriggerAction implements Action {

    private long playerId;
    private int equipmentSlot;
    private int modCount;
    private int itemId;
    private int triggerIndex;
    private long executionTime;

    @Override
    public void perform(World world) {
        PlayerEntity player = world.getPlayerEntities().get(playerId);
        if (player.getEquipmentModCounts()[equipmentSlot] != modCount) {
            return;
        }
        TimerTrigger trigger = (TimerTrigger) ((EquipableItem) world.getContentPack().getItems().get(itemId)).getTriggers().get(triggerIndex);
        for (Alteration alteration : trigger.getAlterations()) {
            alteration.apply(player, player);
        }
        executionTime += trigger.getInterval();
        world.getActionTimerManager().addActionTimerAtWorldTime(this, executionTime);
    }
}
