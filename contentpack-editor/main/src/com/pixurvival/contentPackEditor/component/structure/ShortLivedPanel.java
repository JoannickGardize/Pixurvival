package com.pixurvival.contentPackEditor.component.structure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.structure.ShortLivedStructure;

public class ShortLivedPanel extends StructureSpecificPartPanel {

	private static final long serialVersionUID = 1L;

	private TimeInput durationInput = new TimeInput();

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
