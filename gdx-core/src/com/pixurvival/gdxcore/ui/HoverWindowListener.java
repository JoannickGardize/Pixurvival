package com.pixurvival.gdxcore.ui;

public interface HoverWindowListener {

    void enter(UIWindow window, float x, float y);

    void exit(UIWindow window, float x, float y);

    void moved(UIWindow window, float x, float y);
}
