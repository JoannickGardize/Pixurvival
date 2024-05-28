package com.pixurvival.gdxcore;

import com.pixurvival.gdxcore.ui.*;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface UIContainer {

    ChatUI getChatUI();

    CraftUI getCraftUI();

    MiniMapUI getMiniMapUI();

    TimeUI getTimeUI();

    EquipmentAndInventoryUI getEquipmentAndInventoryUI();

    default Stream<UIWindow> uiWindowStream() {
        return Stream.of(getChatUI(), getCraftUI(), getMiniMapUI(), getTimeUI(), getEquipmentAndInventoryUI());
    }

    default void forEachUIWindow(Consumer<UIWindow> action) {
        uiWindowStream().forEach(action);
    }
}
