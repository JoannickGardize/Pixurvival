package com.pixurvival.contentPackEditor;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import lombok.Getter;

public class ImageService {

	private static final @Getter ImageService instance = new ImageService();

	private Map<String, Image> images = new HashMap<>();

	private ImageService() {
		try {
			File file = new File(getClass().getClassLoader().getResource("images").toURI());
			for (File resourceFile : file.listFiles()) {
				try {
					Image image = ImageIO.read(resourceFile);
					String n = resourceFile.getName();
					images.put(n.substring(0, n.lastIndexOf('.')), image);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	public Image get(String name) {
		return images.get(name);
	}

}
