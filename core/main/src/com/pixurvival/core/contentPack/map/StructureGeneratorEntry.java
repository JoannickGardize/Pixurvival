package com.pixurvival.core.contentPack.map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
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
	public static class StructureGeneratorXmlEntry {

		@XmlAttribute(name = "probability")
		private double probability;

		@XmlAttribute(name = "packRef")
		private String packRef;

		@XmlValue
		private String structure;
	}

	@AllArgsConstructor
	public static class Adapter extends XmlAdapter<StructureGeneratorXmlEntry, StructureGeneratorEntry> {

		private RefContext refContext;

		@Override
		public StructureGeneratorEntry unmarshal(StructureGeneratorXmlEntry v) throws Exception {
			ElementReference ref = new ElementReference(v.getPackRef(), v.getStructure());
			return new StructureGeneratorEntry(v.getProbability(), refContext.get(Structure.class, ref));
		}

		@Override
		public StructureGeneratorXmlEntry marshal(StructureGeneratorEntry v) throws Exception {
			// TODO
			return null;
		}

	}
}
