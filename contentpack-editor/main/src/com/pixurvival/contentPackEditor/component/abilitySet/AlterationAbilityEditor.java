package com.pixurvival.contentPackEditor.component.abilitySet;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.BeanFactory;
import com.pixurvival.contentPackEditor.component.effect.AlterationEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.livingEntity.ability.AlterationAbility;
import com.pixurvival.core.livingEntity.alteration.Alteration;

public class AlterationAbilityEditor<T extends AlterationAbility> extends ElementEditor<T> {

	private static final long serialVersionUID = 1L;

	public AlterationAbilityEditor(boolean showAmmunitionChooser, boolean useScrollPane) {

		// Construction

		ItemStackEditor ammunitionEditor = new ItemStackEditor(false);
		TimeInput cooldownInput = new TimeInput();
		ListEditor<Alteration> alterationsEditor = new VerticalListEditor<>(AlterationEditor::new, BeanFactory.of(Alteration.class), VerticalListEditor.HORIZONTAL, useScrollPane);

		// Binding

		bind(cooldownInput, AlterationAbility::getCooldown, AlterationAbility::setCooldown);
		bind(alterationsEditor, AlterationAbility::getAlterations, AlterationAbility::setAlterations);

		// Layouting

		alterationsEditor.setBorder(LayoutUtils.createGroupBorder("effectAbilityEditor.selfAlterations"));
		JPanel headerPanel;
		if (showAmmunitionChooser) {
			headerPanel = LayoutUtils.createHorizontalLabelledBox("effectAbilityEditor.cooldown", cooldownInput, "effectAbilityEditor.ammunition", ammunitionEditor);
		} else {
			headerPanel = LayoutUtils.createHorizontalLabelledBox("effectAbilityEditor.cooldown", cooldownInput);
		}
		LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 1, headerPanel, alterationsEditor);
	}
}
