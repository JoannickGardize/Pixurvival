package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.PlayerEndGameData;
import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;
import com.pixurvival.gdxcore.menu.MainMenuScreen;
import com.pixurvival.gdxcore.ui.tooltip.RepresenterUtils;

import java.util.function.Supplier;

public class EndGameUI extends UIWindow {

    public EndGameUI() {
        super("endGame");
        setVisible(false);
        setModal(true);

    }

    public void show(EndGameData endGameData) {
        clearChildren();
        RepresenterUtils.appendLabelledRow(this, "hud.endGame.survivedTime", RepresenterUtils.formatHoursMinutesSecondes(endGameData.getTime()));
        World world = PixurvivalGame.getClient().getWorld();
        buildPlayerGroup(world, endGameData.getPlayerWonIds(), () -> "[GREEN]" + PixurvivalGame.getString("hud.endGame.victory"));
        row();
        buildPlayerGroup(world, endGameData.getPlayerLostIds(), () -> "[RED]" + PixurvivalGame.getString("hud.endGame.defeat"));
        row();
        add().colspan(2).expand();
        TextButton okButton = new TextButton(PixurvivalGame.getString("generic.ok"), PixurvivalGame.getSkin());

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
                if (PixurvivalGame.getInstance().getScreen() instanceof WorldScreen) {
                    PixurvivalGame.setScreen(MainMenuScreen.class);
                }
            }
        });
        row();
        add(okButton).colspan(2).expand();
        setVisible(true);
    }

    private void buildPlayerGroup(World world, PlayerEndGameData[] playerData, Supplier<String> titleSupplier) {
        if (playerData.length > 0) {
            add(new Label(titleSupplier.get(), PixurvivalGame.getSkin(), "white")).colspan(2);
            row();
            StringBuilder sb = new StringBuilder();
            String separator = "";
            for (PlayerEndGameData player : playerData) {
                PlayerEntity playerEntity = world.getPlayerEntities().get(player.getPlayerId());
                sb.append(separator);
                sb.append(playerEntity.getName());
                if (player.getRoleId() != -1) {
                    sb.append(" (" + world.getGameMode().getRoles().getRoles().get(player.getRoleId()).getName() + ")");
                }
                separator = ", ";
            }
            add(new Label(sb.toString(), PixurvivalGame.getSkin(), "white")).colspan(2);
        }
    }

    public void update(Viewport viewport) {
        setPosition(viewport.getWorldWidth() / 4, viewport.getWorldHeight() / 4);
        setSize(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
    }

}
