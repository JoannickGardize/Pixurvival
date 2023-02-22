package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Effect + relative angle data.
 *
 * @author SharkHendrix
 */
@Getter
@Setter
@NoArgsConstructor
public class OffsetAngleEffect implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Angle relative to the source angle (target angle, or angle of the ancestor
     * effect). In radians.
     */
    private float offsetAngle = 0;

    /**
     * Random angle in radians centered to the offset.
     */
    private float randomAngle = 0;

    @ElementReference
    private Effect effect;

    public OffsetAngleEffect(Effect effect) {
        super();
        this.effect = effect;
    }
}
