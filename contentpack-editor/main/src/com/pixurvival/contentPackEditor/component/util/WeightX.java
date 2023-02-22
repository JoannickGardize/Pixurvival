package com.pixurvival.contentPackEditor.component.util;

import lombok.AllArgsConstructor;

import java.awt.*;

@AllArgsConstructor
public class WeightX extends LayoutPropertyMarker {
    private float value;

    @Override
    public void apply(GridBagConstraints gbc) {
        gbc.weightx = value;
    }
}
