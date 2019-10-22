package com.pixurvival.contentPackEditor.util;

import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.pixurvival.contentPackEditor.TranslationService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MenuBuilder {

	private Container rootContainer;
	private String translationPreffix;

	public void addItem(String path, Runnable action) {
		addItem(path, action, TranslationService.getInstance().getString(translationPreffix + "." + path));
	}

	public void addItem(String path, Runnable action, String label) {
		String[] split = path.split("\\.");
		JMenuItem item = findOrCreate(rootContainer, split, 0);
		item.setText(label);
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
			item.setText(TranslationService.getInstance().getString(translationPreffix + "." + String.join(".", Arrays.copyOfRange(split, 0, index + 1))));
			addTo(component, item);
			return findOrCreate(item, split, index + 1);
		}
	}

	private void addTo(Container component, JMenuItem item) {
		if (component instanceof JPopupMenu) {
			((JPopupMenu) component).add(item);
		} else if (component instanceof JMenu) {
			((JMenu) component).add(item);
		} else {
			component.add(item);
		}
	}
}
