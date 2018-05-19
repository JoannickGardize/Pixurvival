package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ScreenResizeEvent;

public class UIWindow extends Window implements EventListener {

	public UIWindow(String name) {
		super(PixurvivalGame.getString("hud." + name + ".title"), PixurvivalGame.getSkin());
		setName(name);
		getCaptureListeners().clear();
		setResizable(false);
	}

	public void initialize() {
		getStage().addListener(this);
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof ScreenResizeEvent) {
			moveOnScreenResize((ScreenResizeEvent) event);
		}
		return false;
	}

	public float getCenterX() {
		return getX() + getWidth() / 2;
	}

	public float getCenterY() {
		return getY() + getHeight() / 2;
	}

	private void moveOnScreenResize(ScreenResizeEvent event) {
		if (getCenterX() < event.getPrevScreenWidth() / 2) {
			float ratio = getX() / event.getPrevScreenWidth();
			setX(ratio * event.getNewScreenWidth());
		} else {
			float ratio = (event.getPrevScreenWidth() - getX() - getWidth()) / event.getPrevScreenWidth();
			setX(event.getNewScreenWidth() - ratio * event.getNewScreenWidth() - getWidth());
		}
		if (getCenterY() < event.getPrevScreenHeight() / 2) {
			float ratio = getY() / event.getPrevScreenHeight();
			setY(ratio * event.getNewScreenHeight());
		} else {
			float ratio = (event.getPrevScreenHeight() - getY() - getHeight()) / event.getPrevScreenHeight();
			setY(event.getNewScreenHeight() - ratio * event.getNewScreenHeight() - getHeight());
		}
	}
}
