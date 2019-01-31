package com.pixurvival.contentPackEditor.component.structure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.contentPack.map.Structure.ShortLived;

public class ShortLivedEditor extends ElementEditor<ShortLived> {

	private static final long serialVersionUID = 1L;

	public ShortLivedEditor() {
		DoubleInput durationInput = new DoubleInput(Bounds.positive());

		bind(durationInput, ShortLived::getDuration, ShortLived::setDuration);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.duration", durationInput, gbc);
	}
}
