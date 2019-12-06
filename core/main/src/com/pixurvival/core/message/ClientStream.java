package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientStream {

	private long time;
	private float targetAngle;
	private float targetDistance;
	private long[] acks;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<ClientStream> {

		@Override
		public void write(Kryo kryo, Output output, ClientStream object) {
			output.writeLong(object.time);
			output.writeFloat(object.targetAngle);
			output.writeFloat(object.targetDistance);
			output.writeByte(object.acks.length);
			for (long ack : object.acks) {
				output.writeLong(ack);
			}
		}

		@Override
		public ClientStream read(Kryo kryo, Input input, Class<ClientStream> type) {
			ClientStream clientStream = new ClientStream();
			clientStream.setTime(input.readLong());
			clientStream.targetAngle = input.readFloat();
			clientStream.targetDistance = input.readFloat();
			int length = input.readByte();
			long[] acks = new long[length];
			for (int i = 0; i < length; i++) {
				acks[i] = input.readLong();
			}
			clientStream.setAcks(acks);
			return clientStream;
		}

	}
}
