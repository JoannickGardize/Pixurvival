package com.pixurvival.contentPackEditor.component.item;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.core.livingEntity.stats.StatModifier;
import com.pixurvival.core.livingEntity.stats.StatType;

public class StatModifierEditor extends ElementEditor<StatModifier> {

	private static final long serialVersionUID = 1L;

	public StatModifierEditor() {

		// Construction

		EnumChooser<StatType> statTypeChooser = new EnumChooser<>(StatType.class);
		EnumChooser<StatModifier.OperationType> typeChooser = new EnumChooser<>(StatModifier.OperationType.class, "statModifier.operationType");
		FloatInput valueInput = new FloatInput();

		// Binding

		bind(statTypeChooser, StatModifier::getStatType, StatModifier::setStatType);
		bind(typeChooser, StatModifier::getOperationType, StatModifier::setOperationType);
		bind(valueInput, StatModifier::getValue, StatModifier::setValue);

		// Layouting

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "statModifierEditor.statType", statTypeChooser, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "statModifierEditor.operationType", typeChooser, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "statModifierEditor.value", valueInput, gbc);
		LayoutUtils.nextColumn(gbc);
	}
}
