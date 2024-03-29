package com.pixurvival.contentPackEditor.component.gameMode;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.*;
import com.pixurvival.core.contentPack.gameMode.MapLimits;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;

import javax.swing.*;
import java.awt.*;

public class MapLimitsEditor extends ElementEditor<MapLimits> {

    private static final long serialVersionUID = 1L;

    private NullableElementHelper<MapLimits> nullableElementHelper = new NullableElementHelper<>(this);

    public MapLimitsEditor() {
        super(MapLimits.class);
        BooleanCheckBox shrinkRandomlyCheckBox = new BooleanCheckBox();
        FloatInput initialSizeInput = new FloatInput();
        FloatInput initialDamageInput = new FloatInput();
        ListEditor<MapLimitsAnchor> anchors = new VerticalListEditor<>(MapLimitsAnchorEditor::new, MapLimitsAnchor::new);

        bind(shrinkRandomlyCheckBox, "shrinkRandomly");
        bind(initialSizeInput, "initialSize");
        bind(initialDamageInput, "initialDamagePerSecond");
        bind(anchors, "anchors");

        JPanel contentPanel = nullableElementHelper.getNotNullPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(LayoutUtils.createVerticalBox(LayoutUtils.createHorizontalLabelledBox("mapLimits.shrinkRandomly", shrinkRandomlyCheckBox),
                LayoutUtils.createHorizontalLabelledBox("mapLimits.initialSize", initialSizeInput, "mapLimits.initialDamagePerSecond", initialDamageInput)), BorderLayout.NORTH);
        contentPanel.add(anchors, BorderLayout.CENTER);

        nullableElementHelper.build(MapLimits::new);
    }

    @Override
    protected void valueChanged(ValueComponent<?> source) {
        nullableElementHelper.onValueChanged();
    }
}
