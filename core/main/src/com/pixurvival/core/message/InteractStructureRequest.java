package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InteractStructureRequest {

	int x;
	int y;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<InteractStructureRequest> {

		@Override
		public void write(Kryo kryo, Output output, InteractStructureRequest object) {
			output.writeInt(object.x);
			output.writeInt(object.y);
		}

		@Override
		public InteractStructureRequest read(Kryo kryo, Input input, Class<InteractStructureRequest> type) {
			return new InteractStructureRequest(input.readInt(), input.readInt());
		}

	}
}
