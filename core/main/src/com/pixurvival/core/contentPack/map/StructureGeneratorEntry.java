package com.pixurvival.core.contentPack.map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.pixurvival.core.contentPack.ElementReference;
import com.pixurvival.core.contentPack.RefContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StructureGeneratorEntry {

	private double probability;
	private Structure structure;

	@Getter
	public static class StructureGeneratorXmlEntry extends ElementReference {

		@XmlAttribute(name = "probability")
		private double probability;
	}

	@AllArgsConstructor
	public static class Adapter extends XmlAdapter<StructureGeneratorXmlEntry, StructureGeneratorEntry> {

		private RefContext refContext;

		@Override
		public StructureGeneratorEntry unmarshal(StructureGeneratorXmlEntry v) throws Exception {
			return new StructureGeneratorEntry(v.getProbability(), refContext.get(Structure.class, v));
		}

		@Override
		public StructureGeneratorXmlEntry marshal(StructureGeneratorEntry v) throws Exception {
			// TODO
			return null;
		}

	}
}
