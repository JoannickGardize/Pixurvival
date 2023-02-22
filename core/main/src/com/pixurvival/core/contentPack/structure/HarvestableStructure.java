package com.pixurvival.core.contentPack.structure;

import com.pixurvival.core.contentPack.TimeInterval;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.map.HarvestableStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HarvestableStructure extends Structure {

    private static final long serialVersionUID = 1L;

    @Bounds(min = 0)
    private long harvestingTime;

    @ElementReference
    private ItemReward itemReward;

    @Valid
    private TimeInterval regrowthTime = new TimeInterval();

    @Override
    public StructureEntity newStructureEntity(Chunk chunk, int x, int y) {
        return new HarvestableStructureEntity(chunk, this, x, y);
    }
}
