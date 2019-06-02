package com.pixurvival.contentPackEditor.component.structure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.core.contentPack.structure.ShortLivedStructure;

public class ShortLivedPanel extends StructureSpecificPartPanel {

	private static final long serialVersionUID = 1L;

	private DoubleInput durationInput = new DoubleInput(Bounds.positive());

	public ShortLivedPanel() {

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.duration", durationInput, gbc);
	}

	@Override
	public void bindTo(StructureEditor structureEditor) {
		structureEditor.bind(durationInput, ShortLivedStructure::getDuration, ShortLivedStructure::setDuration, ShortLivedStructure.class);
	}
}
