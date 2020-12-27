package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.sprite.Frame;

public class FrameEditor extends ElementEditor<Frame> {

	private static final long serialVersionUID = 1L;

	public FrameEditor() {
		super(Frame.class);
		IntegerInput xField = new IntegerInput();
		IntegerInput yField = new IntegerInput();

		xField.setColumns(2);
		yField.setColumns(2);

		bind(xField, "x");
		bind(yField, "y");

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.x", xField, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets.left = 5;
		LayoutUtils.addHorizontalLabelledItem(this, "generic.y", yField, gbc);
	}
}
