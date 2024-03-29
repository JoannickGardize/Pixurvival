package com.pixurvival.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.LoadGameException.Reason;
import com.pixurvival.core.World.Type;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackContext;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.ecosystem.ChunkSpawner;
import com.pixurvival.core.contentPack.ecosystem.DarknessSpawner;
import com.pixurvival.core.contentPack.ecosystem.StructureSpawner;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;
import com.pixurvival.core.contentPack.gameMode.event.EventAction;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.livingEntity.KillCreatureEntityAction;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerRespawnAction;
import com.pixurvival.core.map.RemoveDurationStructureAction;
import com.pixurvival.core.map.SpawnAction;
import com.pixurvival.core.map.chunk.*;
import com.pixurvival.core.map.chunk.update.HarvestableStructureUpdate;
import com.pixurvival.core.message.WorldKryo;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.system.interest.PersistenceInterest;
import com.pixurvival.core.system.mapLimits.MapLimitsAnchorRun;
import com.pixurvival.core.system.mapLimits.MapLimitsSystemData;
import com.pixurvival.core.system.mapLimits.NextMapLimitAnchorAction;
import com.pixurvival.core.system.trigger.TimerTriggerAction;
import com.pixurvival.core.util.*;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;

@UtilityClass
public class WorldSerialization {

