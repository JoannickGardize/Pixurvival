package com.pixurvival.contentPackEditor.component.item;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.core.livingEntity.alteration.PersistentStatAlteration;
import com.pixurvival.core.livingEntity.alteration.StatAlterationOperation;
import com.pixurvival.core.livingEntity.stats.StatType;

public class PersistentStatAlterationEditor extends ElementEditor<PersistentStatAlteration> {

	private static final long serialVersionUID = 1L;

	public PersistentStatAlterationEditor() {

		// Construction

		EnumChooser<StatType> statTypeChooser = new EnumChooser<>(StatType.class);
		EnumChooser<StatAlterationOperation> statAlterationOperationChooser = new EnumChooser<>(StatAlterationOperation.class);
		FloatInput valueInput = new FloatInput();

		// Binding

		bind(statTypeChooser, PersistentStatAlteration::getStatType, PersistentStatAlteration::setStatType);
		bind(statAlterationOperationChooser, PersistentStatAlteration::getOperation, PersistentStatAlteration::setOperation);
		bind(valueInput, PersistentStatAlteration::getValue, PersistentStatAlteration::setValue);

		// Layouting

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "persistentStatAlterationEditor.statType", statTypeChooser, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "persistentStatAlterationEditor.operation", statAlterationOperationChooser, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "persistentStatAlterationEditor.value", valueInput, gbc);
		LayoutUtils.nextColumn(gbc);
	}
}
