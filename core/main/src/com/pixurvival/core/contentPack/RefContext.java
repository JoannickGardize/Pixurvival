package com.pixurvival.core.contentPack;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Unmarshaller;

import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.StructureGeneratorEntry;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.ItemReward;
import com.pixurvival.core.item.ItemRewardEntry;

import lombok.Getter;
import lombok.Setter;

public class RefContext {

	@Getter
	@Setter
	private ContentPackFileInfo currentInfo;

	private Map<Class<? extends NamedElement>, RefAdapter<? extends NamedElement>> adapters = new HashMap<>();

	public RefContext() {
		adapters.put(AnimationTemplate.class, new RefAdapter.AnimationTemplateRefAdapter(this));
		adapters.put(Tile.class, new RefAdapter.TileRefAdapter(this));
		adapters.put(SpriteSheet.class, new RefAdapter.SpriteSheetRefAdapter(this));
		adapters.put(Item.class, new RefAdapter.ItemRefAdapter(this));
		adapters.put(ItemReward.class, new RefAdapter.ItemRewardRefAdapter(this));
		adapters.put(Structure.class, new RefAdapter.StructureRefAdapter(this));
	}

	@SuppressWarnings("unchecked")
	public <T extends NamedElement> RefAdapter<T> getAdapter(Class<T> type) {
		return (RefAdapter<T>) adapters.get(type);
	}

	public void setAdapters(Unmarshaller unmarshaller) {
		adapters.values().forEach(a -> unmarshaller.setAdapter(a));
		unmarshaller.setAdapter(new ImageReferenceAdapter(this));
		unmarshaller.setAdapter(new ItemRewardEntry.Adapter(this));
		unmarshaller.setAdapter(new StructureGeneratorEntry.Adapter(this));
	}

	@SuppressWarnings("unchecked")
	public <T extends NamedElement> void addElementSet(Class<T> type, NamedElementSet<T> set) {
		set.finalizeElements();
		((RefAdapter<T>) adapters.get(type)).addSet(currentInfo, set);
		((RefAdapter<T>) adapters.get(type)).setCurrentSet(set);
	}

	public <T extends NamedElement> void removeCurrentSets() {
		adapters.values().forEach(a -> a.removeCurrentSet());
	}

	@SuppressWarnings("unchecked")
	public <T extends NamedElement> T get(Class<T> type, ElementReference reference) {
		try {
			return (T) adapters.get(type).unmarshal(reference);
		} catch (Exception e) {
			return null;
		}
	}
}
