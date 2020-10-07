package com.pixurvival.contentPackEditor.component.item;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.PercentInput;
import com.pixurvival.core.livingEntity.stats.StatModifier;
import com.pixurvival.core.livingEntity.stats.StatModifier.OperationType;
import com.pixurvival.core.livingEntity.stats.StatType;

public class StatModifierEditor extends ElementEditor<StatModifier> {

	private static final long serialVersionUID = 1L;

	private boolean programmaticChange = false;

	public StatModifierEditor() {

		// Construction

		EnumChooser<StatType> statTypeChooser = new EnumChooser<>(StatType.class);
		EnumChooser<StatModifier.OperationType> typeChooser = new EnumChooser<>(StatModifier.OperationType.class, "statModifier.operationType");
		FloatInput valueInput = new FloatInput();
		PercentInput valueAsPercent = new PercentInput();

		// Binding

		bind(statTypeChooser, StatModifier::getStatType, StatModifier::setStatType);
		bind(typeChooser, StatModifier::getOperationType, StatModifier::setOperationType);
		bind(valueInput, StatModifier::getValue, StatModifier::setValue);
		bind(valueAsPercent, StatModifier::getValue, StatModifier::setValue);

		// Layouting

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "statModifierEditor.statType", statTypeChooser, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "statModifierEditor.operationType", typeChooser, gbc);
		LayoutUtils.nextColumn(gbc);
		JLabel valueLabel = LayoutUtils.addHorizontalLabelledItem(this, "statModifierEditor.value", valueInput, gbc);
		LayoutUtils.nextColumn(gbc);
		JLabel valueAsPercentLabel = LayoutUtils.addHorizontalLabelledItem(this, "statModifierEditor.value", valueAsPercent, gbc);

		// events

		typeChooser.addItemListener(e -> {
			if (!programmaticChange) {
				if (typeChooser.getSelectedItem() == OperationType.RELATIVE) {
					valueInput.setVisible(false);
					valueLabel.setVisible(false);
					valueAsPercent.setVisible(true);
					valueAsPercentLabel.setVisible(true);
					valueAsPercent.setValue(valueInput.getValue() == null ? null : valueInput.getValue() / 100f);
				} else {
					valueInput.setVisible(true);
					valueLabel.setVisible(true);
					valueAsPercent.setVisible(false);
					valueAsPercentLabel.setVisible(false);
					valueInput.setValue(valueInput.getValue() == null ? null : valueAsPercent.getValue() * 100f);
				}
			}
		});
	}

	@Override
	public void setValue(StatModifier value, boolean sneaky) {
		programmaticChange = true;
		super.setValue(value, sneaky);
		programmaticChange = false;
	}
}
