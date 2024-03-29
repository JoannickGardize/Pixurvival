package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Texture;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class TextureAnimationSet {

    private Map<ActionAnimation, TextureAnimation> map = new EnumMap<>(ActionAnimation.class);
    private @Getter float yOffset;
    private @Getter float width;
    private @Getter float height;
    private @Getter float shadowWidth;
    private @Getter
    @Setter(AccessLevel.PACKAGE) Texture shadow;

    public TextureAnimationSet(ContentPack pack, SpriteSheet spriteSheet, int pixelWidth, TextureSheet textureSheet) throws ContentPackException {
        float truePixelWidth = 1f / (pixelWidth * GameConstants.PIXEL_PER_UNIT);
        shadowWidth = (float) spriteSheet.getShadowWidth() / GameConstants.PIXEL_PER_UNIT;
        width = (float) spriteSheet.getWidth() / GameConstants.PIXEL_PER_UNIT + truePixelWidth * 2;
        height = (float) spriteSheet.getHeight() / GameConstants.PIXEL_PER_UNIT + truePixelWidth * 2;
        yOffset = (-truePixelWidth) - spriteSheet.getHeightOffset() / 8;

        AnimationTemplate template = spriteSheet.getAnimationTemplate();
        for (Entry<ActionAnimation, Animation> entries : template.getAnimations().entrySet()) {
            Animation animation = entries.getValue();
            TextureAnimation textureAnimation = new TextureAnimation(textureSheet, animation, spriteSheet.getEquipmentOffset());
            map.put(animation.getAction(), textureAnimation);
        }
    }

    public void put(ActionAnimation action, TextureAnimation animation) {
        map.put(action, animation);
    }

    public TextureAnimation get(ActionAnimation action) {
        return map.get(action);
    }

    public void foreachAnimations(Consumer<TextureAnimation> action) {
        map.values().forEach(action);
    }

    public void dispose() {
        map.values().forEach(TextureAnimation::dispose);
    }
}