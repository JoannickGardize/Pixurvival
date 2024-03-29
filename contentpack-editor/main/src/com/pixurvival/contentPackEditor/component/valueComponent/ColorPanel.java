package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.ImageService;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class ColorPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private @Getter ColorInput colorInput = new ColorInput();
    private ColorInputPopup popup;

    public ColorPanel() {
        JButton colorPickerButton = new JButton(new ImageIcon(ImageService.getInstance().get("color_picker")));
        popup = new ColorInputPopup(colorInput);

        colorPickerButton.addActionListener(e -> popup.show(this));
        setLayout(new BorderLayout(2, 0));
        add(colorInput, BorderLayout.CENTER);
        add(colorPickerButton, BorderLayout.EAST);
        setBorder(LayoutUtils.createBorder());
    }
}
