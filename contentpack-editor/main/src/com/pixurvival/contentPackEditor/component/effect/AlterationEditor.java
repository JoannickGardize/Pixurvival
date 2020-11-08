package com.pixurvival.contentPackEditor.component.effect;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.item.StatModifierEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.AngleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.WeightedValueProducerEditor;
import com.pixurvival.core.SoundPreset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.alteration.AddItemAlteration;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.AlterationTarget;
import com.pixurvival.core.livingEntity.alteration.ContinuousDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.DelayedAlteration;
import com.pixurvival.core.livingEntity.alteration.FixedMovementAlteration;
import com.pixurvival.core.livingEntity.alteration.FollowingElementAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantEatAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantHealAlteration;
import com.pixurvival.core.livingEntity.alteration.InvincibleAlteration;
import com.pixurvival.core.livingEntity.alteration.OverridingSpriteSheetAlteration;
import com.pixurvival.core.livingEntity.alteration.PersistentAlteration;
import com.pixurvival.core.livingEntity.alteration.PlaySoundAlteration;
import com.pixurvival.core.livingEntity.alteration.RepeatAlteration;
import com.pixurvival.core.livingEntity.alteration.SilenceAlteration;
import com.pixurvival.core.livingEntity.alteration.SourceDirection;
import com.pixurvival.core.livingEntity.alteration.StatAlteration;
import com.pixurvival.core.livingEntity.alteration.StunAlteration;
import com.pixurvival.core.livingEntity.alteration.TeleportationAlteration;

public class AlterationEditor extends InstanceChangingElementEditor<Alteration> {

	private static final long serialVersionUID = 1L;

	public AlterationEditor(AlterationTarget... targetElements) {
		this(true, targetElements);
	}

