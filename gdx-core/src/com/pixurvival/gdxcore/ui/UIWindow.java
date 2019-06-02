package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ScreenResizeEvent;

public class UIWindow extends Window {

	public UIWindow(String name) {
		super(PixurvivalGame.getString("hud." + name + ".title"), PixurvivalGame.getSkin());
		setName(name);
		// Remove default capture listeners, it contains a listener to click
		// that push the window to the front of the overs, this default behavior
		// totally break the use wee need of it, for example the "item in hand"
		// actor must always be in front.
		getCaptureListeners().clear();
		setResizable(false);
		setMovable(false);
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
