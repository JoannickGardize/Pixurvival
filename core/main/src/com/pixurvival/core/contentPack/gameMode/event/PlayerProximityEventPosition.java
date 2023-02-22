package com.pixurvival.core.contentPack.gameMode.event;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.CollectionUtils;
import com.pixurvival.core.util.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PlayerProximityEventPosition implements EventPosition {

    @Positive
    private float distance;

    @Override
    public void apply(World world, Collection<PlayerEntity> players, Vector2 positionOut, Vector2 targetOut) {
        if (players.isEmpty()) {
            return;
        }
        PlayerEntity randomPlayer = CollectionUtils.get(players, world.getRandom().nextInt(players.size()));
        positionOut.setFromEuclidean(distance, world.getRandom().nextAngle()).add(randomPlayer.getPosition());
        targetOut.set(randomPlayer.getPosition());
    }
}
