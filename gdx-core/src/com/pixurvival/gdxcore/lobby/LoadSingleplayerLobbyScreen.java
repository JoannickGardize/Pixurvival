package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixurvival.core.LoadGameException;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.menu.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class LoadSingleplayerLobbyScreen implements Screen {
    private Stage stage;

    private SaveChooser saveChooser = new SaveChooser();
    private MessageWindow errorWindow = new MessageWindow("generic.error");
    private QuestionWindow removeConfirm = new QuestionWindow("lobby.remove", () -> {
        try {
            Files.delete(saveChooser.getSelectedSave().toPath());
            saveChooser.update();
        } catch (IOException e) {
            errorWindow.getContentLabel().setText(e.toString());
            errorWindow.setVisible(true);
        }
    }, () -> {
    });
    private InputWindow renameWindow = new InputWindow("lobby.rename", "lobby.rename", name -> {
        File saveFile = saveChooser.getSelectedSave();
        try {
            Files.move(saveFile.toPath(), new File(saveFile.getParent(), name).toPath());
            saveChooser.update();
        } catch (IOException e) {
            errorWindow.getContentLabel().setText(e.toString());
            errorWindow.setVisible(true);
        }
    });

    @Override
    public void show() {
        Table mainGroup = new Table();
        mainGroup.align(Align.center);
        mainGroup.setFillParent(true);
        mainGroup.padBottom(2);
        errorWindow.getOkButton().setVisible(true);

        saveChooser.update();

        TextButton playButton = new MenuButton("lobby.play", () -> {
            try {
                PixurvivalGame.getClient().loadAndStartLocalGame(saveChooser.getSelectedSave().getName(), PixurvivalGame.getRequiredChunkManagerPlugins());
            } catch (LoadGameException e) {
                LoadGameUtils.handleLoadGameError(e, errorWindow);
            }
        });
        TextButton removeButton = new MenuButton("lobby.remove",
                () -> removeConfirm.setVisible(PixurvivalGame.getString("lobby.confirmRemove", saveChooser.getSelectedSave().getName())));
        TextButton renameButton = new MenuButton("lobby.rename", () -> renameWindow.setVisible(saveChooser.getSelectedSave().getName()));

        mainGroup.add(new ScrollPane(saveChooser)).fillX();
        mainGroup.row();
        HorizontalGroup buttonGroup = new HorizontalGroup();
        buttonGroup.space(2);
        buttonGroup.addActor(new BackButton());
        buttonGroup.addActor(removeButton);
        buttonGroup.addActor(renameButton);

        mainGroup.add(playButton).fillX();
        mainGroup.row();
        mainGroup.add(buttonGroup);

        stage = new Stage(new ScreenViewport());
        stage.addActor(mainGroup);
        removeConfirm.setVisible(false);
        stage.addActor(removeConfirm);
        stage.addActor(errorWindow);
        renameWindow.setVisible(false);
        stage.addActor(renameWindow);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        errorWindow.update(stage.getViewport());
        removeConfirm.update(stage.getViewport());
        renameWindow.update(stage.getViewport());
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
