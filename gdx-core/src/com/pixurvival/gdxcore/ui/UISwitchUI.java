package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.UIContainer;
import com.pixurvival.gdxcore.input.InputAction;

public class UISwitchUI extends Table {

    public UISwitchUI(UIContainer container) {
        defaults().fill().pad(2).size(50, 50);
        setColor(1, 1, 1, 0.7f);
        add(createSwitchButton("inventory", container.getEquipmentAndInventoryUI(), InputAction.SWITCH_EQUIPMENT_AND_INVENTORY_UI));
        add(createSwitchButton("craft", container.getCraftUI(), InputAction.SWITCH_CRAFT_UI));
        add(createSwitchButton("map", container.getMiniMapUI(), InputAction.SWITCH_MINI_MAP_UI));
        add(createSwitchButton("chat", container.getChatUI(), InputAction.SWITCH_CHAT_UI));
        add(createSwitchButton("time", container.getTimeUI(), InputAction.SWITCH_TIME_UI));
        pack();
    }

    public void updatePosition() {
        setPosition(Gdx.graphics.getWidth() / 2f - getWidth() / 2, Gdx.graphics.getHeight() - getHeight() - 30);
    }

    private ImageButton createSwitchButton(String skin, UIWindow window, InputAction inputAction) {
        ImageButton button = new ImageButton(PixurvivalGame.getSkin(), skin) {
            private ShortcutDrawer shortcutDrawer = new ShortcutDrawer(this, inputAction, ShortcutDrawer.BOTTOM);

            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                shortcutDrawer.draw(batch);
            }
        };
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.setVisible(!window.isVisible());
            }

        });
        button.getImageCell().expand().fill();
        return button;
    }
}
