package com.pixurvival.core.contentPack;

import com.pixurvival.core.contentPack.tag.TagValue;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TaggableElement extends NamedIdentifiedElement {

    @Valid
    private List<TagValue> tags = new ArrayList<>();

    public TaggableElement(String name, int index) {
        super(name, index);
    }
}
