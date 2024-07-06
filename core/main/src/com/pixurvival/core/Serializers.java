package com.pixurvival.core;

import com.pixurvival.core.tag.TagInstance;
import com.pixurvival.core.util.Serializer;
import lombok.Getter;

@Getter
public class Serializers {

    private final Serializer<TagInstance> tagInstanceSerializer;

    public Serializers(World world) {
        tagInstanceSerializer = new TagInstance.Serializer(world);
    }
}
