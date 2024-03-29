package com.pixurvival.contentPackEditor.event;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElementInstanceChangedEvent extends Event {
    private NamedIdentifiedElement oldElement;
    private NamedIdentifiedElement element;
}
