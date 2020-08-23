package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.FloatWrapper;
import com.pixurvival.gdxcore.util.TruncatingQueue;

public class ChatHistory extends Widget implements Cullable {

	private static final float MARGIN = 13;

	private TruncatingQueue<ChatTextEntry> queue;
	private float drawStartY;
	private float drawEndY;

	private float prefHeight;

	public ChatHistory(int historySize) {
		queue = new TruncatingQueue<>(historySize);
		setSize(getPrefWidth(), getPrefHeight());
	}

	public void push(ChatTextEntry chatEntry) {
		queue.push(chatEntry);
		invalidateHierarchy();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();
		FloatWrapper currentHeight = new FloatWrapper(MARGIN);
		queue.forEachReverse(chatEntry -> {
			if (currentHeight.getValue() + chatEntry.getGlyphLayout().height > drawStartY) {
				PixurvivalGame.getDefaultFont().draw(batch, chatEntry.getGlyphLayout(), getX(), getY() + currentHeight.getValue() + chatEntry.getGlyphLayout().height);
			}
			currentHeight.setValue(currentHeight.getValue() + chatEntry.getGlyphLayout().height + MARGIN);
			return currentHeight.getValue() >= drawEndY;
		});
	}

	@Override
	public void setCullingArea(Rectangle cullingArea) {
		drawStartY = cullingArea.y;
		drawEndY = cullingArea.y + cullingArea.height;
	}

	@Override
	public void layout() {
		prefHeight = MARGIN;
		queue.forEach(chatEntry -> {
			chatEntry.buildGlyphLayout(PixurvivalGame.getDefaultFont(), getWidth());
			prefHeight += chatEntry.getGlyphLayout().height + MARGIN;
		});
	}

	@Override
	public float getPrefHeight() {
		validate();
		return prefHeight;
	}

	@Override
	protected void sizeChanged() {
		invalidateHierarchy();
	}
}
