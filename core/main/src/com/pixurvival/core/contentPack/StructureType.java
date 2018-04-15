package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.util.BeanUtils;

@XmlJavaTypeAdapter(StructureType.Adapter.class)
public enum StructureType {
	HARVESTABLE,
	SHORT_LIVED;

	public static class Adapter extends XmlAdapter<String, StructureType> {

		@Override
		public StructureType unmarshal(String v) throws Exception {
			return StructureType.valueOf(BeanUtils.camelToUpperCase(v));
		}

		@Override
		public String marshal(StructureType v) throws Exception {
			return BeanUtils.upperToCamelCase(v.toString());
		}

	}
}
