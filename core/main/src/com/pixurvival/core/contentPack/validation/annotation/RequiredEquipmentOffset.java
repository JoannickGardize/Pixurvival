package com.pixurvival.core.contentPack.validation.annotation;

import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the annotated {@link SpriteSheet} must have an
 * {@link EquipmentOffset}.
 *
 * @author SharkHendrix
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface RequiredEquipmentOffset {

}
