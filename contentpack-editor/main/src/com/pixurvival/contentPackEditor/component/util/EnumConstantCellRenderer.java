package com.pixurvival.contentPackEditor.component.util;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.core.util.CaseUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class EnumConstantCellRenderer implements ListCellRenderer<Enum<?>> {

    private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    private @NonNull String translationPreffix;

    @Override
    public Component getListCellRendererComponent(JList<? extends Enum<?>> list, Enum<?> value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value != null) {
            String text = TranslationService.getInstance().getString(translationPreffix + "." + CaseUtils.upperToCamelCase(value.name()));
            ((JLabel) component).setText(text);
        }
        return component;
    }

}
