package com.pixurvival.core.contentPack.validation;

import java.awt.image.BufferedImage;

import com.pixurvival.core.contentPack.ContentPack;

public interface ImageAccessor {

	default void begin(ContentPack contentPack) {

	}

	/**
	 * @param resourceName
	 *            null is accepted and returns null
	 * @return
	 */
	BufferedImage get(String resourceName);

	default void end() {

	}
}
