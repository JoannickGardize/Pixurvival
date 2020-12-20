package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.function.Supplier;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;

import lombok.Getter;

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

		elementEditor.setLayout(new BorderLayout());
		elementEditor.add(LayoutUtils.labelled("generic.enabled", enableCheckBox), BorderLayout.NORTH);
		elementEditor.add(cardPanel, BorderLayout.CENTER);

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
