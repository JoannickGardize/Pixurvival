package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        animationCounter += Gdx.graphics.getDeltaTime();
        float tileAnimationSpeed = PixurvivalGame.getClient().getWorld().getContentPack().getConstants().getTileAnimationSpeed() / 1000f;
        if (animationCounter >= tileAnimationSpeed) {
            animationCounter -= tileAnimationSpeed;
            animationNumber++;
        }
        ChunkTextureManager chunkTextureManager = PixurvivalGame.getChunkTileTexturesManager();
        DrawUtils.foreachChunksInScreenTopDown(getStage(), 0, c -> {
            ChunkTexture chunkTexture = chunkTextureManager.getChunkTextures().get(c.getPosition());
            if (chunkTexture != null && chunkTexture.isReady()) {
                Texture texture = chunkTexture.getTextures()[(int) (animationNumber % chunkTexture.getTextures().length)];
                batch.draw(texture, c.getPosition().getX() * GameConstants.CHUNK_SIZE, c.getPosition().getY() * GameConstants.CHUNK_SIZE,
                        GameConstants.CHUNK_SIZE + GameConstants.PIXEL_SIZE, GameConstants.CHUNK_SIZE + GameConstants.PIXEL_SIZE);
            }
        });
    }
}
