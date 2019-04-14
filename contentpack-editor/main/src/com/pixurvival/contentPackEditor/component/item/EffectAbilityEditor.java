package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.livingEntity.ability.EffectAbility;

public class EffectAbilityEditor extends ElementEditor<EffectAbility> {

	private static final long serialVersionUID = 1L;

	private ListEditor<Effect> effectsEditor = new VerticalListEditor<>(EffectEntryWrapper::new, () -> null);

	public EffectAbilityEditor() {
		EventManager.getInstance().register(this);

		// Construction

		DoubleInput cooldownInput = new DoubleInput(Bounds.positive());

		// Binding

		bind(cooldownInput, EffectAbility::getCooldown, EffectAbility::setCooldown);
		bind(effectsEditor, EffectAbility::getEffects, EffectAbility::setEffects);

		// Layouting

		effectsEditor.setBorder(LayoutUtils.createGroupBorder("effectAbilityEditor.effects"));
		LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 1, LayoutUtils.labelled("effectAbilityEditor.cooldown", cooldownInput), effectsEditor);

	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		((EffectEntryWrapper) effectsEditor.getEditorForValidation()).setItems(event.getContentPack().getEffects());
	}
}
