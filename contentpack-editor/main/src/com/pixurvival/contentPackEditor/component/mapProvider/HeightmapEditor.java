package com.pixurvival.contentPackEditor.component.mapProvider;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.StringInput;
import com.pixurvival.core.contentPack.map.Heightmap;

import java.awt.*;

public class HeightmapEditor extends ElementEditor<Heightmap> {

    private static final long serialVersionUID = 1L;

    public HeightmapEditor() {
        super(Heightmap.class);

        // Construction

        StringInput nameInput = new StringInput(1);
        IntegerInput numberOfOctaveInput = new IntegerInput();
        FloatInput persistenceInput = new FloatInput();
        FloatInput scaleInput = new FloatInput();

        // Binding

        bind(nameInput, "name");
        bind(numberOfOctaveInput, "numberOfoctaves");
        bind(persistenceInput, "persistence");
        bind(scaleInput, "scale");

        // Layouting

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        LayoutUtils.addHorizontalLabelledItem(this, "generic.name", nameInput, gbc);
        LayoutUtils.addHorizontalLabelledItem(this, "heightmapEditor.persistence", persistenceInput, gbc);
        LayoutUtils.nextColumn(gbc);
        LayoutUtils.addHorizontalLabelledItem(this, "heightmapEditor.numberOfOctave", numberOfOctaveInput, gbc);
        LayoutUtils.addHorizontalLabelledItem(this, "heightmapEditor.scale", scaleInput, gbc);
    }
}
