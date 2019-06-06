package com.pixurvival.gdxcore.textures;

import java.util.function.Function;

import com.badlogic.gdx.graphics.Pixmap;
import com.pixurvival.gdxcore.textures.SpriteSheetPixmap.Region;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PixelTextureBuilder implements Function<Region, Pixmap> {

	private int pixelWidth;
	private int borderColor = 0x000000ff;

	public PixelTextureBuilder(int pixelWidth) {
		super();
		this.pixelWidth = pixelWidth;
	}

	@Override
	public Pixmap apply(Region region) {
		Pixmap result = new Pixmap(region.getWidth() * pixelWidth + 2, region.getHeight() * pixelWidth + 2, region.getFormat());
		for (int px = 0; px < region.getWidth(); px++) {
			for (int py = 0; py < region.getHeight(); py++) {
				result.setColor(region.getPixel(px, py));
				result.fillRectangle(px * pixelWidth + 1, py * pixelWidth + 1, pixelWidth, pixelWidth);
				result.setColor(borderColor);
				if (!isTransparent(region.getPixel(px, py))) {
					if (px == 0 || isTransparent(region.getPixel(px - 1, py))) {
						int x = px * pixelWidth;
						result.drawLine(x, py * pixelWidth + 1, x, (py + 1) * pixelWidth);
					}
					if (px == region.getWidth() - 1 || isTransparent(region.getPixel(px + 1, py))) {
						int x = (px + 1) * pixelWidth + 1;
						result.drawLine(x, py * pixelWidth + 1, x, (py + 1) * pixelWidth);
					}
					if (py == 0 || isTransparent(region.getPixel(px, py - 1))) {
						int y = py * pixelWidth;
						result.drawLine(px * pixelWidth + 1, y, (px + 1) * pixelWidth, y);
					}
					if (py == region.getHeight() - 1 || isTransparent(region.getPixel(px, py + 1))) {
						int y = (py + 1) * pixelWidth + 1;
						result.drawLine(px * pixelWidth + 1, y, (px + 1) * pixelWidth, y);
					}
					if (px == 0 || py == 0 || isTransparent(region.getPixel(px - 1, py - 1))) {
						result.drawPixel(px * pixelWidth, py * pixelWidth);
					}
					if (px == 0 || py == region.getHeight() - 1 || isTransparent(region.getPixel(px - 1, py + 1))) {
						result.drawPixel(px * pixelWidth, (py + 1) * pixelWidth + 1);
					}
					if (px == region.getWidth() - 1 || py == 0 || isTransparent(region.getPixel(px + 1, py - 1))) {
						result.drawPixel((px + 1) * pixelWidth + 1, py * pixelWidth);
					}
					if (px == region.getWidth() - 1 || py == region.getHeight() - 1 || isTransparent(region.getPixel(px + 1, py + 1))) {
						result.drawPixel((px + 1) * pixelWidth + 1, (py + 1) * pixelWidth + 1);
					}
				}
			}
		}
		return result;
	}

	private boolean isTransparent(int rgba) {
		return (rgba & 0x000000ff) == 0;
	}

}
