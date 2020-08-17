package com.pixurvival.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.World.Type;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.ecosystem.ChunkSpawner;
import com.pixurvival.core.contentPack.ecosystem.DarknessSpawner;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.ecosystem.StructureSpawner;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;
import com.pixurvival.core.contentPack.gameMode.event.EventAction;
import com.pixurvival.core.contentPack.serialization.ContentPackSerialization;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.livingEntity.KillCreatureEntityAction;
import com.pixurvival.core.map.RemoveDurationStructureAction;
import com.pixurvival.core.map.SpawnAction;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.ChunkRepository;
import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.map.chunk.CompressedChunkAndEntityData;
import com.pixurvival.core.map.chunk.update.HarvestableStructureUpdate;
import com.pixurvival.core.mapLimits.MapLimitsAnchorRun;
import com.pixurvival.core.mapLimits.MapLimitsRun;
import com.pixurvival.core.mapLimits.NextMapLimitAnchorAction;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.FileUtils;
import com.pixurvival.core.util.Rectangle;
import com.pixurvival.core.util.ReleaseVersion;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.core.util.WorkingDirectory;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WorldSerialization {

	public static void save(World world, ContentPackSerialization contentPackSerialization) throws FileNotFoundException, IOException {
		Kryo kryo = getKryo(world.getGameMode().getEcosystem());
		if (world.getType() != Type.LOCAL) {
			throw new UnsupportedOperationException("Only local games can be saved");
		}
		ByteBuffer buffer = ByteBuffer.allocate(WorldUpdate.BUFFER_SIZE * 2 + ByteBufferUtils.BUFFER_SIZE);
		try (OutputStream output = new BufferedOutputStream(new FileOutputStream(getSaveFile(world.getSaveName()))); Output kryoOutput = new Output(output)) {
			// Global data
			ByteBufferUtils.putString(buffer, ReleaseVersion.getValue());
			ByteBufferUtils.putString(buffer, world.getContentPack().getIdentifier().fileName());
			ByteBufferUtils.putBytes(buffer, contentPackSerialization.getChecksum(world.getContentPack().getIdentifier()));
			buffer.putInt(world.getGameMode().getId());
			buffer.putLong(world.getSeed());
			world.getTime().write(buffer);
			buffer.putLong(world.getEntityPool().getNextId());
			flush(buffer, output);
			// Map and entities data
			Collection<CompressedChunkAndEntityData> mapData;
			synchronized (world.getMap().getRepository()) {
				world.getMap().getRepository();
				world.getMap().saveAll();
				mapData = world.getMap().getRepository().getAll();
			}
			buffer.putInt(mapData.size());
			for (CompressedChunkAndEntityData data : mapData) {
				ByteBufferUtils.putBytes(buffer, data.getCompressedChunk().getData());
				ByteBufferUtils.putBytes(buffer, data.getEntityData());
				flush(buffer, output);
			}
			// kryo stuff
			kryo.writeObject(kryoOutput, world.getActionTimerManager().getActionTimerQueue());
			kryo.writeClassAndObject(kryoOutput, world.getEndGameConditionData());
			kryo.writeObjectOrNull(kryoOutput, world.getMapLimitsRun(), MapLimitsRun.class);
			kryo.writeObject(kryoOutput, world.getChunkCreatureSpawnManager().getActionMemory());
			kryo.writeObject(kryoOutput, world.getSpawnCenter());
			kryo.writeObject(kryoOutput, world.getMyPlayer().getPosition());
			kryoOutput.flush();
		}
	}

	@SuppressWarnings("unchecked")
	public static World load(String saveName, ContentPackSerialization contentPackSerialization) throws IOException, ContentPackException {
		ByteBuffer buffer = ByteBuffer.wrap(FileUtils.readBytes(getSaveFile(saveName)));
		String version = ByteBufferUtils.getString(buffer);
		if (!version.equals(ReleaseVersion.getValue())) {
			Log.warn("The version of the save " + saveName + " is " + version + ", but the game version is " + ReleaseVersion.getValue());
		}
		ContentPackIdentifier identifier = new ContentPackIdentifier(ByteBufferUtils.getString(buffer));
		ContentPack contentPack = contentPackSerialization.load(identifier);
		byte[] saveChecksum = ByteBufferUtils.getBytes(buffer);
		ContentPackValidityCheckResult checkResult = contentPackSerialization.checkValidity(identifier, saveChecksum);
		if (checkResult == ContentPackValidityCheckResult.NOT_SAME) {
			throw new ContentPackException("The content pack of the save is not identical.");
		}
		World world = World.createLocalWorld(contentPack, buffer.getInt(), buffer.getLong());
		Kryo kryo = getKryo(world.getGameMode().getEcosystem());
		world.getTime().apply(buffer);
		world.getEntityPool().setNextId(buffer.getLong());
		int size = buffer.getInt();
		ChunkRepository chunkRepository = world.getMap().getRepository();
		for (int i = 0; i < size; i++) {
			byte[] chunkData = ByteBufferUtils.getBytes(buffer);
			byte[] entityData = ByteBufferUtils.getBytes(buffer);
			chunkRepository.add(new CompressedChunkAndEntityData(new CompressedChunk(world.getMap(), chunkData), entityData));
		}
		try (Input kryoInput = new Input(buffer.array())) {
			kryoInput.setPosition(buffer.position());
			world.getActionTimerManager().setActionTimerQueue(kryo.readObject(kryoInput, PriorityQueue.class));
			world.setEndGameConditionData(kryo.readClassAndObject(kryoInput));
			world.setMapLimitsRun(kryo.readObjectOrNull(kryoInput, MapLimitsRun.class));
			world.getChunkCreatureSpawnManager().setActionMemory(kryo.readObject(kryoInput, HashMap.class));
			world.setSpawnCenter(kryo.readObject(kryoInput, Vector2.class));
			world.getMyPlayer().getPosition().set(kryo.readObject(kryoInput, Vector2.class));
			buffer.position(kryoInput.position());
		}
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

	private static Kryo getKryo(Ecosystem ecosystem) {
		Kryo kryo = new Kryo();
		kryo.setRegistrationRequired(true);
		kryo.register(EventAction.class);
		kryo.register(HarvestableStructureUpdate.class);
		kryo.register(KillCreatureEntityAction.class);
		kryo.register(MapLimitsAnchor.class);
		kryo.register(NextMapLimitAnchorAction.class);
		kryo.register(RemoveDurationStructureAction.class);
		kryo.register(ChunkPosition.class);
		kryo.register(StructureSpawner.class, new ChunkSpawner.Serializer(ecosystem));
		kryo.register(DarknessSpawner.class, new ChunkSpawner.Serializer(ecosystem));
		kryo.register(SpawnAction.class);
		kryo.register(PriorityQueue.class);
		kryo.register(Boolean.class);
		kryo.register(HashMap.class);
		kryo.register(HashSet.class);
		kryo.register(Rectangle.class);
		kryo.register(MapLimitsAnchorRun.class);
		kryo.register(MapLimitsRun.class);
		kryo.register(Vector2.class);
		kryo.register(ActionTimer.class);
		return kryo;
	}

	private File getSaveDirectory() {
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
