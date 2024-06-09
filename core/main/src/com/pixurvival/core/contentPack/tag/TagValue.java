package com.pixurvival.core.contentPack.tag;

import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagValue {

    @ElementReference
    private Tag tag;

    private float value;
}
