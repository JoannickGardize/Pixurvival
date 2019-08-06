package com.pixurvival.contentPackEditor.component.elementChooser;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueChangeListener;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.util.CollectionUtils;

import lombok.Getter;
import lombok.Setter;

public class ElementChooserButton<T extends IdentifiedElement> extends JButton implements ValueComponent<T> {

	private static final long serialVersionUID = 1L;

	private @Getter SearchPopup<T> searchPopup;
	private List<ValueChangeListener<T>> listeners = new ArrayList<>();
	private Function<T, Icon> iconProvider;
	private @Getter JLabel associatedLabel;
	private @Getter T value;
	private @Getter @Setter boolean required;

	public ElementChooserButton(Class<T> elementType) {
		this(createItemsSupplier(elementType), e -> null);
	}

	public ElementChooserButton(Class<T> elementType, boolean required) {
		this(createItemsSupplier(elementType), e -> null, required);
	}

	public ElementChooserButton(Class<T> elementType, Function<T, Icon> iconProvider) {
		this(createItemsSupplier(elementType), iconProvider, true);
	}

	public ElementChooserButton(Class<T> elementType, Function<T, Icon> iconProvider, boolean required) {
		this(createItemsSupplier(elementType), iconProvider, required);
	}

	@SuppressWarnings("unchecked")
	private static <T extends IdentifiedElement> Supplier<Collection<T>> createItemsSupplier(Class<T> elementType) {
		return () -> {
			ContentPack pack = FileService.getInstance().getCurrentContentPack();
			if (pack == null) {
				return Collections.emptyList();
			} else {
				return (Collection<T>) ContentPackEditionService.getInstance().listOf(ElementType.of(elementType));
			}
		};
	}

	public ElementChooserButton(Supplier<Collection<T>> itemsSupplier) {
		this(itemsSupplier, e -> null);
	}

	public ElementChooserButton(Supplier<Collection<T>> itemsSupplier, boolean required) {
		this(itemsSupplier, e -> null, required);
	}

	public ElementChooserButton(Supplier<Collection<T>> itemsSupplier, Function<T, Icon> iconProvider) {
		this(itemsSupplier, iconProvider, true);
	}

	public ElementChooserButton(Supplier<Collection<T>> itemsSupplier, Function<T, Icon> iconProvider, boolean required) {
		super(TranslationService.getInstance().getString("elementChooserButton.none"));
		this.required = required;

		this.iconProvider = iconProvider;
		searchPopup = new SearchPopup<>(itemsSupplier, iconProvider);
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
		if (!required && value == null) {
			return true;
		}
		Collection<T> items = searchPopup.getItems();
		return items != null && CollectionUtils.containsIdentity(items, value);
	}

	@Override
	public void paint(Graphics g) {
		// updateDisplay();
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
