package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.DoubleInterval;

public class DoubleIntervalEditor extends ElementEditor<DoubleInterval> {
	private static final long serialVersionUID = 1L;

	public DoubleIntervalEditor(String groupTitle) {

		// Construction
		DoubleInput minInput = new DoubleInput(Bounds.positive());
		DoubleInput maxInput = new DoubleInput(Bounds.positive());

		// Binding

		bind(minInput, DoubleInterval::getMin, DoubleInterval::setMin);
		bind(maxInput, DoubleInterval::getMax, DoubleInterval::setMax);

		// Layouting
		setLayout(new GridBagLayout());
		setBorder(LayoutUtils.createGroupBorder(groupTitle));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.minimum", minInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "generic.maximum", maxInput, gbc);
	}
}
