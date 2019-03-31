package com.pixurvival.contentPackEditor.component.behaviorSet;

import java.awt.BorderLayout;

import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.behaviorImpl.MoveTowardBehavior;

public class BehaviorSetEditor extends RootElementEditor<BehaviorSet> {

	private static final long serialVersionUID = 1L;

	public BehaviorSetEditor() {
		ListEditor<Behavior> behaviorEditor = new VerticalListEditor<>(BehaviorEditor::new, MoveTowardBehavior::new);

		bind(behaviorEditor, BehaviorSet::getBehaviors, BehaviorSet::setBehaviors);

		setLayout(new BorderLayout());
		add(behaviorEditor, BorderLayout.CENTER);
	}
}
