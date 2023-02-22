package com.pixurvival.core.contentPack.sprite;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpriteSheet extends NamedIdentifiedElement {

    private static final long serialVersionUID = 1L;

    @SpriteWidth
    private int width;

    @SpriteHeight
    private int height;

    @ResourceReference
    private String image;

    @AnimationTemplateFrames
    @ElementReference
    private AnimationTemplate animationTemplate;

    @EquipmentOffsetFrames
    @Nullable
    @ElementReference
    private EquipmentOffset equipmentOffset;

    private float heightOffset;

    private boolean shadow = true;

    private int shadowResizing;

    public int getShadowWidth() {
        return Math.max(0, width + shadowResizing);
    }

}
