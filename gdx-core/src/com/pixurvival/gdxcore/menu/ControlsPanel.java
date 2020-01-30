package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.input.InputAction;
import com.pixurvival.gdxcore.input.InputButton;
import com.pixurvival.gdxcore.input.InputButton.Type;
import com.pixurvival.gdxcore.input.InputMapping;
import com.pixurvival.gdxcore.util.AutoScrollFocusListener;

import lombok.Getter;

public class ControlsPanel extends Table {

	private TextButton[] actionButtons = new TextButton[InputAction.values().length];
	private InputMapping mapping = new InputMapping();
	private @Getter MessageWindow waitingKeyWindow = new MessageWindow("controlPanel.waitingWindow.title");
	private InputProcessor previousInputProcessor;

	public ControlsPanel() {

		waitingKeyWindow.getContentLabel().setText(PixurvivalGame.getString("controlPanel.waitingWindow.content"));
		Table keysTable = createKeysTable();
		ScrollPane keysScrollPane = new ScrollPane(keysTable, PixurvivalGame.getSkin());
		keysScrollPane.setScrollingDisabled(true, false);
		keysScrollPane.setOverscroll(false, false);
		keysScrollPane.setForceScroll(false, true);
		keysScrollPane.setFadeScrollBars(false);
		keysScrollPane.addListener(new AutoScrollFocusListener());

		Table topTable = new Table();

	}

	public void setMapping(InputMapping mapping) {
		this.mapping.set(mapping);
		for (InputAction action : InputAction.values()) {
			InputButton button = mapping.getButton(action);
			TextButton textButton = actionButtons[action.ordinal()];
			if (button == null) {
				textButton.setText("");
			} else {
				textButton.setText(button.toString());
			}
		}
	}

	private void bind(InputAction action, InputButton button) {
		waitingKeyWindow.setVisible(false);
		mapping.bind(action, button);
		actionButtons[action.ordinal()].setText(button.toString());
		Gdx.input.setInputProcessor(previousInputProcessor);
	}

	private Table createKeysTable() {
		Table keysTable = new Table();
		keysTable.defaults().fill().pad(2);
		for (InputAction action : InputAction.values()) {
			Label label = new Label(PixurvivalGame.getString("controlPanel.inputAction." + CaseUtils.upperToCamelCase(action.name())), PixurvivalGame.getSkin());
			label.setAlignment(Align.right);
			keysTable.add(label);
			TextButton button = new TextButton("", PixurvivalGame.getSkin());
			actionButtons[action.ordinal()] = button;
			keysTable.add(button).minWidth(120);
			keysTable.row();

			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					waitingKeyWindow.setVisible(true);
					waitingKeyWindow.toFront();
					previousInputProcessor = Gdx.input.getInputProcessor();

					Gdx.input.setInputProcessor(new InputAdapter() {

						@Override
						public boolean keyDown(int keycode) {
							bind(action, new InputButton(Type.KEYBOARD, keycode));
							return true;
						}

						@Override
						public boolean touchDown(int screenX, int screenY, int pointer, int button) {
							bind(action, new InputButton(Type.MOUSE, button));
							return true;
						}
					});
				}
			});
		}
		return keysTable;
	}
}
