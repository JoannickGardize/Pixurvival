package com.pixurvival.contentPackEditor.relationGraph;

import java.util.HashSet;
import java.util.Set;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
class RelationEntry {

	@NonNull
	private NamedIdentifiedElement element;
	private Set<NamedIdentifiedElement> relations = new HashSet<>();
	private boolean upToDate = false;
}