	public AlterationEditor(boolean showDelayedAlteration, AlterationTarget... targetElements) {
		super("alterationType", showDelayedAlteration);
		if (targetElements.length > 0) {
			EnumChooser<AlterationTarget> alterationTargetChooser = new EnumChooser<>(AlterationTarget.class, targetElements);
			bind(alterationTargetChooser, Alteration::getTargetType, Alteration::setTargetType);
			LayoutUtils.addHorizontally(this, 1, LayoutUtils.createVerticalLabelledBox("generic.type", getTypeChooser(), "alterationEditor.applyTo", alterationTargetChooser), getSpecificPartPanel());
		} else {
			LayoutUtils.addHorizontally(this, 1, LayoutUtils.labelled("generic.type", getTypeChooser()), getSpecificPartPanel());

		}
	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> entries = new ArrayList<>();

		// InstantDamageAlteration
		entries.add(new ClassEntry(InstantDamageAlteration.class, () -> {
			StatFormulaEditor damageAmountEditor = new StatFormulaEditor();
			bind(damageAmountEditor, InstantDamageAlteration::getAmount, InstantDamageAlteration::setAmount, InstantDamageAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", damageAmountEditor));
		}));

		// ContinuousDamageAlteration
		entries.add(new ClassEntry(ContinuousDamageAlteration.class, () -> {
			StatFormulaEditor damageAmountEditor = new StatFormulaEditor();
			bind(damageAmountEditor, ContinuousDamageAlteration::getDamagePerSecond, ContinuousDamageAlteration::setDamagePerSecond, ContinuousDamageAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amountPerSecond", damageAmountEditor));
		}));

		// InstantHealAlteration
		entries.add(new ClassEntry(InstantHealAlteration.class, () -> {
			StatFormulaEditor healAmountEditor = new StatFormulaEditor();
			bind(healAmountEditor, InstantHealAlteration::getAmount, InstantHealAlteration::setAmount, InstantHealAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", healAmountEditor));
		}));

		// InstantEatAlteration
		entries.add(new ClassEntry(InstantEatAlteration.class, () -> {
			StatFormulaEditor eatAmountEditor = new StatFormulaEditor();
			bind(eatAmountEditor, InstantEatAlteration::getAmount, InstantEatAlteration::setAmount, InstantEatAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", eatAmountEditor));
		}));

		// StatAlteration
		entries.add(new ClassEntry(StatAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			EnumChooser<PersistentAlteration.StackPolicy> stackPolicyChooser = new EnumChooser<>(PersistentAlteration.StackPolicy.class);
			StatModifierEditor statModifierEditor = new StatModifierEditor();
			bind(durationInput, StatAlteration::getDuration, StatAlteration::setDuration, StatAlteration.class);
			bind(stackPolicyChooser, StatAlteration::getStackPolicy, StatAlteration::setStackPolicy, StatAlteration.class);
			bind(statModifierEditor, StatAlteration::getStatModifier, StatAlteration::setStatModifier, StatAlteration.class);
			JPanel statAlterationPanel = new JPanel(new BorderLayout());
			statAlterationPanel.add(LayoutUtils.createVerticalLabelledBox("generic.duration", durationInput, "alterationEditor.stackPolicy", stackPolicyChooser), BorderLayout.WEST);
			statAlterationPanel.add(statModifierEditor, BorderLayout.CENTER);
			return statAlterationPanel;
		}));

		// FixedMovementAlteration
		entries.add(new ClassEntry(FixedMovementAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			EnumChooser<AlterationTarget> sourceTypeChooser = new EnumChooser<>(AlterationTarget.class);
			EnumChooser<SourceDirection> sourceDirectionChooser = new EnumChooser<>(SourceDirection.class);
			AngleInput relativeAngleInput = new AngleInput();
			AngleInput randomAngleInput = new AngleInput();
			StatFormulaEditor speedEditor = new StatFormulaEditor();
			bind(durationInput, FixedMovementAlteration::getDuration, FixedMovementAlteration::setDuration, FixedMovementAlteration.class);
			bind(sourceTypeChooser, FixedMovementAlteration::getSourceType, FixedMovementAlteration::setSourceType, FixedMovementAlteration.class);
			bind(sourceDirectionChooser, FixedMovementAlteration::getSourceDirection, FixedMovementAlteration::setSourceDirection, FixedMovementAlteration.class);
			bind(relativeAngleInput, FixedMovementAlteration::getRelativeAngle, FixedMovementAlteration::setRelativeAngle, FixedMovementAlteration.class);
			bind(randomAngleInput, FixedMovementAlteration::getRandomAngle, FixedMovementAlteration::setRandomAngle, FixedMovementAlteration.class);
			bind(speedEditor, FixedMovementAlteration::getSpeed, FixedMovementAlteration::setSpeed, FixedMovementAlteration.class);
			JPanel fmaPanel = new JPanel();
			LayoutUtils.addHorizontally(fmaPanel, LayoutUtils.createVerticalLabelledBox("generic.duration", durationInput, "generic.source", sourceTypeChooser),
					LayoutUtils.createVerticalLabelledBox("offsetAngleEffect.offsetAngle", relativeAngleInput, "generic.randomAngle", randomAngleInput),
					LayoutUtils.createVerticalLabelledBox("alterationEditor.direction", sourceDirectionChooser, "statType.speed", speedEditor));
			return fmaPanel;
		}));

		// StunAlteration
		entries.add(new ClassEntry(StunAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			bind(durationInput, StunAlteration::getDuration, StunAlteration::setDuration, StunAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("generic.duration", durationInput));
		}));

		// InvincibleAlteration
		entries.add(new ClassEntry(InvincibleAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			bind(durationInput, InvincibleAlteration::getDuration, InvincibleAlteration::setDuration, InvincibleAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("generic.duration", durationInput));
		}));

		// TeleportationAlteration
		entries.add(new ClassEntry(TeleportationAlteration.class, JPanel::new));

		if (Boolean.TRUE.equals(params)) {
			// DelayedAlteration
			entries.add(new ClassEntry(DelayedAlteration.class, () -> {
				TimeInput delayInput = new TimeInput();
				AlterationEditor alterationEditor = new AlterationEditor(false);
				bind(delayInput, DelayedAlteration::getDuration, DelayedAlteration::setDuration, DelayedAlteration.class);
				bind(alterationEditor, DelayedAlteration::getAlteration, DelayedAlteration::setAlteration, DelayedAlteration.class);
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(LayoutUtils.labelled("generic.delay", delayInput), BorderLayout.WEST);
				panel.add(alterationEditor, BorderLayout.CENTER);
				return panel;
			}));

			// RepeatAlteration
			entries.add(new ClassEntry(RepeatAlteration.class, () -> {
				IntegerInput numberOfRepeatInput = new IntegerInput(Bounds.positive());
				TimeInput intervalInput = new TimeInput();
				AlterationEditor alterationEditor = new AlterationEditor(false);
				bind(numberOfRepeatInput, RepeatAlteration::getNumberOfRepeat, RepeatAlteration::setNumberOfRepeat, RepeatAlteration.class);
				bind(intervalInput, RepeatAlteration::getInterval, RepeatAlteration::setInterval, RepeatAlteration.class);
				bind(alterationEditor, RepeatAlteration::getAlteration, RepeatAlteration::setAlteration, RepeatAlteration.class);
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(LayoutUtils.createVerticalLabelledBox("alterationEditor.numberOfRepeat", numberOfRepeatInput, "generic.interval", intervalInput), BorderLayout.WEST);
				panel.add(alterationEditor);
				return panel;
			}));
		}

		// FollowingElementAlteration
		entries.add(new ClassEntry(FollowingElementAlteration.class, () -> {
			FollowingElementEditor followingElementEditor = new FollowingElementEditor();
			bind(followingElementEditor, FollowingElementAlteration::getFollowingElement, FollowingElementAlteration::setFollowingElement, FollowingElementAlteration.class);
			return LayoutUtils.single(followingElementEditor);
		}));

		// SilenceAlteration
		entries.add(new ClassEntry(SilenceAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			bind(durationInput, SilenceAlteration::getDuration, SilenceAlteration::setDuration, SilenceAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("generic.duration", durationInput));
		}));

