package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.message.InventoryActionRequest;
import com.pixurvival.core.message.InventoryActionRequest.Type;
import com.pixurvival.gdxcore.PixurvivalGame;

public class InventorySlot extends Button {

	private Inventory inventory;
	private int slotIndex;

	public InventorySlot(Inventory inventory, int slotIndex) {
		super(PixurvivalGame.getSkin());
		this.inventory = inventory;
		this.slotIndex = slotIndex;

		this.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PixurvivalGame.getClient()
						.sendAction(new InventoryActionRequest(Type.CURSOR_MY_INVENTORY, (short) slotIndex));
			}
		});
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		ItemStack itemStack = inventory.getSlot(slotIndex);
		if (itemStack != null) {
			Texture texture = PixurvivalGame.getContentPackTextures().getItem(itemStack.getItem().getId());
			batch.draw(texture, getX() + 2, getY() + 2, getOriginX(), getOriginY(), getWidth() - 4, getHeight() - 4,
					getScaleX(), getScaleY(), getRotation(), 0, 0, texture.getWidth(), texture.getHeight(), false,
					false);
			BitmapFont font = PixurvivalGame.getSkin().getFont("default");
			font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			font.setColor(Color.WHITE);
			GlyphLayout layout = new GlyphLayout(font, String.valueOf(itemStack.getQuantity()));
			font.draw(batch, layout, getX() + getWidth() - layout.width - 2, getY() + getHeight() - 2);
		}
	}
}
