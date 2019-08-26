package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.function.Supplier;

import javax.swing.JScrollPane;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;

public class HorizontalListEditor<E> extends ListEditor<E> {

	private static final long serialVersionUID = 1L;

	public static final int BUTTONS_TYPE_BOTTOM = 0;

	public static final int BUTTONS_TYPE_RIGHT = 1;

	public HorizontalListEditor(Supplier<ValueComponent<E>> elementEditorSupplier, Supplier<E> valueSupplier) {
		this(elementEditorSupplier, valueSupplier, BUTTONS_TYPE_RIGHT);
	}

	public HorizontalListEditor(Supplier<ValueComponent<E>> elementEditorSupplier, Supplier<E> valueSupplier, int buttonsType) {
		super(elementEditorSupplier, valueSupplier);
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(listPanel);
		add(scrollPane, BorderLayout.CENTER);
		if (buttonsType == BUTTONS_TYPE_BOTTOM) {
			add(LayoutUtils.createHorizontalBox(addButton, removeButton), BorderLayout.SOUTH);
			LayoutUtils.setMinimumSize(scrollPane, 0, 70);
		} else {
			addButton.setText("+");
			removeButton.setText("-");
			add(LayoutUtils.createVerticalBox(addButton, removeButton), BorderLayout.EAST);
		}
	}

	@Override
	protected void addEditor(ValueComponent<E> editor) {
		listPanel.add((Component) editor);
	}

	@Override
	protected void removeLast() {
		listPanel.remove(listPanel.getComponentCount() - 1);
	}

}
