package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.GameConstants;
import com.pixurvival.gdxcore.textures.ChunkTexture;
import com.pixurvival.gdxcore.textures.ChunkTextureManager;
import com.pixurvival.gdxcore.util.DrawUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TilesActor extends Actor {

    private float animationCounter;
    private long animationNumber;

    int count;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        animationCounter += Gdx.graphics.getRawDeltaTime();
        float tileAnimationSpeed = PixurvivalGame.getClient().getWorld().getContentPack().getConstants().getTileAnimationSpeed() / 1000f;
        if (animationCounter >= tileAnimationSpeed) {
            animationCounter -= tileAnimationSpeed;
            animationNumber++;
        }
        count = 0;
        ChunkTextureManager chunkTextureManager = PixurvivalGame.getChunkTileTexturesManager();
        DrawUtils.foreachChunksInScreen(getStage(), 0, c -> {
            ChunkTexture chunkTexture = chunkTextureManager.getChunkTextures().get(c.getPosition());
            if (chunkTexture != null && chunkTexture.getTexture() != null) {
                batch.draw(chunkTexture.getTexture(), c.getPosition().getX() * GameConstants.CHUNK_SIZE, c.getPosition().getY() * GameConstants.CHUNK_SIZE,
                        GameConstants.CHUNK_SIZE + GameConstants.PIXEL_SIZE, GameConstants.CHUNK_SIZE + GameConstants.PIXEL_SIZE);
            }
            count++;
        });
    }
}
