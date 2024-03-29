package com.pixurvival.core.contentPack.creature;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.ElementList;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BehaviorSet extends NamedIdentifiedElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    @ElementList(value = Behavior.class)
    @Length(min = 1)
    private List<Behavior> behaviors = new ArrayList<>();

    @Override
    public void initialize() {
        for (int i = 0; i < behaviors.size(); i++) {
            Behavior behavior = behaviors.get(i);
            behavior.setId(i);
            behavior.initialize();

        }
    }
}
