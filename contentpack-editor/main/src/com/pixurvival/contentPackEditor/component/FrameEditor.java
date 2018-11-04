package com.pixurvival.contentPackEditor.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.IntegerInput;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.sprite.Frame;

public class FrameEditor extends ElementEditor<Frame> {

	private static final long serialVersionUID = 1L;

	private IntegerInput xField = new IntegerInput();
	private IntegerInput yField = new IntegerInput();

	public FrameEditor() {

		xField.setColumns(2);
		yField.setColumns(2);

		addSubValue(xField, p -> xField.setValue(p.getX()), (p, c) -> p.setX(c));
		addSubValue(yField, p -> yField.setValue(p.getY()), (p, c) -> p.setY(c));

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.x", xField, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets.left = 5;
		LayoutUtils.addHorizontalLabelledItem(this, "generic.y", yField, gbc);
	}
}
