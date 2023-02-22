package com.pixurvival.contentPackEditor.util;

import lombok.experimental.UtilityClass;

import java.awt.*;
import java.awt.image.BufferedImage;

@UtilityClass
public class ColorUtils {

    /**
     * Compute the "luminance" of the given color, the luminance represents how
     * bright is the color according to human perception.
     *
     * @param color
     * @return the luminance between 0 and 1, the bigger the value is, the brighter
     * the color is for human perception
     */
    public double getLuminance(Color color) {
        double red = color.getRed() / 255.0;
        double green = color.getGreen() / 255.0;
        double blue = color.getBlue() / 255.0;
        return Math.sqrt(0.299 * red * red + 0.587 * green * green + 0.114 * blue * blue);
    }

    public Color getAverageColor(BufferedImage image) {
        float redSum = 0;
        float greenSum = 0;
        float blueSum = 0;
        int width = image.getWidth();
        int height = image.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                redSum += getRed(rgb);
                greenSum += getGreen(rgb);
                blueSum += getBlue(rgb);
            }
        }
        float pixelCount = (float) width * height;
        return new Color(redSum / pixelCount, greenSum / pixelCount, blueSum / pixelCount);
    }

    public static float getRed(int rgb) {
        return ((rgb >> 16) & 0xFF) / 255f;
    }

    public static float getGreen(int rgb) {
        return ((rgb >> 8) & 0xFF) / 255f;
    }

    public static float getBlue(int rgb) {
        return ((rgb >> 0) & 0xFF) / 255f;
    }
}
