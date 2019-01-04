package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;

import lombok.Getter;
import lombok.Setter;

public class ListEditor<E> extends ElementEditor<List<E>> {

	private static final long serialVersionUID = 1L;

	private @Getter @Setter boolean oneRequired = false;
	private @Getter ElementEditor<E> editorForValidation;
	private Supplier<ElementEditor<E>> elementEditorSupplier;
	private JPanel listPanel = new JPanel();

	public ListEditor(Supplier<ElementEditor<E>> elementEditorSupplier, Supplier<E> valueSupplier) {
		this.elementEditorSupplier = elementEditorSupplier;
		editorForValidation = elementEditorSupplier.get();
		setMinimumSize(new Dimension(100, 50));
		setPreferredSize(new Dimension(100, 50));
		setLayout(new BorderLayout());
		listPanel.setLayout(new GridBagLayout());
		JPanel pusherPanel = new JPanel(new BorderLayout());
		pusherPanel.add(listPanel, BorderLayout.NORTH);
		pusherPanel.add(new JPanel(), BorderLayout.CENTER);
		add(new JScrollPane(pusherPanel));

		JButton addButton = new CPEButton("generic.add", () -> add(valueSupplier.get()));
		JButton removeButton = new CPEButton("generic.remove", this::remove);

		add(LayoutUtils.createVerticalBox(3, addButton, removeButton), BorderLayout.SOUTH);
	}

	@Override
	public void setValue(List<E> value) {
		super.setValue(value);
		listPanel.removeAll();
		for (E element : value) {
			addComponent(element);
		}
		listPanel.revalidate();
		listPanel.repaint();
	}

	@Override
	public boolean isValueValid(List<E> value) {
		if (oneRequired && value.isEmpty()) {
			return false;
		}
		for (E item : value) {
			if (!editorForValidation.isValueValid(item)) {
				return false;
			}
		}
		return true;
	}

	public void add(E value) {
		getValue().add(addComponent(value).getValue());
		notifyValueChanged();
		listPanel.revalidate();
		listPanel.repaint();
	}

	private ElementEditor<E> addComponent(E value) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets.top = 5;
		if (listPanel.getComponentCount() == 0) {
			gbc.gridy = 0;
		} else {
			GridBagLayout layout = (GridBagLayout) listPanel.getLayout();
			GridBagConstraints gbc2 = layout.getConstraints(listPanel.getComponent(listPanel.getComponentCount() - 1));
			gbc.gridy = gbc2.gridy + 1;
		}
		ElementEditor<E> elementEditor = elementEditorSupplier.get();
		if (value != null) {
			elementEditor.setValue(value);
		}
		listPanel.add(elementEditor, gbc);
		return elementEditor;
	}

	private void remove() {
		if (!getValue().isEmpty()) {
			getValue().remove(getValue().size() - 1);
			listPanel.remove(listPanel.getComponentCount() - 1);
			notifyValueChanged();
			listPanel.revalidate();
			listPanel.repaint();
		}
	}
}
