package com.pixurvival.contentPackEditor.component.abilitySet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.AngleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;

public class OffsetAngleEffectEditor extends ElementEditor<OffsetAngleEffect> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Effect> effectChooser = new ElementChooserButton<>(Effect.class, true);

	public OffsetAngleEffectEditor() {
		AngleInput offsetAngleInput = new AngleInput();
		AngleInput randomAngleInput = new AngleInput();

		bind(offsetAngleInput, OffsetAngleEffect::getOffsetAngle, OffsetAngleEffect::setOffsetAngle);
		bind(randomAngleInput, OffsetAngleEffect::getRandomAngle, OffsetAngleEffect::setRandomAngle);
		bind(effectChooser, OffsetAngleEffect::getEffect, OffsetAngleEffect::setEffect);

		setupPanel(this, effectChooser, offsetAngleInput, randomAngleInput);
	}

	public static void setupPanel(JPanel panel, ElementChooserButton<Effect> effectChooser, AngleInput offsetAngleInput, AngleInput randomAngleInput) {
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(panel, "offsetAngleEffect.offsetAngle", offsetAngleInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(panel, "generic.randomAngle", randomAngleInput, gbc);
		LayoutUtils.nextColumn(gbc);
		gbc.gridheight = 2;
		panel.add(LayoutUtils.labelled("elementType.effect", effectChooser), gbc);
	}
}
