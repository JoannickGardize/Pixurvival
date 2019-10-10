package com.pixurvival.gdxcore.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.CooldownAbilityData;
import com.pixurvival.core.livingEntity.ability.EffectAbility;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.input.InputAction;
import com.pixurvival.gdxcore.textures.ColorTextures;

public class AbilityCooldownBox extends Actor {

	DecimalFormat secondsFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.US));
	DecimalFormat millisecondsFormat = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));

	private EquipmentAbilityType type;
	private ShortcutDrawer shortcutDrawer;
	private ItemStackDrawer itemStackDrawer;
	private GlyphLayout glyphLayout = new GlyphLayout();

	public AbilityCooldownBox(EquipmentAbilityType type) {
		this.type = type;
		itemStackDrawer = new ItemStackDrawer(this, 2);
		switch (type) {
		case WEAPON_BASE:
			shortcutDrawer = new ShortcutDrawer(this, InputAction.WEAPON_BASE_OR_DROP_ITEM, ShortcutDrawer.BOTTOM);
			break;
		case WEAPON_SPECIAL:
			shortcutDrawer = new ShortcutDrawer(this, InputAction.WEAPON_SPECIAL, ShortcutDrawer.BOTTOM);
			break;
		case ACCESSORY1_SPECIAL:
			shortcutDrawer = new ShortcutDrawer(this, InputAction.ACCESSORY1_SPECIAL, ShortcutDrawer.BOTTOM);
			break;
		case ACCESSORY2_SPECIAL:
			shortcutDrawer = new ShortcutDrawer(this, InputAction.ACCESSORY2_SPECIAL, ShortcutDrawer.BOTTOM);
			break;
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		PixurvivalGame.getSkin().getDrawable("Button-gray").draw(batch, getX(), getY(), getWidth(), getHeight());
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		EffectAbility ability = type.getAbilityGetter().apply(myPlayer.getEquipment());
		itemStackDrawer.setItemStack(type.getItemGetter().apply(myPlayer.getEquipment()));
		if (ability != null && !ability.isEmpty()) {
			itemStackDrawer.draw(batch);
		}
		shortcutDrawer.draw(batch);
		CooldownAbilityData data = (CooldownAbilityData) myPlayer.getAbilityData(type.getAbilityId());
		long cooldown = data.getReadyTimeMillis() - myPlayer.getWorld().getTime().getTimeMillis();

		if (cooldown > 0 || ability == null || ability.isEmpty()) {
			batch.draw(ColorTextures.get(Color.BLACK), getX(), getY(), getWidth(), getHeight());
		}
		if (cooldown > 0) {
			glyphLayout.setText(PixurvivalGame.getOverlayFont(), toCooldownDisplay(cooldown));
			PixurvivalGame.getOverlayFont().draw(batch, glyphLayout, getX() + getWidth() / 2 - glyphLayout.width / 2, getY() + getHeight() / 2 + glyphLayout.height / 2);
		}
	}

	private String toCooldownDisplay(long cooldown) {
		double s = (cooldown / 1000.0) % 60;
		if (s >= 1) {
			return secondsFormat.format(s);
		} else {
			return millisecondsFormat.format(s);
		}
	}
}
