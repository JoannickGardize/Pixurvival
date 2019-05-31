package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;

public class EdiblePanel extends ItemTypePropertiesPanel {

	private static final long serialVersionUID = 1L;

	public EdiblePanel() {

		DoubleInput durationField = new DoubleInput(Bounds.min(0));

	}

	@Override
	public void bindTo(ItemEditor itemEditor) {
		// TODO Auto-generated method stub

	}

}
