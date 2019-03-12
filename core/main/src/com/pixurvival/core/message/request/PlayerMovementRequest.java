package com.pixurvival.core.message.request;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.Direction;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class PlayerMovementRequest implements IPlayerActionRequest {

	private Direction direction;
	private boolean forward;

	public PlayerMovementRequest(PlayerMovementRequest other) {
		direction = other.direction;
		forward = other.forward;
	}

	public void set(PlayerMovementRequest other) {
		direction = other.direction;
		forward = other.forward;
	}

	@Override
	public void apply(PlayerEntity player) {
		player.setMovingAngle(direction.getAngle());
		player.setForward(forward);
	}

	@Override
	public boolean isClientPreapply() {
		return false;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<PlayerMovementRequest> {

		@Override
		public void write(Kryo kryo, Output output, PlayerMovementRequest object) {
			kryo.writeObject(output, object.direction);
			output.writeBoolean(object.forward);

		}

		@Override
		public PlayerMovementRequest read(Kryo kryo, Input input, Class<PlayerMovementRequest> type) {
			PlayerMovementRequest playerActionRequest = new PlayerMovementRequest();
			playerActionRequest.direction = kryo.readObject(input, Direction.class);
			playerActionRequest.forward = input.readBoolean();
			return playerActionRequest;
		}
	}
}
