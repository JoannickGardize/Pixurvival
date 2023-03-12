package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Pixmap;
import com.pixurvival.gdxcore.textures.SpriteSheetPixmap.Region;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TileCutterUtil {

    public static void cut(Region middle, Region topLeft, Region topRight, Region bottomRight, Region bottomLeft, Pixmap pixmap, int x, int y) {

        // top left corner
        topLeft.drawTo(pixmap, x, y, 0, 0, 3, 2);
        topLeft.drawTo(pixmap, x, y, 0, 2, 2, 1);
        topLeft.drawPixelTo(pixmap, x, y, 0, 3);
        topLeft.drawPixelTo(pixmap, x, y, 3, 0);

        // top right corner
        topRight.drawTo(pixmap, x, y, 6, 0, 2, 2);
        topRight.drawPixelTo(pixmap, x, y, 5, 0);
        topRight.drawPixelTo(pixmap, x, y, 7, 2);

        // bottom right corner
        bottomRight.drawTo(pixmap, x, y, 6, 6, 2, 2);
        bottomRight.drawPixelTo(pixmap, x, y, 7, 5);
        bottomRight.drawPixelTo(pixmap, x, y, 5, 7);

        // bottom left corner
        bottomLeft.drawTo(pixmap, x, y, 0, 6, 3, 2);
        bottomLeft.drawTo(pixmap, x, y, 0, 5, 2, 1);
        bottomLeft.drawPixelTo(pixmap, x, y, 0, 4);
        bottomLeft.drawPixelTo(pixmap, x, y, 3, 7);

        // middle
        middle.drawTo(pixmap, x, y, 2, 2, 5, 4);
        middle.drawTo(pixmap, x, y, 3, 1, 3, 1);
        middle.drawTo(pixmap, x, y, 3, 6, 3, 1);
        middle.drawTo(pixmap, x, y, 1, 3, 1, 2);
        middle.drawTo(pixmap, x, y, 7, 3, 1, 2);
        middle.drawPixelTo(pixmap, x, y, 4, 0);
        middle.drawPixelTo(pixmap, x, y, 4, 7);
    }
}
