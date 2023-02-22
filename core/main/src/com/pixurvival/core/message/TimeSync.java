package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeSync {

    private long requesterTime;
    private long responderTime;

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<TimeSync> {

        @Override
        public void write(Kryo kryo, Output output, TimeSync object) {
            output.writeLong(object.requesterTime);
            output.writeLong(object.responderTime);
        }

        @Override
        public TimeSync read(Kryo kryo, Input input, Class<TimeSync> type) {
            return new TimeSync(input.readLong(), input.readLong());
        }

    }

}
