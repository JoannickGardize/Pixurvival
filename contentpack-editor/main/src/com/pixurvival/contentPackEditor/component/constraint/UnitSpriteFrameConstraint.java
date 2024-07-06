package com.pixurvival.contentPackEditor.component.constraint;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.core.contentPack.ImageReferenceHolder;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.validation.handler.UnitSpriteFrameHandler;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.util.function.Predicate;

public class UnitSpriteFrameConstraint implements Predicate<Frame> {

    private @Setter ImageReferenceHolder element;

    @Override
    public boolean test(Frame t) {

        ResourceEntry entry = ResourcesService.getInstance().getResource(element.getImage());
        if (entry == null || !(entry.getPreview() instanceof BufferedImage)) {
            return true;
        }
        return UnitSpriteFrameHandler.test(t, (BufferedImage) entry.getPreview());
    }

}
