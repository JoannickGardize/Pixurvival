package com.pixurvival.gdxcore;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.World;
import com.pixurvival.core.util.Vector2;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CameraControlProcessor extends InputAdapter {

	private @NonNull Viewport viewport;
	private Vector2 relativeCameraPosition = new Vector2();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.MIDDLE) {
			((OrthographicCamera) viewport.getCamera()).zoom = 1;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		updateTargetPosition(screenX, screenY);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		updateTargetPosition(screenX, screenY);
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		((OrthographicCamera) viewport.getCamera()).zoom *= 1 + (amount * 0.2);
		return true;
	}

	public void updateCameraPosition(Vector2 playerPosition) {
		Vector2 targetPosition = new Vector2(playerPosition).add(relativeCameraPosition);
		// float timeForward = Gdx.graphics.getDeltaTime();
		// if (timeForward > 0.5f) {
		// timeForward = 0.5f;
		// }
		// viewport.getCamera().position.x += (targetPosition.x -
		// viewport.getCamera().position.x) * (timeForward / 0.5f);
		// viewport.getCamera().position.y += (targetPosition.y -
		// viewport.getCamera().position.y) * (timeForward / 0.5f);
		viewport.getCamera().position.x = (float) targetPosition.x;
		viewport.getCamera().position.y = (float) targetPosition.y;
	}

	private void updateTargetPosition(int screenX, int screenY) {
		int screenViewportX = MathUtils.clamp(screenX - viewport.getLeftGutterWidth(), 0, viewport.getScreenWidth());
		int screenViewportY = MathUtils.clamp(viewport.getScreenHeight() - 1 - screenY + viewport.getTopGutterHeight(),
				0, viewport.getScreenHeight());
		int w = viewport.getScreenWidth() / 2;
		int h = viewport.getScreenHeight() / 2;
		com.badlogic.gdx.math.Vector2 vector = new com.badlogic.gdx.math.Vector2(screenViewportX - w,
				screenViewportY - h);
		int min = Math.min(w, h);
		if (vector.len2() > min * min) {
			vector.setLength(min);
		}
		double xFactor = vector.x / w;
		double yFactor = vector.y / h;
		double worldDiffX = (World.PLAYER_VIEW_DISTANCE - viewport.getWorldWidth()) / 2.0;
		double worldDiffY = (World.PLAYER_VIEW_DISTANCE - viewport.getWorldHeight()) / 2.0;
		relativeCameraPosition.set(worldDiffX * xFactor, worldDiffY * yFactor);
	}
}
