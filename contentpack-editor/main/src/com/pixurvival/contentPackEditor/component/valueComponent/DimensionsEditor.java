package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.Dimensions;

public class DimensionsEditor extends ElementEditor<Dimensions> {

	private static final long serialVersionUID = 1L;

	public DimensionsEditor() {

		// Construction
		IntegerInput widthInput = new IntegerInput(Bounds.min(1));
		IntegerInput heightInput = new IntegerInput(Bounds.min(1));

		// Binding

		bind(widthInput, Dimensions::getWidth, Dimensions::setWidth);
		bind(heightInput, Dimensions::getHeight, Dimensions::setHeight);

		// Layouting
		setLayout(new GridBagLayout());
		setBorder(LayoutUtils.createGroupBorder("dimensionsEditor.title"));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.width", widthInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "generic.height", heightInput, gbc);
	}
}
