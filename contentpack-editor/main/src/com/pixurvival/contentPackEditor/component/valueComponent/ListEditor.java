package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.Component;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.core.contentPack.IdentifiedElement;

import lombok.Getter;
import lombok.Setter;

public abstract class ListEditor<E> extends ElementEditor<List<E>> {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	private static final long serialVersionUID = 1L;

	private @Getter @Setter boolean oneRequired = false;
	private @Getter ElementEditor<E> editorForValidation;
	private Supplier<ElementEditor<E>> elementEditorSupplier;
	protected JPanel listPanel = new JPanel();
	protected JButton addButton;
	protected JButton removeButton;

	public ListEditor(Supplier<ElementEditor<E>> elementEditorSupplier, Supplier<E> valueSupplier) {
		this.elementEditorSupplier = elementEditorSupplier;
		editorForValidation = elementEditorSupplier.get();
		addButton = new CPEButton("generic.add", () -> add(valueSupplier.get()));
		removeButton = new CPEButton("generic.remove", () -> {
			if (!getValue().isEmpty()) {
				removeLast();
				getValue().remove(getValue().size() - 1);
				notifyValueChanged();
				listPanel.revalidate();
				listPanel.repaint();
			}
		});
	}

	protected abstract void addEditor(ElementEditor<E> editor);

	protected abstract void removeLast();

	@Override
	public void setValue(List<E> value) {
		super.setValue(value);
		listPanel.removeAll();
		for (int i = 0; i < value.size(); i++) {
			E element = value.get(i);
			addComponent(i, element);
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
		if (value instanceof IdentifiedElement) {
			((IdentifiedElement) value).setId(getValue().size());
		}
		getValue().add(addComponent(getValue().size(), value).getValue());
		notifyValueChanged();
		listPanel.revalidate();
		listPanel.repaint();
	}

	@SuppressWarnings("unchecked")
	public void forEachEditors(Consumer<ElementEditor<E>> action) {
		for (Component component : listPanel.getComponents()) {
			action.accept((ElementEditor<E>) component);
		}
		action.accept(editorForValidation);
	}

	private ElementEditor<E> addComponent(int index, E value) {
		ElementEditor<E> elementEditor = elementEditorSupplier.get();
		if (value != null) {
			elementEditor.setValue(value);
		}
		elementEditor.addValueChangeListener(v -> {
			getValue().set(index, v);
			notifyValueChanged();
		});
		addEditor(elementEditor);
		return elementEditor;
	}

}
