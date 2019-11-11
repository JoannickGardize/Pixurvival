package com.pixurvival.contentPackEditor.component.ecosystem;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeIntervalInput;
import com.pixurvival.contentPackEditor.component.valueComponent.WeightedValueProducerEditor;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.ecosystem.ChunkSpawner;

public class DarknessSpawnerEditor<T extends ChunkSpawner> extends ElementEditor<T> {

	private static final long serialVersionUID = 1L;

	private WeightedValueProducerEditor<Creature> creatureChooser = new WeightedValueProducerEditor<>(Creature.class);

	public DarknessSpawnerEditor() {
		setBorder(LayoutUtils.createBorder());

		// Construction

		IntegerInput initialSpawnInput = new IntegerInput(Bounds.positive());
		IntegerInput maximumCreaturesInput = new IntegerInput(Bounds.positive());
		TimeIntervalInput respawnTimeInput = new TimeIntervalInput("structureSpawnerEditor.respawnTimePerChunk");

		// Binding

		bind(creatureChooser, ChunkSpawner::getCreatureChooser, ChunkSpawner::setCreatureChooser);
		bind(initialSpawnInput, ChunkSpawner::getInitialSpawn, ChunkSpawner::setInitialSpawn);
		bind(maximumCreaturesInput, ChunkSpawner::getMaximumCreatures, ChunkSpawner::setMaximumCreatures);
		bind(respawnTimeInput, ChunkSpawner::getRespawnTime, ChunkSpawner::setRespawnTime);

		// Layouting

		setLayout(new BorderLayout());

		JPanel topPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.initialSpawnPerChunk", initialSpawnInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.maximumCreatures", maximumCreaturesInput, gbc);
		add(LayoutUtils.createVerticalBox(topPanel, respawnTimeInput), BorderLayout.NORTH);
		creatureChooser.setBorder(LayoutUtils.createGroupBorder("structureSpawnerEditor.creatureChooser"));
		add(creatureChooser, BorderLayout.SOUTH);
	}
}
