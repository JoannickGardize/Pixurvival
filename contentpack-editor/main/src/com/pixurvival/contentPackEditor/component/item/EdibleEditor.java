package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.item.EdibleItem;

public class EdibleEditor extends ElementEditor<EdibleItem> {

	private static final long serialVersionUID = 1L;

	public EdibleEditor() {

		DoubleInput durationField = new DoubleInput(Bounds.min(0));

	}

}
