package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.Dimensions;

import java.awt.*;

public class DimensionsEditor extends ElementEditor<Dimensions> {

    private static final long serialVersionUID = 1L;

    public DimensionsEditor() {
        super(Dimensions.class);

        // Construction
        IntegerInput widthInput = new IntegerInput();
        IntegerInput heightInput = new IntegerInput();

        // Binding

        bind(widthInput, "width");
        bind(heightInput, "height");

        // Layouting
        setLayout(new GridBagLayout());
        setBorder(LayoutUtils.createGroupBorder("dimensionsEditor.title"));
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        LayoutUtils.addHorizontalLabelledItem(this, "generic.width", widthInput, gbc);
        LayoutUtils.addHorizontalLabelledItem(this, "generic.height", heightInput, gbc);
    }
}
