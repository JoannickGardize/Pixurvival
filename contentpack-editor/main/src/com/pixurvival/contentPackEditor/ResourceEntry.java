package com.pixurvival.contentPackEditor;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.Getter;

@Getter
public class ResourceEntry extends NamedElement {

	private byte[] data;
	private Object preview;

	public ResourceEntry(String name, byte[] data, Object preview) {
		super(name);
		this.data = data;
		this.preview = preview;
	}

}
