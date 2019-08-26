package com.pixurvival.contentPackEditor.component.effect;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;

public class DelayedFollowingElementEditor extends ElementEditor<DelayedFollowingElement> {

	private static final long serialVersionUID = 1L;

	public DelayedFollowingElementEditor() {
		TimeInput delayInput = new TimeInput();
		FollowingElementEditor followingElementEditor = new FollowingElementEditor("generic.delay", delayInput);

		bind(delayInput, DelayedFollowingElement::getDelay, DelayedFollowingElement::setDelay);
		bind(followingElementEditor, DelayedFollowingElement::getFollowingElement, DelayedFollowingElement::setFollowingElement);

		LayoutUtils.fill(this, followingElementEditor);
	}

}
