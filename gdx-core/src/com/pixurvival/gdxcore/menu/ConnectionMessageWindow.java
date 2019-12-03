package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.client.ClientGameAdapter;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.gdxcore.PixurvivalGame;

public class ConnectionMessageWindow extends Window {

	private Label connectionInfoLabel;
	private TextButton okButton;

	public ConnectionMessageWindow() {
		super(PixurvivalGame.getString("menu.multiplayer.connectWindow.title"), PixurvivalGame.getSkin());
		Skin skin = PixurvivalGame.getSkin();

		setModal(true);
		setVisible(false);
		setMovable(false);
		setResizable(false);

		connectionInfoLabel = new Label("", skin);
		connectionInfoLabel.setWrap(true);
		okButton = new TextButton(PixurvivalGame.getString("menu.multiplayer.connectWindow.ok"), skin);
		okButton.setVisible(false);

		add(connectionInfoLabel).expand().top();
		setSize(400, 200);
		row();
		add(okButton).width(100);

		okButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
			}
		});

		PixurvivalGame.getClient().addListener(new ClientGameAdapter() {
			@Override
			public void loginResponse(LoginResponse response) {
				if (response == LoginResponse.OK) {
					connectionInfoLabel.setText(PixurvivalGame.getString("menu.multiplayer.connectWindow.waiting"));
				} else {
					connectionInfoLabel.setText(response.name());
					okButton.setVisible(true);
				}
			}
		});
	}

	public void showWaitingMessage() {
		setVisible(true);
		okButton.setVisible(false);
		connectionInfoLabel.setText(PixurvivalGame.getString("menu.multiplayer.connectWindow.connecting"));
	}

	public void update(Viewport viewport) {
		setPosition(viewport.getWorldWidth() / 2 - getWidth() / 2, viewport.getWorldHeight() / 2 - getHeight() / 2);
		getCell(connectionInfoLabel).width(getWidth() - 20);
	}
}
