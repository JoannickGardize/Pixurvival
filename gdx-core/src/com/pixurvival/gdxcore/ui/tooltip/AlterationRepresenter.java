package com.pixurvival.gdxcore.ui.tooltip;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.contentPack.TranslationKey;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.alteration.AddItemAlteration;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.InstantDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantEatAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantHealAlteration;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AlterationRepresenter {

	private static StringBuilder sb = new StringBuilder();
	private static Map<Class<? extends Alteration>, Function<Alteration, Actor>> builders = new HashMap<>();

	static {
		builders.put(InstantEatAlteration.class, AlterationRepresenter::instantEat);
		builders.put(InstantDamageAlteration.class, AlterationRepresenter::instantDamage);
		builders.put(InstantHealAlteration.class, AlterationRepresenter::instantHeal);
		builders.put(AddItemAlteration.class, AlterationRepresenter::addItem);
	}

	public static Actor represents(Alteration alteration) {
		Function<Alteration, Actor> builder = builders.get(alteration.getClass());
		if (builder == null) {
			return null;
		} else {
			return builder.apply(alteration);
		}
	}

	private static Actor instantEat(Alteration alteration) {
		InstantEatAlteration instantEatAlteration = (InstantEatAlteration) alteration;
		return RepresenterUtils.labelledStatAmount("hud.alteration.food", instantEatAlteration.getAmount());
	}

	private static Actor instantDamage(Alteration alteration) {
		InstantDamageAlteration instantDamageAlteration = (InstantDamageAlteration) alteration;
		return RepresenterUtils.labelledStatAmount("hud.alteration.instantDamage", instantDamageAlteration.getAmount());
	}

	private static Actor instantHeal(Alteration alteration) {
		InstantHealAlteration instantHealAlteration = (InstantHealAlteration) alteration;
		return RepresenterUtils.labelledStatAmount("hud.alteration.instantHeal", instantHealAlteration.getAmount());
	}

	private static Actor addItem(Alteration alteration) {
		AddItemAlteration addItemAlteration = (AddItemAlteration) alteration;
		ItemStack itemStack = addItemAlteration.getItemStack();
		Locale locale = PixurvivalGame.getClient().getCurrentLocale();
		sb.setLength(0);
		sb.append(itemStack.getQuantity());
		sb.append("x ");
		sb.append(PixurvivalGame.getWorld().getContentPack().getTranslation(locale, itemStack.getItem(), TranslationKey.NAME));
		return RepresenterUtils.labelledValue("hud.alteration.addItem", sb.toString());
	}

}
