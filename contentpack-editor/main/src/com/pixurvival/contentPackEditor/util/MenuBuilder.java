package com.pixurvival.contentPackEditor.util;

import com.pixurvival.contentPackEditor.TranslationService;
import lombok.AllArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

@AllArgsConstructor
public class MenuBuilder {

    private Container rootContainer;
    private String translationPreffix;

    public JMenuItem addItem(String path, Runnable action) {
        return addItem(path, action, TranslationService.getInstance().getString(translationPreffix + "." + path));
    }

    public JMenuItem addItem(String path, Runnable action, Icon icon) {
        return addItem(path, action, TranslationService.getInstance().getString(translationPreffix + "." + path), icon);
    }

    public JMenuItem addItem(String path, Runnable action, String label) {
        return addItem(path, action, label, null);
    }

    public JMenuItem addItem(String path, Runnable action, String label, Icon icon) {
        String[] split = path.split("\\.");
        JMenuItem item = findOrCreate(rootContainer, split, 0, icon);
        item.setText(label);
        item.addActionListener(l -> action.run());
        return item;
    }

    private JMenuItem findOrCreate(Container component, String[] split, int index, Icon icon) {
        boolean isLast = index == split.length - 1;
        String currentName = split[index];
        for (Component child : component.getComponents()) {
            if (child instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) child;
                if (item.getName().equals(currentName)) {
                    if (isLast) {
                        return item;
                    } else {
                        return findOrCreate(item, split, index + 1, icon);
                    }
                }
            }
        }
        if (isLast) {
            JMenuItem item = new JMenuItem();
            item.setName(currentName);
            item.setIcon(icon);
            addTo(component, item);
            return item;
        } else {
            JMenu item = new JMenu();
            item.setName(currentName);
            item.setText(TranslationService.getInstance().getString(translationPreffix + "." + String.join(".", Arrays.copyOfRange(split, 0, index + 1))));
            addTo(component, item);
            return findOrCreate(item, split, index + 1, icon);
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
