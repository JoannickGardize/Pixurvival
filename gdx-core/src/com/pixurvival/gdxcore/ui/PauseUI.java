package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.menu.ControlsPanel;
import com.pixurvival.gdxcore.menu.MenuButton;
import com.pixurvival.gdxcore.util.AutoScrollFocusListener;

import lombok.Getter;

public class PauseUI extends Window {

	private @Getter ControlsPanel controlsPanel = new ControlsPanel();

	public PauseUI() {
		super(PixurvivalGame.getString("hud.pause.title"), PixurvivalGame.getSkin());

		setVisible(false);
		setModal(true);
		setMovable(false);
		Table mainMenuTable = new Table();
		mainMenuTable.defaults().pad(2).fill();
		mainMenuTable.add(new MenuButton("hud.pause.resume", () -> setVisible(false)));
		mainMenuTable.row();
		mainMenuTable.add(new MenuButton("hud.pause.options", () -> {
		}));
		mainMenuTable.row();
		mainMenuTable.add(new MenuButton("hud.pause.save", () -> {
		}));
		mainMenuTable.row();
		mainMenuTable.add(new MenuButton("hud.pause.quit", () -> {
		}));

		Stack stack = new Stack(mainMenuTable, controlsPanel);
		mainMenuTable.setVisible(false);

		add(stack).expand().fill();

		addListener(new AutoScrollFocusListener());
	}

	public void update(Viewport viewport) {
		setSize(Math.min(600, viewport.getWorldWidth()), Math.min(800, viewport.getWorldHeight()));
		setPosition((viewport.getWorldWidth() - getWidth()) / 2, (viewport.getWorldHeight() - getHeight()) / 2);
	}
}
