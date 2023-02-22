package com.pixurvival.gdxcore.menu;

import com.pixurvival.client.ClientGameAdapter;
import com.pixurvival.client.ClientGameListener;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.gdxcore.PixurvivalGame;

public class ConnectionMessageWindow extends MessageWindow {

    private ClientGameListener clientListener;

    public ConnectionMessageWindow() {
        super("menu.multiplayer.connectWindow.title");
    }

    public void showWaitingMessage() {
        setVisible(true);
        getOkButton().setVisible(false);
        getContentLabel().setText(PixurvivalGame.getString("menu.multiplayer.connectWindow.connecting"));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible == isVisible()) {
            return;
        }
        PixurvivalGame.getClient().removeListener(clientListener);
        if (visible) {
            PixurvivalGame.getClient().addListener(clientListener = new ClientGameAdapter() {
                @Override
                public void loginResponse(LoginResponse response) {
                    getContentLabel().setText(response.getMessage());
                    getOkButton().setVisible(true);
                }
            });
        }
        super.setVisible(visible);
    }
}
