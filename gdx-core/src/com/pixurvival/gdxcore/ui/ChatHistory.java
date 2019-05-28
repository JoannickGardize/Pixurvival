package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.pixurvival.core.util.IntWrapper;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.FloatWrapper;
import com.pixurvival.gdxcore.util.TruncatingQueue;

public class ChatHistory extends Widget implements Cullable {

	private TruncatingQueue<ChatEntry> queue;
	private int drawStartIndex;
	private int drawSize;

	private float prefWidth;
	private float prefHeight;

	public ChatHistory(int historySize) {
		queue = new TruncatingQueue<>(historySize);
		setSize(getPrefWidth(), getPrefHeight());
	}

	public void push(ChatEntry chatEntry) {
		queue.push(chatEntry);
		invalidateHierarchy();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();
		int size = drawStartIndex + drawSize > queue.size() ? queue.size() - drawStartIndex : drawSize;
		IntWrapper i = new IntWrapper(drawStartIndex);
		queue.forEachRange(drawStartIndex, size, chatEntry -> {
			PixurvivalGame.getDefaultFont().draw(batch, chatEntry.getGlyphLayout(), getX(), getY() + getHeight() - i.getValue() * PixurvivalGame.getDefaultFont().getLineHeight());
			i.increment();
		});
	}

	@Override
	public void setCullingArea(Rectangle cullingArea) {
		drawStartIndex = Math.max(0, (int) ((getPrefHeight() - cullingArea.y - cullingArea.height) / PixurvivalGame.getDefaultFont().getLineHeight()));
		drawSize = (int) Math.ceil(cullingArea.height / PixurvivalGame.getDefaultFont().getLineHeight()) + 1;
	}

	@Override
	public void layout() {
		FloatWrapper newWidth = new FloatWrapper();
		queue.forEach(chatEntry -> {
			chatEntry.buildGlyphLayout(PixurvivalGame.getDefaultFont());
			GlyphLayout glyphLayout = chatEntry.getGlyphLayout();
			if (glyphLayout.width > newWidth.getValue()) {
				newWidth.setValue(glyphLayout.width);
			}
		});
		// setSize(newWidth.getValue(),
		// PixurvivalGame.getDefaultFont().getLineHeight() * queue.size());
		prefWidth = newWidth.getValue();
		prefHeight = PixurvivalGame.getDefaultFont().getLineHeight() * queue.size();
	}

	@Override
	public float getPrefWidth() {
		validate();
		return prefWidth;
	}

	@Override
	public float getPrefHeight() {
		validate();
		return prefHeight;
	}
}
