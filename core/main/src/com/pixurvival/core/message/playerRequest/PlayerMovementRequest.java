package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.Direction;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
@ToString
public class PlayerMovementRequest implements IPlayerActionRequest {

	private long id = -1;
	private Direction direction = Direction.SOUTH;
	private boolean forward;

	public PlayerMovementRequest(PlayerMovementRequest other) {
		direction = other.direction;
		forward = other.forward;
		id = other.id;
	}

	public void set(PlayerMovementRequest other) {
		direction = other.direction;
		forward = other.forward;
		id = other.id;
	}

	@Override
	public void apply(PlayerEntity player) {
		if (id >= player.getLastPlayerMovementRequest().getId()) {
			player.setMovingAngle(direction.getAngle());
			player.setForward(forward);
			player.setLastPlayerMovementRequest(this);
		}
	}

	@Override
	public boolean isClientPreapply() {
		return false;
	}

	public void incrementsId() {
		id++;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<PlayerMovementRequest> {

		@Override
		public void write(Kryo kryo, Output output, PlayerMovementRequest object) {
			output.writeLong(object.id);
			kryo.writeObject(output, object.direction);
			output.writeBoolean(object.forward);

		}

		@Override
		public PlayerMovementRequest read(Kryo kryo, Input input, Class<PlayerMovementRequest> type) {
			PlayerMovementRequest playerActionRequest = new PlayerMovementRequest();
			playerActionRequest.id = input.readLong();
			playerActionRequest.direction = kryo.readObject(input, Direction.class);
			playerActionRequest.forward = input.readBoolean();
			return playerActionRequest;
		}
	}
}
