package com.pixurvival.gdxcore.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import com.badlogic.gdx.graphics.Pixmap;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackReadException;
import com.pixurvival.core.contentPack.SpriteSheet;
import com.pixurvival.gdxcore.graphics.SpriteSheetPixmap.Region;

public class ContentPackTextureAnimations {

	private Map<String, TextureAnimationSet> map = new HashMap<>();

	public void load(ContentPack pack, int pixelWidth) throws ContentPackReadException {
		Function<Region, Pixmap> transform = new PixelTextureBuilder(pixelWidth);
		for (Entry<String, SpriteSheet> entries : pack.getSprites().getSpriteSheets().entrySet()) {
			map.put(entries.getKey(), new TextureAnimationSet(entries.getValue(), transform));
		}
	}
}
