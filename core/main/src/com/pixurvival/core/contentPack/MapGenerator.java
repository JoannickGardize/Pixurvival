package com.pixurvival.core.contentPack;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
@XmlRootElement(name = "mapGenerator")
public class MapGenerator {
	@XmlAttribute(name = "name")
	private String name;
	@XmlAttribute(name = "size")
	private int size;
	@XmlAttribute(name = "noiseFactor")
	private float noiseFactor;
	@XmlElement(name = "layer")
	private @Getter(AccessLevel.NONE) Layer[] layers;

	public Layer getLayer(int index) {
		return layers[index];
	}

	public void foreachLayers(Consumer<Layer> action) {
		for (Layer layer : layers) {
			action.accept(layer);
		}
	}

	public int layerCount() {
		return layers.length;
	}
}
