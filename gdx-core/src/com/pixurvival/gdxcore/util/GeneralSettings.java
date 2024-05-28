package com.pixurvival.gdxcore.util;

import com.pixurvival.gdxcore.UIContainer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneralSettings {

    private String playerName;
    private int lastScreenWidth;
    private int lastScreenHeight;
    private UIState inventoryUIState;
    private UIState craftUIState;
    private UIState mapUIState;
    private UIState chatUIState;
    private UIState timeUIState;

    public void setAllUIStates(UIContainer container) {
        inventoryUIState = new UIState();
        craftUIState = new UIState();
        mapUIState = new UIState();
        chatUIState = new UIState();
        timeUIState = new UIState();
        inventoryUIState.set(container.getEquipmentAndInventoryUI());
        craftUIState.set(container.getCraftUI());
        mapUIState.set(container.getMiniMapUI());
        chatUIState.set(container.getChatUI());
        timeUIState.set(container.getTimeUI());

    }
}
