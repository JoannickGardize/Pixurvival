package com.pixurvival.contentPackEditor.component.ecosystem;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeIntervalInput;
import com.pixurvival.contentPackEditor.component.valueComponent.WeightedValueProducerEditor;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.ecosystem.StructureSpawner;
import com.pixurvival.core.contentPack.structure.Structure;

import javax.swing.*;
import java.awt.*;

public class StructureSpawnerEditor extends ElementEditor<StructureSpawner> {

    private static final long serialVersionUID = 1L;

    private ElementChooserButton<Structure> structureChooser = new ElementChooserButton<>(Structure.class);

    private WeightedValueProducerEditor<Creature> creatureChooser = new WeightedValueProducerEditor<>(Creature.class);

    public StructureSpawnerEditor() {
        super(StructureSpawner.class);
        setBorder(LayoutUtils.createBorder());

        // Construction

        FloatInput spawnRadiusInput = new FloatInput();
        FloatInput managedRadiusInput = new FloatInput();
        IntegerInput initialSpawnInput = new IntegerInput();
        IntegerInput maximumCreaturesInput = new IntegerInput();
        TimeIntervalInput respawnTimeInput = new TimeIntervalInput("structureSpawnerEditor.respawnTimePerChunk");

        // Binding

        bind(structureChooser, "structure");
        bind(creatureChooser, "creatureChooser");
        bind(spawnRadiusInput, "spawnRadius");
        bind(managedRadiusInput, "managedRadius");
        bind(initialSpawnInput, "initialSpawn");
        bind(maximumCreaturesInput, "maximumCreatures");
        bind(respawnTimeInput, "respawnTime");

        // Layouting

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        LayoutUtils.addHorizontalLabelledItem(topPanel, "elementType.structure", structureChooser, gbc);
        LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.spawnRadius", spawnRadiusInput, gbc);
        LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.managedRadius", managedRadiusInput, gbc);
        LayoutUtils.nextColumn(gbc);
        LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.initialSpawnPerChunk", initialSpawnInput, gbc);
        LayoutUtils.addHorizontalLabelledItem(topPanel, "structureSpawnerEditor.maximumCreatures", maximumCreaturesInput, gbc);
        add(LayoutUtils.createVerticalBox(topPanel, respawnTimeInput), BorderLayout.NORTH);
        creatureChooser.setBorder(LayoutUtils.createGroupBorder("structureSpawnerEditor.creatureChooser"));
        add(creatureChooser, BorderLayout.SOUTH);
    }
}
