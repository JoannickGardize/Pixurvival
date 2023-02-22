package com.pixurvival.contentPackEditor.event;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElementRenamedEvent extends Event {

    private String oldName;
    private NamedIdentifiedElement element;
}
