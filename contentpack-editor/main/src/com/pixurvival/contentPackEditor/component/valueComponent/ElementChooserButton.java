package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.core.contentPack.NamedElement;

import lombok.Getter;
import lombok.Setter;

public class ElementChooserButton<T extends NamedElement> extends JButton implements ValueComponent<T> {

	private static final long serialVersionUID = 1L;

	private SearchPopup<T> searchPopup;
	private List<ValueChangeListener<T>> listeners = new ArrayList<>();
	private Function<T, Icon> iconProvider;
	private @Getter JLabel associatedLabel;
	private @Getter T value;
	private @Getter @Setter boolean required;

	public ElementChooserButton(Function<T, Icon> iconProvider) {
		this(iconProvider, true);
	}

	public ElementChooserButton(Function<T, Icon> iconProvider, boolean required) {
		super(TranslationService.getInstance().getString("elementChooserButton.none"));
		this.required = required;

		this.iconProvider = iconProvider;
		searchPopup = new SearchPopup<>(iconProvider);
		addActionListener(e -> {
			searchPopup.setSearchText("");
			searchPopup.show(this);
		});
		searchPopup.addItemSelectionListener(item -> {
			setValue(item);
			listeners.forEach(l -> l.valueChanged(item));
		});
		setValue(null);
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<T> listener) {
		listeners.add(listener);
	}

	@Override
	public void setValue(T item) {
		value = item;
		updateDisplay();
	}

	public void setItems(Collection<T> items) {
		searchPopup.setItems(items);
	}

	@Override
	public void setForeground(Color fg) {
		if (associatedLabel != null) {
			associatedLabel.setForeground(fg);
		}
		super.setForeground(fg);
	}

	@Override
	public void setAssociatedLabel(JLabel label) {
		associatedLabel = label;
		associatedLabel.setForeground(getForeground());
	}

	@Override
	public boolean isValueValid(T value) {
		return !required || (searchPopup.getItems() != null && searchPopup.getItems().contains(value));
	}

	@Override
	public void paint(Graphics g) {
		updateDisplay();
		super.paint(g);
	}

	private void updateDisplay() {
		if (isValueValid()) {
			setIcon(iconProvider.apply(value));
			setForeground(Color.BLACK);
		} else {
			setForeground(Color.RED);
			setIcon(null);
		}
		setText(value == null ? TranslationService.getInstance().getString("elementChooserButton.none") : value.getName());
	}
}
