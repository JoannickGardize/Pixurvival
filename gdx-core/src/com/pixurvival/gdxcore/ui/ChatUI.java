package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.pixurvival.gdxcore.PixurvivalGame;

public class ChatUI extends UIWindow {

	private ChatHistory chatHistory = new ChatHistory(30);
	private TextField inputArea;
	private ScrollPane displayAreScrollPane;

	public ChatUI() {
		super("chat");
		displayAreScrollPane = new ScrollPane(chatHistory, PixurvivalGame.getSkin());

		inputArea = new TextField("test", PixurvivalGame.getSkin());
		inputArea.addCaptureListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					chatHistory.push(new ChatEntry(inputArea.getText()));
					inputArea.setText("");
					validate();
					displayAreScrollPane.setScrollPercentY(1);
				} else if (keycode == Keys.ESCAPE) {
					getStage().unfocusAll();
				}
				return true;
			}
		});

		add(displayAreScrollPane).expand().fill().size(300, 200);
		row();
		add(inputArea).fill();
		pack();
	}

}
