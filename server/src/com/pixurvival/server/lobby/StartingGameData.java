package com.pixurvival.server.lobby;

import com.pixurvival.core.contentPack.ContentPack;
import lombok.Data;

@Data
public class StartingGameData {

    private ContentPack contentPack;
    private int gameModeId;
}
