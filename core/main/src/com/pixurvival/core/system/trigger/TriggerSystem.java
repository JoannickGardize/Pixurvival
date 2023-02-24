package com.pixurvival.core.system.trigger;

import com.pixurvival.core.ActionTimerManager;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.trigger.EquippedTrigger;
import com.pixurvival.core.contentPack.item.trigger.TimerTrigger;
import com.pixurvival.core.contentPack.item.trigger.Trigger;
import com.pixurvival.core.contentPack.item.trigger.UnequippedTrigger;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.system.GameSystem;
import com.pixurvival.core.system.Inject;
import com.pixurvival.core.system.interest.EquipmentInterest;
import com.pixurvival.core.system.interest.WorldUpdateInterest;
import lombok.Setter;

import java.util.*;

@Setter
public class TriggerSystem implements GameSystem, WorldUpdateInterest, EquipmentInterest {

    @Inject
    private ActionTimerManager actionTimerManager;

    @Override
    public boolean isRequired(World world) {
        for (Item item : world.getContentPack().getItems()) {
            if (item instanceof EquipableItem && !((EquipableItem) item).getTriggers().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initialize(World world) {
        world.getContentPack().getItems().forEach(item -> {
            if (item instanceof EquipableItem) {
                EquipableItem equipableItem = (EquipableItem) item;
                Map<Class<? extends Trigger>, List<Trigger>> triggersByType = new IdentityHashMap<>();
                for (int i = 0; i < equipableItem.getTriggers().size(); i++) {
                    Trigger t = equipableItem.getTriggers().get(i);
                    t.setIndex(i);
                    List<Trigger> triggers = triggersByType.computeIfAbsent(t.getClass(), k -> new ArrayList<>());
                    triggers.add(t);
                }
                equipableItem.setTriggersByType(triggersByType);
            }
        });
    }

    @Override
    public void update(float deltaTime) {
        // TODO
    }

    @Override
    public void equipped(PlayerEntity player, int equipmentIndex, EquipableItem item) {
        apply(player, item.getTriggersByType().get(EquippedTrigger.class));
        for (Trigger trigger : item.getTriggersByType().getOrDefault(TimerTrigger.class, Collections.emptyList())) {
            TimerTrigger timerTrigger = (TimerTrigger) trigger;
            TimerTriggerAction timerTriggerAction = new TimerTriggerAction();
            timerTriggerAction.setPlayerId(player.getId());
            timerTriggerAction.setEquipmentSlot(equipmentIndex);
            timerTriggerAction.setModCount(player.getEquipmentModCounts()[equipmentIndex]);
            timerTriggerAction.setItemId(item.getId());
            timerTriggerAction.setTriggerIndex(timerTrigger.getIndex());
            timerTriggerAction.setExecutionTime(timerTrigger.getStartDelay() + player.getWorld().getTime().getTimeMillis());
            actionTimerManager.addActionTimerAtWorldTime(timerTriggerAction, timerTriggerAction.getExecutionTime());
        }
    }

    @Override
    public void unequipped(PlayerEntity player, int equipmentIndex, EquipableItem item) {
        apply(player, item.getTriggersByType().get(UnequippedTrigger.class));
    }

    private void apply(PlayerEntity player, Collection<Trigger> triggers) {
        if (triggers != null) {
            for (Trigger trigger : triggers) {
                trigger.getAlterations().forEach(a -> a.apply(player, player));
            }
        }
    }
}
