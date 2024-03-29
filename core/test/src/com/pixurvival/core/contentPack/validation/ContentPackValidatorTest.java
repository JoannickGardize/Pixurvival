package com.pixurvival.core.contentPack.validation;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.ProcedurallyGeneratedMapProvider;
import com.pixurvival.core.contentPack.map.TileGenerator;
import com.pixurvival.core.contentPack.map.TileHashmapEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.util.List;

class ContentPackValidatorTest {

    private ContentPackFactory factory;
    private ContentPackValidator validator;

    @BeforeEach
    public void initialize(TestInfo testInfo) throws IOException {
        factory = new ContentPackFactory();
        validator = new ContentPackValidator();
        System.out.println(testInfo.getDisplayName());
    }

    @Test
    void minimalPackTest() {
        ContentPack pack = factory.minimalContentPack();
        Assertions.assertEquals(0, validator.validate(pack).asList().size());
    }

    @Test
    void rootElementReferenceErrorTest() {
        ContentPack pack = factory.minimalContentPack();
        pack.getGameModes().get(0).setEcosystem(new Ecosystem());
        List<ErrorNode> errors = validator.validate(pack).asList();
        System.out.println(errors.get(0));
        Assertions.assertEquals(1, errors.size());
    }

    @Test
    void elementReferenceTest() {
        ContentPack pack = factory.minimalContentPack();
        ProcedurallyGeneratedMapProvider mapProvider = new ProcedurallyGeneratedMapProvider();
        factory.addElement(pack, mapProvider);
        mapProvider.setDefaultTile(pack.getTiles().get(0));
        TileGenerator tileGenerator = createErroredTileGenerator(pack);
        Heightmap heightmap = new Heightmap();
        heightmap.setName("test");
        mapProvider.getHeightmaps().add(heightmap);
        tileGenerator.getTileHashmap().setHeightmap(heightmap);
        mapProvider.getTileGenerators().add(tileGenerator);
        Assertions.assertEquals(0, validator.validate(pack).asList().size());
    }

    @Test
    void elementReferenceErrorTest() {
        ContentPack pack = factory.minimalContentPack();
        ProcedurallyGeneratedMapProvider mapProvider = new ProcedurallyGeneratedMapProvider();
        factory.addElement(pack, mapProvider);
        mapProvider.setDefaultTile(pack.getTiles().get(0));
        TileGenerator tileGenerator = createErroredTileGenerator(pack);
        mapProvider.getTileGenerators().add(tileGenerator);
        List<ErrorNode> errors = validator.validate(pack).asList();
        System.out.println(errors.get(0));
        Assertions.assertEquals(1, errors.size());
    }

    private TileGenerator createErroredTileGenerator(ContentPack pack) {
        TileGenerator tileGenerator = new TileGenerator();
        TileHashmapEntry entry = new TileHashmapEntry();
        entry.setTile(pack.getTiles().get(0));
        entry.setNext(1);
        tileGenerator.getTileHashmap().getEntries().add(entry);
        tileGenerator.getTileHashmap().setHeightmap(new Heightmap());
        return tileGenerator;
    }
}
