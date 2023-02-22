package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Texture;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemTexture {
    private Texture texture;
    private Texture shadow;
    private TextureMetrics metrics;

    public void dispose() {
        texture.dispose();
    }
}
