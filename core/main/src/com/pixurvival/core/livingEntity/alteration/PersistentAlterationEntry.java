package com.pixurvival.core.livingEntity.alteration;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = { "source", "alteration" })
public class PersistentAlterationEntry {
	private Object source;
	private PersistentAlteration alteration;

	private @Setter long termTimeMillis;

	public PersistentAlterationEntry(Object source, PersistentAlteration alteration) {
		this.source = source;
		this.alteration = alteration;
	}
}
