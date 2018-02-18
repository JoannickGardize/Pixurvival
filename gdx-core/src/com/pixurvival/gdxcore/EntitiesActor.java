package com.pixurvival.gdxcore;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.EntityPool;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntitiesActor extends Actor {

	private Texture texture = new Texture("badlogic.jpg");

	private @NonNull EntityPool entityPool;

	@Override
	public void draw(Batch batch, float parentAlpha) {
		entityPool.foreach(e -> {
			batch.draw(texture, (float) e.getPosition().x - 0.5f, (float) e.getPosition().y, 1, 1);
		});
	}

}
