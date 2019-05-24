package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.IntegerInterval;

public class IntegerIntervalEditor extends ElementEditor<IntegerInterval> {
	private static final long serialVersionUID = 1L;

	public IntegerIntervalEditor(String groupTitle) {

		// Construction
		IntegerInput minInput = new IntegerInput(Bounds.positive());
		IntegerInput maxInput = new IntegerInput(Bounds.positive());

		// Binding

		bind(minInput, IntegerInterval::getMin, IntegerInterval::setMin);
		bind(maxInput, IntegerInterval::getMax, IntegerInterval::setMax);

		// Layouting
		setLayout(new GridBagLayout());
		setBorder(LayoutUtils.createGroupBorder(groupTitle));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.minimum", minInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "generic.maximum", maxInput, gbc);
	}
}