package com.pixurvival.contentPackEditor.component;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.IntegerInput;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class SpriteSheetEditor extends RootElementEditor<SpriteSheet> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<ResourceEntry> imageField = new ElementChooserButton<>(e -> e.getIcon());
	private IntegerInput widthField = new IntegerInput();
	private IntegerInput heightField = new IntegerInput();
	private ElementChooserButton<AnimationTemplate> animationTemplateField = new ElementChooserButton<>(e -> null);
	private ElementChooserButton<EquipmentOffset> equipmentOffsetField = new ElementChooserButton<>(e -> null);
	private JTabbedPane previewTabs = new JTabbedPane();
	private SpriteSheetPreview preview = new SpriteSheetPreview();

	public SpriteSheetEditor() {
		EventManager.getInstance().register(this);

		// Contruction
		imageField.setItems(ResourcesService.getInstance().getResources());

		previewTabs.setBorder(LayoutUtils.createGroupBorder("generic.preview"));
		previewTabs.add(TranslationService.getInstance().getString("generic.image"), preview);

		// Binding
		addSubValue(imageField, v -> imageField.setValue(v.getImage() == null ? null : ResourcesService.getInstance().getResource(v.getImage())), (v, f) -> v.setImage(f.getName()));
		addSubValue(widthField, v -> widthField.setValue(v.getWidth()), (v, f) -> v.setWidth(f));
		addSubValue(heightField, v -> heightField.setValue(v.getHeight()), (v, f) -> v.setHeight(f));
		addSubValue(animationTemplateField, v -> animationTemplateField.setValue(v.getAnimationTemplate()), (v, f) -> v.setAnimationTemplate(f));
		addSubValue(equipmentOffsetField, v -> equipmentOffsetField.setValue(v.getEquipmentOffset()), (v, f) -> v.setEquipmentOffset(f));

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
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "elementType.animationTemplate", animationTemplateField, gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "elementType.equipmentOffset", equipmentOffsetField, gbc);
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
	protected void valueChanged() {
		preview.setSpriteSheet(getValue());
	}
}
