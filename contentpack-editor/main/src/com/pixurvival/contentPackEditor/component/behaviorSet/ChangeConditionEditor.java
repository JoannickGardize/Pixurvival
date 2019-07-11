package com.pixurvival.contentPackEditor.component.behaviorSet;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.DoubleComparison;
import com.pixurvival.core.contentPack.creature.behaviorImpl.EnnemyDistanceCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.InLightCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.IsDayCondition;
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
		bind(testChooser, EnnemyDistanceCondition::getTest, EnnemyDistanceCondition::setTest, EnnemyDistanceCondition.class);
		bind(targetDistanceInput, EnnemyDistanceCondition::getTargetDistance, EnnemyDistanceCondition::setTargetDistance, EnnemyDistanceCondition.class);
		Component testComponent = LayoutUtils.labelled("generic.distance", testChooser);
		classEntries.add(new ClassEntry(EnnemyDistanceCondition.class, LayoutUtils.createHorizontalBox(testComponent, targetDistanceInput)));

		// TimeCondition
		TimeInput timeInput = new TimeInput();
		bind(timeInput, TimeCondition::getTargetTime, TimeCondition::setTargetTime, TimeCondition.class);
		classEntries.add(new ClassEntry(TimeCondition.class, LayoutUtils.createHorizontalBox(LayoutUtils.labelled("changeConditionType.timeCondition", timeInput))));

		// InLightCondition
		classEntries.add(new ClassEntry(InLightCondition.class, new JPanel()));

		// IsDayCondition
		classEntries.add(new ClassEntry(IsDayCondition.class, new JPanel()));

		return classEntries;
	}

	@Override
	protected void initialize(ChangeCondition oldInstance, ChangeCondition newInstance) {
		newInstance.setNextBehavior(oldInstance.getNextBehavior());
	}

}
