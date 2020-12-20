package com.pixurvival.contentPackEditor.component.effect;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;

public class DelayedFollowingElementEditor extends ElementEditor<DelayedFollowingElement> {

	private static final long serialVersionUID = 1L;

	public DelayedFollowingElementEditor() {
		super(DelayedFollowingElement.class);
		TimeInput delayInput = new TimeInput();
		FollowingElementEditor followingElementEditor = new FollowingElementEditor("generic.delay", delayInput);

		bind(delayInput, "delay");
		bind(followingElementEditor, "followingElement");

		LayoutUtils.fill(this, followingElementEditor);
	}

}
