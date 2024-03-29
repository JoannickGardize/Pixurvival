package com.pixurvival.core.contentPack.structure;

import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.*;
import com.pixurvival.core.map.FactoryStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class FactoryStructure extends Structure {

    private static final long serialVersionUID = 1L;

    @Nullable
    @ElementReference
    @AnimationTemplateRequirement(AnimationTemplateRequirementSet.DEFAULT)
    private SpriteSheet workingSpriteSheet;

    @Bounds(min = 1, max = 16, maxInclusive = true)
    private int recipeSize = 1;

    @Bounds(min = 1, max = 16, maxInclusive = true)
    private int fuelSize = 1;

    @Bounds(min = 1, max = 16, maxInclusive = true)
    private int resultSize = 1;

    @Valid
    @Unique
    private List<FactoryFuel> fuels = new ArrayList<>();

    @Valid
    private List<FactoryCraft> crafts = new ArrayList<>();

    private StructureDeathItemHandling itemHandlingOnDeath = StructureDeathItemHandling.DROP;

    private transient Set<Item> possibleRecipes;

    private transient Set<Item> possibleFuels;

    private transient Map<Item, Float> fuelAmounts;

    private transient float maxTankFuel;

    @Override
    public StructureEntity newStructureEntity(Chunk chunk, int x, int y) {
        return new FactoryStructureEntity(chunk, this, x, y);
    }

    @Override
    public void initialize() {
        Map<Item, Item> tmpRecipes = new IdentityHashMap<>();
        crafts.forEach(c -> c.getRecipes().forEach(i -> tmpRecipes.put(i.getItem(), i.getItem())));
        possibleRecipes = tmpRecipes.keySet();
        fuelAmounts = new IdentityHashMap<>();
        fuels.forEach(f -> fuelAmounts.put(f.getItem(), f.getAmount()));
        possibleFuels = fuelAmounts.keySet();
        computeMaxTankFuel();
    }

    private void computeMaxTankFuel() {
        float maxItemFuel = fuels.stream().map(FactoryFuel::getAmount).max(Float::compare).orElse(0f);
        float maxCraftFuel = crafts.stream().map(FactoryCraft::getFuelConsumption).max(Float::compare).orElse(0f);
        maxTankFuel = maxCraftFuel + maxItemFuel;
    }
}
