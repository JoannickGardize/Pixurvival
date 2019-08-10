package com.pixurvival.contentPackEditor.component.behaviorSet;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

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
import com.pixurvival.core.contentPack.creature.behaviorImpl.BehaviorTarget;
import com.pixurvival.core.contentPack.creature.behaviorImpl.DistanceCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.DoubleComparison;
import com.pixurvival.core.contentPack.creature.behaviorImpl.InLightCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.IsDayCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.TimeCondition;

public class ChangeConditionEditor extends InstanceChangingElementEditor<ChangeCondition> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Behavior> nextBehaviorChooser;

	public ChangeConditionEditor(Supplier<Collection<Behavior>> behaviorCollectionSupplier) {
		super("changeConditionType");
		// Construction

		nextBehaviorChooser = new ElementChooserButton<>(behaviorCollectionSupplier);

		// Binding
		bind(nextBehaviorChooser, ChangeCondition::getNextBehavior, ChangeCondition::setNextBehavior);

		// Layouting
		Component typeChooserComp = LayoutUtils.labelled("generic.type", getTypeChooser());
		Component nextBehaviorComp = LayoutUtils.labelled("changeConditionEditor.nextBehavior", nextBehaviorChooser);
		Component topBox = LayoutUtils.createHorizontalBox(typeChooserComp, nextBehaviorComp);
		LayoutUtils.addVertically(this, topBox, getSpecificPartPanel());

	}

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> classEntries = new ArrayList<>();

		// DistanceCondition
		EnumChooser<BehaviorTarget> targetChooser = new EnumChooser<>(BehaviorTarget.class);
		EnumChooser<DoubleComparison> operatorChooser = new EnumChooser<>(DoubleComparison.class);
		DoubleInput targetDistanceInput = new DoubleInput(Bounds.positive());
		bind(targetChooser, DistanceCondition::getTargetType, DistanceCondition::setTargetType, DistanceCondition.class);
		bind(operatorChooser, DistanceCondition::getOperator, DistanceCondition::setOperator, DistanceCondition.class);
		bind(targetDistanceInput, DistanceCondition::getTargetDistance, DistanceCondition::setTargetDistance, DistanceCondition.class);
		Component testComponent = LayoutUtils.labelled("generic.distance", operatorChooser);
		classEntries.add(new ClassEntry(DistanceCondition.class, LayoutUtils.createHorizontalBox(targetChooser, testComponent, targetDistanceInput)));

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
