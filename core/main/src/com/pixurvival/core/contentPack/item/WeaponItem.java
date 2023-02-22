package com.pixurvival.core.contentPack.item;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.*;
import com.pixurvival.core.livingEntity.ability.ItemAlterationAbility;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
public class WeaponItem extends EquipableItem {

    private static final long serialVersionUID = 1L;

    @ElementReference
    @AnimationTemplateRequirement(AnimationTemplateRequirementSet.CHARACTER)
    @RequiredEquipmentOffset
    private SpriteSheet spriteSheet;

    @Valid
    private ItemAlterationAbility baseAbility = new ItemAlterationAbility();

    @Valid
    private ItemAlterationAbility specialAbility = new ItemAlterationAbility();

    @Override
    public void forEachStatFormula(Consumer<StatFormula> action) {
        baseAbility.forEachStatFormulas(action);
        specialAbility.forEachStatFormulas(action);
    }

    @Override
    public void forEachAlteration(Consumer<Alteration> action) {
        baseAbility.forEachAlteration(action::accept);
        specialAbility.forEachAlteration(action::accept);
    }
}
