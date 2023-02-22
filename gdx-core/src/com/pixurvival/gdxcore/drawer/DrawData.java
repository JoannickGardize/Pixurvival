package com.pixurvival.gdxcore.drawer;

import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import lombok.Data;

@Data
public class DrawData {
    private float timer = 0;
    private Vector2 drawPosition = new Vector2();
    private float overlayOffsetY;
    private boolean firstLoop = true;
    private float angle;
    private TextureAnimation previousAnimation;
    private boolean flip;

    public float getAngleOrReset(TextureAnimation animation) {
        if (animation == previousAnimation) {
            return angle;
        } else {
            previousAnimation = animation;
            angle = 0;
            return 0;
        }
    }
}
