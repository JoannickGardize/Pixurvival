package com.pixurvival.gdxcore;

import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.map.StructureEntity;
import lombok.Getter;
import lombok.Setter;

public class GhostStructure extends StructureEntity {

    @Getter
    @Setter
    private boolean valid;

    protected GhostStructure(Structure definition, int x, int y, boolean valid) {
        super(null, definition, x, y);
        this.valid = valid;
    }
}
