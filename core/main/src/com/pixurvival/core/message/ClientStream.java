package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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
            output.writeVarLong(object.time, true);
            output.writeFloat(object.targetAngle);
            output.writeFloat(object.targetDistance);
            output.writeVarInt(object.acks.length, true);
            long previousAck = 0;
            for (long ack : object.acks) {
                output.writeVarLong(ack - previousAck, false);
                previousAck = ack;
            }
        }

        @Override
        public ClientStream read(Kryo kryo, Input input, Class<ClientStream> type) {
            ClientStream clientStream = new ClientStream();
            clientStream.setTime(input.readVarLong(true));
            clientStream.targetAngle = input.readFloat();
            clientStream.targetDistance = input.readFloat();
            int length = input.readVarInt(true);
            long[] acks = new long[length];
            long previousAck = 0;
            for (int i = 0; i < length; i++) {
                long ack = input.readVarLong(false) + previousAck;
                acks[i] = ack;
                previousAck = ack;
            }
            clientStream.setAcks(acks);
            return clientStream;
        }

    }
}
