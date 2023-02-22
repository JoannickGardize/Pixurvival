package com.pixurvival.core.message;

import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.util.Vector2;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CreateWorld {

    private long id;
    private ContentPackIdentifier contentPackIdentifier;
    private int gameModeId;
    private long myPlayerId;
    private long myOriginalPlayerId;
    private Vector2 myPosition;
    private PlayerInventory inventory;
    private TeamComposition[] teamCompositions;
    private long[] playerDeadIds;
    private int myTeamId;
    private boolean spectator;
    private Vector2 mySpawnCenter;
    private Vector2 worldSpawnCenter;
    private int[] discoveredItemCrafts;
    private Map<String, Object> customData = new HashMap<>();
}
