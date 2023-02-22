package com.pixurvival.contentPackEditor.relationGraph;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
class RelationEntry {

    @NonNull
    private NamedIdentifiedElement element;
    private Set<NamedIdentifiedElement> elementRelations = new HashSet<>();
    private Set<String> resourceRelations = new HashSet<>();
    private boolean upToDate = false;
}
