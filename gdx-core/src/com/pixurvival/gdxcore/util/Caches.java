package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.util.Cache;
import com.pixurvival.gdxcore.PixurvivalGame;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Caches {

    public static final Cache<String, GlyphLayout> overlayGlyphLayout = new Cache<>(s -> {
        BitmapFont font = PixurvivalGame.getOverlayFont();
        return new GlyphLayout(font, s);
    });

    public static final Cache<String, GlyphLayout> overlayYellowGlyphLayout = new Cache<>(s -> {
        BitmapFont font = PixurvivalGame.getOverlayFont();
        return new GlyphLayout(font, s, Color.YELLOW, 0f, Align.left, false);
    });

    public static final Cache<String, GlyphLayout> defaultGlyphLayout = new Cache<>(s -> {
        BitmapFont font = PixurvivalGame.getDefaultFont();
        return new GlyphLayout(font, s);
    });
}
