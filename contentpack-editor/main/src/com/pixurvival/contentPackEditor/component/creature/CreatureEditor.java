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
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.ItemReward;
import com.pixurvival.core.livingEntity.StatSet;
import com.pixurvival.core.livingEntity.StatType;
import com.pixurvival.core.livingEntity.ability.AbilitySet;

public class CreatureEditor extends RootElementEditor<Creature> {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(LayoutUtils.getSpriteSheetIconProvider());
	private ElementChooserButton<BehaviorSet> behaviorSetChooser = new ElementChooserButton<>();
	private ElementChooserButton<ItemReward> itemRewardChooser = new ElementChooserButton<>(false);
	private ElementChooserButton<AbilitySet> abilitySetChooser = new ElementChooserButton<>(false);

	private StatSet statSet = new StatSet();
	private FloatInput strengthInput = new FloatInput();
	private FloatInput agilityInput = new FloatInput();
	private FloatInput intelligenceInput = new FloatInput();
	private JTextField maxHealthField = new JTextField(DECIMAL_FORMAT.format(StatType.MAX_HEALTH.getFormula().apply(statSet)));
	private JTextField speedField = new JTextField(DECIMAL_FORMAT.format(StatType.SPEED.getFormula().apply(statSet)));
	private JTextField armorField = new JTextField((DECIMAL_FORMAT.format(StatType.ARMOR.getFormula().apply(statSet) * 100) + "%"));

	public CreatureEditor() {

		// Creation

		DoubleInput collisionRadiusInput = new DoubleInput(Bounds.positive());
		maxHealthField.setEditable(false);
		speedField.setEditable(false);
		armorField.setEditable(false);

		// Actions
		strengthInput.addValueChangeListener(v -> statSet.get(StatType.STRENGTH).setBase(v));
		agilityInput.addValueChangeListener(v -> statSet.get(StatType.AGILITY).setBase(v));
		intelligenceInput.addValueChangeListener(v -> statSet.get(StatType.INTELLIGENCE).setBase(v));
		statSet.get(StatType.MAX_HEALTH).addListener(v -> maxHealthField.setText(DECIMAL_FORMAT.format(v.getValue())));

		statSet.get(StatType.SPEED).addListener(v -> speedField.setText(DECIMAL_FORMAT.format(v.getValue())));
		statSet.get(StatType.ARMOR).addListener(v -> armorField.setText(DECIMAL_FORMAT.format(v.getValue() * 100) + "%"));

		// Binding

		bind(spriteSheetChooser, Creature::getSpriteSheet, Creature::setSpriteSheet);
		bind(behaviorSetChooser, Creature::getBehaviorSet, Creature::setBehaviorSet);
		bind(itemRewardChooser, Creature::getItemReward, Creature::setItemReward);
		bind(abilitySetChooser, Creature::getAbilitySet, Creature::setAbilitySet);
		bind(collisionRadiusInput, Creature::getCollisionRadius, Creature::setCollisionRadius);
		bind(strengthInput, Creature::getStrength, Creature::setStrength);
		bind(agilityInput, Creature::getAgility, Creature::setAgility);
		bind(intelligenceInput, Creature::getIntelligence, Creature::setIntelligence);

		// Layouting

		JPanel topPanel = new JPanel(new GridBagLayout());
		topPanel.setBorder(LayoutUtils.createGroupBorder("generic.properties"));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.insets.bottom = 2;
		LayoutUtils.addHorizontalLabelledItem(topPanel, "elementType.spriteSheet", spriteSheetChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "elementType.behaviorSet", behaviorSetChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "creatureEditor.collisionRadius", collisionRadiusInput, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "elementType.abilitySet", abilitySetChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(topPanel, "elementType.itemReward", itemRewardChooser, gbc);

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

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		spriteSheetChooser.setItems(event.getContentPack().getSpriteSheets());
		behaviorSetChooser.setItems(event.getContentPack().getBehaviorSets());
		itemRewardChooser.setItems(event.getContentPack().getItemRewards());
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		statSet.get(StatType.STRENGTH).setBase(strengthInput.getValue());
		statSet.get(StatType.AGILITY).setBase(agilityInput.getValue());
		statSet.get(StatType.INTELLIGENCE).setBase(intelligenceInput.getValue());
	}
}
