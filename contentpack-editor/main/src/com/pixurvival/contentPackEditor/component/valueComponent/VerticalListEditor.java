package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;

public class VerticalListEditor<E> extends ListEditor<E> {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	private static final long serialVersionUID = 1L;

	public VerticalListEditor(Supplier<ValueComponent<E>> elementEditorSupplier, Supplier<E> valueSupplier) {
		this(elementEditorSupplier, valueSupplier, VERTICAL);
	}

	public VerticalListEditor(Supplier<ValueComponent<E>> elementEditorSupplier, Supplier<E> valueSupplier, int buttonAlignment) {
		this(elementEditorSupplier, valueSupplier, buttonAlignment, true);
	}

	public VerticalListEditor(Supplier<ValueComponent<E>> elementEditorSupplier, Supplier<E> valueSupplier, int buttonAlignment, boolean useScrollPane) {
		super(elementEditorSupplier, valueSupplier);
		// setMinimumSize(new Dimension(100, 50));
		// setPreferredSize(new Dimension(100, 50));
		listPanel.setBackground(new Color(150, 150, 150));
		setLayout(new BorderLayout());
		listPanel.setLayout(new GridBagLayout());
		listPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
		if (useScrollPane) {
			JPanel pusherPanel = new JPanel(new BorderLayout());
			pusherPanel.add(listPanel, BorderLayout.NORTH);
			pusherPanel.add(new JPanel(), BorderLayout.CENTER);
			add(new JScrollPane(pusherPanel));
		} else {
			add(listPanel);
		}
		if (buttonAlignment == VERTICAL) {
			add(LayoutUtils.createVerticalBox(3, addButton, removeButton), BorderLayout.SOUTH);
		} else {
			add(LayoutUtils.createVerticalBox(3, LayoutUtils.createHorizontalBox(addButton, removeButton)), BorderLayout.SOUTH);
		}
	}

	@Override
	protected void addEditor(ValueComponent<E> editor) {
		((JComponent) editor).setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(3, 2, 3, 2), ((JComponent) editor).getBorder()));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(3, 2, 3, 2);
		gbc.gridy = listPanel.getComponentCount();
		listPanel.add((Component) editor, gbc);
		listPanel.revalidate();
	}

	@Override
	protected void removeLast() {
		listPanel.remove(listPanel.getComponentCount() - 1);
		listPanel.revalidate();
	}
}
