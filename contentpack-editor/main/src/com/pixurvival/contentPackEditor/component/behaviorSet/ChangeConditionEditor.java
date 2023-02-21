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
import com.pixurvival.contentPackEditor.component.valueComponent.PercentInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.BehaviorTarget;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.DistanceCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.DistanceToStructureCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.HealthCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.InLightCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.InventoryContainsCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.IsDayCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.NothingToDoCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.TaskFinishedCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.TileCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.TimeCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.TookDamageCondition;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.util.FloatComparison;

public class ChangeConditionEditor extends InstanceChangingElementEditor<ChangeCondition> {

	private static final long serialVersionUID = 1L;

	public ChangeConditionEditor(Supplier<Collection<Behavior>> behaviorCollectionSupplier) {
		super(ChangeCondition.class, "changeConditionType", null);
		// Construction

		ElementChooserButton<Behavior> nextBehaviorChooser = new ElementChooserButton<>(behaviorCollectionSupplier);
		FloatInput affectedNeighborsDistanceInput = new FloatInput();

		// Binding
		bind(nextBehaviorChooser, "nextBehavior");
		bind(affectedNeighborsDistanceInput, "affectedNeighborsDistance");

		// Layouting
		Component topBox = LayoutUtils.createHorizontalLabelledBox("generic.type", getTypeChooser(), "changeConditionEditor.nextBehavior", nextBehaviorChooser,
				"changeConditionEditor.affectedNeighborsDistance", affectedNeighborsDistanceInput);
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
		classEntries.add(new ClassEntry(TileCondition.class, () -> {
			ElementSetEditor<Tile> tileSet = new ElementSetEditor<>(Tile.class);
			bind(tileSet, "tileSet", TileCondition.class);
			return tileSet;
		}));
		classEntries.add(new ClassEntry(HealthCondition.class, () -> {
			EnumChooser<BehaviorTarget> targetChooser = new EnumChooser<>(BehaviorTarget.class);
			EnumChooser<FloatComparison> operatorChooser = new EnumChooser<>(FloatComparison.class);
			PercentInput percentValueInput = new PercentInput();
			bind(targetChooser, "targetType", HealthCondition.class);
			bind(operatorChooser, "operator", HealthCondition.class);
			bind(percentValueInput, "percentValue", HealthCondition.class);
			Component targetTypeComponent = LayoutUtils.labelled("changeConditionType.timeCondition.healthOf", targetChooser);
			return LayoutUtils.createHorizontalBox(targetTypeComponent, operatorChooser, percentValueInput);
		}));
		classEntries.add(new ClassEntry(DistanceToStructureCondition.class, () -> {
			ElementSetEditor<Structure> structureSet = new ElementSetEditor<>(Structure.class);
			EnumChooser<FloatComparison> operatorChooser = new EnumChooser<>(FloatComparison.class);
			FloatInput distanceInput = new FloatInput();
			bind(structureSet, "structureSet", DistanceToStructureCondition.class);
			bind(operatorChooser, "operator", DistanceToStructureCondition.class);
			bind(distanceInput, "targetDistance", DistanceToStructureCondition.class);
			return LayoutUtils.createVerticalBox(structureSet, LayoutUtils.createHorizontalBox(operatorChooser, distanceInput));
		}));
		return classEntries;
	}

	@Override
	protected void initialize(ChangeCondition oldInstance, ChangeCondition newInstance) {
		newInstance.setNextBehavior(oldInstance.getNextBehavior());
		newInstance.setId(oldInstance.getId());
		newInstance.setAffectedNeighborsDistance(oldInstance.getAffectedNeighborsDistance());
	}

}
