package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.core.system.mapLimits.MapLimitsSystem;
import com.pixurvival.core.time.EternalDayCycleRun;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.drawer.LightDrawer;
import com.pixurvival.gdxcore.input.CameraControlProcessor;
import com.pixurvival.gdxcore.input.InputManager;
import com.pixurvival.gdxcore.input.WorldKeyboardProcessor;
import com.pixurvival.gdxcore.input.WorldMouseProcessor;
import com.pixurvival.gdxcore.notificationpush.Notification;
import com.pixurvival.gdxcore.notificationpush.NotificationPushManager;
import com.pixurvival.gdxcore.notificationpush.Party;
import com.pixurvival.gdxcore.textures.ChunkTileTexturesManager;
import com.pixurvival.gdxcore.textures.ContentPackAssets;
import lombok.Getter;

import java.time.Instant;

// TODO separated class for world stage and hud stage ?
public class WorldScreen implements Screen {

    public enum ScreenConfiguration {
        SQUARE,
        LARGE
    }


    public static final float CAMERA_BOUNDS = GameConstants.PLAYER_VIEW_DISTANCE - 5;
    public static final float VIEWPORT_WORLD_WIDTH = (CAMERA_BOUNDS * 2) * 0.75f;

    private @Getter World world;
    private static @Getter Stage worldStage;
    private @Getter ContentPackAssets contentPackTextures = new ContentPackAssets();
    private @Getter HudStage hudStage;
    private WorldKeyboardProcessor keyboardInputProcessor = new WorldKeyboardProcessor();
    private CameraControlProcessor cameraControlProcessor;
    private EntitiesActor entitiesActor;
    private DefaultSoundsPlayer defaultSoundsPlayer;
    private LightDrawer lightDrawer = new LightDrawer();
    private @Getter ChunkTileTexturesManager chunkTileTexturesManager;
    private ScreenConfiguration screenConfiguration = ScreenConfiguration.LARGE;

    public void initialize(World world) {
        if (this.world != null) {
            throw new IllegalStateException("Cannot change world of the world screen");
        }
        contentPackTextures = new ContentPackAssets();
        try {
            // int screenWidth = Math.min(Gdx.graphics.getWidth(),
            // Gdx.graphics.getHeight());
            // int pixelWidth = Math.round(screenWidth /
            // (WorldScreen.VIEWPORT_WORLD_WIDTH *
            // GameConstants.PIXEL_PER_UNIT));
            // Seems better :
            int pixelWidth = 3;
            Log.info("Loading texture with pixel width : " + pixelWidth);
            contentPackTextures.load(world.getContentPack(), pixelWidth, PixurvivalGame.getInstance().getSoundPresets());
        } catch (ContentPackException e) {
            Log.error("Error when loading contentPack.", e);
        }
        chunkTileTexturesManager = new ChunkTileTexturesManager();
        world.getMap().addListener(chunkTileTexturesManager);
        if (screenConfiguration == ScreenConfiguration.SQUARE) {
            // TODO always extend but change values
            worldStage = new Stage(new FitViewport(VIEWPORT_WORLD_WIDTH, VIEWPORT_WORLD_WIDTH));
        } else {
            worldStage = new Stage(new ExtendViewport(VIEWPORT_WORLD_WIDTH, VIEWPORT_WORLD_WIDTH,
                    CAMERA_BOUNDS * 2, CAMERA_BOUNDS * 2));
        }

        cameraControlProcessor = new CameraControlProcessor(worldStage.getViewport());
        this.world = world;
        worldStage.clear();
        worldStage.addActor(new TilesActor(world.getMap()));
        entitiesActor = new EntitiesActor();
        worldStage.addActor(entitiesActor);
        // worldStage.addActor(new MapAnalyticsDebugActor());
        hudStage = new HudStage(world, worldStage.getViewport());

        defaultSoundsPlayer = new DefaultSoundsPlayer(world);
    }

    public void gameStarted() {
        MapLimitsSystem mapLimitsSystem = world.getSystem(MapLimitsSystem.class);
        if (mapLimitsSystem != null) {
            worldStage.addActor(new MapLimitActor(mapLimitsSystem));
        }
        if (world.getMyPlayer() != null && !world.getMyPlayer().isAlive()) {
            hudStage.onPlayerDield(world.getMyPlayer());
        }
        NotificationPushManager.getInstance().push(Notification.builder().status("In game").party(new Party(world.getPlayerEntities().size(), world.getPlayerEntities().size()))
                .startTime(Instant.now().getEpochSecond()).build());
    }

    public void switchShowCollisionBoxes() {
        entitiesActor.setDebug(!entitiesActor.getDebug());
    }

    @Override
    public void show() {
        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(hudStage);
        im.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Forces to remove focus of ChatUI TextField, or any other
                // focus-grabber
                // widgets
                hudStage.unfocusAll();
                return false;
            }
        });
        im.addProcessor(keyboardInputProcessor);
        im.addProcessor(cameraControlProcessor);
        im.addProcessor(new WorldMouseProcessor());
        Gdx.input.setInputProcessor(im);
    }

    @Override
    public void render(float delta) {
        PlayerMovementRequest request = InputManager.getInstance().updatePlayerMovement();
        if (request != null) {
            PixurvivalGame.getClient().sendAction(request);
        }
        PlayerEntity myPlayer = world.getMyPlayer();
        worldStage.getViewport().apply();
        updateMouseTarget();
        worldStage.act();
        DrawData data = (DrawData) myPlayer.getCustomData();
        cameraControlProcessor.updateCameraPosition(data == null ? myPlayer.getPosition() : data.getDrawPosition());
        worldStage.draw();
        if (!(world.getTime().getDayCycle() instanceof EternalDayCycleRun)) {
            lightDrawer.draw(worldStage);
        }

        hudStage.getViewport().apply();
        Vector2 position = getWorldCursorPosition();
        StructureEntity structure = myPlayer.getWorld().getMap().findClosestStructure(new com.pixurvival.core.util.Vector2(position.x, position.y),
                GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE);
        hudStage.setMouseInteractionIconVisible(ActionPreconditions.canInteract(myPlayer, structure));
        hudStage.act();
        hudStage.draw();
        defaultSoundsPlayer.playSounds();
    }

    @Override
    public void resize(int width, int height) {
        worldStage.getViewport().update(width, height);
        hudStage.resize(width, height, worldStage.getViewport());
        lightDrawer.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        worldStage.dispose();
        hudStage.dispose();
        lightDrawer.dispose();
        contentPackTextures.dispose();
        PixurvivalGame.getClient().getWorld().unload();
        chunkTileTexturesManager.setRunning(false);
        chunkTileTexturesManager.dispose();
        world.unload();
    }

    private void updateMouseTarget() {
        PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
        Vector2 worldPoint = worldStage.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        myPlayer.getTargetPosition().set(worldPoint.x, worldPoint.y);
    }

    public static Vector2 getWorldCursorPosition() {
        return WorldScreen.getWorldStage().getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    }
}
