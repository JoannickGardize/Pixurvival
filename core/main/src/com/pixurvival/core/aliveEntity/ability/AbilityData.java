package com.pixurvival.core.aliveEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.ContentPack;

public interface AbilityData {

	void write(ByteBuffer buffer);

	void apply(ByteBuffer buffer, ContentPack contentPack);

}
