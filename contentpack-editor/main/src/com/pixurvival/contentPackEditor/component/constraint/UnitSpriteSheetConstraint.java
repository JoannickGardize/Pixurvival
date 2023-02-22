package com.pixurvival.contentPackEditor.component.constraint;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.core.contentPack.validation.handler.UnitSpriteSheetHandler;

import java.awt.image.BufferedImage;
import java.util.function.Predicate;

public class UnitSpriteSheetConstraint implements Predicate<ResourceEntry> {

    @Override
    public boolean test(ResourceEntry entry) {
        if (entry == null || !(entry.getPreview() instanceof BufferedImage)) {
            return true;
        }

        return UnitSpriteSheetHandler.test((BufferedImage) entry.getPreview());
    }

}
