package com.pixurvival.core.contentPack.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.Dimensions;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirement;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirementSet;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Nullable;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Structure extends NamedIdentifiedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean solid;

	@Nullable
	@ElementReference
	@AnimationTemplateRequirement(AnimationTemplateRequirementSet.DEFAULT)
	private SpriteSheet spriteSheet;

	private boolean randomHorizontalFlip = false;

	private boolean avoidStuck = false;

	/**
	 * When true, the structure won't block new structure placement and will be
	 * destroyed in this case.
	 */
	private boolean autoDestroy = false;

	@Valid
	private Dimensions dimensions = new Dimensions(1, 1);

	@Positive
	private long duration = 0;

	@ElementReference
	private List<Tile> bannedTiles = new ArrayList<>();

	@Positive
	private float lightEmissionRadius = 0;

	@Positive
	private long deconstructionDuration = 0;

	@Positive
	private float maxHealth = 0;

	private transient Item deconstructionItem;

	/**
	 * Called by the game engine to create a new {@link StructureEntity} of this
	 * Structure definition. Structures with special behaviors must override to
	 * instantiate an appropriate specialization of {@link StructureEntity}.
	 *
	 * @param chunk
	 * @param x     the bottom-left tile X position in world coordinate.
	 * @param y     the bottom-left tile Y position in world coordinate.
	 * @return a StructureEntity instance for this structure definition
	 */
	public StructureEntity newStructureEntity(Chunk chunk, int x, int y) {
		return new StructureEntity(chunk, this, x, y);
	}
}
