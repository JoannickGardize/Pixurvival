package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.pixurvival.gdxcore.PixurvivalGame;

public class ChatUI extends UIWindow {

	private List<String> displayArea;
	private TextField inputArea;
	private ScrollPane displayAreScrollPane;

	public ChatUI() {
		super("chat");

		displayArea = new List<>(PixurvivalGame.getSkin());
		displayAreScrollPane = new ScrollPane(displayArea, PixurvivalGame.getSkin());
		displayAreScrollPane.setForceScroll(false, true);
		inputArea = new TextField("test", PixurvivalGame.getSkin());
		inputArea.addCaptureListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
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
