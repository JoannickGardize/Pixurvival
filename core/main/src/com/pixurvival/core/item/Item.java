package com.pixurvival.core.item;

import com.pixurvival.core.contentPack.Frame;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.ZipContentReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item extends NamedElement {

	private short id;
	private int maxStackSize;
	private Frame frame;
	private ZipContentReference image;

	public Item(String name) {
		super(name);
	}
}
