package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.UILabel;

public class QuestionWindow extends Window {

	public QuestionWindow(String titleKey, String messageKey, Runnable yesAction, Runnable noAction) {
		super(PixurvivalGame.getString(titleKey), PixurvivalGame.getSkin());
		setResizable(false);
		setModal(true);

		TextButton yesButton = new TextButton(PixurvivalGame.getString("generic.yes"), PixurvivalGame.getSkin());
		TextButton noButton = new TextButton(PixurvivalGame.getString("generic.no"), PixurvivalGame.getSkin());

		yesButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				yesAction.run();
				remove();
			}

		});

		noButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				noAction.run();
				remove();
			}
		});

		defaults().pad(2);
		Label label = new UILabel(messageKey, Color.WHITE);
		label.setAlignment(Align.left);
		label.setWrap(true);
		add(label).expand().fill();
		row();
		HorizontalGroup buttonGroup = new HorizontalGroup();
		buttonGroup.addActor(yesButton);
		buttonGroup.addActor(noButton);
		add(buttonGroup);
		pack();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	public void update(Viewport viewport) {
		setPosition(viewport.getWorldWidth() / 2 - getWidth() / 2, viewport.getWorldHeight() / 2 - getHeight() / 2);
	}
}
