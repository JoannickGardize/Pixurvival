package com.pixurvival.contentPackEditor.component.effect;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
import com.pixurvival.contentPackEditor.util.BeanUtils;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.EffectTarget;
import com.pixurvival.core.contentPack.effect.FollowingEffect;
import com.pixurvival.core.contentPack.effect.FollowingElement;
import com.pixurvival.core.contentPack.effect.OrientationType;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class EffectEditor extends RootElementEditor<Effect> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class, LayoutUtils.getSpriteSheetIconProvider(), false);
	private ListEditor<FollowingElement> followingElementsEditor = new VerticalListEditor<>(FollowingElementEditor::new, BeanUtils.filledSupplier(FollowingEffect.class),
			VerticalListEditor.HORIZONTAL);

	public EffectEditor() {

		// Construction

		EnumChooser<OrientationType> orientationTypeChooser = new EnumChooser<>(OrientationType.class);
		BooleanCheckBox solidCheckbox = new BooleanCheckBox();
		BooleanCheckBox loopAnimationCheckbox = new BooleanCheckBox();
		TimeInput durationInput = new TimeInput();
		DoubleInput collisionRadiusInput = new DoubleInput(Bounds.positive());
		EffectMovementEditor effectMovementEditor = new EffectMovementEditor();
		ListEditor<EffectTarget> effectTargetsEditor = new VerticalListEditor<>(EffectTargetEditor::new, EffectTarget::new);

		// Binding

		bind(spriteSheetChooser, Effect::getSpriteSheet, Effect::setSpriteSheet);
		bind(orientationTypeChooser, Effect::getOrientation, Effect::setOrientation);
		bind(solidCheckbox, Effect::isSolid, Effect::setSolid);
		bind(loopAnimationCheckbox, Effect::isLoopAnimation, Effect::setLoopAnimation);
		bind(durationInput, Effect::getDuration, Effect::setDuration);
		bind(collisionRadiusInput, Effect::getCollisionRadius, Effect::setCollisionRadius);
		bind(effectMovementEditor, Effect::getMovement, Effect::setMovement);
		bind(effectTargetsEditor, Effect::getTargets, Effect::setTargets);
		bind(followingElementsEditor, Effect::getFollowingElements, Effect::setFollowingElements);

		// Layouting
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel displayPanel = LayoutUtils.createVerticalLabelledBox("elementType.spriteSheet", spriteSheetChooser, "effectEditor.orientation", orientationTypeChooser, "effectEditor.loopAnimation",
				loopAnimationCheckbox);
		displayPanel.setBorder(LayoutUtils.createGroupBorder("effectEditor.display"));
		JPanel propertiesPanel = LayoutUtils.createVerticalLabelledBox("generic.solid", solidCheckbox, "generic.duration", durationInput, "generic.collisionRadius", collisionRadiusInput);
		propertiesPanel.setBorder(LayoutUtils.createGroupBorder("generic.properties"));
		effectMovementEditor.setBorder(LayoutUtils.createGroupBorder("effectEditor.movement"));

		tabbedPane.addTab(TranslationService.getInstance().getString("generic.generalProperties"),
				LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 2, LayoutUtils.sideBySide(displayPanel, propertiesPanel), effectMovementEditor));
		tabbedPane.addTab(TranslationService.getInstance().getString("effectEditor.targets"), effectTargetsEditor);
		tabbedPane.addTab(TranslationService.getInstance().getString("effectEditor.followingElements"), followingElementsEditor);
		LayoutUtils.fill(this, tabbedPane);
	}
}
