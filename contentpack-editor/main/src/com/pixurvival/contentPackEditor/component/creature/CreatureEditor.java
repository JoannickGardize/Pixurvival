package com.pixurvival.contentPackEditor.component.creature;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.livingEntity.stats.StatType;

public class CreatureEditor extends RootElementEditor<Creature> {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));

	private StatSet statSet = new StatSet();
	private FloatInput strengthInput = new FloatInput();
	private FloatInput agilityInput = new FloatInput();
	private FloatInput intelligenceInput = new FloatInput();

	private AbilityIndexesConstraint abilityIndexesConstraint = new AbilityIndexesConstraint();

	public CreatureEditor() {
		super(Creature.class);

		// Creation

		ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class);
		ElementChooserButton<BehaviorSet> behaviorSetChooser = new ElementChooserButton<>(BehaviorSet.class);
		behaviorSetChooser.addAdditionalCondition(abilityIndexesConstraint);
		ElementChooserButton<ItemReward> itemRewardChooser = new ElementChooserButton<>(ItemReward.class);
		ElementChooserButton<AbilitySet> abilitySetChooser = new ElementChooserButton<>(AbilitySet.class);
		JTextField maxHealthField = new JTextField(DECIMAL_FORMAT.format(StatType.MAX_HEALTH.getFormula().compute(statSet)));
		JTextField speedField = new JTextField(DECIMAL_FORMAT.format(StatType.SPEED.getFormula().compute(statSet)));
		JTextField armorField = new JTextField(DECIMAL_FORMAT.format(StatType.ARMOR.getFormula().compute(statSet) * 100) + "%");
		FloatInput collisionRadiusInput = new FloatInput();
		maxHealthField.setEditable(false);
		speedField.setEditable(false);
		armorField.setEditable(false);
		BooleanCheckBox solidCheckbox = new BooleanCheckBox();
		BooleanCheckBox hideFullLifeBarCheckbox = new BooleanCheckBox();
		TimeInput lifetimeInput = new TimeInput();
		IntegerInput inventorySizeInput = new IntegerInput();

		// Actions

		strengthInput.addValueChangeListener(v -> statSet.get(StatType.STRENGTH).setBase(v));
		agilityInput.addValueChangeListener(v -> statSet.get(StatType.AGILITY).setBase(v));
		intelligenceInput.addValueChangeListener(v -> statSet.get(StatType.INTELLIGENCE).setBase(v));
		statSet.get(StatType.MAX_HEALTH).addListener((o, v) -> maxHealthField.setText(DECIMAL_FORMAT.format(v.getValue())));

		statSet.get(StatType.SPEED).addListener((o, v) -> speedField.setText(DECIMAL_FORMAT.format(v.getValue())));
		statSet.get(StatType.ARMOR).addListener((o, v) -> armorField.setText(DECIMAL_FORMAT.format(v.getValue() * 100) + "%"));

		// Binding

		bind(spriteSheetChooser, "spriteSheet");
		bind(behaviorSetChooser, "behaviorSet");
		bind(itemRewardChooser, "itemReward");
		bind(abilitySetChooser, "abilitySet");
		bind(collisionRadiusInput, "collisionRadius");
		bind(strengthInput, "strength");
		bind(agilityInput, "agility");
		bind(intelligenceInput, "intelligence");
		bind(solidCheckbox, "solid");
		bind(lifetimeInput, "lifetime");
		bind(inventorySizeInput, "inventorySize");
		bind(hideFullLifeBarCheckbox, "hideFullLifeBar");

		// Layouting

		JPanel topPanel = new JPanel(new GridBagLayout());
		topPanel.setBorder(LayoutUtils.createGroupBorder("generic.properties"));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.insets.bottom = 2;
		LayoutUtils.addHorizontalLabelledItem(topPanel, "elementType.spriteSheet", spriteSheetChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "elementType.behaviorSet", behaviorSetChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "generic.collisionRadius", collisionRadiusInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "creatureEditor.lifetime", lifetimeInput, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "elementType.abilitySet", abilitySetChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "elementType.itemReward", itemRewardChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "generic.solid", solidCheckbox, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "creatureEditor.inventorySize", inventorySizeInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "creature.hideFullLifeBar", hideFullLifeBarCheckbox, gbc);

		JPanel statsPanel = new JPanel(new GridBagLayout());
		statsPanel.setBorder(LayoutUtils.createGroupBorder("creatureEditor.stats"));
		gbc = LayoutUtils.createGridBagConstraints();
		gbc.insets.bottom = 2;
		LayoutUtils.addHorizontalLabelledItem(statsPanel, "statType.strength", strengthInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(statsPanel, "statType.agility", agilityInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(statsPanel, "statType.intelligence", intelligenceInput, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(statsPanel, "statType.maxHealth", maxHealthField, gbc);
		LayoutUtils.addHorizontalLabelledItem(statsPanel, "statType.speed", speedField, gbc);
		LayoutUtils.addHorizontalLabelledItem(statsPanel, "statType.armor", armorField, gbc);

		setLayout(new GridBagLayout());
		LayoutUtils.addVertically(this, topPanel, statsPanel);
	}

	@Override
	public void setValue(Creature value, boolean sneaky) {
		abilityIndexesConstraint.setCreature(value);
		super.setValue(value, sneaky);
	}

	@Override
	public boolean isValueValid(Creature value) {
		abilityIndexesConstraint.setCreature(value);
		boolean valid = super.isValueValid(value);
		abilityIndexesConstraint.setCreature(getValue());
		return valid;
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		statSet.get(StatType.STRENGTH).setBase(strengthInput.getValue());
		statSet.get(StatType.AGILITY).setBase(agilityInput.getValue());
		statSet.get(StatType.INTELLIGENCE).setBase(intelligenceInput.getValue());
	}
}
