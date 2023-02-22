package com.pixurvival.core.contentPack.item;

import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StructureItem extends Item {

    private static final long serialVersionUID = 1L;

    @ElementReference
    private Structure structure;

}
