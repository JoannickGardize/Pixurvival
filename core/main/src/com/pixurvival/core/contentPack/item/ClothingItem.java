package com.pixurvival.core.contentPack.item;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirement;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirementSet;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.RequiredEquipmentOffset;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClothingItem extends EquipableItem {

    private static final long serialVersionUID = 1L;

    @ElementReference
    @AnimationTemplateRequirement(AnimationTemplateRequirementSet.CHARACTER)
    @RequiredEquipmentOffset
    private SpriteSheet spriteSheet;

}
