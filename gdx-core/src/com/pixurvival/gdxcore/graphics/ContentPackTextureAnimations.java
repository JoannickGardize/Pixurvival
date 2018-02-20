package com.pixurvival.gdxcore.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackReadException;
import com.pixurvival.core.contentPack.SpriteSheet;

public class ContentPackTextureAnimations {

	private Map<String, TextureAnimationSet> map = new HashMap<>();

	public void load(ContentPack pack, int pixelWidth) throws ContentPackReadException {
		PixelTextureBuilder transform = new PixelTextureBuilder(pixelWidth);
		for (Entry<String, SpriteSheet> entries : pack.getSprites().getSpriteSheets().entrySet()) {
			map.put(entries.getKey(), new TextureAnimationSet(entries.getValue(), transform));
		}
	}

	public TextureAnimationSet get(String name) {
		return map.get(name);
	}
}
