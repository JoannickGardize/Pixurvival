package com.pixurvival.core.tag;

import com.pixurvival.core.World;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.VarLenNumberIO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Setter
@Getter
public class TagInstance {

    private float value;

    private int modCount = 0;

    private long expirationTime = 0;

    public TagInstance(float value) {
        this.value = value;
    }

    @AllArgsConstructor
    public static class Serializer implements com.pixurvival.core.util.Serializer<TagInstance> {

        private final World world;

        @Override
        public void write(ByteBuffer buffer, TagInstance object) {
            buffer.putFloat(object.getValue());
            VarLenNumberIO.writeVarInt(buffer, object.getModCount());
            ByteBufferUtils.writeFutureTime(buffer, world, object.getExpirationTime());
        }

        @Override
        public TagInstance read(ByteBuffer buffer) {
            TagInstance tagInstance = new TagInstance(buffer.getFloat());
            tagInstance.setModCount(VarLenNumberIO.readVarInt(buffer));
            tagInstance.setExpirationTime(ByteBufferUtils.readFutureTime(buffer, world));
            return tagInstance;
        }
    }
}
