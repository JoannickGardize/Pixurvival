package com.pixurvival.core.message;

import java.util.UUID;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.Direction;
import com.pixurvival.core.aliveEntity.Equipment;
import com.pixurvival.core.aliveEntity.PlayerInventory;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.map.CompressedChunk;
import com.pixurvival.core.util.Vector2;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KryoInitializer {

	public static void apply(Kryo kryo) {
		register(kryo, Direction.class);
		register(kryo, Vector2.class);
		register(kryo, PlayerActionRequest.class);
		register(kryo, TimeRequest.class);
		register(kryo, TimeResponse.class);
		register(kryo, LoginRequest.class);
		register(kryo, LoginResponse.class);
		register(kryo, WorldUpdate.class);
		register(kryo, CreateWorld.class);
		register(kryo, InitializeGame.class);
		register(kryo, byte[].class);
		register(kryo, Version.class);
		kryo.register(UUID.class, new UUIDSerializer());
		register(kryo, ContentPackIdentifier.class);
		register(kryo, ContentPackIdentifier[].class);
		register(kryo, ContentPackPart.class);
		register(kryo, RequestContentPacks.class);
		register(kryo, GameReady.class);
		register(kryo, ItemStack.class);
		register(kryo, Inventory.class);
		register(kryo, PlayerInventory.class);
		register(kryo, Equipment.class);
		register(kryo, InventoryActionRequest.class);
		register(kryo, CompressedChunk.class);
		register(kryo, CompressedChunk[].class);
		register(kryo, InteractStructureRequest.class);
		register(kryo, HarvestableStructureUpdate.class);
		register(kryo, AddStructureUpdate.class);
		register(kryo, RemoveStructureUpdate.class);
		register(kryo, CraftItemRequest.class);
		register(kryo, DropItemRequest.class);
		register(kryo, PlayerData.class);
		register(kryo, PlayerData[].class);
		register(kryo, EquipmentActionRequest.class);
	}

	@SuppressWarnings("unchecked")
	private static void register(Kryo kryo, Class<?> clazz) {
		try {
			Class<?>[] internalClasses = clazz.getClasses();
			for (Class<?> internalClass : internalClasses) {
				if (internalClass.getSimpleName().equals("Serializer")
						&& internalClass.getSuperclass() == Serializer.class) {
					kryo.register(clazz, ((Class<? extends Serializer<?>>) internalClass).newInstance());
					return;
				}
			}
			kryo.register(clazz);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static class UUIDSerializer extends Serializer<UUID> {

		@Override
		public void write(Kryo kryo, Output output, UUID object) {
			output.writeLong(object.getMostSignificantBits());
			output.writeLong(object.getLeastSignificantBits());
		}

		@Override
		public UUID read(Kryo kryo, Input input, Class<UUID> type) {
			return new UUID(input.readLong(), input.readLong());
		}

	}
}
