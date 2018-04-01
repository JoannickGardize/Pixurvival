package com.pixurvival.core.contentPack;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.pixurvival.core.contentPack.item.ItemRewards;
import com.pixurvival.core.contentPack.item.Items;
import com.pixurvival.core.contentPack.map.Heightmap;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.StructureGenerator;
import com.pixurvival.core.contentPack.map.Structures;
import com.pixurvival.core.contentPack.map.TileGenerator;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.sprite.Sprites;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.MeleeWeaponItem;

public class ContentPackTest {

	@Test
	public void dependencySuccess() throws ContentPackDenpendencyException, URISyntaxException {
		ContentPacksContext context = new ContentPacksContext(
				new File(getClass().getClassLoader().getResource("dependencySuccess").toURI()));
		ContentPackIdentifier aId = new ContentPackIdentifier("A", new Version(0, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		ContentPackIdentifier bId = new ContentPackIdentifier("B", new Version(0, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		ContentPackIdentifier cId = new ContentPackIdentifier("C", new Version(0, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		ContentPackIdentifier dId = new ContentPackIdentifier("D", new Version(0, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		ContentPackIdentifier eId = new ContentPackIdentifier("E", new Version(0, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		List<ContentPackFileInfo> dependencies = context.resolveDependencies(aId);
		Assert.assertEquals(dependencies.size(), 5);
		Assert.assertEquals(dependencies.get(0), dId);
		Assert.assertEquals(dependencies.get(1), bId);
		Assert.assertEquals(dependencies.get(2), eId);
		Assert.assertEquals(dependencies.get(3), cId);
		Assert.assertEquals(dependencies.get(4), aId);
	}

	@Test
	public void dependencyCycleError() throws URISyntaxException {
		ContentPacksContext context = new ContentPacksContext(
				new File(getClass().getClassLoader().getResource("dependencyCycleError").toURI()));
		ContentPackIdentifier aId = new ContentPackIdentifier("A", new Version(0, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		boolean exceptionThrown = false;
		try {
			context.resolveDependencies(aId);
		} catch (ContentPackDenpendencyException e) {
			exceptionThrown = true;
		}
		Assert.assertTrue(exceptionThrown);
	}

	@Test
	public void dependencyMissing() throws ContentPackDenpendencyException, URISyntaxException {
		ContentPacksContext context = new ContentPacksContext(
				new File(getClass().getClassLoader().getResource("dependencySuccess").toURI()));
		ContentPackIdentifier identifier = new ContentPackIdentifier("Z", new Version(0, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		boolean exceptionThrown = false;
		try {
			context.resolveDependencies(identifier);
		} catch (ContentPackDenpendencyException e) {
			exceptionThrown = true;
		}
		Assert.assertTrue(exceptionThrown);
		identifier = new ContentPackIdentifier("A", new Version(1, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		exceptionThrown = false;
		try {
			context.resolveDependencies(identifier);
		} catch (ContentPackDenpendencyException e) {
			exceptionThrown = true;
		}
		Assert.assertTrue(exceptionThrown);
		identifier = new ContentPackIdentifier("A", new Version(0, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-174396071e1b"));
		exceptionThrown = false;
		try {
			context.resolveDependencies(identifier);
		} catch (ContentPackDenpendencyException e) {
			exceptionThrown = true;
		}
		Assert.assertTrue(exceptionThrown);
	}

	@Test
	public void loadPackNoDependencies() throws URISyntaxException, ContentPackException {
		File packDirectory = new File(getClass().getClassLoader().getResource("loadSuccess").toURI());
		ContentPacksContext context = new ContentPacksContext(packDirectory);
		ContentPackIdentifier aId = new ContentPackIdentifier("B", new Version(0, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		ContentPack pack = context.load(aId);
		testContentPack(new File(packDirectory, "B.zip"), pack);

	}

	@Test
	public void loadPackWithDependencies() throws URISyntaxException, ContentPackException {
		File packDirectory = new File(getClass().getClassLoader().getResource("loadSuccess").toURI());
		ContentPacksContext context = new ContentPacksContext(packDirectory);
		ContentPackIdentifier aId = new ContentPackIdentifier("A", new Version(0, 1),
				UUID.fromString("633d85fe-35f0-499a-b671-184396071e1b"));
		ContentPack pack = context.load(aId);
		testContentPack(new File(packDirectory, "A.zip"), pack);
	}

	private void testContentPack(File file, ContentPack pack) {
		Sprites sprites = pack.getSprites();
		Assert.assertEquals(4, sprites.all().size());
		SpriteSheet spriteSheet = sprites.all().get("character");
		Assert.assertEquals("character", spriteSheet.getName());
		Assert.assertEquals(7, spriteSheet.getWidth());
		Assert.assertEquals(9, spriteSheet.getHeight());
		Assert.assertEquals(new ZipContentReference(file, "images/character.png"), spriteSheet.getImage());
		AnimationTemplate animationTemplate = spriteSheet.getAnimationTemplate();
		Assert.assertEquals("characterAnimation3", animationTemplate.getName());
		Assert.assertEquals(0.2, animationTemplate.getFrameDuration(), Double.MIN_VALUE);
		Assert.assertEquals(8, animationTemplate.getAnimations().size());
		Animation animation = animationTemplate.getAnimations().get(ActionAnimation.STAND_LEFT);
		Assert.assertEquals(ActionAnimation.STAND_LEFT, animation.getAction());
		Assert.assertEquals(1, animation.getFrames().length);
		Assert.assertEquals(0, animation.getFrames()[0].getX());
		Assert.assertEquals(2, animation.getFrames()[0].getY());
		Assert.assertEquals(6, pack.getTiles().all().size());
		Assert.assertEquals(false, pack.getTiles().get("grass").isSolid());
		Assert.assertEquals(new Float(1), pack.getTiles().get("grass").getVelocityFactor());
		Assert.assertEquals(4, pack.getTiles().get("water").getFrames().length);
		Items items = pack.getItems();
		Assert.assertEquals(Item.class, items.get("wood").getClass());
		Assert.assertEquals(MeleeWeaponItem.class, items.get("wood_sword").getClass());
		ItemRewards itemRewards = pack.getItemRewards();
		ItemStack[] reward = itemRewards.get("tree").produce(new Random());
		Assert.assertEquals(1, reward.length);
		Assert.assertEquals(new ItemStack(items.get("wood"), 2), reward[0]);
		Structures structures = pack.getStructures();
		Structure tree = structures.get("tree");
		Assert.assertEquals(4, tree.getHarvestingTime(), Double.MIN_VALUE);
		Assert.assertSame(itemRewards.get("tree"), tree.getItemReward());
		MapGenerator mapGenerator = pack.getMapGenerators().get("default");
		Heightmap heightmap = mapGenerator.getHeightmaps()[0];
		Assert.assertEquals("height", heightmap.getName());
		Assert.assertEquals(5, heightmap.getOctave());
		Assert.assertEquals(0.7, heightmap.getPersistence(), Double.MIN_VALUE);
		Assert.assertEquals(50, heightmap.getScale(), Double.MIN_VALUE);
		TileGenerator tileGenerator = mapGenerator.getTileGenerators()[0];
		Assert.assertSame(heightmap, tileGenerator.getHeightmapConditions()[0].getHeightmap());
		Assert.assertSame(pack.getTiles().get("deepWater"), tileGenerator.getTile());
		StructureGenerator structureGenerator = mapGenerator.getStructureGenerators()[0];
		Assert.assertSame(pack.getStructures().get("tree"),
				structureGenerator.getStructureGeneratorEntries()[0].getStructure());
		Assert.assertEquals(0.4, structureGenerator.getDensity(), Double.MIN_VALUE);
	}
}
