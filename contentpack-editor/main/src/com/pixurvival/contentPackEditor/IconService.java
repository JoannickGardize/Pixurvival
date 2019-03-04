package com.pixurvival.contentPackEditor;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import com.pixurvival.contentPackEditor.component.util.GraphicsUtils;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class IconService {

	private static @Getter IconService instance = new IconService();

	@Getter
	@EqualsAndHashCode
	@AllArgsConstructor
	private static class IconKey {
		private String imageName;
		private Frame frame;
	}

	private Map<IconKey, Icon> itemIcons = new HashMap<>();

	private IconService() {
		EventManager.getInstance().register(this);
	}

	public Icon get(Item item) {
		if (item == null || item.getImage() == null || item.getFrame() == null) {
			return null;
		} else {
			return get(item.getImage(), item.getFrame());
		}
	}

	public Icon get(Tile tile) {
		if (tile == null || tile.getImage() == null || tile.getFrames() == null || tile.getFrames().isEmpty()) {
			return null;
		} else {
			return get(tile.getImage(), tile.getFrames().get(0));
		}
	}

	public Icon get(Structure structure) {
		SpriteSheet spriteSheet;
		if ((spriteSheet = structure.getSpriteSheet()) != null && spriteSheet.getImage() != null) {
			ResourceEntry entry = ResourcesService.getInstance().getResource(spriteSheet.getImage());
			if (entry != null && entry.getPreview() instanceof BufferedImage) {
				return GraphicsUtils.createIcon((BufferedImage) entry.getPreview());
			}
		}
		return null;
	}

	public Icon get(String imageName, Frame frame) {
		if (frame == null) {
			return null;
		}
		IconKey key = new IconKey(imageName, frame);
		Icon imageIcon = itemIcons.get(key);
		if (imageIcon == null) {
			ResourceEntry resource = ResourcesService.getInstance().getResource(imageName);
			if (resource == null || !(resource.getPreview() instanceof BufferedImage)) {
				return null;
			}
			try {
				BufferedImage subimage = ((BufferedImage) resource.getPreview()).getSubimage(
						frame.getX() * GameConstants.PIXEL_PER_UNIT, frame.getY() * GameConstants.PIXEL_PER_UNIT,
						GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT);

				imageIcon = GraphicsUtils.createIcon(subimage);
				itemIcons.put(key, imageIcon);
			} catch (RasterFormatException e) {
				return null;
			}
		}
		return imageIcon;
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		itemIcons.clear();
	}

	@EventListener
	public void elementChangedEvent(ElementChangedEvent event) {
		if (event.getElement() instanceof Item) {
			itemIcons.remove(event.getElement());
		}
	}
}