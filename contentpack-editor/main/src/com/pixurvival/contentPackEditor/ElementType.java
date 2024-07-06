package com.pixurvival.contentPackEditor;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.TaggableElement;
import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.map.MapProvider;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.tag.Tag;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.util.CaseUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.IdentityHashMap;
import java.util.Map;

@RequiredArgsConstructor
public enum ElementType {
    SPRITE_SHEET(SpriteSheet.class),
    ANIMATION_TEMPLATE(AnimationTemplate.class),
    EQUIPMENT_OFFSET(EquipmentOffset.class),
    ITEM(Item.class),
    ITEM_CRAFT(ItemCraft.class),
    ITEM_REWARD(ItemReward.class),
    EFFECT(Effect.class),
    ABILITY_SET(AbilitySet.class),
    BEHAVIOR_SET(BehaviorSet.class),
    CREATURE(Creature.class),
    TILE(Tile.class),
    STRUCTURE(Structure.class),
    MAP_PROVIDER(MapProvider.class),
    ECOSYSTEM(Ecosystem.class),
    GAME_MODE(GameMode.class),
    TAG(Tag.class);

    private static Map<Class<? extends NamedIdentifiedElement>, ElementType> classToType = new IdentityHashMap<>(15);

    static {
        for (ElementType type : ElementType.values()) {

            classToType.put(type.getElementClass(), type);
        }
    }

    private @NonNull
    @Getter Class<? extends NamedIdentifiedElement> elementClass;

    @Override
    public String toString() {
        return TranslationService.getInstance().getString("elementType." + CaseUtils.upperToCamelCase(name()));
    }

    public static ElementType of(NamedIdentifiedElement element) {
        return of(element.getClass());
    }

    @SuppressWarnings("unchecked")
    public static ElementType of(Class<? extends NamedIdentifiedElement> type) {
        // Find the class under "IdentifiedElement", because of hierarchy under
        // item and structures
        while (type.getSuperclass() != NamedIdentifiedElement.class && type.getSuperclass() != TaggableElement.class) {
            type = (Class<? extends NamedIdentifiedElement>) type.getSuperclass();
        }
        return classToType.get(type);
    }
}
