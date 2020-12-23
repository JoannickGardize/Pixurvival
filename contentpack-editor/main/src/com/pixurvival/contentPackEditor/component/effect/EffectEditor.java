package com.pixurvival.contentPackEditor.component.effect;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.BeanFactory;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;
import com.pixurvival.core.contentPack.effect.DrawDepth;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.EffectTarget;
import com.pixurvival.core.contentPack.effect.OrientationType;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.AlterationTarget;

public class EffectEditor extends RootElementEditor<Effect> {

	private static final long serialVersionUID = 1L;

	public EffectEditor() {
		super(Effect.class);

		// Construction

		ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class);
		ListEditor<DelayedFollowingElement> followingElementsEditor = new VerticalListEditor<>(DelayedFollowingElementEditor::new, BeanFactory.of(DelayedFollowingElement.class),
				VerticalListEditor.HORIZONTAL);
		EnumChooser<OrientationType> orientationTypeChooser = new EnumChooser<>(OrientationType.class);
		BooleanCheckBox solidCheckbox = new BooleanCheckBox();
		EnumChooser<DrawDepth> drawDepthChooser = new EnumChooser<>(DrawDepth.class);
		TimeInput durationInput = new TimeInput();
		FloatInput entityCollisionRadiusInput = new FloatInput();
		FloatInput mapCollisionRadiusInput = new FloatInput();
		EffectMovementEditor effectMovementEditor = new EffectMovementEditor();
		ListEditor<EffectTarget> effectTargetsEditor = new VerticalListEditor<>(EffectTargetEditor::new, EffectTarget::new);
		StatFormulaEditor repeatFollowingElementsEditor = new StatFormulaEditor();
		ListEditor<Alteration> deathAlterations = new VerticalListEditor<>(() -> new AlterationEditor(AlterationTarget.ORIGIN, AlterationTarget.SELF), () -> {
			Alteration alteration = BeanFactory.newInstance(Alteration.class);
			alteration.setTargetType(AlterationTarget.SELF);
			return alteration;
		}, VerticalListEditor.HORIZONTAL);

		// Binding

		bind(spriteSheetChooser, "spriteSheet");
		bind(orientationTypeChooser, "orientation");
		bind(solidCheckbox, "solid");
		bind(durationInput, "duration");
		bind(entityCollisionRadiusInput, "entityCollisionRadius");
		bind(mapCollisionRadiusInput, "mapCollisionRadius");
		bind(effectMovementEditor, "movement");
		bind(effectTargetsEditor, "targets");
		bind(followingElementsEditor, "delayedFollowingElements");
		bind(repeatFollowingElementsEditor, "repeatFollowingElements");
		bind(deathAlterations, "deathAlterations");
		bind(drawDepthChooser, "drawDepth");

		// Layouting
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel displayPanel = LayoutUtils.createVerticalLabelledBox("elementType.spriteSheet", spriteSheetChooser, "effectEditor.orientation", orientationTypeChooser, "effectEditor.drawDepth",
				drawDepthChooser);
		displayPanel.setBorder(LayoutUtils.createGroupBorder("effectEditor.display"));
		JPanel propertiesPanel = LayoutUtils.createVerticalLabelledBox("generic.solid", solidCheckbox, "generic.duration", durationInput, "effectEditor.entityCollisionRadius",
				entityCollisionRadiusInput, "effectEditor.mapCollisionRadius", mapCollisionRadiusInput);
		propertiesPanel.setBorder(LayoutUtils.createGroupBorder("generic.properties"));
		effectMovementEditor.setBorder(LayoutUtils.createGroupBorder("effectEditor.movement"));

		tabbedPane.addTab(TranslationService.getInstance().getString("generic.generalProperties"),
				LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 2, LayoutUtils.sideBySide(displayPanel, propertiesPanel), effectMovementEditor));
		tabbedPane.addTab(TranslationService.getInstance().getString("effectEditor.targets"), effectTargetsEditor);
		JPanel followingElementsPanel = new JPanel(new BorderLayout());
		followingElementsPanel.add(LayoutUtils.single(LayoutUtils.labelled("generic.repeat", repeatFollowingElementsEditor)), BorderLayout.NORTH);
		followingElementsPanel.add(followingElementsEditor, BorderLayout.CENTER);
		tabbedPane.addTab(TranslationService.getInstance().getString("effectEditor.delayedFollowingElements"), followingElementsPanel);
		tabbedPane.addTab(TranslationService.getInstance().getString("effectEditor.deathAlterations"), deathAlterations);
		LayoutUtils.fill(this, tabbedPane);
	}
}
