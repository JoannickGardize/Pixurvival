package com.pixurvival.core.map.chunk;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.WorldKryo;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.LongSequenceIOHelper;
import com.pixurvival.core.util.VarLenNumberIO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Arrays;

@AllArgsConstructor
public class CompressedChunk {

    private TiledMap map;
    private @Getter byte[] data;

    public CompressedChunk(Chunk chunk) {
        map = chunk.getMap();
        ByteBuffer buffer = ByteBufferUtils.getThreadSafeInstance();
        buffer.reset();
        VarLenNumberIO.writeVarInt(buffer, chunk.getPosition().getX());
        VarLenNumberIO.writeVarInt(buffer, chunk.getPosition().getY());
        VarLenNumberIO.writePositiveVarLong(buffer, chunk.getUpdateTimestamp());
        MapTile currentTile = null;
        int currentLength = 0;
        for (MapTile tile : chunk.getTiles()) {
            if (tile != currentTile) {
                if (currentTile != null) {
                    VarLenNumberIO.writePositiveVarInt(buffer, currentLength);
                    buffer.put((byte) currentTile.getTileDefinition().getId());
                }
                currentLength = 1;
                currentTile = tile;
            } else {
                currentLength++;
            }
        }
        VarLenNumberIO.writePositiveVarInt(buffer, currentLength);
        buffer.put((byte) currentTile.getTileDefinition().getId());
        VarLenNumberIO.writePositiveVarInt(buffer, chunk.getStructures().size());
        LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
        chunk.forEachStructureGroup((typeId, list) -> {
            buffer.put(typeId.byteValue());
            VarLenNumberIO.writePositiveVarInt(buffer, list.size());
            list.forEach(structure -> {
                buffer.put((byte) (structure.getTileX() - chunk.getOffsetX()));
                buffer.put((byte) (structure.getTileY() - chunk.getOffsetY()));
                structure.writeData(buffer, idSequence);
            });
        });
        data = Arrays.copyOf(buffer.array(), buffer.position());
    }

    public Chunk buildChunk() {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int x = VarLenNumberIO.readVarInt(buffer);
        int y = VarLenNumberIO.readVarInt(buffer);
        Chunk chunk = new Chunk(map, x, y);
        chunk.setUpdateTimestamp(VarLenNumberIO.readPositiveVarLong(buffer));
        MapTile[] chunkData = chunk.getTiles();
        int dataPosition = 0;
        while (dataPosition < (GameConstants.CHUNK_SIZE + 2) * (GameConstants.CHUNK_SIZE + 2)) {
            int length = VarLenNumberIO.readPositiveVarInt(buffer);
            MapTile tile = map.getMapTilesById()[buffer.get()];
            Arrays.fill(chunkData, dataPosition, dataPosition + length, tile);
            dataPosition += length;
        }
        int structureTypeCount = VarLenNumberIO.readPositiveVarInt(buffer);
        LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
        for (int i = 0; i < structureTypeCount; i++) {
            int typeId = buffer.get();
            int groupCount = VarLenNumberIO.readPositiveVarInt(buffer);
            for (int j = 0; j < groupCount; j++) {
                StructureEntity structure = chunk.addStructureSilently(map.getWorld().getContentPack().getStructures().get(typeId), buffer.get() + chunk.getOffsetX(),
                        buffer.get() + chunk.getOffsetY());
                structure.applyData(buffer, idSequence);
            }
        }
        chunk.computeMetadata();
        chunk.setCompressed(this);
        return chunk;
    }

    public ChunkPosition getPosition() {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return new ChunkPosition(VarLenNumberIO.readVarInt(buffer), VarLenNumberIO.readVarInt(buffer));
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<CompressedChunk> {

        @Override
        public void write(Kryo kryo, Output output, CompressedChunk object) {
            output.writeInt(object.data.length);
            output.write(object.data);
        }

        @Override
        public CompressedChunk read(Kryo kryo, Input input, Class<CompressedChunk> type) {
            World world = ((WorldKryo) kryo).getWorld();
            int length = input.readInt();
            return new CompressedChunk(world.getMap(), input.readBytes(length));
        }
    }
}
