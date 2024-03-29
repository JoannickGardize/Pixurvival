package com.pixurvival.core.contentPack.item;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
public class EdibleItem extends Item {

    private static final long serialVersionUID = 1L;

    @Positive
    private long duration;

    @Valid
    private List<Alteration> alterations = new ArrayList<>();

    @Override
    public void forEachStatFormula(Consumer<StatFormula> action) {
        alterations.forEach(a -> a.forEachStatFormulas(action));
    }

    @Override
    public void forEachAlteration(Consumer<Alteration> action) {
        alterations.forEach(action);
    }
}
