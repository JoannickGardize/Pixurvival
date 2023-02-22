package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerEquipmentAbilityRequest implements IPlayerActionRequest {

    /**
     * null means stop current ability
     */
    private EquipmentAbilityType type;
    private float angle;
    private float distance;

    @Override
    public void apply(PlayerEntity player) {
        player.startEquipmentAbility(type);
        if (type != null) {
            player.getTargetPosition().set(player.getPosition()).addEuclidean(distance, angle);
        }
    }

    @Override
    public boolean isClientPreapply() {
        return false;
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<PlayerEquipmentAbilityRequest> {

        @Override
        public void write(Kryo kryo, Output output, PlayerEquipmentAbilityRequest object) {
            if (object.type == null) {
                output.writeByte(-1);
            } else {
                output.writeByte(object.type.ordinal());
                output.writeFloat(object.angle);
                output.writeFloat(object.distance);
            }
        }

        @Override
        public PlayerEquipmentAbilityRequest read(Kryo kryo, Input input, Class<PlayerEquipmentAbilityRequest> type) {
            byte ordinal = input.readByte();
            if (ordinal == -1) {
                return new PlayerEquipmentAbilityRequest(null, 0, 0);
            } else {
                return new PlayerEquipmentAbilityRequest(EquipmentAbilityType.values()[ordinal], input.readFloat(), input.readFloat());
            }
        }
    }
}
