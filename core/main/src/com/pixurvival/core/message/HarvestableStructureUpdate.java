package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HarvestableStructureUpdate {

	private int x;
	private int y;
	private boolean harvested;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<HarvestableStructureUpdate> {

		@Override
		public void write(Kryo kryo, Output output, HarvestableStructureUpdate object) {
			output.writeInt(object.x);
			output.writeInt(object.y);
			output.writeBoolean(object.harvested);
		}

		@Override
		public HarvestableStructureUpdate read(Kryo kryo, Input input, Class<HarvestableStructureUpdate> type) {
			return new HarvestableStructureUpdate(input.readInt(), input.readInt(), input.readBoolean());
		}
	}
}
