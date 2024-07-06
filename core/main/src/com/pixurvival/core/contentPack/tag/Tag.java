package com.pixurvival.core.contentPack.tag;

import com.pixurvival.core.contentPack.ImageReferenceHolder;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.TriggerHolder;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.trigger.Trigger;
import com.pixurvival.core.contentPack.validation.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Tag extends NamedIdentifiedElement implements TriggerHolder, ImageReferenceHolder {

    private String displayName = "";
    private int color = Color.WHITE.getRGB();

    @Nullable
    @UnitSpriteFrame
    @Valid
    private Frame displayIconFrame;

    @Nullable
    @UnitSpriteSheet
    @ResourceReference
    private String displayIconImage;

    private float defaultValue = 0;

    private TagStackPolicy valueStackPolicy = TagStackPolicy.REPLACE;

    private TagStackPolicy durationStackPolicy = TagStackPolicy.REPLACE;

    @Valid
    private List<Trigger> triggers = new ArrayList<>();

    private transient Map<Class<? extends Trigger>, List<Trigger>> triggersByType;

    @Override
    public String getImage() {
        return displayIconImage;
    }
}
