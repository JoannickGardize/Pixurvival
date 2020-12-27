package com.pixurvival.core.contentPack.validation.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

/**
 * Annotation for {@link SpriteSheet} fields, to indicates the required actions
 * in its {@link AnimationTemplate}.
 * 
 * @author SharkHendrix
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface AnimationTemplateRequirement {
	AnimationTemplateRequirementSet value();
}
