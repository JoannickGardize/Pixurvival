package com.pixurvival.core.contentPack.structure;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirement;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirementSet;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Nullable;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.map.FactoryStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FactoryStructure extends Structure {

	private static final long serialVersionUID = 1L;

	@Nullable
	@ElementReference
	@AnimationTemplateRequirement(AnimationTemplateRequirementSet.DEFAULT)
	private SpriteSheet workingSpriteSheet;

	@Bounds(min = 1)
	private int recipeSize = 1;

	@Bounds(min = 1)
	private int fuelSize = 1;

	@Bounds(min = 1)
	private int resultSize = 1;

	@Valid
	private List<FactoryFuel> fuels = new ArrayList<>();

	@Valid
	private List<FactoryCraft> crafts = new ArrayList<>();

	private transient Set<Item> possibleRecipes;

	private transient Set<Item> possibleFuels;

	private transient Map<Item, Float> fuelAmounts;

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
	}
}
