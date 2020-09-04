package com.pixurvival.core.contentPack.map;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

public class ImageMapping<T extends IdentifiedElement> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Valid
	@Required
	@Getter
	@Setter
	private List<ColorMapping<T>> colorMapping = new ArrayList<>();

	private transient Map<Integer, T> colorMap;

	private transient BufferedImage image;

	public void initializeMapping() {
		colorMap = new HashMap<>();
		for (ColorMapping<T> mapping : colorMapping) {
			colorMap.put(mapping.getColor(), mapping.getElement());
		}
	}

	public void initializeImage(ContentPack contentPack, String resourceName) {
		try {
			image = ImageIO.read(new ByteArrayInputStream(contentPack.getResource(resourceName)));
		} catch (IOException e) {
			Log.warn("Image mapping resource not found: " + resourceName);
			image = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
		}
	}

	public T getElementAt(int x, int y) {
		if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
			return null;
		} else {
			return colorMap.get(image.getRGB(x, image.getHeight() - 1 - y));
		}
	}
}
