package com.pixurvival.contentPackEditor.component.elementChooser;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueChangeListener;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.util.CollectionUtils;
import com.pixurvival.core.util.ReflectionUtils;

import lombok.Getter;
import lombok.Setter;

public class ElementChooserButton<T extends IdentifiedElement> extends JButton implements ValueComponent<T> {

	private static final long serialVersionUID = 1L;

	private @Getter SearchPopup<T> searchPopup;
	private List<ValueChangeListener<T>> listeners = new ArrayList<>();
	private @Getter JLabel associatedLabel;
	private @Getter T value;
	private @Getter @Setter boolean required;

	public ElementChooserButton(Class<T> elementType) {
		this(createContentPackItemsSupplier(elementType), true);
	}

	public ElementChooserButton(Class<T> elementType, boolean required) {
		this(createContentPackItemsSupplier(elementType), required);
	}

	public static <T extends IdentifiedElement> Supplier<Collection<T>> createContentPackItemsSupplier(Class<T> elementType) {
		return () -> getContentPackItems(elementType);
	}

	@SuppressWarnings("unchecked")
	public static <T extends IdentifiedElement> Collection<T> getContentPackItems(Class<T> elementType) {
		ContentPack pack = FileService.getInstance().getCurrentContentPack();
		if (pack == null) {
			return Collections.emptyList();
		} else if (elementType.getSuperclass() == IdentifiedElement.class) {
			return (Collection<T>) ContentPackEditionService.getInstance().listOf(ElementType.of(elementType));
		} else {
			Class<? extends IdentifiedElement> superClass = ReflectionUtils.getSuperClassUnder(elementType, IdentifiedElement.class);
			return ((Collection<T>) ContentPackEditionService.getInstance().listOf(ElementType.of(superClass))).stream().filter(elementType::isInstance).collect(Collectors.toList());
		}
	}

	public ElementChooserButton(Supplier<Collection<T>> itemsSupplier) {
		this(itemsSupplier, true);
	}

	public ElementChooserButton(Supplier<Collection<T>> itemsSupplier, boolean required) {
		super(TranslationService.getInstance().getString("elementChooserButton.none"));
		this.required = required;

		searchPopup = new SearchPopup<>(itemsSupplier);
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
		updateDisplay();
		super.paint(g);
	}

	private void updateDisplay() {

		if (isValueValid()) {
			setIcon(IconService.getInstance().get(value));
			setForeground(Color.BLACK);
		} else {
			setForeground(Color.RED);
			setIcon(null);
		}
		setText(value == null ? TranslationService.getInstance().getString("elementChooserButton.none") : value.getName());
	}
}
