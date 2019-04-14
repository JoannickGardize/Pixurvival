package com.pixurvival.contentPackEditor.component.behaviorSet;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.DoubleComparison;
import com.pixurvival.core.contentPack.creature.behaviorImpl.PlayerDistanceCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.TimeCondition;

public class ChangeConditionEditor extends InstanceChangingElementEditor<ChangeCondition> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Behavior> nextBehaviorChooser = new ElementChooserButton<>();

	public ChangeConditionEditor() {
		super("changeConditionType");
		// Construction

		// Binding
		bind(nextBehaviorChooser, ChangeCondition::getNextBehavior, ChangeCondition::setNextBehavior);

		// Layouting
		Component typeChooserComp = LayoutUtils.labelled("generic.type", getTypeChooser());
		Component nextBehaviorComp = LayoutUtils.labelled("changeConditionEditor.nextBehavior", nextBehaviorChooser);
		Component topBox = LayoutUtils.createHorizontalBox(typeChooserComp, nextBehaviorComp);
		LayoutUtils.addVertically(this, topBox, getSpecificPartPanel());

	}

	public void setBehaviorList(List<Behavior> list) {
		nextBehaviorChooser.setItems(list);
	}

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> classEntries = new ArrayList<>();

		// PlayerDistanceCondition
		EnumChooser<DoubleComparison> testChooser = new EnumChooser<>(DoubleComparison.class);
		DoubleInput targetDistanceInput = new DoubleInput(Bounds.positive());
		bind(testChooser, PlayerDistanceCondition::getTest, PlayerDistanceCondition::setTest, PlayerDistanceCondition.class);
		bind(targetDistanceInput, PlayerDistanceCondition::getTargetDistance, PlayerDistanceCondition::setTargetDistance, PlayerDistanceCondition.class);
		Component testComponent = LayoutUtils.labelled("generic.distance", testChooser);
		classEntries.add(new ClassEntry(PlayerDistanceCondition.class, LayoutUtils.createHorizontalBox(testComponent, targetDistanceInput)));

		// TimeCondition
		DoubleInput timeInput = new DoubleInput(Bounds.positive());
		bind(timeInput, TimeCondition::getTargetTime, TimeCondition::setTargetTime, TimeCondition.class);
		classEntries.add(new ClassEntry(TimeCondition.class, LayoutUtils.createHorizontalBox(LayoutUtils.labelled("changeConditionType.timeCondition", timeInput))));

		return classEntries;
	}

	@Override
	protected void initialize(ChangeCondition oldInstance, ChangeCondition newInstance) {
		newInstance.setNextBehavior(oldInstance.getNextBehavior());
	}

}
