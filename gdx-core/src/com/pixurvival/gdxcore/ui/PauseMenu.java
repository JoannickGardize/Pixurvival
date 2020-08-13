package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.World;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;
import com.pixurvival.gdxcore.menu.ControlsPanel;
import com.pixurvival.gdxcore.menu.MenuButton;
import com.pixurvival.gdxcore.util.Scene2dUtils;

import lombok.Getter;

public class PauseMenu extends Window {

	private @Getter ControlsPanel controlsPanel = new ControlsPanel(this::backToMainMenu);
	private Table mainMenuTable = new Table();

	public PauseMenu() {
		super(PixurvivalGame.getString("hud.pause.title"), PixurvivalGame.getSkin());

		setVisible(false);
		setModal(true);
		setMovable(false);
		mainMenuTable.defaults().pad(2).fill();
		mainMenuTable.add(new MenuButton("hud.pause.resume", () -> {
			Screen screen = PixurvivalGame.getInstance().getScreen();
			if (screen instanceof WorldScreen) {
				((WorldScreen) screen).switchPauseMenu();
			}
		}));
		// mainMenuTable.row();
		// mainMenuTable.add(new MenuButton("hud.pause.settings", () -> {
		// }));
		mainMenuTable.row();
		mainMenuTable.add(new MenuButton("hud.pause.controls", () -> {
			mainMenuTable.setVisible(false);
			controlsPanel.setVisible(true);
		}));
		mainMenuTable.row();
		MenuButton saveButton = new MenuButton("hud.pause.save", () -> {
			// TODO
		});
		saveButton.setDisabled(PixurvivalGame.getClient().getWorld().getType() != World.Type.LOCAL);
		mainMenuTable.add(saveButton);
		mainMenuTable.row();
		mainMenuTable.add(new MenuButton("hud.pause.quit", Gdx.app::exit));

		Stack stack = new Stack(mainMenuTable, controlsPanel);
		controlsPanel.setVisible(false);

		add(stack).expand().fill();
	}

	public void backToMainMenu() {
		mainMenuTable.setVisible(true);
		controlsPanel.setVisible(false);
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible && !isVisible()) {
			backToMainMenu();
		}
		super.setVisible(visible);
	}

	public void update() {
		Viewport viewport = getStage().getViewport();
		setSize(Math.min(800, viewport.getWorldWidth()), Math.min(900, viewport.getWorldHeight()));
		Scene2dUtils.positionToCenter(this);
		Scene2dUtils.positionToCenter(controlsPanel.getPopupWindow());
	}
}
