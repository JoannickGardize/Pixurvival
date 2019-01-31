package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;

public class VerticalListEditor<E> extends ListEditor<E> {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	private static final long serialVersionUID = 1L;

	public VerticalListEditor(Supplier<ElementEditor<E>> elementEditorSupplier, Supplier<E> valueSupplier) {
		this(elementEditorSupplier, valueSupplier, VERTICAL);
	}

	public VerticalListEditor(Supplier<ElementEditor<E>> elementEditorSupplier, Supplier<E> valueSupplier,
			int buttonAlignment) {
		super(elementEditorSupplier, valueSupplier);
		setMinimumSize(new Dimension(100, 50));
		setPreferredSize(new Dimension(100, 50));
		setLayout(new BorderLayout());
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
		JPanel pusherPanel = new JPanel(new BorderLayout());
		pusherPanel.add(listPanel, BorderLayout.NORTH);
		pusherPanel.add(new JPanel(), BorderLayout.CENTER);
		add(new JScrollPane(pusherPanel));

		if (buttonAlignment == VERTICAL) {
			add(LayoutUtils.createVerticalBox(3, addButton, removeButton), BorderLayout.SOUTH);
		} else {
			add(LayoutUtils.createVerticalBox(3, LayoutUtils.createHorizontalBox(3, addButton, removeButton)),
					BorderLayout.SOUTH);
		}
	}

	@Override
	protected void addEditor(ElementEditor<E> editor) {
		editor.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(3, 2, 3, 2), editor.getBorder()));
		listPanel.add(editor);
	}

	@Override
	protected void removeLast() {
		listPanel.remove(listPanel.getComponentCount() - 1);
	}
}
