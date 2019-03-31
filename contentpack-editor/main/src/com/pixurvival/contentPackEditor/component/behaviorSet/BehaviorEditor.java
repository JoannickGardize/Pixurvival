package com.pixurvival.contentPackEditor.component.behaviorSet;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.StringInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.MoveTowardBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.PlayerDistanceCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.TurnAroundBehavior;

public class BehaviorEditor extends ElementEditor<Behavior> {

	private static final long serialVersionUID = 1L;

	private JPanel specificPartPanel;
	private JComboBox<BehaviorType> typeChooser;

	public BehaviorEditor() {

		// Construction

		StringInput nameInput = new StringInput(1);
		IntegerInput abilityIndexInput = new IntegerInput(Bounds.min(-1));
		typeChooser = new JComboBox<>(BehaviorType.values());
		createSpecificPart();
		ListEditor<ChangeCondition> changeConditionsEditor = new VerticalListEditor<>(LayoutUtils.bordered(ChangeConditionEditor::new), PlayerDistanceCondition::new, ListEditor.HORIZONTAL, false);

		// Actions

		typeChooser.addItemListener(e -> {
			if (typeChooser.isPopupVisible() && e.getStateChange() == ItemEvent.SELECTED) {
				changeInstance(((BehaviorType) e.getItem()).getBehaviorSupplier().get());
			}
		});

		// Binding

		bind(nameInput, Behavior::getName, Behavior::setName);
		bind(abilityIndexInput, Behavior::getAbilityToUseId, Behavior::setAbilityToUseId);
		bind(changeConditionsEditor, Behavior::getChangeConditions, Behavior::setChangeConditions);

		// Layouting

		Component nameComp = LayoutUtils.labelled("generic.name", nameInput);
		Component abilityIndexComp = LayoutUtils.labelled("behaviorEditor.abilityIndex", abilityIndexInput);
		Component typeChooserComp = LayoutUtils.labelled("generic.type", typeChooser);
		JPanel topPanel = LayoutUtils.createHorizontalBox(nameComp, abilityIndexComp, typeChooserComp);
		changeConditionsEditor.setBorder(LayoutUtils.createGroupBorder("behaviorEditor.changeConditions"));
		LayoutUtils.addVertically(this, topPanel, specificPartPanel, changeConditionsEditor);

	}

	private void createSpecificPart() {
		specificPartPanel = new JPanel(new CardLayout());

		// GET_AWAY
		specificPartPanel.add(new JPanel(), BehaviorType.GET_AWAY.name());

		// MOVE_TOWARD
		DoubleInput minDistanceInput = new DoubleInput(Bounds.positive());
		bind(minDistanceInput, MoveTowardBehavior::getMinDistance, MoveTowardBehavior::setMinDistance, MoveTowardBehavior.class);
		specificPartPanel.add(LayoutUtils.createHorizontalBox(LayoutUtils.labelled("behaviorEditor.minDistance", minDistanceInput)), BehaviorType.MOVE_TOWARD.name());

		// TURN_AROUND
		minDistanceInput = new DoubleInput(Bounds.positive());
		DoubleInput maxDistanceInput = new DoubleInput(Bounds.positive());
		bind(minDistanceInput, TurnAroundBehavior::getMinDistance, TurnAroundBehavior::setMinDistance, TurnAroundBehavior.class);
		bind(maxDistanceInput, TurnAroundBehavior::getMaxDistance, TurnAroundBehavior::setMaxDistance, TurnAroundBehavior.class);
		specificPartPanel.add(
				LayoutUtils.createHorizontalBox(LayoutUtils.labelled("behaviorEditor.minDistance", minDistanceInput), LayoutUtils.labelled("behaviorEditor.maxDistance", maxDistanceInput)),
				BehaviorType.TURN_AROUND.name());

		// WANDER_BEHAVIOR
		specificPartPanel.add(new JPanel(), BehaviorType.WANDER.name());
	}

	private void changeInstance(Behavior newInstance) {
		if (newInstance == null) {
			return;
		}
		Behavior oldInstance = getValue();
		if (oldInstance == null) {
			setValue(newInstance);
			return;
		}
		newInstance.setName(oldInstance.getName());
		newInstance.setAbilityToUseId(oldInstance.getAbilityToUseId());
		newInstance.setChangeConditions(oldInstance.getChangeConditions());
		setValue(newInstance);
		notifyValueChanged();
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		if (source == this) {
			BehaviorType type = BehaviorType.of(getValue());
			((CardLayout) specificPartPanel.getLayout()).show(specificPartPanel, type.name());
			typeChooser.setSelectedItem(type);
		}
	}
}
