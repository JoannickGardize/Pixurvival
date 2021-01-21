package com.pixurvival.contentPackEditor.component.behaviorSet;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.AngleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementSetEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.PercentInput;
import com.pixurvival.contentPackEditor.component.valueComponent.StringInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.BehaviorTarget;
import com.pixurvival.core.contentPack.creature.behaviorImpl.DoNothingBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.GetAwayBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.GetAwayFromLightBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.HarvestBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.MoveTowardBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.PickUpItemsBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.TurnAroundBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.VanishBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.WanderAnchor;
import com.pixurvival.core.contentPack.creature.behaviorImpl.WanderBehavior;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.DistanceCondition;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.structure.Structure;

public class BehaviorEditor extends InstanceChangingElementEditor<Behavior> {

	private static final long serialVersionUID = 1L;

	private ListEditor<ChangeCondition> changeConditionsEditor;

	public BehaviorEditor(Supplier<Collection<Behavior>> behaviorCollectionSupplier) {
		super(Behavior.class, "behaviorType", null);

		changeConditionsEditor = new VerticalListEditor<>(() -> {
			ChangeConditionEditor editor = new ChangeConditionEditor(behaviorCollectionSupplier);
			editor.setBorder(LayoutUtils.createBorder());
			return editor;
		}, DistanceCondition::new, ListEditor.HORIZONTAL, false);

		// Construction

		StringInput nameInput = new StringInput();
		IntegerInput abilityIndexInput = new IntegerInput();

		// Binding

		bind(nameInput, "name");
		bind(abilityIndexInput, "abilityToUseId");
		bind(changeConditionsEditor, "changeConditions");

		// Layouting

		Component nameComp = LayoutUtils.labelled("generic.name", nameInput);
		Component abilityIndexComp = LayoutUtils.labelled("behaviorEditor.abilityIndex", abilityIndexInput);
		Component typeChooserComp = LayoutUtils.labelled("generic.type", getTypeChooser());
		JPanel topPanel = LayoutUtils.createHorizontalBox(nameComp, abilityIndexComp, typeChooserComp);
		changeConditionsEditor.setBorder(LayoutUtils.createGroupBorder("behaviorEditor.changeConditions"));
		LayoutUtils.addVertically(this, topPanel, getSpecificPartPanel(), changeConditionsEditor);

	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> classEntries = new ArrayList<>();

		// GET_AWAY
		classEntries.add(new ClassEntry(GetAwayBehavior.class, () -> {
			EnumChooser<BehaviorTarget> targetChooser = new EnumChooser<>(BehaviorTarget.class);
			bind(targetChooser, "targetType", GetAwayBehavior.class);
			return LayoutUtils.createHorizontalBox(targetChooser);
		}));

		// MOVE_TOWARD
		classEntries.add(new ClassEntry(MoveTowardBehavior.class, () -> {
			EnumChooser<BehaviorTarget> targetChooser = new EnumChooser<>(BehaviorTarget.class);
			FloatInput minDistanceInput = new FloatInput();
			AngleInput randomAngleInput = new AngleInput();
			bind(minDistanceInput, "minDistance", MoveTowardBehavior.class);
			bind(targetChooser, "targetType", MoveTowardBehavior.class);
			bind(randomAngleInput, "randomAngle", MoveTowardBehavior.class);
			return LayoutUtils.createHorizontalLabelledBox("generic.target", targetChooser, "generic.minDistance", minDistanceInput, "generic.randomAngle", randomAngleInput);
		}));

		// TURN_AROUND
		classEntries.add(new ClassEntry(TurnAroundBehavior.class, () -> {
			EnumChooser<BehaviorTarget> targetChooser = new EnumChooser<>(BehaviorTarget.class);
			FloatInput minDistanceInput = new FloatInput();
			FloatInput maxDistanceInput = new FloatInput();
			bind(minDistanceInput, "minDistance", TurnAroundBehavior.class);
			bind(maxDistanceInput, "maxDistance", TurnAroundBehavior.class);
			bind(targetChooser, "targetType", TurnAroundBehavior.class);
			return LayoutUtils.createHorizontalLabelledBox("generic.target", targetChooser, "generic.minDistance", minDistanceInput, "generic.maxDistance", maxDistanceInput);
		}));

		// WANDER
		classEntries.add(new ClassEntry(WanderBehavior.class, () -> {
			PercentInput moveRateInput = new PercentInput();
			PercentInput forwardFactorInput = new PercentInput();
			EnumChooser<WanderAnchor> wanderAnchorChooser = new EnumChooser<>(WanderAnchor.class);
			bind(wanderAnchorChooser, "anchorType", WanderBehavior.class);
			bind(moveRateInput, "moveRate", WanderBehavior.class);
			bind(forwardFactorInput, "forwardFactor", WanderBehavior.class);
			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
			LayoutUtils.addHorizontalLabelledItem(panel, "wanderBehavior.anchor", wanderAnchorChooser, gbc);
			LayoutUtils.addHorizontalLabelledItem(panel, "wanderBehavior.moveRate", moveRateInput, gbc);
			LayoutUtils.nextColumn(gbc);
			LayoutUtils.addHorizontalLabelledItem(panel, "wanderBehavior.forwardFactor", forwardFactorInput, gbc);
			return panel;
		}));

		// GET_AWAY_FROM_LIGHT
		classEntries.add(new ClassEntry(GetAwayFromLightBehavior.class, JPanel::new));

		// VANISH
		classEntries.add(new ClassEntry(VanishBehavior.class, JPanel::new));

		// Do nothing
		classEntries.add(new ClassEntry(DoNothingBehavior.class, () -> {
			EnumChooser<BehaviorTarget> targetChooser = new EnumChooser<>(BehaviorTarget.class);
			bind(targetChooser, "targetType", DoNothingBehavior.class);
			return LayoutUtils.single(LayoutUtils.labelled("generic.target", targetChooser));
		}));

		// HarvestBehavior
		classEntries.add(new ClassEntry(HarvestBehavior.class, () -> {
			FloatInput searchDistanceInput = new FloatInput();
			ElementSetEditor<Structure> structureSetEditor = new ElementSetEditor<>(Structure.class);
			bind(searchDistanceInput, "searchDistance", HarvestBehavior.class);
			bind(structureSetEditor, "structures", HarvestBehavior.class);
			structureSetEditor.setBorder(LayoutUtils.createGroupBorder("behaviorEditor.consideredStructures"));
			return LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 1, LayoutUtils.labelled("generic.searchDistance", searchDistanceInput), structureSetEditor);
		}));

		// PickUpItemsBehavior
		classEntries.add(new ClassEntry(PickUpItemsBehavior.class, () -> {
			FloatInput searchDistanceInput = new FloatInput();
			ElementSetEditor<Item> itemSetEditor = new ElementSetEditor<>(Item.class);
			bind(searchDistanceInput, "searchDistance", PickUpItemsBehavior.class);
			bind(itemSetEditor, "items", PickUpItemsBehavior.class);
			itemSetEditor.setBorder(LayoutUtils.createGroupBorder("behaviorEditor.itemsToPickUp"));
			return LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 1, LayoutUtils.labelled("generic.searchDistance", searchDistanceInput), itemSetEditor);
		}));

		return classEntries;
	}

	@Override
	protected void initialize(Behavior oldInstance, Behavior newInstance) {
		newInstance.setName(oldInstance.getName());
		newInstance.setId(oldInstance.getId());
		newInstance.setAbilityToUseId(oldInstance.getAbilityToUseId());
		newInstance.setChangeConditions(oldInstance.getChangeConditions());
	}
}
