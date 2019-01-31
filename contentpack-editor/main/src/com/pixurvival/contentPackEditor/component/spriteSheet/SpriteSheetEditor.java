package com.pixurvival.contentPackEditor.component.spriteSheet;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class SpriteSheetEditor extends RootElementEditor<SpriteSheet> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<ResourceEntry> imageField = new ElementChooserButton<>(ResourceEntry::getIcon);
	private IntegerInput widthField = new IntegerInput(Bounds.min(1));
	private IntegerInput heightField = new IntegerInput(Bounds.min(1));
	private ElementChooserButton<AnimationTemplate> animationTemplateField = new ElementChooserButton<>(e -> null);
	private ElementChooserButton<EquipmentOffset> equipmentOffsetField = new ElementChooserButton<>(e -> null, false);
	private JTabbedPane previewTabs = new JTabbedPane();
	private SpriteSheetPreview preview = new SpriteSheetPreview();

	public SpriteSheetEditor() {
		// Contruction
		imageField.setItems(ResourcesService.getInstance().getResources());
		previewTabs.setBorder(LayoutUtils.createGroupBorder("generic.preview"));
		previewTabs.add(TranslationService.getInstance().getString("generic.image"), preview);

		// Binding
		bind(imageField, v -> v.getImage() == null ? null : ResourcesService.getInstance().getResource(v.getImage()),
				(v, f) -> v.setImage(f == null ? null : f.getName()));
		bind(widthField, SpriteSheet::getWidth, SpriteSheet::setWidth);
		bind(heightField, SpriteSheet::getHeight, SpriteSheet::setHeight);
		bind(animationTemplateField, SpriteSheet::getAnimationTemplate, SpriteSheet::setAnimationTemplate);
		bind(equipmentOffsetField, SpriteSheet::getEquipmentOffset, SpriteSheet::setEquipmentOffset);

		// Layouting
		JPanel propertiesPanel = new JPanel(new GridBagLayout());
		propertiesPanel.setBorder(LayoutUtils.createGroupBorder("generic.properties"));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets.top = 3;
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "generic.image", imageField, gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "spriteSheetEditor.width", widthField, gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "spriteSheetEditor.height", heightField, gbc);
		LayoutUtils.addHorizontalSeparator(propertiesPanel, gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "elementType.animationTemplate", animationTemplateField,
				gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "elementType.equipmentOffset", equipmentOffsetField,
				gbc);
		LayoutUtils.addEmptyFiller(propertiesPanel, gbc);

		setLayout(new BorderLayout(10, 0));
		add(propertiesPanel, BorderLayout.WEST);
		add(previewTabs, BorderLayout.CENTER);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		animationTemplateField.setItems(event.getContentPack().getAnimationTemplates());
		equipmentOffsetField.setItems(event.getContentPack().getEquipmentOffsets());
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		preview.setSpriteSheet(getValue());
	}
}
