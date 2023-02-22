package com.pixurvival.contentPackEditor.component.spriteSheet;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

@RequiredArgsConstructor
public class SpriteDimensionConstraint implements Predicate<Number> {

    @NonNull
    private ToIntFunction<BufferedImage> dimensionGetter;

    @Setter
    private SpriteSheet spriteSheet;

    @Override
    public boolean test(Number value) {
        if (spriteSheet == null) {
            return true;
        }
        ResourceEntry resourceEntry = ResourcesService.getInstance().getResource(spriteSheet.getImage());
        if (resourceEntry == null) {
            return true;
        }
        if (!(resourceEntry.getPreview() instanceof BufferedImage)) {
            return true;
        }
        int spriteSize = value.intValue();
        int imageSize = dimensionGetter.applyAsInt((BufferedImage) resourceEntry.getPreview());
        return spriteSize > 0 && spriteSize <= imageSize && imageSize % spriteSize == 0;
    }
}
