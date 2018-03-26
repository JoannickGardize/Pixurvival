package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.Getter;
import lombok.Setter;

public class ItemStackDrawer {

	private @Getter @Setter ItemStack itemStack;
	private Actor actor;
	private float padding;

	public ItemStackDrawer(Actor actor, float padding) {
		this.actor = actor;
		this.padding = padding;
	}

	public void draw(Batch batch) {
		if (itemStack != null) {
			Texture texture = PixurvivalGame.getContentPackTextures().getItem(itemStack.getItem().getId()).getTexture();
			batch.draw(texture, actor.getX() + padding, actor.getY() + padding, actor.getOriginX(), actor.getOriginY(),
					actor.getWidth() - padding * 2, actor.getHeight() - padding * 2, actor.getScaleX(),
					actor.getScaleY(), actor.getRotation(), 0, 0, texture.getWidth(), texture.getHeight(), false,
					false);
			if (itemStack.getQuantity() > 1) {
				BitmapFont font = PixurvivalGame.getSkin().getFont("default");
				font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				font.setColor(Color.WHITE);
				GlyphLayout layout = new GlyphLayout(font, String.valueOf(itemStack.getQuantity()));
				font.draw(batch, layout, actor.getX() + actor.getWidth() - layout.width - padding,
						actor.getY() + actor.getHeight() - padding * 2);
			}
		}
	}
}
