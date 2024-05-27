package com.pixurvival.gdxcore;

import com.pixurvival.gdxcore.ui.*;

import java.util.function.Consumer;

public interface UIContainer {

    ChatUI getChatUI();

    CraftUI getCraftUI();

    MiniMapUI getMiniMapUI();

    TimeUI getTimeUI();

    EquipmentAndInventoryUI getEquipmentAndInventoryUI();

    default void forEachUIWindow(Consumer<UIWindow> window) {
        window.accept(getChatUI());
        window.accept(getCraftUI());
        window.accept(getMiniMapUI());
        window.accept(getTimeUI());
        window.accept(getEquipmentAndInventoryUI());
    }
}
