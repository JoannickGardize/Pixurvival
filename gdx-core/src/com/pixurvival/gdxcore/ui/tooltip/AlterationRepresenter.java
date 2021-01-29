package com.pixurvival.gdxcore.ui.tooltip;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.alteration.AddItemAlteration;
import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.InstantDamageAlteration;
import com.pixurvival.core.alteration.InstantEatAlteration;
import com.pixurvival.core.alteration.InstantHealAlteration;
import com.pixurvival.core.alteration.PermanentStatAlteration;
import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.WeightedValueProducer.Entry;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AlterationRepresenter {

	private static Map<Class<? extends Alteration>, Function<Alteration, Actor>> builders = new HashMap<>();

	static {
		builders.put(InstantEatAlteration.class, AlterationRepresenter::instantEat);
		builders.put(InstantDamageAlteration.class, AlterationRepresenter::instantDamage);
		builders.put(InstantHealAlteration.class, AlterationRepresenter::instantHeal);
		builders.put(AddItemAlteration.class, AlterationRepresenter::addItem);
		builders.put(PermanentStatAlteration.class, AlterationRepresenter::permanentStat);
	}

	/**
	 * @param alteration
	 * @return the actor that represents the given alteration, or null if this
	 *         alteration has no representation to display specifically.
	 */
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

	private static Actor permanentStat(Alteration alteration) {
		PermanentStatAlteration permanentStatAlteration = (PermanentStatAlteration) alteration;
		Table table = RepresenterUtils.createTable();
		RepresenterUtils.appendRawLabelledRow(table,
				RepresenterUtils.getColorTag(permanentStatAlteration.getStatType())
						+ PixurvivalGame.getString("hud.alteration.permanent", RepresenterUtils.getTranslation(permanentStatAlteration.getStatType())),
				RepresenterUtils.statAmount(permanentStatAlteration.getAmount()));
		return table;
	}

	private static Actor addItem(Alteration alteration) {
		AddItemAlteration addItemAlteration = (AddItemAlteration) alteration;
		WeightedValueProducer<ItemStack> itemStacks = addItemAlteration.getItemStacks();
		Table table = new Table();
		table.add(new Label(PixurvivalGame.getString("hud.alteration.addItem"), PixurvivalGame.getSkin(), "white")).expand();
		for (Entry<ItemStack> itemStack : itemStacks.getBackingArray()) {
			RepresenterUtils.appendItemStack(table, itemStack.getElement());
		}
		return table;
	}

}
