package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.math.Vector2;

import lombok.Data;

@Data
public class OverlayInfos {
	protected Vector2 referencePosition = new Vector2();
	protected float scaleX;
	protected float scaleY;
}
