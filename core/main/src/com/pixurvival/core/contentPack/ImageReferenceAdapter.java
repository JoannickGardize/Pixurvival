package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
class ImageReferenceAdapter extends XmlAdapter<String, ZipContentReference> {

	private @NonNull RefContext context;

	@Override
	public ZipContentReference unmarshal(String v) throws Exception {
		return new ZipContentReference(context.getCurrentInfo().getFile(), "images/" + v);
	}

	@Override
	public String marshal(ZipContentReference v) throws Exception {
		return v.getEntryName();
	}

}
