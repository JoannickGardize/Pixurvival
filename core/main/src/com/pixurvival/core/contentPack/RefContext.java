package com.pixurvival.core.contentPack;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Unmarshaller;

import lombok.Getter;
import lombok.Setter;

public class RefContext {

	@Getter
	@Setter
	private ContentPackFileInfo currentInfo;

	private Map<Class<? extends NamedElement>, RefAdapter<? extends NamedElement>> adapters = new HashMap<>();

	public RefContext() {
		adapters.put(AnimationTemplate.class, new RefAdapter.AnimationTemplateRefAdapter(this));
		adapters.put(Tile.class, new RefAdapter.TileRefAdapter(this));
		adapters.put(SpriteSheet.class, new RefAdapter.SpriteSheetRefAdapter(this));
	}

	@SuppressWarnings("unchecked")
	public <T extends NamedElement> RefAdapter<T> getAdapter(Class<T> type) {
		return (RefAdapter<T>) adapters.get(type);
	}

	public void setAdapters(Unmarshaller unmarshaller) {
		adapters.values().forEach(a -> unmarshaller.setAdapter(a));
		unmarshaller.setAdapter(new ImageReferenceAdapter(this));
	}

	@SuppressWarnings("unchecked")
	public <T extends NamedElement> void addElementSet(Class<T> type, NamedElementSet<T> set) {
		((RefAdapter<T>) adapters.get(type)).addSet(currentInfo, set);
		((RefAdapter<T>) adapters.get(type)).setCurrentSet(set);
	}

	public <T extends NamedElement> void removeCurrentSets() {
		adapters.values().forEach(a -> a.removeCurrentSet());
	}
}
