package com.pixurvival.gdxcore.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.notificationpush.NotificationPushManager;
import com.pixurvival.gdxcore.util.upnp.UPnP;
import com.pixurvival.server.util.ServerMainArgs;

public class HostMultiplayerMenuScreen implements Screen {

	private Stage stage = new Stage(new ScreenViewport());
	private ConnectionMessageWindow connectionMessageWindow;

	private boolean upnpIpReady = false;
	private TextField nameField;
	private TextField ipField;

	public HostMultiplayerMenuScreen() {
		Skin skin = PixurvivalGame.getSkin();
		Table table = new Table();
		table.setFillParent(true);
		table.defaults().pad(5);

		table.add(new Label(PixurvivalGame.getString("menu.multiplayer.myIp"), skin)).right();
		ipField = new TextField(PixurvivalGame.getString("menu.multiplayer.requesting"), skin);
		ipField.setDisabled(true);
		table.add(ipField);
		TextButton copyButton = new TextButton(PixurvivalGame.getString("menu.multiplayer.copy"), skin);
		copyButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				StringSelection stringSelection = new StringSelection(ipField.getText());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		});
		table.add(copyButton);
		table.row();

		table.add(new Label(PixurvivalGame.getString("menu.multiplayer.serverPort"), skin)).right();
		TextField portField = new TextField("7777", skin);
		table.add(portField).colspan(2);
		table.row();

		table.add(new Label(PixurvivalGame.getString("menu.multiplayer.playerName"), skin)).right();
		nameField = new TextField("Bob", skin);
		table.add(nameField).colspan(2);
		table.row();

		Table buttonTable = new Table();
		buttonTable.defaults().pad(0, 5, 0, 5).prefWidth(100);
		buttonTable.add(new BackButton(() -> PixurvivalGame.getInstance().disposeServer())).center();
		TextButton connectButton = new TextButton(PixurvivalGame.getString("menu.multiplayer.hostAndPlay"), skin);
		buttonTable.add(connectButton).center();

		table.add(buttonTable).colspan(3);

		connectionMessageWindow = new ConnectionMessageWindow();

		stage.addActor(table);
		stage.addActor(connectionMessageWindow);

		connectButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				int port = Integer.parseInt(portField.getText());
				if (UPnP.isUPnPAvailable()) {
					checkPortForwarding(port);
				} else {
					hostAndPlay(port);
				}
			}
		});
		new Thread(() -> {
			UPnP.waitInit();
			upnpIpReady = true;
		}).start();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		String username = NotificationPushManager.getInstance().getUsername();
		if (username != null) {
			nameField.setText(username);
		}
		connectionMessageWindow.setVisible(false);
	}

	@Override
	public void render(float delta) {
		if (upnpIpReady) {
			String myIp = UPnP.getExternalIP();
			ipField.setText(myIp == null ? "No network" : myIp);
			upnpIpReady = false;
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		connectionMessageWindow.update(stage.getViewport());
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

	private void checkPortForwarding(int port) {
		if (!UPnP.isUPnPAvailable()) {
			Log.warn("No UPnP devices found.");
			hostAndPlay(port);
		}
		if (!UPnP.isMappedTCP(port) || !UPnP.isMappedUDP(port)) {
			connectionMessageWindow.showWaitingMessage();
			boolean success = true;
			if (!UPnP.isMappedUDP(port)) {
				success = UPnP.openPortUDP(port) && success;
			}
			if (!UPnP.isMappedTCP(port)) {
				success = UPnP.openPortTCP(port);
			}
			if (!success) {
				Log.warn("Unable to port forward automatically TCP and UDP port " + port);
			}
			hostAndPlay(port);
		} else {
			hostAndPlay(port);
		}
	}

	private void hostAndPlay(int port) {
		connectionMessageWindow.showWaitingMessage();
		ServerMainArgs args = new ServerMainArgs();
		args.setContentPackDirectory(PixurvivalGame.getClient().getContentPackContext().getWorkingDirectory().getPath());
		args.setPort(port);
		args.setOnGameBeginning("/op " + nameField.getText());
		PixurvivalGame.getInstance().startServer(args);
		new Thread(() -> PixurvivalGame.getClient().connectToServer("localhost", port, nameField.getText())).start();
	}
}