    public static void save(World world, ContentPackContext contentPackContext) throws IOException {
        Kryo kryo = getKryo(world);
        if (world.getType() != Type.LOCAL) {
            throw new UnsupportedOperationException("Only local games can be saved");
        }
        ByteBuffer buffer = ByteBuffer.allocate(WorldUpdate.BUFFER_SIZE * 2 + ByteBufferUtils.BUFFER_SIZE);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream(); Output kryoOutput = new Output(output)) {
            // Global data
            ByteBufferUtils.putString(buffer, ReleaseVersion.actual().toString());
            ByteBufferUtils.putString(buffer, world.getContentPack().getIdentifier().fileName());
            try {
                ByteBufferUtils.putBytes(buffer, contentPackContext.getChecksum(world.getContentPack().getIdentifier()));
            } catch (ContentPackException e) {
                throw new IOException(e);
            }
            VarLenNumberIO.writePositiveVarInt(buffer, world.getGameMode().getId());
            buffer.putLong(world.getSeed());
            world.getTime().write(buffer);
            VarLenNumberIO.writePositiveVarLong(buffer, world.getEntityPool().getNextId());
            VarLenNumberIO.writePositiveVarLong(buffer, world.getMap().getNextStructureId());
            flush(buffer, output);
            // Teams
            world.getTeamSet().write(buffer);
            // Players data
            VarLenNumberIO.writePositiveVarInt(buffer, world.getPlayerEntities().size());
            LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
            world.getPlayerEntities().values().forEach(p -> {
                idSequence.write(buffer, p.getId());
                p.writeRepositoryUpdate(buffer);
            });
            // Map and entities data
            Collection<ServerChunkRepositoryEntry> mapData = world.getMap().saveAll();
            VarLenNumberIO.writePositiveVarInt(buffer, mapData.size());
            for (ServerChunkRepositoryEntry data : mapData) {
                VarLenNumberIO.writePositiveVarLong(buffer, data.getTime());
                ByteBufferUtils.putBytes(buffer, data.getCompressedChunk().getData());
                ByteBufferUtils.putBytes(buffer, data.getEntityData());
                flush(buffer, output);
            }
            // kryo stuff
            kryo.writeObject(kryoOutput, world.getActionTimerManager().getActionTimerQueue());
            kryo.writeObject(kryoOutput, world.getEndGameConditionData());
            world.getInterestSubscriptionSet().get(PersistenceInterest.class).publish(pi -> pi.save(kryoOutput, kryo));
            kryo.writeObject(kryoOutput, world.getChunkCreatureSpawnManager().getActionMemory());
            kryo.writeObject(kryoOutput, world.getSpawnCenter());
            kryoOutput.flush();
            Files.write(getSaveFile(world.getSaveName()).toPath(), output.toByteArray());
        }
    }

    @SuppressWarnings("unchecked")
    public static World load(String saveName, ContentPackContext contentPackSerialization, Collection<ChunkManagerPlugin> chunkManagerPlugins)
            throws IOException, LoadGameException {
        ByteBuffer buffer = ByteBuffer.wrap(FileUtils.readBytes(getSaveFile(saveName)));
        // Global data
        ReleaseVersion version = ReleaseVersion.valueFor(ByteBufferUtils.getString(buffer));
        if (!ReleaseVersion.actual().isSavesCompatibleWith(version)) {
            throw new LoadGameException(Reason.INCOMPATIBLE_VERSION, version, ReleaseVersion.actual());
        }
        ContentPackIdentifier identifier = new ContentPackIdentifier(ByteBufferUtils.getString(buffer));
        ContentPack contentPack;
        try {
            contentPack = contentPackSerialization.load(identifier);
        } catch (ContentPackException e) {
            throw new LoadGameException(Reason.PARSE_EXCEPTION, e.getMessage());
        }
        byte[] saveChecksum = ByteBufferUtils.getBytes(buffer);
        ContentPackValidityCheckResult checkResult;
        try {
            checkResult = contentPackSerialization.checkSameness(identifier, saveChecksum);
        } catch (ContentPackException e) {
            throw new LoadGameException(Reason.CONTENT_PACK_FILE_NOT_FOUND, identifier);
        }
        if (checkResult == ContentPackValidityCheckResult.NOT_SAME) {
            throw new LoadGameException(Reason.NOT_SAME_CONTENT_PACK, identifier);
        }
        World world = World.createExistingLocalWorld(contentPack, VarLenNumberIO.readPositiveVarInt(buffer), buffer.getLong(), chunkManagerPlugins);
        Kryo kryo = getKryo(world);
        world.getTime().apply(buffer);
        world.getEntityPool().setNextId(VarLenNumberIO.readPositiveVarLong(buffer));
        world.getMap().setNextStructureId(VarLenNumberIO.readPositiveVarLong(buffer));
        // Teams
        world.getTeamSet().apply(buffer);
        // Players data
        int playerCount = VarLenNumberIO.readPositiveVarInt(buffer);
        LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
        for (int i = 0; i < playerCount; i++) {
            PlayerEntity playerEntity = new PlayerEntity();
            playerEntity.setId(idSequence.read(buffer));
            playerEntity.setWorld(world);
            playerEntity.initialize();
            playerEntity.applyRepositoryUpdate(buffer);
            world.getPlayerEntities().put(playerEntity.getId(), playerEntity);
            if (playerEntity.isAlive()) {
                world.getEntityPool().addOld(playerEntity);
            }
        }
        // Map and entities data
        int size = VarLenNumberIO.readPositiveVarInt(buffer);
        ChunkRepository chunkRepository = world.getMap().getRepository();
        for (int i = 0; i < size; i++) {
            long time = VarLenNumberIO.readPositiveVarLong(buffer);
            byte[] chunkData = ByteBufferUtils.getBytes(buffer);
            byte[] entityData = ByteBufferUtils.getBytes(buffer);
            chunkRepository.add(new ServerChunkRepositoryEntry(time, new CompressedChunk(world.getMap(), chunkData), entityData));
        }
        // kryo stuff
        try (Input kryoInput = new Input(buffer.array())) {
            kryoInput.setPosition(buffer.position());
            world.getActionTimerManager().setActionTimerQueue(kryo.readObject(kryoInput, PriorityQueue.class));
            world.setEndGameConditionData(kryo.readObject(kryoInput, HashMap.class));
            world.getInterestSubscriptionSet().get(PersistenceInterest.class).publish(pi -> pi.load(kryoInput, kryo));
            world.getChunkCreatureSpawnManager().setActionMemory(kryo.readObject(kryoInput, HashMap.class));
            world.setSpawnCenter(kryo.readObject(kryoInput, Vector2.class));
            buffer.position(kryoInput.position());
        }
        world.setSaveName(saveName);
        return world;
    }

    public static String getNewSaveFileName() {
        List<String> existing = Arrays.asList(listSaves());
        int discriminator = 1;
        while (existing.contains("Game " + discriminator)) {
            discriminator++;
        }
        return "Game " + discriminator;
    }

    public static String[] listSaves() {
        return getSaveDirectory().list();
    }

    public static File[] listSavesFiles() {
        return getSaveDirectory().listFiles();
    }

    private static Kryo getKryo(World world) {
        WorldKryo kryo = new WorldKryo();
        kryo.setWorld(world);
        kryo.setReferences(false);
        kryo.setRegistrationRequired(true);
        kryo.register(EventAction.class);
        kryo.register(HarvestableStructureUpdate.class);
        kryo.register(KillCreatureEntityAction.class);
        kryo.register(MapLimitsAnchor.class);
        kryo.register(NextMapLimitAnchorAction.class);
        kryo.register(RemoveDurationStructureAction.class);
        kryo.register(ChunkPosition.class);
        kryo.register(StructureSpawner.class, new ChunkSpawner.Serializer(world.getGameMode().getEcosystem()));
        kryo.register(DarknessSpawner.class, new ChunkSpawner.Serializer(world.getGameMode().getEcosystem()));
        kryo.register(SpawnAction.class);
        kryo.register(PriorityQueue.class);
        kryo.register(Boolean.class);
        kryo.register(HashMap.class);
        kryo.register(HashSet.class);
        kryo.register(Rectangle.class);
        kryo.register(MapLimitsAnchorRun.class);
        kryo.register(Vector2.class);
        kryo.register(ActionTimer.class);
        kryo.register(PlayerRespawnAction.class);
        kryo.register(MapLimitsSystemData.class);
        kryo.register(TimerTriggerAction.class);
        return kryo;
    }

    public static File getSaveDirectory() {
        File saveDir = new File(WorkingDirectory.get(), "saves");
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        return saveDir;
    }

    private static File getSaveFile(String name) {
        return new File(getSaveDirectory(), name);
    }

    private static void flush(ByteBuffer buffer, OutputStream output) throws IOException {
        output.write(buffer.array(), 0, buffer.position());
        buffer.position(0);
    }
}
