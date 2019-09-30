package com.pixurvival.contentPackEditor.component.util;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class BackingArrayComboBoxModel<E> extends AbstractListModel<E> implements ComboBoxModel<E> {

	private static final long serialVersionUID = 1L;

	private @NonNull E[] array;
	private @Getter @Setter Object selectedItem;

	@Override
	public E getElementAt(int index) {
		return array[index];
	}

	@Override
	public int getSize() {
		return array.length;
	}
}
