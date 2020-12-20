package com.pixurvival.contentPackEditor.component.abilitySet;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.BeanFactory;
import com.pixurvival.contentPackEditor.component.effect.AlterationEditor;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.livingEntity.ability.AlterationAbility;
import com.pixurvival.core.livingEntity.alteration.Alteration;

public abstract class AlterationAbilityEditor<T extends AlterationAbility> extends ElementEditor<T> {

	public AlterationAbilityEditor(Class<T> type) {
		super(type);
	}

	private static final long serialVersionUID = 1L;

	protected void build(boolean useScrollPane, String specificLabel, ValueComponent<?> specificComponent) {
		// Construction

		TimeInput cooldownInput = new TimeInput();
		ListEditor<Alteration> alterationsEditor = new VerticalListEditor<>(AlterationEditor::new, BeanFactory.of(Alteration.class), VerticalListEditor.HORIZONTAL, useScrollPane);

		// Binding

		bind(cooldownInput, "cooldown");
		bind(alterationsEditor, "alterations");

		// Layouting

		alterationsEditor.setBorder(LayoutUtils.createGroupBorder("alterationAbilityEditor.selfAlterations"));
		JPanel headerPanel;
		headerPanel = LayoutUtils.createHorizontalLabelledBox("alterationAbilityEditor.cooldown", cooldownInput, specificLabel, specificComponent);
		LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 1, headerPanel, alterationsEditor);
	}
}
