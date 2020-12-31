package com.pixurvival.contentPackEditor.settings;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Skin {

	SYSTEM_DEFAULT(() -> {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}),
	METAL(() -> {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	});

	private @Getter Runnable lookAndFeelApplier;
}
