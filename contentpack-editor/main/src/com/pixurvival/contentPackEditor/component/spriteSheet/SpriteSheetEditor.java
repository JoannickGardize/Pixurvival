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
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class SpriteSheetEditor extends RootElementEditor<SpriteSheet> {

	private static final long serialVersionUID = 1L;

	private SpriteSheetPreview preview = new SpriteSheetPreview();

	public SpriteSheetEditor() {
		super(SpriteSheet.class);
		// Contruction
		ElementChooserButton<ResourceEntry> imageField = new ElementChooserButton<>(ResourcesService.getInstance().getResourcesSupplier());
		IntegerInput widthField = new IntegerInput();
		IntegerInput heightField = new IntegerInput();
		ElementChooserButton<AnimationTemplate> animationTemplateField = new ElementChooserButton<>(AnimationTemplate.class);
		ElementChooserButton<EquipmentOffset> equipmentOffsetField = new ElementChooserButton<>(EquipmentOffset.class, false);
		JTabbedPane previewTabs = new JTabbedPane();
		FloatInput heightOffsetInput = new FloatInput();
		BooleanCheckBox shadowCheckBox = new BooleanCheckBox();
		previewTabs.setBorder(LayoutUtils.createGroupBorder("generic.preview"));
		previewTabs.add(TranslationService.getInstance().getString("generic.image"), preview);

		// Binding
		bind(imageField, "image").getter(v -> v.getImage() == null ? null : ResourcesService.getInstance().getResource(v.getImage())).setter((v, f) -> v.setImage(f == null ? null : f.getName()));
		bind(widthField, "width");
		bind(heightField, "height");
		bind(animationTemplateField, "animationTemplate");
		bind(equipmentOffsetField, "equipmentOffset");
		bind(heightOffsetInput, "heightOffset");
		bind(shadowCheckBox, "shadow");

		// Layouting
		JPanel propertiesPanel = new JPanel(new GridBagLayout());
		propertiesPanel.setBorder(LayoutUtils.createGroupBorder("generic.properties"));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();

		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "generic.image", imageField, gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "spriteSheetEditor.width", widthField, gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "spriteSheetEditor.height", heightField, gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "spriteSheetEditor.heightOffset", heightOffsetInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "spriteSheetEditor.shadow", shadowCheckBox, gbc);
		LayoutUtils.addHorizontalSeparator(propertiesPanel, gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "elementType.animationTemplate", animationTemplateField, gbc);
		LayoutUtils.addHorizontalLabelledItem(propertiesPanel, "elementType.equipmentOffset", equipmentOffsetField, gbc);
		LayoutUtils.addEmptyFiller(propertiesPanel, gbc);

		setLayout(new BorderLayout(10, 0));
		add(propertiesPanel, BorderLayout.WEST);
		add(previewTabs, BorderLayout.CENTER);
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		preview.setSpriteSheet(getValue());
	}
}
