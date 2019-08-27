package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.util.Vector2;

public class Vector2Editor extends ElementEditor<Vector2> {

	private static final long serialVersionUID = 1L;

	public Vector2Editor() {
		DoubleInput xInput = new DoubleInput(Bounds.none());
		DoubleInput yInput = new DoubleInput(Bounds.none());

		bind(xInput, Vector2::getX, Vector2::setX);
		bind(yInput, Vector2::getY, Vector2::setY);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.x", xInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "generic.y", yInput, gbc);
	}
}
