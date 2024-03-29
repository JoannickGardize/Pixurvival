package com.pixurvival.core.contentPack.validation;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.validation.annotation.*;
import com.pixurvival.core.contentPack.validation.handler.StaticMapResourceMissing;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class ErrorToString {

    private static final Map<Class<?>, Function<Object, String>> TO_STRING_FUNCTIONS = new HashMap<>();

    static {
        addToStringFunction(Bounds.class, b -> {
            StringBuilder sb = new StringBuilder("the value must be ");
            if (b.min() != Float.NEGATIVE_INFINITY) {
                sb.append(b.minInclusive() ? ">= " : "> ").append(b.min());
                if (b.max() != Float.POSITIVE_INFINITY) {
                    sb.append(" and ");
                }
            }
            if (b.max() != Float.POSITIVE_INFINITY) {
                sb.append(b.maxInclusive() ? "<= " : "< ").append(b.max());
            }
            return sb.toString();
        });
        addToStringFunction(ElementList.class, e -> "the element list is wrongly indexed, or is missing");
        addToStringFunction(ElementReference.class, e -> "the referenced element does not exists");
        addToStringFunction(ElementReferenceOrValid.class, e -> "the referenced element does not exists");
        addToStringFunction(Length.class, l -> {
            StringBuilder sb = new StringBuilder("the element must have a length ");
            if (l.min() > 0) {
                sb.append(">= ").append(l.min());
                if (l.max() < Integer.MAX_VALUE) {
                    sb.append(" and ");
                }
            }
            if (l.max() < Integer.MAX_VALUE) {
                sb.append("< ").append(l.max());
            }
            return sb.toString();
        });
        addToStringFunction(NullErrorCause.class, n -> "the element is missing");
        addToStringFunction(Pattern.class, p -> "the character sequence must match the pattern " + p.value());
        addToStringFunction(Positive.class, p -> "the value cannot be negative");
        addToStringFunction(ResourceReference.class, p -> "the resource does not exists or is not " + (p.type() == ResourceType.IMAGE ? "an image" : "a sound (.wav, .mp3)"));
        addToStringFunction(SpriteWidth.class, p -> "the sprite width does not divide correctly the image width");
        addToStringFunction(SpriteHeight.class, p -> "the sprite height is not a multiple of the image height");
        addToStringFunction(SpriteHeight.class, p -> "the sprite height is not a multiple of the image height");
        addToStringFunction(AnimationTemplateRequirement.class, a -> "the Animation Template of the Sprite Sheet must contains at least the following actions: " + a.value());
        addToStringFunction(RequiredEquipmentOffset.class, p -> "the Sprite Sheet must have an Equipment Offset");
        addToStringFunction(AnimationTemplateFrames.class, p -> "the Animation Template has a frame that does not exists for this Sprite Sheet's image (FYI top-left is 0; 0)");
        addToStringFunction(EquipmentOffsetFrames.class, p -> "the Equipment Offset is not compatible with this Sprite Sheet's image");
        addToStringFunction(AbilityIndexes.class, p -> "the Behavior Set has unknown ability indexes for the creature's Ability Set");
        addToStringFunction(UnitSpriteSheet.class, p -> "the image must have a width and height divisible by " + GameConstants.PIXEL_PER_UNIT);
        addToStringFunction(UnitSpriteFrame.class,
                p -> "the frame does not correspond to a " + GameConstants.PIXEL_PER_UNIT + "x" + GameConstants.PIXEL_PER_UNIT + " sprite of the image (FYI top-left is 0; 0)");
        addToStringFunction(Ascending.class,
                p -> "the values of the list must be in ascending order" + (p.lastValue() > Float.NEGATIVE_INFINITY ? " and the last value must be " + p.lastValue() : ""));
        addToStringFunction(StaticMapResourceMissing.class, r -> "the image resource " + r.getResourceName() + " is missing");
        addToStringFunction(Unique.class, u -> "the elements must be unique");
    }

    public static String toString(Object cause) {
        return TO_STRING_FUNCTIONS.get(cause instanceof Annotation ? ((Annotation) cause).annotationType() : cause.getClass()).apply(cause);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> void addToStringFunction(Class<T> type, Function<T, String> function) {
        TO_STRING_FUNCTIONS.put(type, (Function) function);
    }
}
