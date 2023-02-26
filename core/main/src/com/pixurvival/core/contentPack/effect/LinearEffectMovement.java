package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
public class LinearEffectMovement implements EffectMovement {

    private static final long serialVersionUID = 1L;

    private static class MovementData {
        Vector2 targetPosition;
        float targetDistance;
    }

    @Positive
    private float initialDistance;
    @Positive
    private float speed;
    private boolean relative;
    private boolean destroyAtTargetPosition;

    @Override
    public void initialize(EffectEntity entity, float angle) {
        TeamMember ancestor = entity.getAncestor();
        entity.getPosition().set(ancestor.getPosition());
        if (initialDistance > 0) {
            entity.getPosition().add(Vector2.fromEuclidean(initialDistance, angle));
        }
        entity.setForward(true);
        if (relative && ancestor instanceof Entity && ((Entity) ancestor).isForward()) {
            Vector2 velocityVector = Vector2.fromEuclidean(speed, angle).add(((Entity) ancestor).getTargetVelocity());
            entity.setVelocityDirect(velocityVector);
        } else {
            entity.setSpeed(speed);
            entity.setMovingAngle(angle);
            entity.updateVelocity();
        }
        if (entity.getWorld().isServer() && destroyAtTargetPosition) {
            MovementData data = new MovementData();
            entity.setMovementData(data);
            float d = entity.getWorld().getTime().getDeltaTime() * entity.getSpeed();
            data.targetPosition = ancestor.getTargetPosition().copy();
            data.targetDistance = d * d;
        }
    }

    @Override
    public void update(EffectEntity entity) {
        if (entity.getWorld().isServer() && destroyAtTargetPosition) {
            MovementData data = (MovementData) entity.getMovementData();
            if (entity.getPosition().distanceSquared(data.targetPosition) <= data.targetDistance) {
                entity.setAlive(false);
            }
        }
    }

    @Override
    public float getSpeedPotential(EffectEntity entity) {
        return entity.isAlive() ? entity.getSpeed() : 0;
    }

    @Override
    public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {
        buffer.putFloat(entity.getSpeed());
    }

    @Override
    public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
        entity.setSpeed(buffer.getFloat());
    }

    @Override
    public void writeRepositoryUpdate(ByteBuffer buffer, EffectEntity entity) {
        if (destroyAtTargetPosition) {
            MovementData data = (MovementData) entity.getMovementData();
            data.targetPosition.write(buffer);
            buffer.putFloat(data.targetDistance);
        }
    }

    @Override
    public void applyRepositoryUpdate(ByteBuffer buffer, EffectEntity entity) {
        if (destroyAtTargetPosition) {
            MovementData data = new MovementData();
            data.targetPosition = new Vector2();
            entity.setMovementData(data);
            data.targetPosition.apply(buffer);
            data.targetDistance = buffer.getFloat();
        }

    }
}
