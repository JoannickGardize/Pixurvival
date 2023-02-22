package com.pixurvival.core.contentPack.gameMode.event;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class StaticEventPosition implements EventPosition {

    /**
     * Position of the event, relative to the initial spawn center of the party.
     */
    private Vector2 position = new Vector2();

    @Override
    public void apply(World world, Collection<PlayerEntity> players, Vector2 positionOut, Vector2 targetOut) {
        positionOut.set(position).add(world.getSpawnCenter());
    }
}
