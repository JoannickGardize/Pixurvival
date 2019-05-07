package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateTargetPositionRequest implements IPlayerActionRequest {

	private Vector2 targetPosition;

	@Override
	public void apply(PlayerEntity player) {
		player.getTargetPosition().set(targetPosition);
	}

	@Override
	public boolean isClientPreapply() {
		return false;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<UpdateTargetPositionRequest> {

		@Override
		public void write(Kryo kryo, Output output, UpdateTargetPositionRequest object) {
			output.writeDouble(object.targetPosition.getX());
			output.writeDouble(object.targetPosition.getY());
		}

		@Override
		public UpdateTargetPositionRequest read(Kryo kryo, Input input, Class<UpdateTargetPositionRequest> type) {
			return new UpdateTargetPositionRequest(new Vector2(input.readDouble(), input.readDouble()));
		}
	}

}
