package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixurvival.core.LoadGameException;
import com.pixurvival.core.WorldSerialization;
import com.pixurvival.core.message.lobby.LobbyData;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.core.util.FileUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.menu.BackButton;
import com.pixurvival.gdxcore.menu.MessageWindow;

public class NewSingleplayerLobbyScreen implements Screen {
    private Stage stage;

    private GameModeChooser gameModeChooser;

    private LobbyData lobbyDataToApply = null;
    private MessageWindow errorWindow = new MessageWindow("generic.error");

    @Override
    public void show() {
        Table mainGroup = new Table();
        mainGroup.setFillParent(true);
        errorWindow.getOkButton().setVisible(true);

        gameModeChooser = new GameModeChooser();
        gameModeChooser.setData(PixurvivalGame.getClient().getSinglePlayerLobbyData());
        mainGroup.defaults().pad(4);
        mainGroup.add(gameModeChooser).colspan(2);
        mainGroup.row();
        mainGroup.add(new Label(PixurvivalGame.getString("lobby.saveName"), PixurvivalGame.getSkin()));
        TextField saveNameField = new TextField(WorldSerialization.getNewSaveFileName(), PixurvivalGame.getSkin());
        mainGroup.add(saveNameField);
        mainGroup.row();

        TextButton playButton = new TextButton(PixurvivalGame.getString("lobby.play"), PixurvivalGame.getSkin());

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    if (FileUtils.isValidFileName(saveNameField.getText())) {
                        PixurvivalGame.getClient().startNewLocalGame(saveNameField.getText(), PixurvivalGame.getRequiredChunkManagerPlugins());
                    } else {
                        errorWindow.getContentLabel().setText("Save name must be a valid file name (no special character)");
                        errorWindow.setVisible(true);
                    }
                } catch (LoadGameException e) {
                    LoadGameUtils.handleLoadGameError(e, errorWindow);
                }
            }

        });

        mainGroup.add(new BackButton()).fill();
        mainGroup.add(playButton).fill();

        stage = new Stage(new ScreenViewport());
        stage.addActor(mainGroup);
        stage.addActor(errorWindow);
        Gdx.input.setInputProcessor(stage);
    }

    public void received(LobbyMessage message) {
        if (message instanceof LobbyData) {
            lobbyDataToApply = (LobbyData) message;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        if (lobbyDataToApply != null) {
            gameModeChooser.setData(lobbyDataToApply);
            lobbyDataToApply = null;
        }
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        errorWindow.update(stage.getViewport());
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}
