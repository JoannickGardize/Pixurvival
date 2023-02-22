package com.pixurvival.core.contentPack.gameMode.event;

import com.pixurvival.core.Direction;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class RandomRectangeEventPosition implements EventPosition {

    private float x;
    private float y;
    @Positive
    private float width;
    @Positive
    private float height;
    private Direction targetDirection = Direction.NORTH;

    @Override
    public void apply(World world, Collection<PlayerEntity> players, Vector2 positionOut, Vector2 targetOut) {
        positionOut.set(x + world.getRandom().nextFloat() * width, y + world.getRandom().nextFloat() * height);
        targetOut.setFromEuclidean(1, targetDirection.getAngle()).add(positionOut);
    }
}
