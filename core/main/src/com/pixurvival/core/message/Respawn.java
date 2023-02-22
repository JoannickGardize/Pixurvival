package com.pixurvival.core.message;

import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.Vector2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Respawn {

    private long playerId;
    private Vector2 playerPosition;

    public Respawn(PlayerEntity player) {
        playerId = player.getId();
        playerPosition = player.getPosition();
    }
}