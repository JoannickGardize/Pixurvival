package com.pixurvival.contentPackEditor.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;

@AllArgsConstructor
public enum Skin {

    SYSTEM_DEFAULT(() -> {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }),
    METAL(() -> {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    });

    private @Getter Runnable lookAndFeelApplier;
}
