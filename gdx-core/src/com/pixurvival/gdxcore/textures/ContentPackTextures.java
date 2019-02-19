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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item;
import com.pixurvival.gdxcore.textures.SpriteSheetPixmap.Region;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ContentPackTextures {

	private Map<SpriteSheet, TextureAnimationSet> animationSet;
	private Map<Integer, Texture> textureShadows;
	private TextureRegion[][] tileMapTextures;
	private ItemTexture[] itemTextures;
	private int[] tileAvgColors;
	private @Getter double truePixelWidth;

	@AllArgsConstructor
	private class ImageEntry {
		SpriteSheetPixmap spriteSheetPixmap;
		Texture texture;
	}

	public void load(ContentPack pack, int pixelWidth) throws ContentPackException {
		truePixelWidth = 1.0 / (pixelWidth * GameConstants.PIXEL_PER_UNIT);
		textureShadows = new HashMap<>();
		loadAnimationSet(pack, pixelWidth);
		loadTileMapTextures(pack);
		loadItemTextures(pack, pixelWidth);
	}

	public TextureAnimationSet getAnimationSet(SpriteSheet spriteSheet) {
		return animationSet.get(spriteSheet);
	}

	public TextureRegion getTile(int id, long frame) {
		TextureRegion[] frames = tileMapTextures[id];
		return frames[(int) (frame % frames.length)];
	}

	public int getTileColor(int id) {
		return tileAvgColors[id];
	}

	public ItemTexture getItem(int id) {
		return itemTextures[id];
	}

	private void loadAnimationSet(ContentPack pack, int pixelWidth) throws ContentPackException {
		animationSet = new HashMap<>();
		PixelTextureBuilder transform = new PixelTextureBuilder(pixelWidth);
		for (SpriteSheet spriteSheet : pack.getSpriteSheets()) {
			TextureAnimationSet set = new TextureAnimationSet(spriteSheet, transform);
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
		tileMapTextures = new TextureRegion[tilesbyId.size()][];
		tileAvgColors = new int[tilesbyId.size()];
		Map<String, ImageEntry> images = new HashMap<>();
		for (int i = 0; i < tilesbyId.size(); i++) {
			Tile tile = tilesbyId.get(i);
			ImageEntry image = images.get(tile.getImage());
			if (image == null) {
				SpriteSheetPixmap spriteSheetPixmap = new SpriteSheetPixmap(pack.getResource(tile.getImage()),
						GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT);
				Texture texture = new Texture(spriteSheetPixmap);
				texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
				image = new ImageEntry(spriteSheetPixmap, texture);
				images.put(tile.getImage(), image);
			}
			List<Frame> frames = tile.getFrames();
			TextureRegion[] textures = new TextureRegion[frames.size()];
			for (int j = 0; j < frames.size(); j++) {
				Frame frame = frames.get(j);
				textures[j] = new TextureRegion(image.texture, frame.getX() * GameConstants.PIXEL_PER_UNIT,
						frame.getY() * GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT,
						GameConstants.PIXEL_PER_UNIT);
			}
			tileMapTextures[i] = textures;
			tileAvgColors[i] = getAverageColor(
					image.spriteSheetPixmap.getRegion(frames.get(0).getX(), frames.get(0).getY()));
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
				SpriteSheetPixmap spriteSheetPixmap = new SpriteSheetPixmap(pack.getResource(item.getImage()),
						GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT);
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
}
