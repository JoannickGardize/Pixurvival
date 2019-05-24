package com.pixurvival.contentPackEditor.component.effect;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.core.livingEntity.alteration.StatMultiplier;
import com.pixurvival.core.livingEntity.stats.StatType;

public class StatMultiplierEditor extends ElementEditor<StatMultiplier> {

	private static final long serialVersionUID = 1L;

	public StatMultiplierEditor() {

		// Construction

		EnumChooser<StatType> statTypeChooser = new EnumChooser<>(StatType.class);
		FloatInput multiplierInput = new FloatInput();

		// Binding

		bind(statTypeChooser, StatMultiplier::getStatType, StatMultiplier::setStatType);
		bind(multiplierInput, StatMultiplier::getMultiplier, StatMultiplier::setMultiplier);

		// Layouting

		LayoutUtils.addSideBySide(this, statTypeChooser, LayoutUtils.labelled("generic.multiplySymbol", multiplierInput));
	}
}
