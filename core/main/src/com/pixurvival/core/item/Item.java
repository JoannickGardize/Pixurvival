package com.pixurvival.core.item;

import java.io.Serializable;

import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.sprite.Frame;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class Item extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private short id;
	private int maxStackSize;
	private Frame frame;
	private String image;

	public Item(String name) {
		super(name);
	}
}
