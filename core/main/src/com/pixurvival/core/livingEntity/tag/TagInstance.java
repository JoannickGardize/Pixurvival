package com.pixurvival.core.livingEntity.tag;

import com.pixurvival.core.contentPack.tag.Tag;
import com.pixurvival.core.contentPack.tag.TagValue;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TagInstance {

    private final Tag tag;

    @Setter
    private float value;

    @Setter
    private int modCount = 0;

    @Setter
    private long expirationTime = 0;

    public TagInstance(TagValue tagValue) {
        this.tag = tagValue.getTag();
        this.value = tagValue.getValue();
    }

    public TagInstance(TagValue tagValue, long expirationTime) {
        this(tagValue);
        this.expirationTime = expirationTime;
    }
}
