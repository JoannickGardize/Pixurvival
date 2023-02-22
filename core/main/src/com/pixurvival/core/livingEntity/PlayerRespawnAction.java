package com.pixurvival.core.livingEntity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.Action;
import com.pixurvival.core.World;
import com.pixurvival.core.entity.EntitySearchUtils;
import com.pixurvival.core.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerRespawnAction implements Action {

    private long playerId;
    private boolean respawnToAlly;

    @Override
    public void perform(World world) {
        PlayerEntity playerEntity = world.getPlayerEntities().get(playerId);
        if (playerEntity == null || playerEntity.isAlive()) {
            return;
        }
        Vector2 respawnPosition = respawnToAlly && playerEntity.getTeam().aliveMemberCount() > 0 ? EntitySearchUtils.closest(playerEntity.getPosition(), playerEntity.getTeam().getAliveMembers())
                : playerEntity.getSpawnPosition();
        playerEntity.respawn(respawnPosition);
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<PlayerRespawnAction> {

        @Override
        public void write(Kryo kryo, Output output, PlayerRespawnAction object) {
            output.writeVarLong(object.playerId, true);
            output.writeBoolean(object.respawnToAlly);
        }

        @Override
        public PlayerRespawnAction read(Kryo kryo, Input input, Class<PlayerRespawnAction> type) {
            PlayerRespawnAction respawnAction = new PlayerRespawnAction();
            respawnAction.playerId = input.readVarLong(true);
            respawnAction.respawnToAlly = input.readBoolean();
            return respawnAction;
        }

    }
}
