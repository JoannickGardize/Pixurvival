package com.pixurvival.core.contentPack.validation;

import com.pixurvival.core.contentPack.ContentPack;

import java.awt.image.BufferedImage;

public interface ImageAccessor {

    default void begin(ContentPack contentPack) {

    }

    /**
     * @param resourceName null is accepted and returns null
     * @return
     */
    BufferedImage get(String resourceName);

    default void end() {

    }
}
