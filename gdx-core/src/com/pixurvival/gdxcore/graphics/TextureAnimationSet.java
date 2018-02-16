package com.pixurvival.gdxcore.graphics;

import java.util.HashMap;
import java.util.Map;

public class TextureAnimationSet {
	private Map<String, TextureAnimation> textureAnimations = new HashMap<>();

	public void put(String name, TextureAnimation animation) {
		textureAnimations.put(name, animation);
	}

	public TextureAnimation get(String name) {
		return textureAnimations.get(name);
	}
}
