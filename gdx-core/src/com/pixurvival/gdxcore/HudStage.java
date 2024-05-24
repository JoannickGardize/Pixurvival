package com.pixurvival.gdxcore;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.debug.DebugInfosActor;
import com.pixurvival.gdxcore.overlay.OverlaysActor;
import com.pixurvival.gdxcore.ui.*;
import com.pixurvival.gdxcore.ui.interactionDialog.InteractionDialogUI;
import com.pixurvival.gdxcore.ui.tooltip.FactoryTooltip;
import com.pixurvival.gdxcore.ui.tooltip.ItemCraftTooltip;
import com.pixurvival.gdxcore.ui.tooltip.ItemTooltip;
import com.pixurvival.gdxcore.ui.tooltip.SubStatsTooltip;
import com.pixurvival.gdxcore.util.FillActor;
import lombok.Getter;

import java.util.Collection;

public class HudStage extends Stage {

    private @Getter ChatUI chatUI = new ChatUI();
    private CraftUI craftUI = new CraftUI();
    private MiniMapUI miniMapUI = new MiniMapUI();
    private StatusBarUI statusBarUI = new StatusBarUI();
    private @Getter EndGameUI endGameUI = new EndGameUI();
    private PauseMenu pauseUI = new PauseMenu();
    private TimeUI timeUI = new TimeUI();
    private @Getter EquipmentAndInventoryUI equipmentAndInventoryUI = new EquipmentAndInventoryUI();

    private RespawnTimerActor respawnTimerActor = new RespawnTimerActor();
    private MouseIconActor mouseIconActor = new MouseIconActor();
    private DebugInfosActor debugInfosActors;
    private FillActor blackPauseBackground = new FillActor(new Color(0, 0, 0, 0.5f));

    public HudStage(World world, Viewport worldViewport) {
        super(new ScreenViewport());
        clear();
        addActor(pauseUI);
        addActor(pauseUI.getControlsPanel().getPopupWindow());
        HeldItemStackActor heldItemStackActor = new HeldItemStackActor();
        OverlaysActor overlayActor = new OverlaysActor(worldViewport);
        addListener(overlayActor);
        addActor(overlayActor);
        world.getEntityPool().addListener(respawnTimerActor);
        addActor(respawnTimerActor);
        addActor(miniMapUI);
        addActor(equipmentAndInventoryUI);
        addActor(craftUI);
        world.getMyPlayer().getInventory().addListener(craftUI);
        world.getChatManager().addListener(chatUI);
        addActor(timeUI);
        addActor(chatUI);
        InteractionDialogUI.getInstance();
        addActor(InteractionDialogUI.getInstance());
        addActor(heldItemStackActor);
        addActor(statusBarUI);
        addActor(mouseIconActor);
        statusBarUI.updatePosition();
        addActor(ItemCraftTooltip.getInstance());
        addActor(FactoryTooltip.getInstance());
        addActor(ItemTooltip.getInstance());
        addActor(SubStatsTooltip.getInstance());
        SubStatsTooltip.getInstance().setVisible(false);
        addActor(endGameUI);
        debugInfosActors = new DebugInfosActor();
        debugInfosActors.setVisible(false);
        addActor(debugInfosActors);
        blackPauseBackground.setVisible(false);
        addActor(blackPauseBackground);

        PixurvivalGame.getClient().getMyInventory().addListener(ItemCraftTooltip.getInstance());
        PixurvivalGame.getClient().getMyInventory().addListener(FactoryTooltip.getInstance());
        world.getMyPlayer().getStats().addListener(ItemTooltip.getInstance());
        world.getMyPlayer().getStats().addListener(ItemCraftTooltip.getInstance());
        initializeCursorManager();
        setDefaultUIPositions();
    }

    @Override
    public void dispose() {
        miniMapUI.dispose();
        super.dispose();
    }

    public void resize(int width, int height, Viewport worldViewport, Viewport hudViewport) {
        getViewport().update(width, height, true);
        statusBarUI.updatePosition();
        pauseUI.update();
        endGameUI.update(hudViewport);
        respawnTimerActor.setPosition(width / 2f, height - height / 3f);
        InteractionDialogUI.getInstance().sizeAndPosition();
        ScreenResizeEvent event = new ScreenResizeEvent(worldViewport);
        getRoot().fire(event);
    }

    public void onPlayerDield(PlayerEntity player) {
        respawnTimerActor.playerDied(player);
    }

    public void switchShowDebugInfos() {
        debugInfosActors.setVisible(!debugInfosActors.isVisible());
    }

    public void showEndGame(EndGameData data) {
        endGameUI.show(data);
    }

    public void switchPauseMenu() {
        pauseUI.toFront();
        boolean pausing = !pauseUI.isVisible();
        pauseUI.setVisible(pausing);
        blackPauseBackground.setVisible(pausing);
        PixurvivalGame.getClient().requestPause(pausing);
    }

    public void addItemCrafts(Collection<ItemCraft> crafts) {
        craftUI.addItemCrafts(crafts);
    }

    public void setMouseInteractionIconVisible(boolean visible) {
        mouseIconActor.setVisible(visible);
    }

    private void initializeCursorManager() {
        UIWindowCursorManager manager = new UIWindowCursorManager();
        chatUI.addHoverWindowListener(manager);
        craftUI.addHoverWindowListener(manager);
        miniMapUI.addHoverWindowListener(manager);
        timeUI.addHoverWindowListener(manager);
        equipmentAndInventoryUI.addHoverWindowListener(manager);
    }

    private void setDefaultUIPositions() {
        chatUI.setPosition(0, 0);
        equipmentAndInventoryUI.setPosition(0, chatUI.getY() + chatUI.getHeight());
        timeUI.setPosition(0, getViewport().getWorldHeight() - timeUI.getHeight());
        miniMapUI.setPosition(getViewport().getWorldWidth() - miniMapUI.getWidth(),
                getViewport().getWorldHeight() - miniMapUI.getHeight());
        craftUI.setHeight(getViewport().getWorldHeight() - miniMapUI.getHeight());
        craftUI.setPosition(getViewport().getWorldWidth() - craftUI.getWidth(), 0);
    }
}
