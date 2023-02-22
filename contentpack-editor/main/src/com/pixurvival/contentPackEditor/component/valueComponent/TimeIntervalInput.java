package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.TimeInterval;

import java.awt.*;

public class TimeIntervalInput extends ElementEditor<TimeInterval> {
    private static final long serialVersionUID = 1L;

    public TimeIntervalInput(String groupTitle) {
        super(TimeInterval.class);

        // Construction
        TimeInput minInput = new TimeInput();
        TimeInput maxInput = new TimeInput();

        // Binding

        bind(minInput, "min");
        bind(maxInput, "max");

        // Layouting
        setLayout(new GridBagLayout());
        setBorder(LayoutUtils.createGroupBorder(groupTitle));
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        LayoutUtils.addHorizontalLabelledItem(this, "generic.minimum", minInput, gbc);
        LayoutUtils.addHorizontalLabelledItem(this, "generic.maximum", maxInput, gbc);
    }
}
