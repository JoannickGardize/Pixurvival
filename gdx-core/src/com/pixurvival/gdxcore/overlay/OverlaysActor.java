package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.entity.EntityCollection;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ScreenResizeEvent;
import com.pixurvival.gdxcore.WorldScreen;
import com.pixurvival.gdxcore.util.DrawUtils;

import java.util.EnumMap;
import java.util.Map;

public class OverlaysActor extends Actor implements EventListener {

    @SuppressWarnings("rawtypes")
    private Map<EntityGroup, OverlayDrawer> entityOverlayDrawers = new EnumMap<>(EntityGroup.class);

    /**
     * Area where overlay elements can be drawn (on top of the world)
     */
    private Rectangle scissors = new Rectangle();

    private Viewport worldViewport;

    private DistantAllyMarkerDrawer distantAlliesDrawer = new DistantAllyMarkerDrawer();
    private StructureOverlayDrawer structureOverlayDrawer = new StructureOverlayDrawer();

    public OverlaysActor(Viewport worldViewport) {
        this.worldViewport = worldViewport;
        entityOverlayDrawers.put(EntityGroup.PLAYER, new PlayerEntityOverlayDrawer());
        entityOverlayDrawers.put(EntityGroup.CREATURE, new CreatureEntityOverlayDrawer());
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void draw(Batch batch, float parentAlpha) {
        ScissorStack.pushScissors(scissors);
        batch.setColor(1, 1, 1, 0.75f);
        DrawUtils.foreachChunksInScreen(WorldScreen.getWorldStage(), 3, chunk -> {
            chunk.getStructures().forEach(e -> e.getValue().forEach(ms -> structureOverlayDrawer.draw(batch, worldViewport, ms)));
            EntityCollection entityPool = chunk.getEntities();
            entityOverlayDrawers.forEach((group, drawer) -> entityPool.get(group).forEach(e -> drawer.draw(batch, worldViewport, e)));

        });
        PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
        if (myPlayer != null) {
            for (PlayerEntity ally : myPlayer.getTeam().getAliveMembers()) {
                if (!ally.equals(myPlayer)) {
                    distantAlliesDrawer.draw(batch, worldViewport, ally);
                }
            }
        }
        batch.flush();
        ScissorStack.popScissors();
    }

    @Override
    public boolean handle(Event event) {
        if (event instanceof ScreenResizeEvent) {
            Viewport viewport = ((ScreenResizeEvent) event).getViewport();
            scissors.setX(viewport.getScreenX());
            scissors.setY(viewport.getScreenY());
            scissors.setWidth(viewport.getScreenWidth());
            scissors.setHeight(viewport.getScreenHeight());
        }
        return false;
    }
}
