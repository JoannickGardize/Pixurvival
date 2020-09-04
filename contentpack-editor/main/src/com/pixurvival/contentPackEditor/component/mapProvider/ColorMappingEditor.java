package com.pixurvival.contentPackEditor.component.mapProvider;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ColorInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ColorPanel;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.map.ColorMapping;
import com.pixurvival.core.util.CaseUtils;

import lombok.Getter;

public class ColorMappingEditor<T extends IdentifiedElement> extends ElementEditor<ColorMapping<T>> {

	private static final long serialVersionUID = 1L;

	private @Getter ElementChooserButton<T> elementChooser;
	private ColorPanel colorPanel = new ColorPanel();

	public ColorMappingEditor(Class<T> elementType) {
		elementChooser = new ElementChooserButton<>(elementType);

		bind(elementChooser, ColorMapping::getElement, ColorMapping::setElement);
		bind(colorPanel.getColorInput(), ColorMapping::getColor, ColorMapping::setColor);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "elementType." + CaseUtils.pascalToCamelCase(elementType.getSimpleName()), elementChooser, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "generic.color", colorPanel, gbc);
	}

	public ColorInput getColorInput() {
		return colorPanel.getColorInput();
	}
}
