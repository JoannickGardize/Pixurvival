package com.pixurvival.core.contentPack;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefAdapter<T extends NamedElement> extends XmlAdapter<ElementReference, T> {

	private Map<ContentPackIdentifier, NamedElementSet<T>> allSets = new HashMap<>();

	private @NonNull RefContext context;

	public void addSet(ContentPackIdentifier identifier, NamedElementSet<T> set) {
		allSets.put(identifier, set);
	}

	public void setCurrentSet(NamedElementSet<T> set) {
		allSets.put(null, set);
	}

	public void removeCurrentSet() {
		allSets.remove(null);
	}

	@Override
	public T unmarshal(ElementReference v) throws Exception {
		if (context.getCurrentInfo() == null) {
			throw new ContentPackReadException("Missing dependency context");
		}
		NamedElementSet<T> set = null;
		if (v.getPackRef() == null) {
			set = allSets.get(null);
		} else if (context.getCurrentInfo().getDependencies() != null) {
			set = allSets.get(context.getCurrentInfo().getDependencies().byRef(v.getPackRef()));
		}
		if (set == null) {
			throw new ContentPackReadException("Unknown content pack reference : " + v.getPackRef());
		}
		T element = set.get(v.getName());
		if (element == null) {
			throw new ContentPackReadException("Unknown element reference : " + v.getName());
		}
		return element;
	}

	@Override
	public ElementReference marshal(T v) throws Exception {
		// TODO récupérer la référence
		return new ElementReference(null, v.getName());
	}

	public static class AnimationTemplateRefAdapter extends RefAdapter<AnimationTemplate> {

		public AnimationTemplateRefAdapter(RefContext context) {
			super(context);
		}

	}
}
