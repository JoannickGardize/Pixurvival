package com.pixurvival.gdxcore.input.processor;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.message.playerRequest.PlayerEquipmentAbilityRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class EquipmentAbilityProcessor implements InputActionProcessor {

    /**
     * Static because common to all abilities
     */
    private static List<EquipmentAbilityType> abilityStack = new ArrayList<>();

    private EquipmentAbilityType equipmentAbilityType;

    @Override
    public void buttonDown() {
        sendAbilityRequest(equipmentAbilityType);
        abilityStack.add(equipmentAbilityType);
    }

    private void sendAbilityRequest(EquipmentAbilityType equipmentAbilityTypeToSend) {
        PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
        World world = PixurvivalGame.getClient().getWorld();
        com.pixurvival.core.util.Vector2 targetPosition = world.getType().isClient() ? myPlayer.getTargetPosition() : null;
        PixurvivalGame.getClient()
                .sendAction(new PlayerEquipmentAbilityRequest(equipmentAbilityTypeToSend, myPlayer.getPosition().angleToward(targetPosition), myPlayer.getPosition().distance(targetPosition)));
    }

    @Override
    public void buttonUp() {
        if (abilityStack.isEmpty()) {
            return;
        }
        EquipmentAbilityType currentAbility = abilityStack.get(abilityStack.size() - 1);
        abilityStack.removeIf(e -> e == equipmentAbilityType);
        if (abilityStack.isEmpty()) {
            PixurvivalGame.getClient().sendAction(new PlayerEquipmentAbilityRequest(null, 0, 0));
        } else {
            EquipmentAbilityType newAbility = abilityStack.get(abilityStack.size() - 1);
            if (newAbility != currentAbility) {
                sendAbilityRequest(newAbility);
            }
        }
    }

}
