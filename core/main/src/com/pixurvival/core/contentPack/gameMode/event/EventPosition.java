package com.pixurvival.core.contentPack.gameMode.event;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.Vector2;

import java.util.Collection;

public interface EventPosition {

    void apply(World world, Collection<PlayerEntity> players, Vector2 positionOut, Vector2 targetOut);

}
