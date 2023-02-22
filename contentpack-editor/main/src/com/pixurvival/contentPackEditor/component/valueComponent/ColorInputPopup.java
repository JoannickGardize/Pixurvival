package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.util.RelativePopup;

import javax.swing.*;
import java.awt.*;

public class ColorInputPopup extends RelativePopup {

    private static final long serialVersionUID = 1L;

    private JColorChooser chooser = new JColorChooser();

    private boolean manualChange;

    public ColorInputPopup(ColorInput colorInput) {
        setLayout(new BorderLayout());
        add(chooser, BorderLayout.CENTER);

        chooser.getSelectionModel().addChangeListener(e -> {
            if (!manualChange) {
                colorInput.setValue(chooser.getColor().getRGB());
                colorInput.notifyValueChanged();
            }
        });
        pack();
    }

    public void setColor(Color color) {
        manualChange = true;
        chooser.setColor(color);
        manualChange = false;
    }
}
