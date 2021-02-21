package com.pixurvival.contentPackEditor.component.effect;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.item.StatModifierEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.AngleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementSetEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.WeightedValueProducerEditor;
import com.pixurvival.core.SoundPreset;
import com.pixurvival.core.alteration.AddItemAlteration;
import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.AlterationTarget;
import com.pixurvival.core.alteration.ContinuousDamageAlteration;
import com.pixurvival.core.alteration.DelayedAlteration;
import com.pixurvival.core.alteration.DropItemsAlteration;
import com.pixurvival.core.alteration.FixedMovementAlteration;
import com.pixurvival.core.alteration.FollowingElementAlteration;
import com.pixurvival.core.alteration.InstantDamageAlteration;
import com.pixurvival.core.alteration.InstantEatAlteration;
import com.pixurvival.core.alteration.InstantHealAlteration;
import com.pixurvival.core.alteration.InvincibleAlteration;
import com.pixurvival.core.alteration.OverridingSpriteSheetAlteration;
import com.pixurvival.core.alteration.PermanentStatAlteration;
import com.pixurvival.core.alteration.PersistentAlteration;
import com.pixurvival.core.alteration.PlayCustomSoundAlteration;
import com.pixurvival.core.alteration.PlaySoundAlteration;
import com.pixurvival.core.alteration.RepeatAlteration;
import com.pixurvival.core.alteration.SetSpawnPositionAlteration;
import com.pixurvival.core.alteration.SilenceAlteration;
import com.pixurvival.core.alteration.SourceDirection;
import com.pixurvival.core.alteration.StatAlteration;
import com.pixurvival.core.alteration.StunAlteration;
import com.pixurvival.core.alteration.TeleportationAlteration;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.stats.StatType;

public class AlterationEditor extends InstanceChangingElementEditor<Alteration> {

	private static final long serialVersionUID = 1L;

	public AlterationEditor(AlterationTarget... targetElements) {
		this(true, targetElements);
	}

