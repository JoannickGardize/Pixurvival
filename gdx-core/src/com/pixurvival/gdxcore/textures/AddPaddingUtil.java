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
		region.drawAllTo(pixmap, 0, 1);
		for (int y = 0; y < region.getHeight(); y++) {
			pixmap.drawPixel(region.getWidth(), y, region.getPixel(region.getWidth() - 1, y));
		}
		for (int x = 0; x < region.getWidth(); x++) {
			pixmap.drawPixel(x, 0, region.getPixel(x, 0));
		}
		pixmap.drawPixel(region.getWidth(), 0, region.getPixel(region.getWidth() - 1, 0));

		Texture texture = new Texture(pixmap);
		pixmap.dispose();
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		return texture;
	}

}
