package com.pixurvival.contentPackEditor.component.structure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleIntervalEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.map.Structure.Harvestable;
import com.pixurvival.core.item.ItemReward;

public class HarvestableEditor extends ElementEditor<Harvestable> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<ItemReward> rewardChooser = new ElementChooserButton<>();

	public HarvestableEditor() {
		EventManager.getInstance().register(this);

		// Construction
		DoubleInput harvestingTimeInput = new DoubleInput(Bounds.positive());
		DoubleIntervalEditor respawnTimeEditor = new DoubleIntervalEditor("structureEditor.harvestable.respawnTime");

		// Binding
		bind(harvestingTimeInput, Harvestable::getHarvestingTime, Harvestable::setHarvestingTime);
		bind(rewardChooser, Harvestable::getItemReward, Harvestable::setItemReward);
		bind(respawnTimeEditor, Harvestable::getRespawnTime, Harvestable::setRespawnTime);

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
}