	public AlterationEditor(boolean showDelayedAlteration, AlterationTarget... targetElements) {
		super(Alteration.class, "alterationType", showDelayedAlteration);
		if (targetElements.length > 0) {
			EnumChooser<AlterationTarget> alterationTargetChooser = new EnumChooser<>(AlterationTarget.class, targetElements);
			bind(alterationTargetChooser, "targetType");
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
			BooleanCheckBox applyToStructuresCheckBox = new BooleanCheckBox();
			DamageAttributesEditor damageAttributesEditor = new DamageAttributesEditor();
			bind(damageAmountEditor, "amount", InstantDamageAlteration.class);
			bind(applyToStructuresCheckBox, "applyToStructures", InstantDamageAlteration.class);
			bind(damageAttributesEditor, "attributes", InstantDamageAlteration.class);
			return (JPanel) LayoutUtils.sideBySide(
					LayoutUtils.createVerticalLabelledBox("alterationEditor.amount", damageAmountEditor, "alterationEditor.applyToStructures", applyToStructuresCheckBox), damageAttributesEditor);
		}));

		// ContinuousDamageAlteration
		entries.add(new ClassEntry(ContinuousDamageAlteration.class, () -> {
			StatFormulaEditor damageAmountEditor = new StatFormulaEditor();
			DamageAttributesEditor damageAttributesEditor = new DamageAttributesEditor();
			bind(damageAmountEditor, "damagePerSecond", ContinuousDamageAlteration.class);
			bind(damageAttributesEditor, "attributes", ContinuousDamageAlteration.class);
			return (JPanel) LayoutUtils.sideBySide(LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amountPerSecond", damageAmountEditor)), damageAttributesEditor);
		}));

		// InstantHealAlteration
		entries.add(new ClassEntry(InstantHealAlteration.class, () -> {
			StatFormulaEditor healAmountEditor = new StatFormulaEditor();
			bind(healAmountEditor, "amount", InstantHealAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", healAmountEditor));
		}));

		// InstantEatAlteration
		entries.add(new ClassEntry(InstantEatAlteration.class, () -> {
			StatFormulaEditor eatAmountEditor = new StatFormulaEditor();
			bind(eatAmountEditor, "amount", InstantEatAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", eatAmountEditor));
		}));

		// StatAlteration
		entries.add(new ClassEntry(StatAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			EnumChooser<PersistentAlteration.StackPolicy> stackPolicyChooser = new EnumChooser<>(PersistentAlteration.StackPolicy.class);
			StatModifierEditor statModifierEditor = new StatModifierEditor();
			bind(durationInput, "duration", StatAlteration.class);
			bind(stackPolicyChooser, "stackPolicy", StatAlteration.class);
			bind(statModifierEditor, "statModifier", StatAlteration.class);
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
			bind(durationInput, "duration", FixedMovementAlteration.class);
			bind(sourceTypeChooser, "sourceType", FixedMovementAlteration.class);
			bind(sourceDirectionChooser, "sourceDirection", FixedMovementAlteration.class);
			bind(relativeAngleInput, "relativeAngle", FixedMovementAlteration.class);
			bind(randomAngleInput, "randomAngle", FixedMovementAlteration.class);
			bind(speedEditor, "speed", FixedMovementAlteration.class);
			JPanel fmaPanel = new JPanel();
			LayoutUtils.addHorizontally(fmaPanel, LayoutUtils.createVerticalLabelledBox("generic.duration", durationInput, "generic.source", sourceTypeChooser),
					LayoutUtils.createVerticalLabelledBox("offsetAngleEffect.offsetAngle", relativeAngleInput, "generic.randomAngle", randomAngleInput),
					LayoutUtils.createVerticalLabelledBox("alterationEditor.direction", sourceDirectionChooser, "statType.speed", speedEditor));
			return fmaPanel;
		}));

		// StunAlteration
		entries.add(new ClassEntry(StunAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			bind(durationInput, "duration", StunAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("generic.duration", durationInput));
		}));

		// InvincibleAlteration
		entries.add(new ClassEntry(InvincibleAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			bind(durationInput, "duration", InvincibleAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("generic.duration", durationInput));
		}));

		// TeleportationAlteration
		entries.add(new ClassEntry(TeleportationAlteration.class, JPanel::new));

		if (Boolean.TRUE.equals(params)) {
			// DelayedAlteration
			entries.add(new ClassEntry(DelayedAlteration.class, () -> {
				TimeInput delayInput = new TimeInput();
				AlterationEditor alterationEditor = new AlterationEditor(false);
				bind(delayInput, "duration", DelayedAlteration.class);
				bind(alterationEditor, "alteration", DelayedAlteration.class);
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(LayoutUtils.labelled("generic.delay", delayInput), BorderLayout.WEST);
				panel.add(alterationEditor, BorderLayout.CENTER);
				return panel;
			}));

			// RepeatAlteration
			entries.add(new ClassEntry(RepeatAlteration.class, () -> {
				IntegerInput numberOfRepeatInput = new IntegerInput();
				TimeInput intervalInput = new TimeInput();
				AlterationEditor alterationEditor = new AlterationEditor(false);
				bind(numberOfRepeatInput, "numberOfRepeat", RepeatAlteration.class);
				bind(intervalInput, "interval", RepeatAlteration.class);
				bind(alterationEditor, "alteration", RepeatAlteration.class);
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(LayoutUtils.createVerticalLabelledBox("alterationEditor.numberOfRepeat", numberOfRepeatInput, "generic.interval", intervalInput), BorderLayout.WEST);
				panel.add(alterationEditor);
				return panel;
			}));
		}

		// FollowingElementAlteration
		entries.add(new ClassEntry(FollowingElementAlteration.class, () -> {
			FollowingElementEditor followingElementEditor = new FollowingElementEditor();
			bind(followingElementEditor, "followingElement", FollowingElementAlteration.class);
			return LayoutUtils.single(followingElementEditor);
		}));

		// SilenceAlteration
		entries.add(new ClassEntry(SilenceAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			bind(durationInput, "duration", SilenceAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("generic.duration", durationInput));
		}));

		// OverridingSpriteSheetEditor
		entries.add(new ClassEntry(OverridingSpriteSheetAlteration.class, () -> {
			TimeInput durationInput = new TimeInput();
			ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class);
			bind(durationInput, "duration", OverridingSpriteSheetAlteration.class);
			bind(spriteSheetChooser, "spriteSheet", OverridingSpriteSheetAlteration.class);
			return LayoutUtils.createHorizontalLabelledBox("generic.duration", durationInput, "elementType.spriteSheet", spriteSheetChooser);
		}));

		// AddItemAlteration
		entries.add(new ClassEntry(AddItemAlteration.class, () -> {
			StatFormulaEditor repeatEditor = new StatFormulaEditor();
			WeightedValueProducerEditor<ItemStack> itemStacksEditor = new WeightedValueProducerEditor<>(ItemStackEditor::new, ItemStack::new);
			itemStacksEditor.setBorder(LayoutUtils.createGroupBorder("alterationEditor.chooseItemList"));
			BooleanCheckBox dropRemainderCheckbox = new BooleanCheckBox();

			bind(repeatEditor, "repeat", AddItemAlteration.class);
			bind(itemStacksEditor, "itemStacks", AddItemAlteration.class);
			bind(dropRemainderCheckbox, "dropRemainder", AddItemAlteration.class);

			return LayoutUtils.createVerticalBox(LayoutUtils.single(LayoutUtils.createHorizontalLabelledBox("generic.repeat", repeatEditor, "alterationEditor.dropRemainder", dropRemainderCheckbox)),
					itemStacksEditor);
		}));

		// PlaySoundAlteration
		entries.add(new ClassEntry(PlaySoundAlteration.class, () -> {
			EnumChooser<SoundPreset> soundPresetChooser = new EnumChooser<>(SoundPreset.class);
			bind(soundPresetChooser, "preset", PlaySoundAlteration.class);
			return LayoutUtils.single(LayoutUtils.labelled("alterationEditor.sound", soundPresetChooser));
		}));

		// PlayCustomSoundAlteration
		entries.add(new ClassEntry(PlayCustomSoundAlteration.class, () -> {
			ElementChooserButton<ResourceEntry> soundChooser = new ElementChooserButton<>(ResourcesService.getInstance().getResourcesSupplier());
			bind(soundChooser, "sound", PlayCustomSoundAlteration.class).getter(v -> v.getSound() == null ? null : ResourcesService.getInstance().getResource(v.getSound()))
					.setter((v, f) -> v.setSound(f == null ? null : f.getName()));
			return LayoutUtils.single(LayoutUtils.labelled("alterationEditor.sound", soundChooser));
		}));

		// DropItemsAlteration
		entries.add(new ClassEntry(DropItemsAlteration.class, () -> {
			ElementSetEditor<Item> itemsSet = new ElementSetEditor<>(Item.class);
			itemsSet.setBorder(LayoutUtils.createGroupBorder("alterationEditor.itemToDrop"));
			IntegerInput maxQuantityInput = new IntegerInput();
			bind(itemsSet, "items", DropItemsAlteration.class);
			bind(maxQuantityInput, "maxQuantity", DropItemsAlteration.class);
			return LayoutUtils.createVerticalBox(itemsSet, LayoutUtils.labelled("alterationEditor.maxQuantity", maxQuantityInput));
		}));

		// PermanentStatAlteration
		entries.add(new ClassEntry(PermanentStatAlteration.class, () -> {
			EnumChooser<StatType> statTypeChooser = new EnumChooser<>(StatType.class);
			StatFormulaEditor amountEditor = new StatFormulaEditor();
			bind(statTypeChooser, "statType", PermanentStatAlteration.class);
			bind(amountEditor, "amount", PermanentStatAlteration.class);
			return LayoutUtils.createHorizontalLabelledBox("statModifierEditor.statType", statTypeChooser, "generic.add", amountEditor);

		}));

		// SetSpawnPositionAlteration
		entries.add(new ClassEntry(SetSpawnPositionAlteration.class, () -> {
			return LayoutUtils.single(new JLabel(TranslationService.getInstance().getString("alterationEditor.setSpawnPosition")));
		}));

		return entries;
	}

	@Override
	protected void initialize(Alteration oldInstance, Alteration newInstance) {
		newInstance.setTargetType(oldInstance.getTargetType());
	}

}
