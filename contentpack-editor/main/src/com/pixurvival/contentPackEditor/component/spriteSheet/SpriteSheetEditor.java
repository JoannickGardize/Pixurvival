package com.pixurvival.contentPackEditor.component.spriteSheet;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

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
import com.pixurvival.core.contentPack.validation.handler.AnimationTemplateFramesHandler;
import com.pixurvival.core.contentPack.validation.handler.EquipmentOffsetFramesHandler;

public class SpriteSheetEditor extends RootElementEditor<SpriteSheet> {

	private static final long serialVersionUID = 1L;

	private SpriteSheetPreview preview = new SpriteSheetPreview();
	private SpriteDimensionConstraint widthConstraint = new SpriteDimensionConstraint(BufferedImage::getWidth);
	private SpriteDimensionConstraint heightConstraint = new SpriteDimensionConstraint(BufferedImage::getHeight);
	private FrameNumberConstraint<AnimationTemplate> animationTemplateConstraint = new FrameNumberConstraint<>(
			(spriteSheet, t, frameX, frameY) -> AnimationTemplateFramesHandler.test(t, frameX, frameY));
	private FrameNumberConstraint<EquipmentOffset> equipmentOffsetConstraint = new FrameNumberConstraint<>(
			(spriteSheet, t, frameX, frameY) -> EquipmentOffsetFramesHandler.test(spriteSheet, frameX, frameY));

	public SpriteSheetEditor() {
		super(SpriteSheet.class);
		// Contruction
		ElementChooserButton<ResourceEntry> imageField = new ElementChooserButton<>(ResourcesService.getInstance().getResourcesSupplier());
		IntegerInput widthField = new IntegerInput();
		widthField.setConstraint(widthConstraint);
		IntegerInput heightField = new IntegerInput();
		heightField.setConstraint(heightConstraint);
		ElementChooserButton<AnimationTemplate> animationTemplateField = new ElementChooserButton<>(AnimationTemplate.class);
		animationTemplateField.addAdditionalCondition(animationTemplateConstraint);
		ElementChooserButton<EquipmentOffset> equipmentOffsetField = new ElementChooserButton<>(EquipmentOffset.class);
		equipmentOffsetField.addAdditionalCondition(equipmentOffsetConstraint);
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

		imageField.addValueChangeListener(e -> {
			widthField.updateValue();
			heightField.updateValue();
		});
	}

	@Override
	public void setValue(SpriteSheet value, boolean sneaky) {
		widthConstraint.setSpriteSheet(value);
		heightConstraint.setSpriteSheet(value);
		animationTemplateConstraint.setSpriteSheet(value);
		equipmentOffsetConstraint.setSpriteSheet(value);
		super.setValue(value, sneaky);
	}

	@Override
	public boolean isValueValid(SpriteSheet value) {
		widthConstraint.setSpriteSheet(value);
		heightConstraint.setSpriteSheet(value);
		animationTemplateConstraint.setSpriteSheet(value);
		equipmentOffsetConstraint.setSpriteSheet(value);
		boolean valid = super.isValueValid(value);
		widthConstraint.setSpriteSheet(getValue());
		heightConstraint.setSpriteSheet(getValue());
		animationTemplateConstraint.setSpriteSheet(getValue());
		equipmentOffsetConstraint.setSpriteSheet(getValue());
		return valid;
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		preview.setSpriteSheet(getValue());
	}
}
