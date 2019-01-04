package com.pixurvival.contentPackEditor.component.item;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueChangeListener;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.item.Item.Details;

public class ItemDetailsEditor extends JPanel implements ValueComponent<Details> {

	private static final long serialVersionUID = 1L;

	private ItemType currentType;
	private ElementEditor<Details> elementEditor;
	private List<ValueChangeListener<Details>> listeners = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public ItemDetailsEditor() {
		setLayout(new CardLayout());
		for (ItemType type : ItemType.values()) {
			add(type.getDetailsEditor(), type.name());
		}
		elementEditor = (ElementEditor<Details>) ItemType.values()[0].getDetailsEditor();
	}

	@SuppressWarnings("unchecked")
	public void changeType(ItemType newType) {
		if (newType == currentType) {
			return;
		}
		currentType = newType;
		elementEditor = (ElementEditor<Details>) currentType.getDetailsEditor();
		try {
			elementEditor.setValue(currentType.getDetailsType().newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		listeners.forEach(l -> l.valueChanged(elementEditor.getValue()));
		((CardLayout) getLayout()).show(this, newType.name());
	}

	@Override
	public Details getValue() {
		return elementEditor.getValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Details value) {
		currentType = ItemType.forClass(value.getClass());
		elementEditor = (ElementEditor<Details>) currentType.getDetailsEditor();
		((CardLayout) getLayout()).show(this, currentType.name());
		elementEditor.setValue(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isValueValid(Details value) {
		return ((ElementEditor<Details>) ItemType.forClass(value.getClass()).getDetailsEditor()).isValueValid(value);
	}

	@Override
	public void setAssociatedLabel(JLabel label) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JLabel getAssociatedLabel() {
		return elementEditor.getAssociatedLabel();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addValueChangeListener(ValueChangeListener<Details> listener) {
		listeners.add(listener);
		for (ItemType type : ItemType.values()) {
			ElementEditor<Details> editor = (ElementEditor<Details>) type.getDetailsEditor();
			editor.addValueChangeListener(listener);
		}
	}
}
