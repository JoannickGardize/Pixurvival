package fr.sharkhendrix.pixurvival.core.map;

import fr.sharkhendrix.pixurvival.core.item.ItemReward;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class HarvestableStructure extends Structure {

	private @Setter boolean harvested;

	public abstract ItemReward getItemReward();
}
