package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.pixurvival.gdxcore.textures.SpriteSheetPixmap.Region;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AddPaddingUtil {

	public static Texture apply(Region region) {
		Pixmap pixmap = new Pixmap(region.getWidth() + 1, region.getHeight() + 1, region.getFormat());
		region.drawTo(pixmap, 1, 0);
		for (int y = 0; y < region.getHeight(); y++) {
			pixmap.drawPixel(0, y, region.getPixel(0, y));
		}
		for (int x = 0; x < region.getHeight(); x++) {
			pixmap.drawPixel(x + 1, region.getHeight(), region.getPixel(x, region.getHeight() - 1));
		}
		pixmap.drawPixel(0, region.getHeight(), region.getPixel(0, region.getHeight() - 1));

		Texture texture = new Texture(pixmap);
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		return texture;
	}

}
