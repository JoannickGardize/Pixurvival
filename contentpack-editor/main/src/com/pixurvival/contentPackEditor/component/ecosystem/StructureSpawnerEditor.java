package com.pixurvival.contentPackEditor.component.ecosystem;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.WeightedValueProducerEditor;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.ecosystem.StructureSpawner;
import com.pixurvival.core.contentPack.structure.Structure;

public class StructureSpawnerEditor extends ElementEditor<StructureSpawner> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Structure> structureChooser = new ElementChooserButton<>(IconService.getInstance()::get, true);

	private WeightedValueProducerEditor<Creature> creatureChooser = new WeightedValueProducerEditor<>(c -> null, ContentPack::getCreatures);

	public StructureSpawnerEditor() {
		setBorder(LayoutUtils.createBorder());
		ContentPack currentPack = FileService.getInstance().getCurrentContentPack();
		if (currentPack != null) {
			setItems(currentPack);
		}

		// Construction

		DoubleInput spawnRadiusInput = new DoubleInput(Bounds.positive());
		DoubleInput managedRadiusInput = new DoubleInput(Bounds.positive());
		IntegerInput initialSpawnPerChunkInput = new IntegerInput(Bounds.positive());
		IntegerInput maximumCreaturesInput = new IntegerInput(Bounds.positive());
		TimeInput respawnTimePerChunk = new TimeInput();

		// Binding

		bind(structureChooser, StructureSpawner::getStructure, StructureSpawner::setStructure);
		bind(creatureChooser, StructureSpawner::getCreatureChooser, StructureSpawner::setCreatureChooser);
		bind(spawnRadiusInput, StructureSpawner::getSpawnRadius, StructureSpawner::setSpawnRadius);
		bind(managedRadiusInput, StructureSpawner::getManagedRadius, StructureSpawner::setManagedRadius);
		bind(initialSpawnPerChunkInput, StructureSpawner::getInitialSpawnPerChunk, StructureSpawner::setInitialSpawnPerChunk);
		bind(maximumCreaturesInput, StructureSpawner::getMaximumCreatures, StructureSpawner::setMaximumCreatures);
		bind(respawnTimePerChunk, StructureSpawner::getRespawnTimePerChunk, StructureSpawner::setRespawnTimePerChunk);

		// Layouting

		setLayout(new BorderLayout());

		JPanel topPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(topPanel, "elementType.structure", structureChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.spawnRadius", spawnRadiusInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.managedRadius", managedRadiusInput, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.initialSpawnPerChunk", initialSpawnPerChunkInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.maximumCreatures", maximumCreaturesInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.respawnTimePerChunk", respawnTimePerChunk, gbc);
		add(topPanel, BorderLayout.NORTH);
		creatureChooser.setBorder(LayoutUtils.createGroupBorder("structureSpawnerEditor.creatureChooser"));
		add(creatureChooser, BorderLayout.SOUTH);
	}

	public void setItems(ContentPack contentPack) {
		structureChooser.setItems(contentPack.getStructures());
		creatureChooser.setAllItems(contentPack.getCreatures());
	}
}
