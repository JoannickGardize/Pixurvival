package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.pixurvival.core.message.lobby.LobbyPlayer;
import com.pixurvival.gdxcore.PixurvivalGame;

public class PlayerRow extends Label {

    public PlayerRow(LobbyPlayer player) {
        super(player.getPlayerName(), PixurvivalGame.getSkin(), "default", player.isReady() ? Color.GREEN : Color.RED);
    }
}
