package com.pixurvival.contentPackEditor;

import lombok.Getter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageService {

    private static final @Getter ImageService instance = new ImageService();

    private Map<String, Image> images = new HashMap<>();

    @SneakyThrows
    private ImageService() {
        loadImage("cross");
        loadImage("down");
        loadImage("elements_icons");
        loadImage("remove");
        loadImage("up");
        loadImage("icon");
        loadImage("color_picker");
    }

    public Image get(String name) {
        return images.get(name);
    }

    private void loadImage(String name) throws IOException {
        Image image = ImageIO.read(getClass().getClassLoader().getResource("images/" + name + ".png"));
        images.put(name, image);
    }
}
