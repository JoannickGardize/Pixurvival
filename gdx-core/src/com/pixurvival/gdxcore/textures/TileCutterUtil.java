package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.pixurvival.core.GameConstants;
import com.pixurvival.gdxcore.textures.SpriteSheetPixmap.Region;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TileCutterUtil {

	public static Texture cut(Region middle, Region topLeft, Region topRight, Region bottomRight, Region bottomLeft) {
		SpriteSheetPixmap pixmap = SpriteSheetPixmap.singleSprite(GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT, Format.RGBA8888);

		// top left corner
		topLeft.drawTo(pixmap, 0, 0, 3, 2);
		topLeft.drawTo(pixmap, 0, 2, 2, 1);
		topLeft.drawPixelTo(pixmap, 0, 3);
		topLeft.drawPixelTo(pixmap, 3, 0);

		// top right corner
		topRight.drawTo(pixmap, 6, 0, 2, 2);
		topRight.drawPixelTo(pixmap, 5, 0);
		topRight.drawPixelTo(pixmap, 7, 2);

		// bottom right corner
		bottomRight.drawTo(pixmap, 6, 6, 2, 2);
		bottomRight.drawPixelTo(pixmap, 7, 5);
		bottomRight.drawPixelTo(pixmap, 5, 7);

		// bottom left corner
		bottomLeft.drawTo(pixmap, 0, 6, 3, 2);
		bottomLeft.drawTo(pixmap, 0, 5, 2, 1);
		bottomLeft.drawPixelTo(pixmap, 0, 4);
		bottomLeft.drawPixelTo(pixmap, 3, 7);

		// middle
		middle.drawTo(pixmap, 2, 2, 5, 4);
		middle.drawTo(pixmap, 3, 1, 3, 1);
		middle.drawTo(pixmap, 3, 6, 3, 1);
		middle.drawTo(pixmap, 1, 3, 1, 2);
		middle.drawTo(pixmap, 7, 3, 1, 2);
		middle.drawPixelTo(pixmap, 4, 0);
		middle.drawPixelTo(pixmap, 4, 7);

		Texture result = AddPaddingUtil.apply(pixmap.getRegion(0, 0));
		pixmap.dispose();
		return result;
	}
}
