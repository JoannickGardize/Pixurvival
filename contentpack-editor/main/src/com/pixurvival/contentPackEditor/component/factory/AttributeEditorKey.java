package com.pixurvival.contentPackEditor.component.factory;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class AttributeEditorKey {

	private Class<?> type;
	private Set<AttributeEditorFlag> flags;

	public AttributeEditorKey(Class<?> type) {
		this.type = type;
	}

	public AttributeEditorKey(Class<?> type, AttributeEditorFlag... flags) {
		this.type = type;
		this.flags = flags.length > 0 ? EnumSet.of(flags[0], flags) : EnumSet.noneOf(AttributeEditorFlag.class);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, flags);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof AttributeEditorKey)) {
			return false;
		}
		AttributeEditorKey other = (AttributeEditorKey) obj;
		return type == other.type && Objects.equals(flags, other.flags);
	}

}
