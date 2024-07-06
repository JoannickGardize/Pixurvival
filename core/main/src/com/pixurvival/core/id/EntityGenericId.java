package com.pixurvival.core.id;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.World;
import com.pixurvival.core.entity.Entity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EntityGenericId<T extends Entity> implements GenericId<T> {

    private final long id;

    @Override
    public T find(World world) {
        return world.getEntityPool().get();
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<EntityGenericId<?>> {

        @Override
        public void write(Kryo kryo, Output output, EntityGenericId<?> entityGenericId) {
            output.writeVarLong(entityGenericId.id, true);
        }

        @Override
        public EntityGenericId<?> read(Kryo kryo, Input input, Class<EntityGenericId<?>> aClass) {
            return new EntityGenericId<>(input.readVarLong(true));
        }
    }
}
