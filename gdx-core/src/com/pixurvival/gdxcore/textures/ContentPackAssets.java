package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.SoundPreset;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.util.FileUtils;
import com.pixurvival.gdxcore.textures.SpriteSheetPixmap.Region;
import com.pixurvival.gdxcore.util.EmptySound;
import com.pixurvival.gdxcore.util.InputStreamFileHandleProxy;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.Map.Entry;

public class ContentPackAssets {

    private Map<SpriteSheet, TextureAnimationSet> animationSet;
    private Map<Integer, Texture> textureShadows;
    private Map<Float, Texture> lightTextures;
    private ItemTexture[] itemTextures;
    private int[] tileAvgColors;
    private int[] structureAvgColors;
    private @Getter float truePixelWidth;
    private @Getter float largestLightRadius;
    private SpriteSheetPixmap[] tilePixmaps;
    private Sound[] sounds;

    public void load(ContentPack pack, int pixelWidth, Sound[] presetSounds) throws ContentPackException {
        truePixelWidth = 1.0f / (pixelWidth * GameConstants.PIXEL_PER_UNIT);
        textureShadows = new HashMap<>();
        loadAnimationSet(pack, pixelWidth);
        loadTileMapTextures(pack);
        loadItemTextures(pack, pixelWidth);
        loadLights(pack);
        loadCustomSounds(pack, presetSounds);
    }

    public TextureAnimationSet getAnimationSet(SpriteSheet spriteSheet) {
        return animationSet.get(spriteSheet);
    }

    public SpriteSheetPixmap getTilePixmap(Tile tile) {
        return tilePixmaps[tile.getId()];
    }

    public int getTileColor(int id) {
        return tileAvgColors[id];
    }

    public int getStructureColor(int id) {
        return structureAvgColors[id];
    }

    public ItemTexture getItem(int id) {
        return itemTextures[id];
    }

    public Texture getLightTexture(float radius) {
        return lightTextures.get(radius);
    }

    private void loadAnimationSet(ContentPack pack, int pixelWidth) throws ContentPackException {
        animationSet = new IdentityHashMap<>();
        Map<SpriteSheetImageKey, TextureSheet> textureSheets = new HashMap<>();
        Map<SpriteSheet, SpriteSheetPixmap> pixmaps = new IdentityHashMap<>();
        PixelTextureBuilder transform = new PixelTextureBuilder(pixelWidth);
        for (SpriteSheet spriteSheet : pack.getSpriteSheets()) {
            SpriteSheetImageKey key = new SpriteSheetImageKey(spriteSheet);
            TextureSheet textureSheet = textureSheets.get(key);
            if (textureSheet == null) {
                SpriteSheetPixmap sheetPixmap = new SpriteSheetPixmap(pack.getResource(spriteSheet.getImage()), spriteSheet.getWidth(), spriteSheet.getHeight());
                pixmaps.put(spriteSheet, sheetPixmap);
                textureSheet = new TextureSheet(sheetPixmap, transform);
                textureSheets.put(key, textureSheet);
            }
            TextureAnimationSet set = new TextureAnimationSet(pack, spriteSheet, pixelWidth, textureSheet);
            if (spriteSheet.isShadow()) {
                set.setShadow(getShadow(spriteSheet.getShadowWidth()));
            }
            set.foreachAnimations(a -> a.setShadow(getShadow(a.getShadowWidth())));
            animationSet.put(spriteSheet, set);
        }
        loadStructureAvgColors(pack, pixmaps);
        pixmaps.values().forEach(p -> p.dispose());
        transform.dispose();
    }

