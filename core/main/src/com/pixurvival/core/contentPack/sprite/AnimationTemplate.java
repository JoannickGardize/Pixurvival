package com.pixurvival.core.contentPack.sprite;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
public class AnimationTemplate extends NamedIdentifiedElement {

    private static final long serialVersionUID = 1L;

    @Valid
    @Length(min = 1)
    private Map<ActionAnimation, Animation> animations = new EnumMap<>(ActionAnimation.class);

}
