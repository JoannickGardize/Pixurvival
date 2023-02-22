package com.pixurvival.contentPackEditor.component.ecosystem;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeIntervalInput;
import com.pixurvival.contentPackEditor.component.valueComponent.WeightedValueProducerEditor;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.ecosystem.DarknessSpawner;

import javax.swing.*;
import java.awt.*;

public class DarknessSpawnerEditor extends ElementEditor<DarknessSpawner> {

    private static final long serialVersionUID = 1L;

    private WeightedValueProducerEditor<Creature> creatureChooser = new WeightedValueProducerEditor<>(Creature.class);

    public DarknessSpawnerEditor() {
        super(DarknessSpawner.class);
        setBorder(LayoutUtils.createBorder());

        // Construction

        IntegerInput initialSpawnInput = new IntegerInput();
        IntegerInput maximumCreaturesInput = new IntegerInput();
        TimeIntervalInput respawnTimeInput = new TimeIntervalInput("structureSpawnerEditor.respawnTimePerChunk");

        // Binding

        bind(creatureChooser, "creatureChooser");
        bind(initialSpawnInput, "initialSpawn");
        bind(maximumCreaturesInput, "maximumCreatures");
        bind(respawnTimeInput, "respawnTime");

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
