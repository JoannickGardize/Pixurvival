package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.LongInterval;

public class TimeIntervalInput extends ElementEditor<LongInterval> {
	private static final long serialVersionUID = 1L;

	public TimeIntervalInput(String groupTitle) {

		// Construction
		TimeInput minInput = new TimeInput();
		TimeInput maxInput = new TimeInput();

		// Binding

		bind(minInput, LongInterval::getMin, LongInterval::setMin);
		bind(maxInput, LongInterval::getMax, LongInterval::setMax);

		// Layouting
		setLayout(new GridBagLayout());
		setBorder(LayoutUtils.createGroupBorder(groupTitle));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.minimum", minInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "generic.maximum", maxInput, gbc);
	}
}