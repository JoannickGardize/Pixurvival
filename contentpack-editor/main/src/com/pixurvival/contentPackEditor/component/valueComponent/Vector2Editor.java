package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.util.Vector2;

import java.awt.*;

public class Vector2Editor extends ElementEditor<Vector2> {

    private static final long serialVersionUID = 1L;

    public Vector2Editor() {
        super(Vector2.class);
        FloatInput xInput = new FloatInput();
        FloatInput yInput = new FloatInput();

        bind(xInput, "x");
        bind(yInput, "y");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        LayoutUtils.addHorizontalLabelledItem(this, "generic.x", xInput, gbc);
        LayoutUtils.nextColumn(gbc);
        LayoutUtils.addHorizontalLabelledItem(this, "generic.y", yInput, gbc);
    }
}
