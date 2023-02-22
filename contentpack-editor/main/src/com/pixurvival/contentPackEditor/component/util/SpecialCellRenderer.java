package com.pixurvival.contentPackEditor.component.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

@RequiredArgsConstructor
public class SpecialCellRenderer<T> implements ListCellRenderer<T> {

    private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    private @NonNull Function<T, String> stringFunction;

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ((JLabel) component).setText(stringFunction.apply(value));
        return component;
    }

}
