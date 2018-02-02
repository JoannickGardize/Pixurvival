package fr.sharkhendrix.pixurvival.core.network.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeResponse {

	private long requesterTime;
	private long responderTime;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<TimeResponse> {

		@Override
		public void write(Kryo kryo, Output output, TimeResponse object) {
			output.writeLong(object.requesterTime);
			output.writeLong(object.responderTime);
		}

		@Override
		public TimeResponse read(Kryo kryo, Input input, Class<TimeResponse> type) {
			return new TimeResponse(input.readLong(), input.readLong());
		}

	}

}
