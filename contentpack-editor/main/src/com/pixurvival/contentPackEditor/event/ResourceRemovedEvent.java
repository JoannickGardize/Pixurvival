package com.pixurvival.contentPackEditor.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResourceRemovedEvent extends Event {

    private String resourceName;
}
