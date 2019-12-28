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
import com.pixurvival.core.contentPack.Dimensions;
import com.pixurvival.core.contentPack.TranslationKey;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.livingEntity.ability.ItemAlterationAbility;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.stats.StatModifier;
import com.pixurvival.core.livingEntity.stats.StatModifier.OperationType;
import com.pixurvival.core.util.Cache;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.Separator;

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
		builders.put(StructureItem.class, ItemCharacteristicsRepresenter::structure);
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
		table.defaults().fill().pad(2);
		table.add(new Label(sb.toString(), PixurvivalGame.getSkin(), "white")).expand();
		sb.setLength(0);
		RepresenterUtils.appendTime(sb, edibleItem.getDuration());
		table.add(new Label(sb.toString(), PixurvivalGame.getSkin(), "white"));
		for (Alteration alteration : edibleItem.getAlterations()) {
			Actor actor = AlterationRepresenter.represents(alteration);
			if (actor != null) {
				table.row();
				table.add(actor).pad(0).colspan(2).expand();
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

		if (!accessoryItem.getAbility().isEmpty()) {
			appendAbility(locale, contentPack, false, item, accessoryItem.getAbility(), table);
		}
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

	private static Actor structure(Item item) {
		Structure structure = ((StructureItem) item).getStructure();

		Table table = new Table();
		table.defaults().fill().pad(2);
		Dimensions dimension = structure.getDimensions();
		RepresenterUtils.appendLabelledRow(table, "hud.item.dimensions", dimension.getWidth() + "x" + dimension.getHeight());
		if (structure.getDuration() > 0) {
			RepresenterUtils.appendLabelledRow(table, "hud.item.duration", RepresenterUtils.formatHoursMinutesSecondes(structure.getDuration(), true));
		}
		if (structure.getLightEmissionRadius() > 0) {
			RepresenterUtils.appendLabelledRow(table, "hud.item.lighEmission", RepresenterUtils.DECIMAL_FORMAT.format(structure.getLightEmissionRadius()));
		}
		return table;
	}

	private static void appendAbility(Locale locale, ContentPack contentPack, boolean isBase, Item item, ItemAlterationAbility ability, Table table) {
		String abilityName = "[ORANGE] " + PixurvivalGame.getString(isBase ? "hud.item.baseAbility" : "hud.item.specialAbility") + " "
				+ contentPack.getTranslation(locale, item, isBase ? TranslationKey.ITEM_BASE_ABILITY_NAME : TranslationKey.ITEM_SPECIAL_ABILITY_NAME);
		table.add(new Separator()).colspan(2).pad(0);
		table.row();
		table.add(new Label(abilityName, PixurvivalGame.getSkin(), "white")).colspan(2);
		table.row();
		RepresenterUtils.appendLabelledRow(table, "hud.item.cooldown", RepresenterUtils.time(ability.getCooldown()));
		if (ability.getAmmunition().getItem() != null) {
			table.add(new Label(PixurvivalGame.getString("hud.item.ammunition"), PixurvivalGame.getSkin(), "white")).expand();
			table.add(RepresenterUtils.itemStack(ability.getAmmunition()));
			table.row();
		}
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
				if (value > 0) {
					sb.append("+");
				}
				sb.append(RepresenterUtils.DECIMAL_FORMAT.format(value));
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
