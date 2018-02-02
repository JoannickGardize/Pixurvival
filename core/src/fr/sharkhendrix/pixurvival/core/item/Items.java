package fr.sharkhendrix.pixurvival.core.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Items")
public class Items {

	@XmlElements({ @XmlElement(name = "EquipableItem", type = EquipableItem.class) })
	private List<Item> itemList;

	private boolean mapBuilt = false;

	private Map<String, Item> itemMap = new HashMap<>();

	public Item get(String name) {
		ensureMapBuilt();
		return itemMap.get(name);
	}

	private void ensureMapBuilt() {
		if (!mapBuilt) {
			for (Item item : itemList) {
				itemMap.put(item.getName(), item);
			}
			itemList.clear();
			mapBuilt = true;
		}
	}

}
