package com.pixurvival.contentPackEditor.component.effect;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.abilitySet.OffsetAngleEffectEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.effect.FollowingEffect;
import com.pixurvival.core.contentPack.effect.FollowingElement;

public class FollowingElementEditor extends InstanceChangingElementEditor<FollowingElement> {

	private static final long serialVersionUID = 1L;

	public FollowingElementEditor() {
		super("followingElementType");

		TimeInput delayInput = new TimeInput();

		bind(delayInput, FollowingElement::getDelay, FollowingElement::setDelay);

		setLayout(new BorderLayout());
		add(LayoutUtils.createHorizontalLabelledBox("generic.delay", delayInput, "generic.type", getTypeChooser()), BorderLayout.NORTH);
		add(getSpecificPartPanel());
	}

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> entries = new ArrayList<>();

		OffsetAngleEffectEditor offsetAngleEffectEditor = new OffsetAngleEffectEditor();
		offsetAngleEffectEditor.setItems(FileService.getInstance().getCurrentContentPack().getEffects());

		bind(offsetAngleEffectEditor, FollowingEffect::getOffsetAngleEffect, FollowingEffect::setOffsetAngleEffect, FollowingEffect.class);
		entries.add(new ClassEntry(FollowingEffect.class, offsetAngleEffectEditor));

		return entries;
	}

	@Override
	protected void initialize(FollowingElement oldInstance, FollowingElement newInstance) {
		newInstance.setDelay(oldInstance.getDelay());
	}

}
