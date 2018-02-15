package com.pixurvival.gdxcore;

import com.badlogic.gdx.InputProcessor;
import com.pixurvival.core.message.Direction;
import com.pixurvival.core.message.PlayerActionRequest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeyboardInputProcessor implements InputProcessor {

	private @NonNull KeyMapping keyMapping;
	private PlayerActionRequest playerAction = new PlayerActionRequest();
	private PlayerActionRequest previousPlayerAction = new PlayerActionRequest();

	public KeyboardInputProcessor() {
		playerAction.setDirection(Direction.SOUTH);
		playerAction.setForward(false);
	}

	@Override
	public boolean keyDown(int keycode) {

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
