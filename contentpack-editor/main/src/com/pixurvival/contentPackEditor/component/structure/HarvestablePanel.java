package com.pixurvival.contentPackEditor.component.structure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeIntervalInput;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.structure.HarvestableStructure;

public class HarvestablePanel extends StructureSpecificPartPanel {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<ItemReward> rewardChooser = new ElementChooserButton<>(ItemReward.class);
	private TimeInput harvestingTimeInput = new TimeInput();
	private TimeIntervalInput regrowthTimeEditor = new TimeIntervalInput("structureEditor.harvestable.regrowthTime");

	public HarvestablePanel() {
		// Binding

		// Layouting
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "structureEditor.harvestable.harvestingTime", harvestingTimeInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "elementType.itemReward", rewardChooser, gbc);
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		add(regrowthTimeEditor, gbc);

	}

	@Override
	public void bindTo(StructureEditor structureEditor) {
		structureEditor.bind(harvestingTimeInput, "harvestingTime", HarvestableStructure.class);
		structureEditor.bind(rewardChooser, "itemReward", HarvestableStructure.class);
		structureEditor.bind(regrowthTimeEditor, "regrowthTime", HarvestableStructure.class);
	}
}
