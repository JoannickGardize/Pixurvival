package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.sprite.Frame;

public class FrameEditor extends ElementEditor<Frame> {

	private static final long serialVersionUID = 1L;

	private IntegerInput xField = new IntegerInput(Bounds.positive());
	private IntegerInput yField = new IntegerInput(Bounds.positive());

	public FrameEditor() {

		xField.setColumns(2);
		yField.setColumns(2);

		bind(xField, Frame::getX, Frame::setX);
		bind(yField, Frame::getY, Frame::setY);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.x", xField, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets.left = 5;
		LayoutUtils.addHorizontalLabelledItem(this, "generic.y", yField, gbc);
	}
}
