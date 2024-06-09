package com.pixurvival.core.contentPack.trigger;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Trigger {

    @Valid
    private List<Alteration> alterations = new ArrayList<>();

    private transient int index;
}
