package com.pixurvival.gdxcore.ui.tooltip;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.TranslationKey;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.livingEntity.ability.AlterationAbility;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.stats.StatModifier;
import com.pixurvival.core.livingEntity.stats.StatModifier.OperationType;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.Separator;
import com.pixurvival.gdxcore.util.Cache;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemCharacteristicsRepresenter {

	private static StringBuilder sb = new StringBuilder();
	private static Map<Class<? extends Item>, Function<Item, Actor>> builders = new HashMap<>();
	private static Cache<String, Description> abilityDescriptionCache = new Cache<>(key -> {
		Locale locale = PixurvivalGame.getClient().getCurrentLocale();
		ContentPack contentPack = PixurvivalGame.getWorld().getContentPack();
		return DescriptionParser.parse(contentPack.getTranslation(locale, key));
	});

	static {
		builders.put(EdibleItem.class, ItemCharacteristicsRepresenter::edible);
		builders.put(ClothingItem.class, ItemCharacteristicsRepresenter::clothing);
		builders.put(AccessoryItem.class, ItemCharacteristicsRepresenter::accessory);
		builders.put(WeaponItem.class, ItemCharacteristicsRepresenter::weapon);
	}

	public static Actor represents(Item item) {
		Function<Item, Actor> builder = builders.get(item.getClass());
		if (builder == null) {
			return null;
		} else {
			return builder.apply(item);
		}
	}

	private static Actor edible(Item item) {
		EdibleItem edibleItem = (EdibleItem) item;
		Table table = new Table();
		sb.setLength(0);
		sb.append(PixurvivalGame.getString("hud.edibleItem.duration")).append(" ");
		table.defaults().pad(2);
		table.add(new Label(sb.toString(), PixurvivalGame.getSkin(), "white")).expand().fill();
		sb.setLength(0);
		RepresenterUtils.appendTime(sb, edibleItem.getDuration());
		table.add(new Label(sb.toString(), PixurvivalGame.getSkin(), "white")).fill();
		for (Alteration alteration : edibleItem.getAlterations()) {
			Actor actor = AlterationRepresenter.represents(alteration);
			if (actor != null) {
				table.row();
				table.add(actor).colspan(2).expand().fill();
			}
		}
		return table;
	}

	private static Actor clothing(Item item) {
		return equipable((EquipableItem) item);
	}

	private static Actor accessory(Item item) {
		AccessoryItem accessoryItem = (AccessoryItem) item;
		Locale locale = PixurvivalGame.getClient().getCurrentLocale();
		ContentPack contentPack = PixurvivalGame.getWorld().getContentPack();

		Table table = equipable(accessoryItem);

		appendAbility(locale, contentPack, false, item, accessoryItem.getAbility(), table);

		return table;
	}

	private static Actor weapon(Item item) {
		WeaponItem weaponItem = (WeaponItem) item;
		Locale locale = PixurvivalGame.getClient().getCurrentLocale();
		ContentPack contentPack = PixurvivalGame.getWorld().getContentPack();

		Table table = equipable(weaponItem);

		appendAbility(locale, contentPack, true, item, weaponItem.getBaseAbility(), table);
		if (!weaponItem.getSpecialAbility().isEmpty()) {
			appendAbility(locale, contentPack, false, item, weaponItem.getSpecialAbility(), table);
		}

		return table;
	}

	private static void appendAbility(Locale locale, ContentPack contentPack, boolean isBase, Item item, AlterationAbility ability, Table table) {
		String abilityName = "[ORANGE] " + PixurvivalGame.getString(isBase ? "hud.item.baseAbility" : "hud.item.specialAbility") + " "
				+ contentPack.getTranslation(locale, item, isBase ? TranslationKey.ITEM_BASE_ABILITY_NAME : TranslationKey.ITEM_SPECIAL_ABILITY_NAME);
		table.add(new Separator()).colspan(2).pad(0);
		table.row();
		table.add(new Label(abilityName, PixurvivalGame.getSkin(), "white")).colspan(2);
		table.row();
		RepresenterUtils.appendLabelledRow(table, "hud.item.cooldown", RepresenterUtils.time(ability.getCooldown()));
		table.row();
		table.add(new TooltipText(abilityDescriptionCache.get(isBase ? TranslationKey.ITEM_BASE_ABILITY_DESCRIPTION.getKey(item) : TranslationKey.ITEM_SPECIAL_ABILITY_DESCRIPTION.getKey(item)).get()))
				.colspan(2);
		table.row();
	}

	private static Table equipable(EquipableItem item) {
		Table table = new Table();
		table.defaults().fill().pad(2);
		for (StatModifier statModifier : item.getStatModifiers()) {
			String colorTag = RepresenterUtils.getColorTag(statModifier.getStatType());
			String statLabel = RepresenterUtils.getTranslation(statModifier.getStatType());
			Label label = new Label(colorTag + statLabel, PixurvivalGame.getSkin(), "white");
			table.add(label).expand();
			sb.setLength(0);
			sb.append(colorTag);
			if (statModifier.getOperationType() == OperationType.ADDITIVE) {
				float value = statModifier.getValue();
				sb.append(value > 0 ? "+" + value : value);
			} else {
				sb.append(statModifier.getValue() / 100);
				sb.append("%");
			}
			Label valueLabel = new Label(sb.toString(), PixurvivalGame.getSkin(), "white");
			valueLabel.setAlignment(Align.right);
			table.add(valueLabel);
			table.row();
		}
		return table;
	}
}