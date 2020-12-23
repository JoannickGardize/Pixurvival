package com.pixurvival.core.contentPack.validation;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assertions;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.RemainingTeamEndCondition;
import com.pixurvival.core.contentPack.map.StaticMapProvider;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.FrameOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class ContentPackFactory {

	private int nextNameSalt = 0;

	public ContentPack minimalContentPack() {
		ContentPack contentPack = new ContentPack();
		contentPack.initialize();
		contentPack.setReleaseVersion("ALPHA_5B");
		contentPack.setIdentifier(new ContentPackIdentifier("Test", 1, 0));
		contentPack.getConstants().setDefaultCharacter(addMinimalCharacterSpriteSheet(contentPack));
		contentPack.getConstants().setOutsideTile(addMinimalTile(contentPack));
		addMinimalGameMode(contentPack);
		return contentPack;
	}

	public SpriteSheet addMinimalCharacterSpriteSheet(ContentPack contentPack) {
		SpriteSheet spriteSheet = new SpriteSheet();
		spriteSheet.setImage(addImageResourceIfNotExists(contentPack, 8, 8));
		spriteSheet.setWidth(8);
		spriteSheet.setHeight(8);
		spriteSheet.setAnimationTemplate(addMinimalCharacterAnimationTemplate(contentPack));
		spriteSheet.setEquipmentOffset(addMinimalEquipmentOffset(contentPack));

		addElement(contentPack, spriteSheet);

		return spriteSheet;
	}

	public AnimationTemplate addMinimalCharacterAnimationTemplate(ContentPack contentPack) {
		AnimationTemplate template = new AnimationTemplate();

		template.getAnimations().put(ActionAnimation.MOVE_DOWN, minimalAnimation(ActionAnimation.MOVE_DOWN));
		template.getAnimations().put(ActionAnimation.MOVE_LEFT, minimalAnimation(ActionAnimation.MOVE_LEFT));
		template.getAnimations().put(ActionAnimation.MOVE_RIGHT, minimalAnimation(ActionAnimation.MOVE_RIGHT));
		template.getAnimations().put(ActionAnimation.MOVE_UP, minimalAnimation(ActionAnimation.MOVE_UP));
		template.getAnimations().put(ActionAnimation.STAND_DOWN, minimalAnimation(ActionAnimation.STAND_DOWN));
		template.getAnimations().put(ActionAnimation.STAND_LEFT, minimalAnimation(ActionAnimation.STAND_LEFT));
		template.getAnimations().put(ActionAnimation.STAND_RIGHT, minimalAnimation(ActionAnimation.STAND_RIGHT));
		template.getAnimations().put(ActionAnimation.STAND_UP, minimalAnimation(ActionAnimation.STAND_UP));
		template.getAnimations().put(ActionAnimation.WORK_DOWN, minimalAnimation(ActionAnimation.WORK_DOWN));
		template.getAnimations().put(ActionAnimation.WORK_LEFT, minimalAnimation(ActionAnimation.WORK_LEFT));
		template.getAnimations().put(ActionAnimation.WORK_RIGHT, minimalAnimation(ActionAnimation.WORK_RIGHT));
		template.getAnimations().put(ActionAnimation.WORK_UP, minimalAnimation(ActionAnimation.WORK_UP));

		addElement(contentPack, template);

		return template;
	}

	public EquipmentOffset addMinimalEquipmentOffset(ContentPack contentPack) {
		EquipmentOffset equipmentOffset = new EquipmentOffset();
		equipmentOffset.getFrameOffsets().add(new FrameOffset());
		addElement(contentPack, equipmentOffset);
		return equipmentOffset;
	}

	public Tile addMinimalTile(ContentPack contentPack) {
		Tile tile = new Tile();
		tile.getFrames().add(new Frame());
		tile.setImage(addImageResourceIfNotExists(contentPack, 8, 8));
		addElement(contentPack, tile);
		return tile;
	}

	public StaticMapProvider addMinimalStaticMapProvider(ContentPack contentPack) {
		StaticMapProvider staticMapProvider = new StaticMapProvider();
		staticMapProvider.setDefaultTile(addMinimalTile(contentPack));
		addElement(contentPack, staticMapProvider);
		return staticMapProvider;
	}

	public Ecosystem addMinimalEcosystem(ContentPack contentPack) {
		Ecosystem ecosystem = new Ecosystem();
		addElement(contentPack, ecosystem);
		return ecosystem;
	}

	public GameMode addMinimalGameMode(ContentPack contentPack) {
		GameMode gameMode = new GameMode();
		gameMode.setMapProvider(addMinimalStaticMapProvider(contentPack));
		gameMode.setEcosystem(addMinimalEcosystem(contentPack));
		gameMode.getEndGameConditions().add(new RemainingTeamEndCondition());
		addElement(contentPack, gameMode);
		return gameMode;
	}

	public Animation minimalAnimation(ActionAnimation actionAnimation) {
		Animation animation = new Animation(actionAnimation);
		animation.getFrames().add(new Frame());
		return animation;
	}

	public String addImageResourceIfNotExists(ContentPack contentPack, int width, int height) {
		String name = "image" + width + "x" + height + ".png";
		if (!contentPack.containsResource(name)) {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			try (ByteArrayOutputStream output = new ByteArrayOutputStream(1024)) {
				ImageIO.write(image, "png", output);
				contentPack.addResource(name, output.toByteArray());
			} catch (IOException e) {
				Assertions.fail(e.toString());
			}
		}
		return name;
	}

	public void addElement(ContentPack contentPack, NamedIdentifiedElement element) {
		List<NamedIdentifiedElement> list = contentPack.listOf(element.getClass());
		element.setName(nextUniqueName());
		element.setId(list.size());
		list.add(element);
	}

	private String nextUniqueName() {
		return "element" + (nextNameSalt++);
	}
}
