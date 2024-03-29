package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
public class StaticEffectMovement implements EffectMovement {

    private static final long serialVersionUID = 1L;

    @Positive
    private float minDistance;
    @Positive
    private float maxDistance;

    @Override
    public void initialize(EffectEntity entity, float angle) {
        TeamMember ancestor = entity.getAncestor();
        float distanceSquared = ancestor.getPosition().distanceSquared(ancestor.getTargetPosition());

        if (distanceSquared <= minDistance * minDistance) {
            entity.getPosition().set(ancestor.getPosition()).addEuclidean(minDistance, angle);
        } else if (distanceSquared >= maxDistance * maxDistance) {
            entity.getPosition().set(ancestor.getPosition()).addEuclidean(maxDistance, angle);
        } else {
            entity.getPosition().set(ancestor.getPosition()).addEuclidean((float) Math.sqrt(distanceSquared), angle);
        }
    }

    @Override
    public void update(EffectEntity entity) {
        // Nothing
    }

    @Override
    public float getSpeedPotential(EffectEntity entity) {
        return 0;
    }

    @Override
    public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {
    }

    @Override
    public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
    }
}
