package com.pixurvival.contentPackEditor.component.item;

import java.awt.Component;
import java.awt.Image;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetPreview;
import com.pixurvival.contentPackEditor.component.util.InteractionListener;
import com.pixurvival.contentPackEditor.component.util.RelativePopup;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class ItemFrameChooserPopup extends RelativePopup {

	private static final long serialVersionUID = 1L;

	private SpriteSheet spriteSheet = new SpriteSheet();
	private SpriteSheetPreview preview = new SpriteSheetPreview();

	public ItemFrameChooserPopup() {
		spriteSheet.setHeight(8);
		spriteSheet.setWidth(8);
		preview.setSpriteSheet(spriteSheet);
		setContentPane(preview);
	}

	public void show(Component relativeTo, String imageResourceName) {
		spriteSheet.setImage(imageResourceName);
		ResourceEntry resource = ResourcesService.getInstance().getResource(imageResourceName);
		if (!(resource.getPreview() instanceof Image)) {
			return;
		}
		Image image = (Image) resource.getPreview();
		setSize(image.getWidth(null) * 8, image.getHeight(null) * 8);
		show(relativeTo);
	}

	public void addInteractionListener(InteractionListener listener) {
		preview.addInteractionListener(listener);
	}

}
