package com.pixurvival.gdxcore.drawer;

import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.map.FactoryStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

public class FactoryStructureEntityDrawer extends StructureEntityDrawer {

    @Override
    protected TextureAnimationSet getTextureAnimationSet(StructureEntity e) {
        FactoryStructureEntity facStructure = (FactoryStructureEntity) e;
        FactoryStructure definition = (FactoryStructure) facStructure.getDefinition();
        return PixurvivalGame.getContentPackTextures()
                .getAnimationSet(definition.getWorkingSpriteSheet() == null || !facStructure.isWorking() ? definition.getSpriteSheet() : definition.getWorkingSpriteSheet());
    }
}
