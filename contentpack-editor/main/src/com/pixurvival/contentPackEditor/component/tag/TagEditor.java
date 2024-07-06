package com.pixurvival.contentPackEditor.component.tag;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.ImageFramePreview;
import com.pixurvival.contentPackEditor.component.constraint.UnitSpriteFrameConstraint;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.item.ItemFrameChooserPopup;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetPreview;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.InteractionListener;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.*;
import com.pixurvival.core.contentPack.tag.Tag;
import com.pixurvival.core.contentPack.tag.TagStackPolicy;

import javax.swing.*;
import java.awt.*;

public class TagEditor extends RootElementEditor<Tag> implements InteractionListener {

    private ImageFramePreview itemPreview = new ImageFramePreview();
    private ItemFrameChooserPopup itemFrameChooserPopup = new ItemFrameChooserPopup();
    private FrameEditor displayIconFrameEditor = new FrameEditor();
    private UnitSpriteFrameConstraint frameConstraint = new UnitSpriteFrameConstraint();

    public TagEditor() {
        super(Tag.class);

        // Construction

        // TODO Image constraint
        StringInput displayNameInput = new StringInput();
        ColorPanel colorPanel = new ColorPanel();

        CPEButton frameChooser = new CPEButton("generic.select");
        frameChooser.addAction(() -> {
            if (ResourcesService.getInstance().containsResource(getValue().getDisplayIconImage())) {
                itemFrameChooserPopup.show(frameChooser, getValue().getDisplayIconImage());
            }
        });
        ElementChooserButton<ResourceEntry> displayIconImageButton = new ElementChooserButton<>(ResourcesService.getInstance().getResourcesSupplier());
        FloatInput defaultValueInput = new FloatInput();
        EnumChooser<TagStackPolicy> valueStackPolicyChooser = new EnumChooser<>(TagStackPolicy.class);
        EnumChooser<TagStackPolicy> durationStackPolicyChooser = new EnumChooser<>(TagStackPolicy.class);

        // binding

        bind(displayNameInput, "displayName");
        bind(colorPanel.getColorInput(), "color");
        bind(displayIconFrameEditor, "displayIconFrame");
        bind(displayIconImageButton, "displayIconImage")
                .getter(v -> v.getDisplayIconImage() == null ? null : ResourcesService.getInstance().getResource(v.getDisplayIconImage()))
                .setter((v, f) -> v.setDisplayIconImage(f == null ? null : f.getName()));
        bind(defaultValueInput, "defaultValue");
        bind(valueStackPolicyChooser, "valueStackPolicy");
        bind(durationStackPolicyChooser, "durationStackPolicy");

        itemFrameChooserPopup.addInteractionListener(this);
        displayIconImageButton.addValueChangeListener(v -> itemPreview.setImage(v == null ? null : v.getName()));
        displayIconFrameEditor.addValueChangeListener(v -> itemPreview.setFrame(v));
        displayIconFrameEditor.setAdditionalConstraint(frameConstraint);

        // Layouting

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        displayPanel.setBorder(LayoutUtils.createGroupBorder("generic.display"));
        LayoutUtils.addHorizontalLabelledItem(displayPanel, "tagEditor.displayName", displayNameInput, gbc);
        LayoutUtils.addHorizontalLabelledItem(displayPanel, "tagEditor.color", colorPanel, gbc);
        LayoutUtils.nextColumn(gbc);
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        displayPanel.add(itemPreview, gbc);
        LayoutUtils.nextColumn(gbc);
        gbc.fill = GridBagConstraints.NONE;
        LayoutUtils.addHorizontalLabelledItem(displayPanel, "tagEditor.displayIconImage", displayIconImageButton, gbc);
        LayoutUtils.addHorizontalLabelledItem(displayPanel, "generic.frame",
                LayoutUtils.createHorizontalBox(displayIconFrameEditor, frameChooser), gbc);
        JPanel propertiesPanel = LayoutUtils.createVerticalLabelledBox(
                "tagEditor.defaultValue", defaultValueInput,
                "tagEditor.valueStackPolicy", valueStackPolicyChooser,
                "tagEditor.durationStackPolicy", durationStackPolicyChooser
        );
        propertiesPanel.setBorder(LayoutUtils.createGroupBorder("generic.properties"));
        LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 0, displayPanel, propertiesPanel);
    }

    @Override
    public void setValue(Tag value, boolean sneaky) {
        frameConstraint.setElement(value);
        super.setValue(value, sneaky);
    }

    @Override
    public void interactionPerformed(Object data) {
        SpriteSheetPreview.ClickEvent clickEvent = (SpriteSheetPreview.ClickEvent) data;
        getValue().getDisplayIconFrame().setX(clickEvent.getSpriteX());
        getValue().getDisplayIconFrame().setY(clickEvent.getSpriteY());
        displayIconFrameEditor.setValue(getValue().getDisplayIconFrame());
        displayIconFrameEditor.notifyValueChanged();
        itemFrameChooserPopup.setVisible(false);
    }

    @Override
    public boolean isValueValid(Tag value) {
        frameConstraint.setElement(value);
        boolean valid = super.isValueValid(value);
        frameConstraint.setElement(getValue());
        return valid;
    }
}
