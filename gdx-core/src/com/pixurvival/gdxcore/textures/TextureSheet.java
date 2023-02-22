package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.pixurvival.gdxcore.textures.SpriteSheetPixmap.Region;
import lombok.Getter;

import java.util.function.Function;

public class TextureSheet {

    private Texture[] textures;
    private @Getter int sizeX;
    private @Getter int sizeY;
    private TextureMetrics[] metrics;

    public TextureSheet(SpriteSheetPixmap spriteSheetPixmap, Function<Region, Pixmap> transform) {
        sizeX = spriteSheetPixmap.getTileCountX();
        sizeY = spriteSheetPixmap.getTileCountY();
        textures = new Texture[sizeX * sizeY];
        metrics = new TextureMetrics[sizeX * sizeY];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Region region = spriteSheetPixmap.getRegion(x, y);
                Texture texture = new Texture(transform.apply(region));
                texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
                int index = x + y * sizeX;
                textures[index] = texture;
                metrics[index] = buildMetrics(region);
            }
        }
    }

    public Texture get(int x, int y) {
        return textures[x + y * sizeX];
    }

    public TextureMetrics getMetrics(int x, int y) {
        return metrics[x + y * sizeX];
    }

    private TextureMetrics buildMetrics(Region region) {
        TextureMetrics metric = new TextureMetrics();
        // x offset
        for (int x = 0; x < region.getWidth(); x++) {
            if (!verticalLineIsTransparent(region, x)) {
                metric.setOffsetX(x);
                break;
            }
        }
        // y offset
        for (int y = 0; y < region.getHeight(); y++) {
            if (!horizontalLineIsTransparent(region, region.getHeight() - 1 - y)) {
                metric.setOffsetY(y);
                break;
            }
        }

        // width
        for (int x = 0; x < region.getWidth(); x++) {
            if (!verticalLineIsTransparent(region, region.getWidth() - 1 - x)) {
                metric.setWidth(region.getWidth() - x - metric.getOffsetX());
                break;
            }
        }

        // height
        for (int y = 0; y < region.getHeight(); y++) {
            if (!horizontalLineIsTransparent(region, y)) {
                metric.setHeight(region.getHeight() - y - metric.getOffsetY());
                break;
            }
        }

        metric.computeWorldUnits();
        return metric;
    }

    private boolean isTransparent(int rgba) {
        return (rgba & 0x000000ff) == 0;
    }

    private boolean horizontalLineIsTransparent(Region region, int y) {
        for (int x = 0; x < region.getWidth(); x++) {
            if (!isTransparent(region.getPixel(x, y))) {
                return false;
            }
        }
        return true;
    }

    private boolean verticalLineIsTransparent(Region region, int x) {
        for (int y = 0; y < region.getHeight(); y++) {
            if (!isTransparent(region.getPixel(x, y))) {
                return false;
            }
        }
        return true;
    }

}
