package com.pixurvival.gdxcore.input;

import com.pixurvival.core.Direction;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.gdxcore.util.UserDirectory;

import lombok.Getter;
import lombok.Setter;

public class InputManager {

	private static @Getter InputManager instance = new InputManager();

	private @Getter @Setter InputMapping mapping = UserDirectory.loadInputMapping();
	private boolean[] pressedActions = new boolean[InputAction.values().length];
	private @Setter boolean playerMovementChanged = false;
	private PlayerMovementRequest movementRequest = new PlayerMovementRequest();

	public void buttonDown(InputButton button) {
		InputAction action = mapping.getAction(button);
		if (action != null) {
			pressedActions[action.ordinal()] = true;
			action.getProcessor().buttonDown();
		}
	}

	public void buttonUp(InputButton button) {
		InputAction action = mapping.getAction(button);
		if (action != null) {
			pressedActions[action.ordinal()] = false;
			action.getProcessor().buttonUp();
		}
	}

	public boolean isPressed(InputAction action) {
		return pressedActions[action.ordinal()];
	}

	public PlayerMovementRequest updatePlayerMovement() {
		if (playerMovementChanged) {
			boolean left = isPressed(InputAction.MOVE_LEFT);
			boolean up = isPressed(InputAction.MOVE_UP);
			boolean right = isPressed(InputAction.MOVE_RIGHT);
			boolean down = isPressed(InputAction.MOVE_DOWN);
			movementRequest.setForward(true);
			if (right) {
				if (up) {
					movementRequest.setDirection(Direction.NORTH_EAST);
				} else if (down) {
					movementRequest.setDirection(Direction.SOUTH_EAST);
				} else {
					movementRequest.setDirection(Direction.EAST);
				}
			} else if (left) {
				if (up) {
					movementRequest.setDirection(Direction.NORTH_WEST);
				} else if (down) {
					movementRequest.setDirection(Direction.SOUTH_WEST);
				} else {
					movementRequest.setDirection(Direction.WEST);
				}
			} else if (up) {
				movementRequest.setDirection(Direction.NORTH);
			} else if (down) {
				movementRequest.setDirection(Direction.SOUTH);
			} else {
				movementRequest.setForward(false);
			}
			movementRequest.incrementsId();
			playerMovementChanged = false;
			return movementRequest;
		} else {
			return null;
		}
	}
}
