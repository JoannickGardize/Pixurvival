package com.pixurvival.core.contentPack.validation.handler;

import com.pixurvival.core.contentPack.validation.annotation.SpriteHeight;

import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

public class SpriteHeightHandler extends SpriteDimensionHandler {

    @Override
    public Collection<Class<? extends Annotation>> getHandledAnnotations() {
        return Collections.singleton(SpriteHeight.class);
    }

    @Override
    protected int getDimension(BufferedImage image) {
        return image.getHeight();
    }
}
