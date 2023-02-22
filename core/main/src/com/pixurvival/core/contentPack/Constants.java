package com.pixurvival.core.contentPack;

import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Constants implements Serializable {

    private static final long serialVersionUID = 1L;

    @ElementReference
    @AnimationTemplateRequirement(AnimationTemplateRequirementSet.CHARACTER)
    @RequiredEquipmentOffset
    private SpriteSheet defaultCharacter;

    @ElementReference
    private Tile outsideTile;

    @Bounds(min = 0)
    private long tileAnimationSpeed = 300;
}
