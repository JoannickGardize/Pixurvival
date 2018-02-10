package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeRequest {

	private long requesterTime;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<TimeRequest> {

		@Override
		public void write(Kryo kryo, Output output, TimeRequest object) {
			output.writeLong(object.requesterTime);
		}

		@Override
		public TimeRequest read(Kryo kryo, Input input, Class<TimeRequest> type) {
			return new TimeRequest(input.readLong());
		}

	}
}
