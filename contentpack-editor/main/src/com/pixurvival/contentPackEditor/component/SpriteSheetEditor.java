package com.pixurvival.contentPackEditor.component;

import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.ImageIcon;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class SpriteSheetEditor extends ElementEditor<SpriteSheet> {

	private static final long serialVersionUID = 1L;

	private AutoCompleteTextField<ResourceEntry> imageField = new AutoCompleteTextField<>(e -> {
		if (e.getPreview() instanceof Image) {
			return new ImageIcon((Image) e.getPreview());
		}
		return null;
	});

	public SpriteSheetEditor() {
		setLayout(new GridBagLayout());
		imageField.setItems(ResourcesService.getInstance().getResources());
		add(imageField);
	}

	@Override
	protected void elementChanged(SpriteSheet element) {

	}

}
