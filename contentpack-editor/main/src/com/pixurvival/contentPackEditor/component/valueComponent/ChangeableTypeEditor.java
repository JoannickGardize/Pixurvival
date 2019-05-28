package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.util.BeanUtils;

@Deprecated
public class ChangeableTypeEditor<T> extends JPanel implements ValueComponent<T> {

	private static final long serialVersionUID = 1L;

	private Class<? extends T> currentType;
	private List<ValueChangeListener<T>> listeners = new ArrayList<>();
	private Map<Class<? extends T>, ElementEditor<T>> typesMap = new HashMap<>();

	public ChangeableTypeEditor() {
		setLayout(new CardLayout());
	}

	@SuppressWarnings("unchecked")
	public void addType(Class<?> type, String editorsPackageName) {
		ElementEditor<T> editor;
		try {
			editor = (ElementEditor<T>) Class.forName(editorsPackageName + "." + type.getSimpleName() + "Editor").newInstance();
			typesMap.put((Class<? extends T>) type, editor);
			add(editor, type.getSimpleName());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void changeType(Class<? extends T> newType) {
		if (newType == currentType) {
			return;
		}
		currentType = newType;
		ElementEditor<T> elementEditor = typesMap.get(newType);
		elementEditor.setValue(BeanUtils.newFilledInstance(newType));
		listeners.forEach(l -> l.valueChanged(elementEditor.getValue()));
		((CardLayout) getLayout()).show(this, newType.getSimpleName());
	}

	@Override
	public T getValue() {
		if (currentType == null) {
			return null;
		}
		return typesMap.get(currentType).getValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(T value) {
		currentType = (Class<? extends T>) value.getClass();
		((CardLayout) getLayout()).show(this, currentType.getSimpleName());
		typesMap.get(currentType).setValue(value);
	}

	@Override
	public boolean isValueValid(T value) {
		return typesMap.get(value.getClass()).isValueValid(value);
	}

	@Override
	public void setAssociatedLabel(JLabel label) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JLabel getAssociatedLabel() {
		return typesMap.get(currentType).getAssociatedLabel();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addValueChangeListener(ValueChangeListener<T> listener) {
		listeners.add(listener);
		for (ElementEditor editor : typesMap.values()) {
			editor.addValueChangeListener(listener);
		}
	}
}
