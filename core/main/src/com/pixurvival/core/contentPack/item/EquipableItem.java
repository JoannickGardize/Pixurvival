package com.pixurvival.core.contentPack.item;

import com.pixurvival.core.contentPack.item.trigger.Trigger;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.stats.StatModifier;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class EquipableItem extends Item {

    private static final long serialVersionUID = 1L;

    @Valid
    private List<StatModifier> statModifiers = new ArrayList<>();

    @Valid
    private List<Trigger> triggers = new ArrayList<>();

    private transient Map<Class<? extends Trigger>, List<Trigger>> triggersByType;
}
