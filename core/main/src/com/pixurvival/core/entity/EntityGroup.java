package com.pixurvival.core.entity;

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
	PLAYER((w, id) -> {
		PlayerEntity entity = w.getPlayerEntities().get(id);
		return entity;
	}, PlayerEntity.class),
	ITEM_STACK((w, id) -> new ItemStackEntity(), ItemStackEntity.class),
	CREATURE((w, id) -> new CreatureEntity(), CreatureEntity.class),
	EFFECT((w, id) -> new EffectEntity(), EffectEntity.class);

	public static final byte END_MARKER = -1;
	public static final byte REMOVE_ALL_MARKER = -2;

	private @Getter EntitySupplier entitySupplier;

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
