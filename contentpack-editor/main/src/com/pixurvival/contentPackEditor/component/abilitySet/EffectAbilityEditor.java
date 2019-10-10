package com.pixurvival.contentPackEditor.component.abilitySet;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.effect.AlterationEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;
import com.pixurvival.core.livingEntity.ability.EffectAbility;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatAlteration;

public class EffectAbilityEditor extends ElementEditor<EffectAbility> {

	private static final long serialVersionUID = 1L;

	public EffectAbilityEditor(boolean showAmmunitionChooser, boolean useScrollPane) {

		// Construction

		ListEditor<OffsetAngleEffect> effectsEditor;
		ItemStackEditor ammunitionEditor = new ItemStackEditor(false);
		TimeInput cooldownInput = new TimeInput();
		effectsEditor = new VerticalListEditor<>(OffsetAngleEffectEditor::new, OffsetAngleEffect::new, VerticalListEditor.HORIZONTAL, useScrollPane);
		ListEditor<Alteration> selfAlterationsEditor = new VerticalListEditor<>(AlterationEditor::new, StatAlteration::new, VerticalListEditor.HORIZONTAL, useScrollPane);

		// Binding

		bind(cooldownInput, EffectAbility::getCooldown, EffectAbility::setCooldown);
		bind(ammunitionEditor, EffectAbility::getAmmunition, EffectAbility::setAmmunition);
		bind(effectsEditor, EffectAbility::getOffsetAngleEffects, EffectAbility::setOffsetAngleEffects);
		bind(selfAlterationsEditor, EffectAbility::getSelfAlterations, EffectAbility::setSelfAlterations);

		// Layouting

		selfAlterationsEditor.setBorder(LayoutUtils.createGroupBorder("effectAbilityEditor.selfAlterations"));
		effectsEditor.setBorder(LayoutUtils.createGroupBorder("effectAbilityEditor.effects"));
		JPanel headerPanel;
		if (showAmmunitionChooser) {
			headerPanel = LayoutUtils.createHorizontalLabelledBox("effectAbilityEditor.cooldown", cooldownInput, "effectAbilityEditor.ammunition", ammunitionEditor);
		} else {
			headerPanel = LayoutUtils.createHorizontalLabelledBox("effectAbilityEditor.cooldown", cooldownInput);
		}
		LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 1, headerPanel, LayoutUtils.createVerticalBox(selfAlterationsEditor, effectsEditor));

	}
}