		// OverridingSpriteSheetEditor
		entries.add(new ClassEntry(OverridingSpriteSheetAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class, false);
			bind(durationInput, OverridingSpriteSheetAlteration::getDuration, OverridingSpriteSheetAlteration::setDuration, OverridingSpriteSheetAlteration.class);
			bind(spriteSheetChooser, OverridingSpriteSheetAlteration::getSpriteSheet, OverridingSpriteSheetAlteration::setSpriteSheet, OverridingSpriteSheetAlteration.class);
			return LayoutUtils.createHorizontalLabelledBox("generic.duration", durationInput, "elementType.spriteSheet", spriteSheetChooser);
		}));

		// AddItemAlteration
		entries.add(new ClassEntry(AddItemAlteration.class, () -> {
			StatFormulaEditor repeatEditor = new StatFormulaEditor();
			WeightedValueProducerEditor<ItemStack> itemStacksEditor = new WeightedValueProducerEditor<>(ItemStackEditor::new, ItemStack::new);
			itemStacksEditor.setBorder(LayoutUtils.createGroupBorder("alterationEditor.chooseItemList"));
			BooleanCheckBox dropRemainderCheckbox = new BooleanCheckBox();

			bind(repeatEditor, AddItemAlteration::getRepeat, AddItemAlteration::setRepeat, AddItemAlteration.class);
			bind(itemStacksEditor, AddItemAlteration::getItemStacks, AddItemAlteration::setItemStacks, AddItemAlteration.class);
			bind(dropRemainderCheckbox, AddItemAlteration::isDropRemainder, AddItemAlteration::setDropRemainder, AddItemAlteration.class);

			return LayoutUtils.createVerticalBox(LayoutUtils.single(LayoutUtils.createHorizontalLabelledBox("generic.repeat", repeatEditor, "alterationEditor.dropRemainder", dropRemainderCheckbox)),
					itemStacksEditor);
		}));

		// PlaySoundAlteration
		entries.add(new ClassEntry(PlaySoundAlteration.class, () -> {
			EnumChooser<SoundPreset> soundPresetChooser = new EnumChooser<>(SoundPreset.class);
			bind(soundPresetChooser, PlaySoundAlteration::getPreset, PlaySoundAlteration::setPreset, PlaySoundAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("alterationEditor.sound", soundPresetChooser));
		}));

		return entries;
	}

	@Override
	protected void initialize(Alteration oldInstance, Alteration newInstance) {
		newInstance.setTargetType(oldInstance.getTargetType());
	}

}
