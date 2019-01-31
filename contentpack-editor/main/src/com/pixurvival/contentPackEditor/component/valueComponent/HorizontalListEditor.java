package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.BorderLayout;
import java.util.function.Supplier;

import javax.swing.JScrollPane;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;

public class HorizontalListEditor<E> extends ListEditor<E> {

	private static final long serialVersionUID = 1L;

	public HorizontalListEditor(Supplier<ElementEditor<E>> elementEditorSupplier, Supplier<E> valueSupplier) {
		super(elementEditorSupplier, valueSupplier);
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(listPanel);
		add(scrollPane, BorderLayout.CENTER);
		add(LayoutUtils.createHorizontalBox(3, addButton, removeButton), BorderLayout.SOUTH);
		LayoutUtils.setMinimumSize(scrollPane, 0, 70);
	}

	@Override
	protected void addEditor(ElementEditor<E> editor) {
		listPanel.add(editor);
	}

	@Override
	protected void removeLast() {
		listPanel.remove(listPanel.getComponentCount() - 1);
	}

}
