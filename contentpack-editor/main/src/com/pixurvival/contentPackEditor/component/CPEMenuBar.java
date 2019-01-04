package com.pixurvival.contentPackEditor.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.util.Arrays;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.TranslationService;

public class CPEMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	public CPEMenuBar() {
		FileService fs = FileService.getInstance();
		final ResourcesDialog resourcesDialog = new ResourcesDialog((Frame) SwingUtilities.getRoot(this));
		final IdentifierDialog identifierDialog = new IdentifierDialog((Frame) SwingUtilities.getRoot(this));

		addItem("file.new", fs::newContentPack);
		addItem("file.open", fs::open);
		addItem("file.save", fs::save);
		addItem("file.saveAs", fs::saveAs);
		addItem("contentPack.identifier", () -> identifierDialog.setVisible(true));
		addItem("contentPack.resources", () -> resourcesDialog.setVisible(true));
		addItem("contentPack.constants", () -> {
		});
	}

	private void addItem(String path, Runnable action) {
		String[] split = path.split("\\.");
		String textKey = "menuBar." + path;
		JMenuItem item = findOrCreate(this, split, 0);
		item.setText(TranslationService.getInstance().getString(textKey));
		item.addActionListener(l -> action.run());
	}

	private JMenuItem findOrCreate(Container component, String[] split, int index) {
		boolean isLast = index == split.length - 1;
		String currentName = split[index];
		for (Component child : component.getComponents()) {
			if (child instanceof JMenuItem) {
				JMenuItem item = (JMenuItem) child;
				if (item.getName().equals(currentName)) {
					if (isLast) {
						return item;
					} else {
						return findOrCreate(item, split, index + 1);
					}
				}
			}
		}
		if (isLast) {
			JMenuItem item = new JMenuItem();
			item.setName(currentName);
			addTo(component, item);
			return item;
		} else {
			JMenu item = new JMenu();
			item.setName(currentName);
			item.setText(TranslationService.getInstance().getString("menuBar." + String.join(".", Arrays.copyOfRange(split, 0, index + 1))));
			addTo(component, item);
			return findOrCreate(item, split, index + 1);
		}
	}

	private void addTo(Container component, JMenuItem item) {
		if (component == this) {
			add(item);
		} else {
			((JMenu) component).add(item);
		}
	}
}
