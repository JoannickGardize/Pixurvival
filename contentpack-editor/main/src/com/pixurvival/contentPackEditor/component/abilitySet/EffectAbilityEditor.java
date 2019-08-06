package com.pixurvival.contentPackEditor.component.abilitySet;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.livingEntity.ability.EffectAbility;

public class EffectAbilityEditor extends ElementEditor<EffectAbility> {

	private static final long serialVersionUID = 1L;

	private ListEditor<OffsetAngleEffect> effectsEditor;
	private ElementChooserButton<Item> ammunitionChooser = new ElementChooserButton<>(Item.class, IconService.getInstance()::get, false);

	public EffectAbilityEditor(boolean showAmmunitionChooser, boolean useScrollPane) {

		// Construction

		TimeInput cooldownInput = new TimeInput();
		effectsEditor = new VerticalListEditor<>(OffsetAngleEffectEditor::new, OffsetAngleEffect::new, VerticalListEditor.HORIZONTAL, useScrollPane);

		// Binding

		bind(cooldownInput, EffectAbility::getCooldown, EffectAbility::setCooldown);
		bind(ammunitionChooser, EffectAbility::getAmmunition, EffectAbility::setAmmunition);
		bind(effectsEditor, EffectAbility::getOffsetAngleEffects, EffectAbility::setOffsetAngleEffects);

		// Layouting

		effectsEditor.setBorder(LayoutUtils.createGroupBorder("effectAbilityEditor.effects"));
		JPanel headerPanel;
		if (showAmmunitionChooser) {
			headerPanel = LayoutUtils.createHorizontalLabelledBox("effectAbilityEditor.cooldown", cooldownInput, "effectAbilityEditor.ammunition", ammunitionChooser);
		} else {
			headerPanel = LayoutUtils.labelled("effectAbilityEditor.cooldown", cooldownInput);
		}
		LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 1, headerPanel, effectsEditor);

	}
}
