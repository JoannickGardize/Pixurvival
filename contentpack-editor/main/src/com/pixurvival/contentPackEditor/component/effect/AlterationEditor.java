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
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
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
import com.pixurvival.core.livingEntity.alteration.RepeatAlteration;
import com.pixurvival.core.livingEntity.alteration.SilenceAlteration;
import com.pixurvival.core.livingEntity.alteration.SourceDirection;
import com.pixurvival.core.livingEntity.alteration.StatAlteration;
import com.pixurvival.core.livingEntity.alteration.StunAlteration;
import com.pixurvival.core.livingEntity.alteration.TeleportationAlteration;

public class AlterationEditor extends InstanceChangingElementEditor<Alteration> {

	private static final long serialVersionUID = 1L;

	public AlterationEditor() {
		this(true);
	}

	public AlterationEditor(boolean showDelayedAlteration) {
		super("alterationType", showDelayedAlteration);
		EnumChooser<AlterationTarget> alterationTargetChooser = new EnumChooser<>(AlterationTarget.class, AlterationTarget.TARGET, AlterationTarget.ORIGIN);
		bind(alterationTargetChooser, Alteration::getTargetType, Alteration::setTargetType);
		LayoutUtils.addHorizontally(this, 1, LayoutUtils.createVerticalLabelledBox("generic.type", getTypeChooser(), "generic.target", alterationTargetChooser), getSpecificPartPanel());
	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> entries = new ArrayList<>();

		// InstantDamageAlteration
		StatAmountEditor damageAmountEditor = new StatAmountEditor();
		bind(damageAmountEditor, InstantDamageAlteration::getAmount, InstantDamageAlteration::setAmount, InstantDamageAlteration.class);
		entries.add(new ClassEntry(InstantDamageAlteration.class, LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", damageAmountEditor))));

		// ContinuousDamageAlteration
		damageAmountEditor = new StatAmountEditor();
		bind(damageAmountEditor, ContinuousDamageAlteration::getDamagePerSecond, ContinuousDamageAlteration::setDamagePerSecond, ContinuousDamageAlteration.class);
		entries.add(new ClassEntry(ContinuousDamageAlteration.class, LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amountPerSecond", damageAmountEditor))));

		// InstantHealAlteration
		StatAmountEditor healAmountEditor = new StatAmountEditor();
		bind(healAmountEditor, InstantHealAlteration::getAmount, InstantHealAlteration::setAmount, InstantHealAlteration.class);
		entries.add(new ClassEntry(InstantHealAlteration.class, LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", healAmountEditor))));

		// InstantEatAlteration
		StatAmountEditor eatAmountEditor = new StatAmountEditor();
		bind(eatAmountEditor, InstantEatAlteration::getAmount, InstantEatAlteration::setAmount, InstantEatAlteration.class);
		entries.add(new ClassEntry(InstantEatAlteration.class, LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", eatAmountEditor))));

		// StatAlteration
		TimeInput durationInput = new TimeInput();
		EnumChooser<PersistentAlteration.StackPolicy> stackPolicyChooser = new EnumChooser<>(PersistentAlteration.StackPolicy.class);
		StatModifierEditor statModifierEditor = new StatModifierEditor();
		bind(durationInput, StatAlteration::getDuration, StatAlteration::setDuration, StatAlteration.class);
		bind(stackPolicyChooser, StatAlteration::getStackPolicy, StatAlteration::setStackPolicy, StatAlteration.class);
		bind(statModifierEditor, StatAlteration::getStatModifier, StatAlteration::setStatModifier, StatAlteration.class);
		JPanel statAlterationPanel = new JPanel(new BorderLayout());
		statAlterationPanel.add(LayoutUtils.createVerticalLabelledBox("generic.duration", durationInput, "alterationEditor.stackPolicy", stackPolicyChooser), BorderLayout.WEST);
		statAlterationPanel.add(statModifierEditor, BorderLayout.CENTER);
		entries.add(new ClassEntry(StatAlteration.class, statAlterationPanel));

		// FixedMovementAlteration
		durationInput = new TimeInput();
		EnumChooser<AlterationTarget> sourceTypeChooser = new EnumChooser<>(AlterationTarget.class);
		EnumChooser<SourceDirection> sourceDirectionChooser = new EnumChooser<>(SourceDirection.class);
		AngleInput relativeAngleInput = new AngleInput();
		AngleInput randomAngleInput = new AngleInput();
		StatAmountEditor speedEditor = new StatAmountEditor();
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
		entries.add(new ClassEntry(FixedMovementAlteration.class, fmaPanel));

		// StunAlteration
		durationInput = new TimeInput();
		bind(durationInput, StunAlteration::getDuration, StunAlteration::setDuration, StunAlteration.class);
		entries.add(new ClassEntry(StunAlteration.class, LayoutUtils.single(LayoutUtils.labelled("generic.duration", durationInput))));

		// InvincibleAlteration
		durationInput = new TimeInput();
		bind(durationInput, InvincibleAlteration::getDuration, InvincibleAlteration::setDuration, InvincibleAlteration.class);
		entries.add(new ClassEntry(InvincibleAlteration.class, LayoutUtils.single(LayoutUtils.labelled("generic.duration", durationInput))));

		// TeleportationAlteration
		entries.add(new ClassEntry(TeleportationAlteration.class, new JPanel()));

		// DelayedAlteration
		if (Boolean.TRUE.equals(params)) {
			TimeInput delayInput = new TimeInput();
			AlterationEditor alterationEditor = new AlterationEditor(false);
			bind(delayInput, DelayedAlteration::getDuration, DelayedAlteration::setDuration, DelayedAlteration.class);
			bind(alterationEditor, DelayedAlteration::getAlteration, DelayedAlteration::setAlteration, DelayedAlteration.class);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(LayoutUtils.labelled("generic.delay", delayInput), BorderLayout.WEST);
			panel.add(alterationEditor);
			entries.add(new ClassEntry(DelayedAlteration.class, panel));

			// RepeatAlteration
			IntegerInput numberOfRepeatInput = new IntegerInput(Bounds.positive());
			TimeInput intervalInput = new TimeInput();
			alterationEditor = new AlterationEditor(false);
			bind(numberOfRepeatInput, RepeatAlteration::getNumberOfRepeat, RepeatAlteration::setNumberOfRepeat, RepeatAlteration.class);
			bind(intervalInput, RepeatAlteration::getInterval, RepeatAlteration::setInterval, RepeatAlteration.class);
			bind(alterationEditor, RepeatAlteration::getAlteration, RepeatAlteration::setAlteration, RepeatAlteration.class);
			panel = new JPanel(new BorderLayout());
			panel.add(LayoutUtils.createVerticalLabelledBox("alterationEditor.numberOfRepeat", numberOfRepeatInput, "generic.interval", intervalInput), BorderLayout.WEST);
			panel.add(alterationEditor);
			entries.add(new ClassEntry(RepeatAlteration.class, panel));
		}

		// FollowingElementAlteration
		FollowingElementEditor followingElementEditor = new FollowingElementEditor();
		bind(followingElementEditor, FollowingElementAlteration::getFollowingElement, FollowingElementAlteration::setFollowingElement, FollowingElementAlteration.class);
		entries.add(new ClassEntry(FollowingElementAlteration.class, LayoutUtils.single(followingElementEditor)));

		// SilenceAlteration
		durationInput = new TimeInput();
		bind(durationInput, SilenceAlteration::getDuration, SilenceAlteration::setDuration, SilenceAlteration.class);
		entries.add(new ClassEntry(SilenceAlteration.class, LayoutUtils.single(LayoutUtils.labelled("generic.duration", durationInput))));

		// OverridingSpriteSheetEditor
		durationInput = new TimeInput();
		ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class, false);
		bind(durationInput, OverridingSpriteSheetAlteration::getDuration, OverridingSpriteSheetAlteration::setDuration, OverridingSpriteSheetAlteration.class);
		bind(spriteSheetChooser, OverridingSpriteSheetAlteration::getSpriteSheet, OverridingSpriteSheetAlteration::setSpriteSheet, OverridingSpriteSheetAlteration.class);
		entries.add(new ClassEntry(OverridingSpriteSheetAlteration.class, LayoutUtils.createHorizontalLabelledBox("generic.duration", durationInput, "elementType.spriteSheet", spriteSheetChooser)));

		// AddItemAlteration
		ItemStackEditor itemStackEditor = new ItemStackEditor();
		BooleanCheckBox dropRemainderCheckbox = new BooleanCheckBox();
		bind(itemStackEditor, AddItemAlteration::getItemStack, AddItemAlteration::setItemStack, AddItemAlteration.class);
		bind(dropRemainderCheckbox, AddItemAlteration::isDropRemainder, AddItemAlteration::setDropRemainder, AddItemAlteration.class);
		entries.add(new ClassEntry(AddItemAlteration.class,
				LayoutUtils.single(LayoutUtils.createHorizontalLabelledBox("elementType.item", itemStackEditor, "alterationEditor.dropRemainder", dropRemainderCheckbox))));

		return entries;
	}

	@Override
	protected void initialize(Alteration oldInstance, Alteration newInstance) {
		newInstance.setTargetType(oldInstance.getTargetType());
	}

}
