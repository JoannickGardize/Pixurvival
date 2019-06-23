package com.pixurvival.contentPackEditor.component.structure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeIntervalInput;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.structure.HarvestableStructure;

public class HarvestablePanel extends StructureSpecificPartPanel {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<ItemReward> rewardChooser = new ElementChooserButton<>();
	private TimeInput harvestingTimeInput = new TimeInput();
	private TimeIntervalInput respawnTimeEditor = new TimeIntervalInput("structureEditor.harvestable.respawnTime");

	public HarvestablePanel() {
		EventManager.getInstance().register(this);

		// Binding

		// Layouting
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "structureEditor.harvestable.harvestingTime", harvestingTimeInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "elementType.itemReward", rewardChooser, gbc);
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		add(respawnTimeEditor, gbc);

	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		rewardChooser.setItems(event.getContentPack().getItemRewards());
	}

	@Override
	public void bindTo(StructureEditor structureEditor) {
		structureEditor.bind(harvestingTimeInput, HarvestableStructure::getHarvestingTime, HarvestableStructure::setHarvestingTime, HarvestableStructure.class);
		structureEditor.bind(rewardChooser, HarvestableStructure::getItemReward, HarvestableStructure::setItemReward, HarvestableStructure.class);
		structureEditor.bind(respawnTimeEditor, HarvestableStructure::getRespawnTime, HarvestableStructure::setRespawnTime, HarvestableStructure.class);
	}
}
