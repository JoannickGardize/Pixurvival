package com.pixurvival.contentPackEditor;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import com.pixurvival.contentPackEditor.component.util.GraphicsUtils;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.item.Item;

import lombok.Getter;

public class IconService {

	private static @Getter IconService instance = new IconService();

	private Map<Item, Icon> itemIcons = new HashMap<>();

	private IconService() {
		EventManager.getInstance().register(this);
	}

	public Icon get(Item item) {
		Frame frame = item.getFrame();
		if (frame == null) {
			return null;
		}
		Icon imageIcon = itemIcons.get(item);
		if (imageIcon == null) {
			ResourceEntry resource = ResourcesService.getInstance().getResource(item.getImage());
			if (resource == null) {
				return null;
			}
			if (!(resource.getPreview() instanceof BufferedImage)) {
				return null;
			}
			BufferedImage subimage = ((BufferedImage) resource.getPreview()).getSubimage(frame.getX() * GameConstants.PIXEL_PER_UNIT, frame.getY() * GameConstants.PIXEL_PER_UNIT,
					GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT);
			imageIcon = GraphicsUtils.createIcon(subimage);
			itemIcons.put(item, imageIcon);
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
