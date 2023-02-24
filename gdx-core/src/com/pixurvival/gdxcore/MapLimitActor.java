package com.pixurvival.gdxcore;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.system.mapLimits.MapLimitsSystem;
import com.pixurvival.core.util.Rectangle;
import com.pixurvival.gdxcore.textures.ColorTextures;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MapLimitActor extends Actor {

    public static final Color LIMIT_COLOR = new Color(1, 1, 1, 0.6f);
    public static final float BORDER_SIZE = 0.2f;

    private MapLimitsSystem mapLimitsSystem;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Rectangle limitRectangle = mapLimitsSystem.getPersistenceData().getRectangle();
        Vector3 camPos = getStage().getCamera().position;
        OrthographicCamera camera = (OrthographicCamera) getStage().getCamera();
        Viewport viewport = getStage().getViewport();
        float width = viewport.getWorldWidth() * camera.zoom;
        float height = viewport.getWorldHeight() * camera.zoom;
        float startX = camPos.x - width / 2;
        float startY = camPos.y - height / 2;
        float endX = startX + width;
        float endY = startY + height;
        Texture texture;
        if (startX < limitRectangle.getStartX()) {
            texture = ColorTextures.get(LIMIT_COLOR);
            batch.draw(texture, startX, startY, limitRectangle.getStartX() - startX, height);
            float startYForX = Math.max(startY, limitRectangle.getStartY()) - BORDER_SIZE;
            float heightForX = Math.min(endY, limitRectangle.getEndY()) - startYForX + BORDER_SIZE;
            batch.draw(texture, limitRectangle.getStartX() - BORDER_SIZE, startYForX, BORDER_SIZE, heightForX);
        }
        if (endX > limitRectangle.getEndX()) {
            texture = ColorTextures.get(LIMIT_COLOR);
            batch.draw(texture, limitRectangle.getEndX(), startY, endX - limitRectangle.getEndX(), height);
            float startYForX = Math.max(startY, limitRectangle.getStartY()) - BORDER_SIZE;
            float heightForX = Math.min(endY, limitRectangle.getEndY()) - startYForX + BORDER_SIZE;
            batch.draw(texture, limitRectangle.getEndX(), startYForX, BORDER_SIZE, heightForX);
        }
        if (startY < limitRectangle.getStartY()) {
            texture = ColorTextures.get(LIMIT_COLOR);
            float startXForY = Math.max(startX, limitRectangle.getStartX());
            float widthForY = Math.min(endX, limitRectangle.getEndX()) - startXForY;
            batch.draw(texture, startXForY, startY, widthForY, limitRectangle.getStartY() - startY);
            batch.draw(texture, startXForY, limitRectangle.getStartY() - BORDER_SIZE, widthForY, BORDER_SIZE);
        }
        if (endY > limitRectangle.getEndY()) {
            texture = ColorTextures.get(LIMIT_COLOR);
            float startXForY = Math.max(startX, limitRectangle.getStartX());
            float widthForY = Math.min(endX, limitRectangle.getEndX()) - startXForY;
            batch.draw(texture, startXForY, limitRectangle.getEndY(), widthForY, endY - limitRectangle.getEndY());
            batch.draw(texture, startXForY, limitRectangle.getEndY(), widthForY, BORDER_SIZE);

        }
    }
}
