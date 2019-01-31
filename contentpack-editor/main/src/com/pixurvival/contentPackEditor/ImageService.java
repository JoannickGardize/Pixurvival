package com.pixurvival.contentPackEditor;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import lombok.Getter;
import lombok.SneakyThrows;

public class ImageService {

	private static final @Getter ImageService instance = new ImageService();

	private Map<String, Image> images = new HashMap<>();

	@SneakyThrows
	private ImageService() {
		File file = new File(getClass().getClassLoader().getResource("images").toURI());
		for (File resourceFile : file.listFiles()) {
			Image image = ImageIO.read(resourceFile);
			String n = resourceFile.getName();
			images.put(n.substring(0, n.lastIndexOf('.')), image);
		}
	}

	public Image get(String name) {
		return images.get(name);
	}

}
