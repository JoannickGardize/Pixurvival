package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;

/**
 * Change cursor image according to move/resize actions on {@link UIWindow}s.
 */
public class UIWindowCursorManager implements HoverWindowListener {
    @Override
    public void enter(UIWindow window, float x, float y) {
        computeCursorImage(window, x, y);
    }

    @Override
    public void exit(UIWindow window, float x, float y) {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    @Override
    public void moved(UIWindow window, float x, float y) {
        computeCursorImage(window, x, y);
    }

    private void computeCursorImage(UIWindow window, float x, float y) {
        float resizeBorder = window.getResizeBorder() / 2.0f;
        if (x < window.getPadLeft() - resizeBorder
                || x > window.getWidth() - window.getPadRight() + resizeBorder
                || y < window.getPadBottom() - resizeBorder) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            return;
        }
        if (x < window.getPadLeft() + resizeBorder) {
            if (y < window.getPadBottom() + resizeBorder) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.NESWResize);
            } else {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
            }
        } else if (x > window.getWidth() - window.getPadRight() - resizeBorder) {
            if (y < window.getPadBottom() + resizeBorder) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.NWSEResize);
            } else {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
            }
        } else if (y > window.getHeight() - window.getPadTop()) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.AllResize);
        } else if (y < window.getPadBottom() + resizeBorder) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
        } else {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    }
}
