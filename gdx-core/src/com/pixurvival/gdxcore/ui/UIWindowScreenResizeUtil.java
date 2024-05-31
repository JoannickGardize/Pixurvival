package com.pixurvival.gdxcore.ui;

import com.pixurvival.gdxcore.UIContainer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UIWindowScreenResizeUtil {

    /**
     * Intelligently move UI windows on screen resize in a way that keep their distances to the closest edges.
     *
     * @param container
     * @param previousWidth
     * @param previousHeight
     * @param width
     * @param height
     */
    public static void resize(UIContainer container, int previousWidth, int previousHeight, int width, int height) {
        if (previousWidth == 0 && previousHeight == 0 || width == 0 || height == 0) {
            return;
        }
        container.forEachUIWindow(w -> {
            float leftDistance = w.getX();
            float rightDistance = previousWidth - w.getX() - w.getWidth();
            float bottomDistance = w.getY();
            float topDistance = previousHeight - w.getY() - w.getHeight();
            if (rightDistance < leftDistance) {
                w.setX(w.getX() + width - previousWidth);
            }
            if (topDistance < bottomDistance) {
                w.setY(w.getY() + height - previousHeight);
            }
        });
    }
}
