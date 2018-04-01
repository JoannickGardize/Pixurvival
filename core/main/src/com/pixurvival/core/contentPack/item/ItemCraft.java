package com.pixurvival.core.contentPack.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ItemCraft extends NamedElement {

	private @Setter byte id;

	@XmlAttribute(name = "duration")
	private double duration;

	@XmlElement(name = "result")
	@XmlJavaTypeAdapter(ItemStackXmlEntry.Adapter.class)
	private ItemStack result;

	@XmlElementWrapper(name = "recipes")
	@XmlElement(name = "recipe")
	@XmlJavaTypeAdapter(ItemStackXmlEntry.Adapter.class)
	private ItemStack[] recipes;
}
