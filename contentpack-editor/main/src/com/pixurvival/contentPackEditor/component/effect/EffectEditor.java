package com.pixurvival.contentPackEditor.component.effect;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.BeanFactory;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;
import com.pixurvival.core.contentPack.effect.DrawDepth;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.EffectTarget;
import com.pixurvival.core.contentPack.effect.FollowingElement;
import com.pixurvival.core.contentPack.effect.OrientationType;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.livingEntity.alteration.Alteration;

public class EffectEditor extends RootElementEditor<Effect> {

	private static final long serialVersionUID = 1L;

	public EffectEditor() {

		// Construction

		ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class, LayoutUtils.getSpriteSheetIconProvider(), false);
		ListEditor<DelayedFollowingElement> followingElementsEditor = new VerticalListEditor<>(DelayedFollowingElementEditor::new, BeanFactory.of(DelayedFollowingElement.class),
				VerticalListEditor.HORIZONTAL);
		EnumChooser<OrientationType> orientationTypeChooser = new EnumChooser<>(OrientationType.class);
		BooleanCheckBox solidCheckbox = new BooleanCheckBox();
		BooleanCheckBox loopAnimationCheckbox = new BooleanCheckBox();
		EnumChooser<DrawDepth> drawDepthChooser = new EnumChooser<>(DrawDepth.class);
		TimeInput durationInput = new TimeInput();
		DoubleInput collisionRadiusInput = new DoubleInput(Bounds.positive());
		EffectMovementEditor effectMovementEditor = new EffectMovementEditor();
		ListEditor<EffectTarget> effectTargetsEditor = new VerticalListEditor<>(EffectTargetEditor::new, EffectTarget::new);
		StatAmountEditor repeatFollowingElementsEditor = new StatAmountEditor();
		ListEditor<FollowingElement> deathFollowingElements = new VerticalListEditor<>(FollowingElementEditor::new, BeanFactory.of(FollowingElement.class));
		ListEditor<Alteration> deathAlterations = new VerticalListEditor<>(AlterationEditor::new, BeanFactory.of(Alteration.class), VerticalListEditor.HORIZONTAL);

		// Binding

		bind(spriteSheetChooser, Effect::getSpriteSheet, Effect::setSpriteSheet);
		bind(orientationTypeChooser, Effect::getOrientation, Effect::setOrientation);
		bind(solidCheckbox, Effect::isSolid, Effect::setSolid);
		bind(loopAnimationCheckbox, Effect::isLoopAnimation, Effect::setLoopAnimation);
		bind(durationInput, Effect::getDuration, Effect::setDuration);
		bind(collisionRadiusInput, Effect::getCollisionRadius, Effect::setCollisionRadius);
		bind(effectMovementEditor, Effect::getMovement, Effect::setMovement);
		bind(effectTargetsEditor, Effect::getTargets, Effect::setTargets);
		bind(followingElementsEditor, Effect::getDelayedFollowingElements, Effect::setDelayedFollowingElements);
		bind(repeatFollowingElementsEditor, Effect::getRepeatFollowingElements, Effect::setRepeatFollowingElements);
		bind(deathFollowingElements, Effect::getDeathFollowingElements, Effect::setDeathFollowingElements);
		bind(deathAlterations, Effect::getDeathAlterations, Effect::setDeathAlterations);
		bind(drawDepthChooser, Effect::getDrawDepth, Effect::setDrawDepth);

		// Layouting
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel displayPanel = LayoutUtils.createVerticalLabelledBox("elementType.spriteSheet", spriteSheetChooser, "effectEditor.orientation", orientationTypeChooser, "effectEditor.loopAnimation",
				loopAnimationCheckbox, "effectEditor.drawDepth", drawDepthChooser);
		displayPanel.setBorder(LayoutUtils.createGroupBorder("effectEditor.display"));
		JPanel propertiesPanel = LayoutUtils.createVerticalLabelledBox("generic.solid", solidCheckbox, "generic.duration", durationInput, "generic.collisionRadius", collisionRadiusInput);
		propertiesPanel.setBorder(LayoutUtils.createGroupBorder("generic.properties"));
		effectMovementEditor.setBorder(LayoutUtils.createGroupBorder("effectEditor.movement"));

		tabbedPane.addTab(TranslationService.getInstance().getString("generic.generalProperties"),
				LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 2, LayoutUtils.sideBySide(displayPanel, propertiesPanel), effectMovementEditor));
		tabbedPane.addTab(TranslationService.getInstance().getString("effectEditor.targets"), effectTargetsEditor);
		JPanel followingElementsPanel = new JPanel(new BorderLayout());
		followingElementsPanel.add(LayoutUtils.single(LayoutUtils.labelled("generic.repeat", repeatFollowingElementsEditor)), BorderLayout.NORTH);
		followingElementsPanel.add(followingElementsEditor, BorderLayout.CENTER);
		tabbedPane.addTab(TranslationService.getInstance().getString("effectEditor.delayedFollowingElements"), followingElementsPanel);
		tabbedPane.addTab(TranslationService.getInstance().getString("effectEditor.deathFollowingElements"), deathFollowingElements);
		tabbedPane.addTab(TranslationService.getInstance().getString("effectEditor.deathAlterations"), deathAlterations);
		LayoutUtils.fill(this, tabbedPane);
	}
}
