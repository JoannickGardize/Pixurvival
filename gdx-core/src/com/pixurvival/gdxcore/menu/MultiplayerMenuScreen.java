package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.notificationpush.Notification;
import com.pixurvival.gdxcore.notificationpush.NotificationPushManager;

public class MultiplayerMenuScreen implements Screen {

    private Stage stage = new Stage(new ScreenViewport());

    public MultiplayerMenuScreen() {
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().prefWidth(400).prefHeight(50).pad(5, 0, 5, 0);

        Skin skin = PixurvivalGame.getSkin();
        TextButton joinButton = new TextButton(PixurvivalGame.getString("menu.multiplayer.join"), skin);
        TextButton hostAndPlayButton = new TextButton(PixurvivalGame.getString("menu.multiplayer.hostAndPlay"), skin);

        table.add(joinButton);
        table.row();
        table.add(hostAndPlayButton);
        table.row();
        table.add(new BackButton());

        stage.addActor(table);

        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PixurvivalGame.setScreen(JoinMultiplayerMenuScreen.class);
            }
        });
        hostAndPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PixurvivalGame.setScreen(HostMultiplayerMenuScreen.class);
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        NotificationPushManager.getInstance().push(Notification.builder().status("In menus").build());
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
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
