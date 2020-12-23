package com.pixurvival.core.contentPack.validation;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.pixurvival.core.contentPack.ContentPack;

public class DefaultImageAccessor implements ImageAccessor {

	private Map<String, BufferedImage> images = new HashMap<>();
	private ContentPack contentPack;

	@Override
	public void begin(ContentPack contentPack) {
		this.contentPack = contentPack;
		images.clear();
	}

	@Override
	public BufferedImage get(String resourceName) {
		return images.computeIfAbsent(resourceName, r -> {
			try {
				byte[] data = contentPack.getResource(r);
				if (data == null) {
					return null;
				} else {
					return ImageIO.read(new ByteArrayInputStream(data));
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	@Override
	public void end() {
		images.clear();
	}

}