    public Texture getShadow(int shadowWidth) {
        Texture texture = textureShadows.get(shadowWidth);
        if (texture != null) {
            return texture;
        }
        int height = shadowWidth / 2;
        Pixmap pixmap = new Pixmap(shadowWidth, height, Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.setBlending(Blending.None);
        int y1 = height / 2;
        int y2 = height % 2 == 0 ? y1 + 1 : y1;
        int xSub = 0;
        int end = height % 2 == 0 ? height / 2 : height / 2 + 1;
        for (int y = 0; y < end; y++) {
            pixmap.drawLine(xSub, y1, shadowWidth - 1 - xSub, y1);
            pixmap.drawLine(xSub, y2, shadowWidth - 1 - xSub, y2);
            y1--;
            y2++;
            xSub += y + 1;
            if (xSub >= shadowWidth / 2) {
                break;
            }
        }
        texture = new Texture(pixmap);
        pixmap.dispose();
        textureShadows.put(shadowWidth, texture);
        return texture;
    }

    public Sound getSound(int id) {
        return sounds[id];
    }

    public void dispose() {
        animationSet.values().forEach(TextureAnimationSet::dispose);
        textureShadows.values().forEach(Texture::dispose);
        lightTextures.values().forEach(Texture::dispose);
        for (ItemTexture texture : itemTextures) {
            texture.dispose();
        }
        if (tilePixmaps != null) {
            for (Pixmap pixmap : tilePixmaps) {
                if (!pixmap.isDisposed()) {
                    pixmap.dispose();
                }
            }
            tilePixmaps = null;
        }
        Arrays.stream(sounds, SoundPreset.values().length, sounds.length).forEach(Sound::dispose);
    }

    private void loadTileMapTextures(ContentPack pack) {
        List<Tile> tilesbyId = pack.getTiles();
        tilePixmaps = new SpriteSheetPixmap[tilesbyId.size()];
        tileAvgColors = new int[tilesbyId.size()];
        Map<String, SpriteSheetPixmap> tilePixmapMap = new HashMap<>();
        for (int i = 0; i < tilesbyId.size(); i++) {
            Tile tile = tilesbyId.get(i);
            SpriteSheetPixmap pixmap = tilePixmapMap.get(tile.getImage());
            if (pixmap == null) {
                pixmap = new SpriteSheetPixmap(pack.getResource(tile.getImage()), GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT);
                tilePixmapMap.put(tile.getImage(), pixmap);
            }
            List<Frame> frames = tile.getFrames();
            tilePixmaps[i] = pixmap;
            tileAvgColors[i] = getAverageColor(pixmap.getRegion(frames.get(0).getX(), frames.get(0).getY()));
        }
    }

    private void loadStructureAvgColors(ContentPack contentPack, Map<SpriteSheet, SpriteSheetPixmap> pixmaps) {
        structureAvgColors = new int[contentPack.getStructures().size()];
        for (int i = 0; i < contentPack.getStructures().size(); i++) {
            Structure structure = contentPack.getStructures().get(i);
            if (structure.getSpriteSheet() != null) {
                Frame frame = structure.getSpriteSheet().getAnimationTemplate().getAnimations().get(ActionAnimation.DEFAULT).getFrames().get(0);
                structureAvgColors[i] = getAverageColor(pixmaps.get(structure.getSpriteSheet()).getRegion(frame.getX(), frame.getY()));
            }
        }
    }

    private int getAverageColor(Region region) {
        float redSum = 0;
        float greenSum = 0;
        float blueSum = 0;
        for (int x = 0; x < region.getWidth(); x++) {
            for (int y = 0; y < region.getHeight(); y++) {
                Color color = new Color(region.getPixel(x, y));
                redSum += color.r;
                greenSum += color.g;
                blueSum += color.b;
            }
        }
        int pixelCount = region.getWidth() * region.getHeight();
        return Color.rgba8888(new Color(redSum / pixelCount, greenSum / pixelCount, blueSum / pixelCount, 1));
    }

    private void loadItemTextures(ContentPack pack, int pixelWidth) throws ContentPackException {
        List<Item> itemsById = pack.getItems();
        itemTextures = new ItemTexture[itemsById.size()];
        Map<String, TextureSheet> images = new HashMap<>();

        for (int i = 0; i < itemsById.size(); i++) {
            Item item = itemsById.get(i);
            TextureSheet textureSheet = images.get(item.getImage());
            if (textureSheet == null) {
                SpriteSheetPixmap spriteSheetPixmap = new SpriteSheetPixmap(pack.getResource(item.getImage()), GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT);
                PixelTextureBuilder builder = new PixelTextureBuilder(pixelWidth);
                textureSheet = new TextureSheet(spriteSheetPixmap, builder);
                builder.dispose();
                images.put(item.getImage(), textureSheet);
            }
            ItemTexture itemTexture = new ItemTexture();
            itemTexture.setTexture(textureSheet.get(item.getFrame().getX(), item.getFrame().getY()));
            TextureMetrics metric = textureSheet.getMetrics(item.getFrame().getX(), item.getFrame().getY());
            itemTexture.setMetrics(metric);
            itemTexture.setShadow(getShadow(metric.getWidth()));
            itemTextures[i] = itemTexture;
        }
    }

    private void loadLights(ContentPack pack) {
        largestLightRadius = 0;
        lightTextures = new HashMap<>();
        for (Structure structure : pack.getStructures()) {
            float radius = structure.getLightEmissionRadius();
            if (radius > 0) {
                lightTextures.put(radius, createLightTexture(radius));
                if (largestLightRadius < radius) {
                    largestLightRadius = radius;
                }
            }
        }
    }

    private void loadCustomSounds(ContentPack pack, Sound[] presetSounds) {
        sounds = new Sound[pack.getSoundIdByName().size() + presetSounds.length];
        System.arraycopy(presetSounds, 0, sounds, 0, presetSounds.length);
        for (Entry<String, Integer> sound : pack.getSoundIdByName().entrySet()) {
            byte[] data = pack.getResource(sound.getKey());
            try {
                sounds[sound.getValue()] = Gdx.audio.newSound(new InputStreamFileHandleProxy(() -> new ByteArrayInputStream(data), FileUtils.fileExtensionOf(sound.getKey())));
            } catch (Exception e) {
                Log.error("Error when trying to load the sound " + sound.getKey(), e);
                sounds[sound.getValue()] = EmptySound.getInstance();
            }
        }
    }

    private Texture createLightTexture(float radius) {
        int size = Math.round(radius * GameConstants.PIXEL_PER_UNIT * 2);
        Pixmap pixmap = new Pixmap(size, size, Format.RGBA8888);
        pixmap.setColor(Color.rgba8888(1, 1, 1, 0));
        pixmap.fill();
        int halfWidth = pixmap.getWidth() / 2;
        for (int i = halfWidth - 1; i >= 0; i -= 1) {
            pixmap.setColor(new Color(1, 1, 1, 1f - ((float) i / halfWidth)));
            pixmap.fillCircle(halfWidth, halfWidth, i);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        return texture;
    }
}
