package com.pixurvival.core.contentPack.structure;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirement;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirementSet;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Nullable;
import com.pixurvival.core.map.InventoryStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryStructure extends Structure {

	private static final long serialVersionUID = 1L;

	@Nullable
	@ElementReference
	@AnimationTemplateRequirement(AnimationTemplateRequirementSet.DEFAULT)
	private SpriteSheet openSpriteSheet;

	@Bounds(min = 1, max = 64, maxInclusive = true)
	private int inventorySize = 1;

	private StructureDeathItemHandling itemHandlingOnDeath = StructureDeathItemHandling.DROP;

	@Override
	public StructureEntity newStructureEntity(Chunk chunk, int x, int y) {
		return new InventoryStructureEntity(chunk, this, x, y);
	}
}
