package com.pixurvival.contentPackEditor.component.item;

import java.awt.Rectangle;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.ResourcePreview;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.item.Item;

public class ItemPreview extends ResourcePreview {

	private static final long serialVersionUID = 1L;

	public void setItem(Item item) {
		setImage(item.getImage());
		setFrame(item.getFrame());
		repaint();
	}

	private void setImage(String image) {
		ResourceEntry entry = ResourcesService.getInstance().getResource(image);
		if (entry == null) {
			setObject(null);
		} else {
			setObject(entry.getPreview());
		}
	}

	private void setFrame(Frame frame) {
		setRectangle(new Rectangle(frame.getX() * GameConstants.PIXEL_PER_UNIT, frame.getY() * GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT));
	}
}
