package com.pixurvival.core.contentPack.gameMode;

import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.team.Team;

import java.util.function.Consumer;

public enum TeamSet {
    ALL {
        @Override
        public void forEach(PlayerEntity playerEntity, Consumer<Team> action) {
            playerEntity.getWorld().getTeamSet().forEach(action);
        }
    },
    SAME {
        @Override
        public void forEach(PlayerEntity playerEntity, Consumer<Team> action) {
            action.accept(playerEntity.getTeam());
        }
    },
    OTHERS {
        @Override
        public void forEach(PlayerEntity playerEntity, Consumer<Team> action) {
            playerEntity.getWorld().getTeamSet().forEach(team -> {
                if (team != playerEntity.getTeam()) {
                    action.accept(team);
                }
            });
        }
    };

    public abstract void forEach(PlayerEntity playerEntity, Consumer<Team> action);
}
