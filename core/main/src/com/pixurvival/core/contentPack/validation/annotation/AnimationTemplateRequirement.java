package com.pixurvival.core.contentPack.validation.annotation;

import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for {@link SpriteSheet} fields, to indicates the required actions
 * in its {@link AnimationTemplate}.
 *
 * @author SharkHendrix
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface AnimationTemplateRequirement {
    AnimationTemplateRequirementSet value();
}
