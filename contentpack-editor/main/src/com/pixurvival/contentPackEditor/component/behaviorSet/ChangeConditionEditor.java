package com.pixurvival.contentPackEditor.component.behaviorSet;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementSetEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.BehaviorTarget;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.DistanceCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.FloatComparison;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.InLightCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.InventoryContainsCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.IsDayCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.NothingToDoCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.TaskFinishedCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.TimeCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.TookDamageCondition;
import com.pixurvival.core.contentPack.item.Item;

public class ChangeConditionEditor extends InstanceChangingElementEditor<ChangeCondition> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Behavior> nextBehaviorChooser;

	public ChangeConditionEditor(Supplier<Collection<Behavior>> behaviorCollectionSupplier) {
		super(ChangeCondition.class, "changeConditionType", null);
		// Construction

		nextBehaviorChooser = new ElementChooserButton<>(behaviorCollectionSupplier);

		// Binding
		bind(nextBehaviorChooser, "nextBehavior");

		// Layouting
		Component typeChooserComp = LayoutUtils.labelled("generic.type", getTypeChooser());
		Component nextBehaviorComp = LayoutUtils.labelled("changeConditionEditor.nextBehavior", nextBehaviorChooser);
		Component topBox = LayoutUtils.createHorizontalBox(typeChooserComp, nextBehaviorComp);
		LayoutUtils.addVertically(this, topBox, getSpecificPartPanel());

	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> classEntries = new ArrayList<>();

		// DistanceCondition
		classEntries.add(new ClassEntry(DistanceCondition.class, () -> {
			EnumChooser<BehaviorTarget> targetChooser = new EnumChooser<>(BehaviorTarget.class);
			EnumChooser<FloatComparison> operatorChooser = new EnumChooser<>(FloatComparison.class);
			FloatInput targetDistanceInput = new FloatInput();
			bind(targetChooser, "targetType", DistanceCondition.class);
			bind(operatorChooser, "operator", DistanceCondition.class);
			bind(targetDistanceInput, "targetDistance", DistanceCondition.class);
			Component testComponent = LayoutUtils.labelled("generic.distance", operatorChooser);
			return LayoutUtils.createHorizontalBox(targetChooser, testComponent, targetDistanceInput);
		}));

		// TimeCondition
		classEntries.add(new ClassEntry(TimeCondition.class, () -> {
			TimeInput timeInput = new TimeInput();
			bind(timeInput, "targetTime", TimeCondition.class);
			return LayoutUtils.createHorizontalBox(LayoutUtils.labelled("changeConditionType.timeCondition", timeInput));
		}));

		// TookDamageCondition
		classEntries.add(new ClassEntry(TookDamageCondition.class, JPanel::new));

		// InLightCondition
		classEntries.add(new ClassEntry(InLightCondition.class, JPanel::new));

		// IsDayCondition
		classEntries.add(new ClassEntry(IsDayCondition.class, JPanel::new));

		// TaskFinishedCondition
		classEntries.add(new ClassEntry(TaskFinishedCondition.class, JPanel::new));

		// NothingToDoCondition
		classEntries.add(new ClassEntry(NothingToDoCondition.class, JPanel::new));

		classEntries.add(new ClassEntry(InventoryContainsCondition.class, () -> {
			EnumChooser<FloatComparison> operatorChooser = new EnumChooser<>(FloatComparison.class);
			IntegerInput valueInput = new IntegerInput();
			ElementSetEditor<Item> itemSetEditor = new ElementSetEditor<>(Item.class);
			bind(operatorChooser, "operator", InventoryContainsCondition.class);
			bind(valueInput, "value", InventoryContainsCondition.class);
			bind(itemSetEditor, "items", InventoryContainsCondition.class);

			return LayoutUtils.createVerticalBox(itemSetEditor,
					LayoutUtils.createHorizontalBox(new JLabel(TranslationService.getInstance().getString("behaviorEditor.sum")), operatorChooser, valueInput));
		}));
		return classEntries;
	}

	@Override
	protected void initialize(ChangeCondition oldInstance, ChangeCondition newInstance) {
		newInstance.setNextBehavior(oldInstance.getNextBehavior());
	}

}
