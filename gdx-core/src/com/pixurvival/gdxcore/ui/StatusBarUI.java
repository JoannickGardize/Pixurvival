package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.gdxcore.PixurvivalGame;

public class StatusBarUI extends Table {

    public StatusBarUI() {
        defaults().fill().pad(2);
        add(new ValueBarActor(() -> PixurvivalGame.getClient().getMyPlayer().getHealth(),
                () -> PixurvivalGame.getClient().getMyPlayer().getMaxHealth(), "generic.health", Color.GREEN))
                .colspan(4).padLeft(15).padRight(15).height(20);
        row();
        if (PixurvivalGame.getWorld().getGameMode().getHungerPerMinute() != 0) {
            add(new ValueBarActor(() -> PixurvivalGame.getClient().getMyPlayer().getHunger(),
                    () -> PlayerEntity.MAX_HUNGER, "generic.hunger", Color.YELLOW))
                    .colspan(4).padLeft(15).padRight(15).height(20);
            row();
        }
        defaults().size(50, 50);
        add(new AbilityCooldownBox(EquipmentAbilityType.WEAPON_BASE));
        add(new AbilityCooldownBox(EquipmentAbilityType.WEAPON_SPECIAL));
        add(new AbilityCooldownBox(EquipmentAbilityType.ACCESSORY1_SPECIAL));
        add(new AbilityCooldownBox(EquipmentAbilityType.ACCESSORY2_SPECIAL));
        pack();
        setColor(1, 1, 1, 0.5f);
    }

    public void updatePosition() {
        setPosition(Gdx.graphics.getWidth() / 2f - getWidth() / 2, 30);
    }
}
