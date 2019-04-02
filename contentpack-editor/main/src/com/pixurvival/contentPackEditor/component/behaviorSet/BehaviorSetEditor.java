package com.pixurvival.contentPackEditor.component.behaviorSet;

import java.awt.BorderLayout;
import java.util.List;

import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.behaviorImpl.MoveTowardBehavior;

public class BehaviorSetEditor extends RootElementEditor<BehaviorSet> {

	private static final long serialVersionUID = 1L;

	private ListEditor<Behavior> behaviorEditors = new VerticalListEditor<>(BehaviorEditor::new, MoveTowardBehavior::new);

	public BehaviorSetEditor() {

		bind(behaviorEditors, BehaviorSet::getBehaviors, BehaviorSet::setBehaviors);

		setLayout(new BorderLayout());
		add(behaviorEditors, BorderLayout.CENTER);
	}

	@Override
	public boolean isValueValid(BehaviorSet value) {
		if (value == null) {
			return false;
		}
		List<Behavior> previousList = null;
		if (getValue() != null) {
			previousList = getValue().getBehaviors();
		}
		setBehaviorList(value.getBehaviors());
		boolean result = super.isValueValid(value);
		setBehaviorList(previousList);
		return result;
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		if (source == this) {
			setBehaviorList(getValue().getBehaviors());
		}
	}

	private void setBehaviorList(List<Behavior> list) {
		behaviorEditors.forEachEditors(e -> ((BehaviorEditor) e).setBehaviorList(list));
	}
}
