package com.pixurvival.gdxcore.graphics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackReadException;
import com.pixurvival.core.contentPack.Frame;
import com.pixurvival.core.contentPack.SpriteSheet;
import com.pixurvival.core.contentPack.Tile;
import com.pixurvival.gdxcore.graphics.SpriteSheetPixmap.Region;

public class ContentPackTextures {

	private Map<String, TextureAnimationSet> animationSet;
	private Map<Integer, Texture> textureShadows;
	private TextureRegion[][] tileMapTextures;
	private int[] tileAvgColors;

	public void load(ContentPack pack, int pixelWidth) throws ContentPackReadException {
		textureShadows = new HashMap<>();
		loadAnimationSet(pack, pixelWidth);
		loadTileMapTextures(pack);
	}

	public TextureAnimationSet getAnimationSet(String name) {
		return animationSet.get(name);
	}

	public TextureRegion getTile(int id, long frame) {
		TextureRegion[] frames = tileMapTextures[id];
		return frames[(int) (frame % frames.length)];
	}

	public int getTileColor(int id) {
		return tileAvgColors[id];
	}

	private void loadAnimationSet(ContentPack pack, int pixelWidth) throws ContentPackReadException {
		animationSet = new HashMap<>();
		PixelTextureBuilder transform = new PixelTextureBuilder(pixelWidth);
		for (Entry<String, SpriteSheet> entries : pack.getSprites().all().entrySet()) {
			TextureAnimationSet set = new TextureAnimationSet(entries.getValue(), transform);
			set.setShadow(getShadow(entries.getValue().getWidth()));
			animationSet.put(entries.getKey(), set);
		}
	}

	private Texture getShadow(int shadowWidth) {
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

	private void loadTileMapTextures(ContentPack pack) throws ContentPackReadException {
		List<Tile> tilesbyId = pack.getTilesById();
		tileMapTextures = new TextureRegion[tilesbyId.size()][];
		tileAvgColors = new int[tilesbyId.size()];
		SpriteSheetPixmap spriteSheetPixmap = new SpriteSheetPixmap(pack.getTiles().getImage().read(),
				World.PIXEL_PER_UNIT, World.PIXEL_PER_UNIT);
		Texture texture = new Texture(spriteSheetPixmap);
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		for (int i = 0; i < tilesbyId.size(); i++) {
			Tile tile = tilesbyId.get(i);
			Frame[] frames = tile.getFrames();
			TextureRegion[] textures = new TextureRegion[frames.length];
			for (int j = 0; j < frames.length; j++) {
				Frame frame = frames[j];
				textures[j] = new TextureRegion(texture, frame.getX() * World.PIXEL_PER_UNIT,
						frame.getY() * World.PIXEL_PER_UNIT, World.PIXEL_PER_UNIT, World.PIXEL_PER_UNIT);
			}
			tileMapTextures[i] = textures;
			tileAvgColors[i] = getAverageColor(spriteSheetPixmap.getRegion(frames[0].getX(), frames[0].getY()));
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
		float pixelCount = region.getWidth() * region.getHeight();
		return Color.rgba8888(new Color(redSum / pixelCount, greenSum / pixelCount, blueSum / pixelCount, 1));
		// return Color.rgba8888(new Color(region.getPixel(0, 0)));
	}
}
