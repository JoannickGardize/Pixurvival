package com.pixurvival.core.contentPack;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

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
		Assert.assertEquals(1, sprites.getSpriteSheets().size());
		SpriteSheet spriteSheet = sprites.getSpriteSheets().get("character");
		Assert.assertEquals("character", spriteSheet.getName());
		Assert.assertEquals(7, spriteSheet.getWidth());
		Assert.assertEquals(9, spriteSheet.getHeight());
		Assert.assertEquals(new ZipContentReference(file, "images/character.png"), spriteSheet.getImage());
		AnimationTemplate animationTemplate = spriteSheet.getAnimationTemplate();
		Assert.assertEquals("characterAnimation3", animationTemplate.getName());
		Assert.assertEquals(0.3, animationTemplate.getFrameDuration(), Double.MIN_VALUE);
		Assert.assertEquals(8, animationTemplate.getAnimations().size());
		Animation animation = animationTemplate.getAnimations().get("standLeft");
		Assert.assertEquals("standLeft", animation.getName());
		Assert.assertEquals(1, animation.getFrames().length);
		Assert.assertEquals(0, animation.getFrames()[0].getX());
		Assert.assertEquals(2, animation.getFrames()[0].getY());
	}
}
