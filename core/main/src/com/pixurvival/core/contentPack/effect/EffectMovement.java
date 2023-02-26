package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.entity.EffectEntity;

import java.io.Serializable;
import java.nio.ByteBuffer;

public interface EffectMovement extends Serializable {

    /**
     * Only called on the server side.
     *
     * @param entity
     */
    void initialize(EffectEntity entity, float angle);

    void update(EffectEntity entity);

    float getSpeedPotential(EffectEntity entity);

    void writeUpdate(ByteBuffer buffer, EffectEntity entity);

    void applyUpdate(ByteBuffer buffer, EffectEntity entity);

    default boolean isDestroyWithAncestor() {
        return false;
    }

    default void writeRepositoryUpdate(ByteBuffer buffer, EffectEntity entity) {

    }

    default void applyRepositoryUpdate(ByteBuffer buffer, EffectEntity entity) {

    }
}
