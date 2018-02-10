package com.pixurvival.core.contentPack;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Unmarshaller;

import lombok.Getter;
import lombok.Setter;

public class NamedElementRefContext {

	@Getter
	@Setter
	private Dependencies currentDependencies;

	private Map<Class<? extends NamedElement>, NamedElementRefAdapter<? extends NamedElement>> adapters = new HashMap<>();

	public NamedElementRefContext() {
		adapters.put(AnimationTemplate.class, new NamedElementRefAdapter.AnimationTemplateRefAdapter(this));
	}

	public void setAdapters(Unmarshaller unmarshaller) {
		adapters.values().forEach(a -> unmarshaller.setAdapter(a));
	}

	@SuppressWarnings("unchecked")
	public <T extends NamedElement> void addElementSet(ContentPackIdentifier identifier, Class<T> type,
			NamedElementSet<T> set) {
		((NamedElementRefAdapter<T>) adapters.get(type)).addSet(identifier, set);
		((NamedElementRefAdapter<T>) adapters.get(type)).setCurrentSet(set);
	}

	public <T extends NamedElement> void removeCurrentSets() {
		adapters.values().forEach(a -> a.removeCurrentSet());
	}
}
