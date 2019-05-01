package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerEquipmentAbilityRequest implements IPlayerActionRequest {

	/**
	 * null means stop current ability
	 */
	private EquipmentAbilityType type;
	private Vector2 targetPosition;

	@Override
	public void apply(PlayerEntity player) {
		if (targetPosition != null) {
			player.getTargetPosition().set(targetPosition);
		}
		player.startEquipmentAbility(type);
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
				output.writeDouble(object.targetPosition.getX());
				output.writeDouble(object.targetPosition.getY());
			}
		}

		@Override
		public PlayerEquipmentAbilityRequest read(Kryo kryo, Input input, Class<PlayerEquipmentAbilityRequest> type) {
			byte ordinal = input.readByte();
			if (ordinal == -1) {
				return new PlayerEquipmentAbilityRequest(null, null);
			} else {

				return new PlayerEquipmentAbilityRequest(EquipmentAbilityType.values()[ordinal], new Vector2(input.readDouble(), input.readDouble()));
			}
		}
	}
}
