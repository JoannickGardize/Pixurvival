package com.pixurvival.core;

import lombok.Getter;

@Getter
public class AdditionalAttribute {

	private Object value;
	private Class<?> type;
	private Class<?>[] genericTypes;

	public AdditionalAttribute(Object value, Class<?> type, Class<?>... genericTypes) {
		this.value = value;
		this.type = type;
		this.genericTypes = genericTypes;
	}
}
