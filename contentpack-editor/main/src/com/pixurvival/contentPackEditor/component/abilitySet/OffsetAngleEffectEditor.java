package com.pixurvival.contentPackEditor.component.abilitySet;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.AngleInput;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;

import javax.swing.*;
import java.awt.*;

public class OffsetAngleEffectEditor extends ElementEditor<OffsetAngleEffect> {

    private static final long serialVersionUID = 1L;

    private ElementChooserButton<Effect> effectChooser = new ElementChooserButton<>(Effect.class);

    public OffsetAngleEffectEditor() {
        super(OffsetAngleEffect.class);
        AngleInput offsetAngleInput = new AngleInput();
        AngleInput randomAngleInput = new AngleInput();

        bind(offsetAngleInput, "offsetAngle");
        bind(randomAngleInput, "randomAngle");
        bind(effectChooser, "effect");

        setupPanel(this, effectChooser, offsetAngleInput, randomAngleInput);
    }

    public static void setupPanel(JPanel panel, ElementChooserButton<Effect> effectChooser, AngleInput offsetAngleInput, AngleInput randomAngleInput) {
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        LayoutUtils.addHorizontalLabelledItem(panel, "offsetAngleEffect.offsetAngle", offsetAngleInput, gbc);
        LayoutUtils.addHorizontalLabelledItem(panel, "generic.randomAngle", randomAngleInput, gbc);
        LayoutUtils.nextColumn(gbc);
        gbc.gridheight = 2;
        panel.add(LayoutUtils.single(LayoutUtils.labelled("elementType.effect", effectChooser)), gbc);
    }
}
