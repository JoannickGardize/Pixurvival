package com.pixurvival.gdxcore.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;

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
			return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (PixurvivalGame.getInstance().isZoomEnabled()) {
			((OrthographicCamera) viewport.getCamera()).zoom *= 1 + (amount * 0.2);
		}
		return true;
	}

	public void updateCameraPosition(Vector2 playerPosition) {
		updateTargetPosition(Gdx.input.getX(), Gdx.input.getY());
		Vector2 targetPosition = new Vector2(playerPosition).add(relativeCameraPosition);
		float timeForward = Gdx.graphics.getRawDeltaTime();
		if (timeForward > 0.1f) {
			timeForward = 0.1f;
		}
		viewport.getCamera().position.x += (targetPosition.getX() - viewport.getCamera().position.x) * (timeForward / 0.1f);
		viewport.getCamera().position.y += (targetPosition.getY() - viewport.getCamera().position.y) * (timeForward / 0.1f);

		// viewport.getCamera().position.x = (float) targetPosition.x;
		// viewport.getCamera().position.y = (float) targetPosition.y;
	}

	private void updateTargetPosition(int screenX, int screenY) {
		int screenViewportX = screenX - viewport.getLeftGutterWidth();
		int screenViewportY = viewport.getScreenHeight() - 1 - screenY + viewport.getTopGutterHeight();
		int w = viewport.getScreenWidth() / 2;
		int h = viewport.getScreenHeight() / 2;
		// com.badlogic.gdx.math.Vector2 vector = new
		// com.badlogic.gdx.math.Vector2(screenViewportX - w, screenViewportY -
		// h);
		// int min = Math.min(w, h);
		// if (vector.len2() > min * min) {
		// vector.setLength(min);
		// }
		float xFactor = MathUtils.clamp((screenViewportX - w) / (w * 0.8f), -1, 1);
		float yFactor = MathUtils.clamp((screenViewportY - h) / (h * 0.8f), -1, 1);
		float worldDiffX = Math.max(0, WorldScreen.CAMERA_BOUNDS - viewport.getWorldWidth() / 2.0f);
		float worldDiffY = Math.max(0, WorldScreen.CAMERA_BOUNDS - viewport.getWorldHeight() / 2.0f);
		relativeCameraPosition.set(worldDiffX * xFactor, worldDiffY * yFactor);
	}
}
