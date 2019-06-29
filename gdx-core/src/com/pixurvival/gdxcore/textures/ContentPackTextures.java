package com.pixurvival.gdxcore.textures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.gdxcore.textures.SpriteSheetPixmap.Region;

import lombok.Getter;

public class ContentPackTextures {

	private Map<SpriteSheet, TextureAnimationSet> animationSet;
	private Map<Integer, Texture> textureShadows;
	private Map<Double, Texture> lightTextures;
	private Texture[][] tileMapTextures;
	private ItemTexture[] itemTextures;
	private int[] tileAvgColors;
	private @Getter double truePixelWidth;
	private @Getter double largestLightRadius;

	public void load(ContentPack pack, int pixelWidth) throws ContentPackException {
		truePixelWidth = 1.0 / (pixelWidth * GameConstants.PIXEL_PER_UNIT);
		textureShadows = new HashMap<>();
		loadAnimationSet(pack, pixelWidth);
		loadTileMapTextures(pack);
		loadItemTextures(pack, pixelWidth);
		loadLights(pack);
	}

	public TextureAnimationSet getAnimationSet(SpriteSheet spriteSheet) {
		return animationSet.get(spriteSheet);
	}

	public Texture getTile(int id, long frame) {
		Texture[] frames = tileMapTextures[id];
		return frames[(int) (frame % frames.length)];
	}

	public int getTileColor(int id) {
		return tileAvgColors[id];
	}

	public ItemTexture getItem(int id) {
		return itemTextures[id];
	}

	public Texture getLightTexture(double radius) {
		return lightTextures.get(radius);
	}

	private void loadAnimationSet(ContentPack pack, int pixelWidth) throws ContentPackException {
		animationSet = new HashMap<>();
		PixelTextureBuilder transform = new PixelTextureBuilder(pixelWidth);
		for (SpriteSheet spriteSheet : pack.getSpriteSheets()) {
			TextureAnimationSet set = new TextureAnimationSet(pack, spriteSheet, transform);
			set.setShadow(getShadow(spriteSheet.getWidth()));
			set.foreachAnimations(a -> a.setShadow(getShadow(a.getShadowWidth())));
			animationSet.put(spriteSheet, set);
		}
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
		textureShadows.put(shadowWidth, texture);
		return texture;
	}

	private void loadTileMapTextures(ContentPack pack) throws ContentPackException {
		List<Tile> tilesbyId = pack.getTiles();
		tileMapTextures = new Texture[tilesbyId.size()][];
		tileAvgColors = new int[tilesbyId.size()];
		Map<String, SpriteSheetPixmap> images = new HashMap<>();
		for (int i = 0; i < tilesbyId.size(); i++) {
			Tile tile = tilesbyId.get(i);
			SpriteSheetPixmap pixmap = images.get(tile.getImage());
			if (pixmap == null) {
				pixmap = new SpriteSheetPixmap(pack.getResource(tile.getImage()), GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT);
				images.put(tile.getImage(), pixmap);
			}
			List<Frame> frames = tile.getFrames();
			Texture[] textures = new Texture[frames.size()];
			for (int j = 0; j < frames.size(); j++) {
				Frame frame = frames.get(j);
				textures[j] = AddPaddingUtil.apply(pixmap.getRegion(frame.getX(), frame.getY()));
			}
			tileMapTextures[i] = textures;
			tileAvgColors[i] = getAverageColor(pixmap.getRegion(frames.get(0).getX(), frames.get(0).getY()));
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
				textureSheet = new TextureSheet(spriteSheetPixmap, new PixelTextureBuilder(pixelWidth));
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
			double radius = structure.getLightEmissionRadius();
			if (radius > 0) {
				lightTextures.put(radius, createLightTexture(radius));
				if (largestLightRadius < radius) {
					largestLightRadius = radius;
				}
			}
		}
	}

	private Texture createLightTexture(double radius) {
		int size = (int) Math.round(radius * GameConstants.PIXEL_PER_UNIT * 2);
		Pixmap pixmap = new Pixmap(size, size, Format.RGBA8888);
		pixmap.setColor(Color.rgba8888(1, 1, 1, 0));
		pixmap.fill();
		pixmap.setColor(Color.WHITE);
		pixmap.fillCircle(pixmap.getWidth() / 2, pixmap.getHeight() / 2 - 1, pixmap.getWidth() / 2 - 1);
		pixmap.setColor(new Color(1, 1, 1, 0.5f));
		pixmap.drawCircle(pixmap.getWidth() / 2, pixmap.getHeight() / 2 - 1, pixmap.getWidth() / 2 - 1);

		Texture texture = new Texture(pixmap);
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		return texture;
	}
}
