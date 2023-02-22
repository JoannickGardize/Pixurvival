package com.pixurvival.contentPackEditor.component.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyIcon implements Icon {

    public static final EmptyIcon INSTANCE = new EmptyIcon();

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {

        // Nothing to paint
    }

    @Override
    public int getIconWidth() {
        return 0;
    }

    @Override
    public int getIconHeight() {
        return 0;
    }

}
