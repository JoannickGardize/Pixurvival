package com.pixurvival.gdxcore.util;

import com.pixurvival.gdxcore.ui.UIWindow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UIState {

    private float x;
    private float y;
    private float width;
    private float height;
    private boolean visible;

    @SuppressWarnings("unused")
    public static UIState valueOf(String str) {
        String[] values = str.split(",");
        return new UIState(Float.valueOf(values[0]), Float.valueOf(values[1]),
                Float.valueOf(values[2]), Float.valueOf(values[3]), Boolean.valueOf(values[4]));
    }

    @Override
    public String toString() {
        return x + "," + y + "," + width + "," + height + "," + visible;
    }

    public void set(UIWindow window) {
        x = window.getX();
        y = window.getY();
        width = window.getWidth();
        height = window.getHeight();
        visible = window.isVisible();
    }

    public void apply(UIWindow window) {
        window.setX(x);
        window.setY(y);
        window.setWidth(width);
        window.setHeight(height);
        window.setVisible(visible);
    }
}
