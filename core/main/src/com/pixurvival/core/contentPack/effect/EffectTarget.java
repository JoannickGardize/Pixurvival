package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EffectTarget implements Serializable {

    private static final long serialVersionUID = 1L;

    private TargetType targetType;

    private boolean destroyWhenCollide;

    @Valid
    private List<Alteration> alterations = new ArrayList<>();
}
