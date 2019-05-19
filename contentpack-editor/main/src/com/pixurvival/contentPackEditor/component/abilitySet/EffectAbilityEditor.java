package com.pixurvival.contentPackEditor.component.abilitySet;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.livingEntity.ability.EffectAbility;

public class EffectAbilityEditor extends ElementEditor<EffectAbility> {

	private static final long serialVersionUID = 1L;

	private ListEditor<Effect> effectsEditor;
	private ElementChooserButton<Item> ammunitionChooser = new ElementChooserButton<>(IconService.getInstance()::get, false);

	public EffectAbilityEditor(boolean showAmmunitionChooser, boolean useScrollPane) {

		// Construction

		DoubleInput cooldownInput = new DoubleInput(Bounds.positive());
		effectsEditor = new VerticalListEditor<>(EffectEntryWrapper::new, () -> null, VerticalListEditor.HORIZONTAL, useScrollPane);

		// Binding

		bind(cooldownInput, EffectAbility::getCooldown, EffectAbility::setCooldown);
		bind(ammunitionChooser, EffectAbility::getAmmunition, EffectAbility::setAmmunition);
		bind(effectsEditor, EffectAbility::getEffects, EffectAbility::setEffects);

		// Layouting

		effectsEditor.setBorder(LayoutUtils.createGroupBorder("effectAbilityEditor.effects"));
		JPanel headerPanel;
		if (showAmmunitionChooser) {
			headerPanel = LayoutUtils.createHorizontalLabelledBox("effectAbilityEditor.cooldown", cooldownInput, "effectAbilityEditor.ammunition", ammunitionChooser);
		} else {
			headerPanel = LayoutUtils.labelled("effectAbilityEditor.cooldown", cooldownInput);
		}
		LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 1, headerPanel, effectsEditor);

	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		((EffectEntryWrapper) effectsEditor.getEditorForValidation()).setItems(event.getContentPack().getEffects());
		ammunitionChooser.setItems(event.getContentPack().getItems());
	}
}
