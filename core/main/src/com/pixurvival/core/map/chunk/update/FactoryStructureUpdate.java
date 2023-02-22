package com.pixurvival.core.map.chunk.update;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.map.FactoryStructureEntity;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;

public class FactoryStructureUpdate extends StructureUpdate {

    private boolean working;

    public FactoryStructureUpdate(int x, int y, long id, boolean working) {
        super(x, y, id);
        this.working = working;
    }

    @Override
    public void apply(Chunk chunk) {
        MapTile mapTile = chunk.tileAt(getX(), getY());
        StructureEntity structure = mapTile.getStructure();
        if (structure != null && structure.getId() == getId()) {
            FactoryStructureEntity factory = (FactoryStructureEntity) mapTile.getStructure();
            factory.setWorking(working);
            chunk.notifyStructureChanged(factory, this);
        }
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<FactoryStructureUpdate> {

        @Override
        public void write(Kryo kryo, Output output, FactoryStructureUpdate object) {
            output.writeInt(object.getX());
            output.writeInt(object.getY());
            output.writeVarLong(object.getId(), true);
            output.writeBoolean(object.working);
        }

        @Override
        public FactoryStructureUpdate read(Kryo kryo, Input input, Class<FactoryStructureUpdate> type) {
            return new FactoryStructureUpdate(input.readInt(), input.readInt(), input.readVarLong(true), input.readBoolean());
        }
    }

}
