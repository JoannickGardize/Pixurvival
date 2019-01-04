package com.pixurvival.contentPackEditor.component.spriteSheet;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementChooserButton;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Getter;

public class SpriteSheetChooserPreviewTabs extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTabbedPane previewTabs = new JTabbedPane();
	private @Getter ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<SpriteSheet>(LayoutUtils.getSpriteSheetIconProvider());
	private @Getter SpriteSheetPreview spriteSheetPreview = new SpriteSheetPreview();

	public SpriteSheetChooserPreviewTabs() {
		super(new BorderLayout());
		setBorder(LayoutUtils.createGroupBorder("generic.preview"));
		add(spriteSheetChooser, BorderLayout.NORTH);
		add(previewTabs, BorderLayout.CENTER);
		previewTabs.add(TranslationService.getInstance().getString("generic.image"), spriteSheetPreview);

		spriteSheetChooser.addValueChangeListener(s -> {
			spriteSheetPreview.setSpriteSheet(s);
		});
		EventManager.getInstance().register(this);
	}

	public void addTab(String name, Component component) {
		previewTabs.add(name, component);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		spriteSheetChooser.setItems(event.getContentPack().getSpriteSheets());
	}
}
