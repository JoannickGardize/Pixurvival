package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class NullableElementHelper<T> {

    private ElementEditor<T> elementEditor;
    private BooleanCheckBox enableCheckBox = new BooleanCheckBox();
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    private @Getter JPanel notNullPanel = new JPanel();

    public NullableElementHelper(ElementEditor<T> elementEditor) {
        this.elementEditor = elementEditor;

    }

    public void build(Supplier<T> newInstanceSupplier) {
        build(newInstanceSupplier, elementEditor);
    }

    public void build(Supplier<T> newInstanceSupplier, JPanel parentPanel) {
        enableCheckBox.addActionListener(e -> {
            boolean checked = enableCheckBox.isSelected();
            if (checked && elementEditor.getValue() == null) {
                elementEditor.setValue(newInstanceSupplier.get());
                elementEditor.notifyValueChanged();
            } else if (!checked && elementEditor.getValue() != null) {
                elementEditor.setValue(null);
                elementEditor.notifyValueChanged();
            }
            if (checked) {
                cardLayout.show(cardPanel, "NOT_NULL");
            } else {
                cardLayout.show(cardPanel, "NULL");
            }
        });

        cardPanel.setLayout(cardLayout);
        cardPanel.add(notNullPanel, "NOT_NULL");
        cardPanel.add(new JPanel(), "NULL");

        parentPanel.setLayout(new BorderLayout());
        parentPanel.add(LayoutUtils.labelled("generic.enabled", enableCheckBox), BorderLayout.NORTH);
        parentPanel.add(cardPanel, BorderLayout.CENTER);

        cardLayout.show(cardPanel, "NULL");
    }

    public void onValueChanged() {
        if (elementEditor.getValue() == null) {
            enableCheckBox.setSelected(false);
            cardLayout.show(cardPanel, "NULL");
        } else {
            enableCheckBox.setSelected(true);
            cardLayout.show(cardPanel, "NOT_NULL");
        }
    }
}
