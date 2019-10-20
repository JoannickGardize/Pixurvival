package com.pixurvival.core.entity;

import java.util.function.Supplier;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EntityGroup {
	PLAYER(true, PlayerEntity::new, PlayerEntity.class),
	ITEM_STACK(false, ItemStackEntity::new, ItemStackEntity.class),
	CREATURE(false, CreatureEntity::new, CreatureEntity.class),
	EFFECT(false, EffectEntity::new, EffectEntity.class);

	public static final byte END_MARKER = -1;

	private @Getter boolean persistentInstance;

	private @Getter Supplier<Entity> entitySupplier;

	private @Getter Class<? extends Entity> type;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<EntityGroup> {

		@Override
		public void write(Kryo kryo, Output output, EntityGroup object) {
			output.writeByte(object.ordinal());
		}

		@Override
		public EntityGroup read(Kryo kryo, Input input, Class<EntityGroup> type) {
			return EntityGroup.values()[input.readByte()];
		}

	}
}
