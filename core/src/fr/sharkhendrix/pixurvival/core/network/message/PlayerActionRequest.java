package fr.sharkhendrix.pixurvival.core.network.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerActionRequest {

	private Direction direction;
	private boolean forward;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<PlayerActionRequest> {

		@Override
		public void write(Kryo kryo, Output output, PlayerActionRequest object) {
			kryo.writeObject(output, object.direction);
			output.writeBoolean(object.forward);

		}

		@Override
		public PlayerActionRequest read(Kryo kryo, Input input, Class<PlayerActionRequest> type) {
			PlayerActionRequest playerActionRequest = new PlayerActionRequest();
			playerActionRequest.direction = kryo.readObject(input, Direction.class);
			playerActionRequest.forward = input.readBoolean();
			return playerActionRequest;
		}
	}

}
