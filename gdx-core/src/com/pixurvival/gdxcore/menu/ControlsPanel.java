package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.input.*;
import com.pixurvival.gdxcore.input.InputButton.Type;
import com.pixurvival.gdxcore.ui.ShortcutDrawer;
import com.pixurvival.gdxcore.ui.UILabel;
import com.pixurvival.gdxcore.util.AutoScrollFocusListener;
import com.pixurvival.gdxcore.util.UserDirectory;
import lombok.Getter;

public class ControlsPanel extends Table {

    private TextButton[] actionButtons = new TextButton[InputAction.values().length];
    private InputMapping mapping = new InputMapping();
    private @Getter MessageWindow popupWindow = new MessageWindow("controlPanel.waitingWindow.title");
    private InputProcessor previousInputProcessor;
    private MenuButton saveButton;

    public ControlsPanel(Runnable backAction) {
        Table keysTable = createKeysTable();
        ScrollPane keysScrollPane = new ScrollPane(keysTable, PixurvivalGame.getSkin());
        keysScrollPane.setScrollingDisabled(true, false);
        keysScrollPane.setOverscroll(false, false);
        keysScrollPane.setForceScroll(false, true);
        keysScrollPane.setFadeScrollBars(false);
        keysScrollPane.addListener(new AutoScrollFocusListener());

        Table resetDefaultsTable = new Table();
        resetDefaultsTable.defaults().pad(4).fill();
        SelectBox<InputMapping> defaultsBox = new SelectBox<>(PixurvivalGame.getSkin());
        defaultsBox.setItems(InputMappingDefaults.qwerty()); // Since LWJGL3
        MenuButton applyButton = new MenuButton("generic.apply", () -> setMapping(defaultsBox.getSelected()));
        resetDefaultsTable.add(new UILabel("controlPanel.resetDefaults")).expand();
        resetDefaultsTable.add(defaultsBox);
        resetDefaultsTable.add(applyButton);
        resetDefaultsTable.row();
        saveButton = new MenuButton("generic.save", () -> {
            if (!saveButton.isDisabled()) {
                UserDirectory.saveInputMapping(mapping);
                InputManager.getInstance().getMapping().set(mapping);
                ShortcutDrawer.updateTexts();
                saveButton.setDisabled(true);
            }
        });
        Button backButton = new MenuButton("generic.back", backAction);
        Table buttonsTable = new Table();
        buttonsTable.add(saveButton).expand().fill().pad(2);
        buttonsTable.add(backButton).fill().pad(2);
        defaults().pad(2).fill();
        add(keysScrollPane).expand();
        row();
        Table bottomTable = new Table();
        bottomTable.defaults().pad(2).expand().fill();
        bottomTable.setBackground(PixurvivalGame.getSkin().getDrawable("light-panel"));
        bottomTable.add(resetDefaultsTable);
        bottomTable.row();
        bottomTable.add(buttonsTable);
        add(bottomTable);
    }

    public void showWaitingWindow() {
        popupWindow.getTitleLabel().setText(PixurvivalGame.getString("controlPanel.waitingWindow.title"));
        popupWindow.getContentLabel().setText(PixurvivalGame.getString("controlPanel.waitingWindow.content"));
        popupWindow.getOkButton().setVisible(false);
        popupWindow.setVisible(true);
        popupWindow.toFront();
    }

    public void setMapping(InputMapping mapping) {
        this.mapping.set(mapping);
        for (InputAction action : InputAction.values()) {
            InputButton button = mapping.getButton(action);
            TextButton textButton = actionButtons[action.ordinal()];
            if (button == null) {
                textButton.setText("");
            } else {
                textButton.setText(button.toString());
            }
        }
        saveButton.setDisabled(mapping.equals(InputManager.getInstance().getMapping()));
    }

    @Override
    public void setVisible(boolean visible) {
        if (!isVisible() && visible) {
            setMapping(InputManager.getInstance().getMapping());
        }
        super.setVisible(visible);
    }

    private void bind(InputAction action, InputButton button) {
        popupWindow.setVisible(false);
        Gdx.input.setInputProcessor(previousInputProcessor);
        mapping.bind(action, button);
        actionButtons[action.ordinal()].setText(button.toString());
        saveButton.setDisabled(mapping.equals(InputManager.getInstance().getMapping()));
    }

    private Table createKeysTable() {
        Table keysTable = new Table();
        keysTable.defaults().fill().pad(2);
        for (InputAction action : InputAction.values()) {
            Label label = new Label(getInputActionTranslation(action), PixurvivalGame.getSkin());
            label.setAlignment(Align.right);
            keysTable.add(label);
            TextButton button = new TextButton("", PixurvivalGame.getSkin());
            actionButtons[action.ordinal()] = button;
            keysTable.add(button).minWidth(120);
            keysTable.row();

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showWaitingWindow();
                    previousInputProcessor = Gdx.input.getInputProcessor();

                    Gdx.input.setInputProcessor(new InputAdapter() {

                        @Override
                        public boolean keyDown(int keycode) {
                            bind(action, new InputButton(Type.KEYBOARD, keycode));
                            return true;
                        }

                        @Override
                        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                            bind(action, new InputButton(Type.MOUSE, button));
                            return true;
                        }
                    });
                }
            });
        }
        return keysTable;
    }

    private String getInputActionTranslation(InputAction action) {
        return PixurvivalGame.getString("controlPanel.inputAction." + CaseUtils.upperToCamelCase(action.name()));
    }
}
