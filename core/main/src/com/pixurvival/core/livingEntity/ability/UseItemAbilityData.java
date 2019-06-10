package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.Time;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UseItemAbilityData extends WorkAbilityData {
	
	private EdibleItem edibleItem;
	
	public void setEdibleItem(EdibleItem edibleItem) {
		this.edibleItem = edibleItem;
		setDurationMillis(Time.secToMillis(edibleItem.getDuration()));
		
	}

	@Override
	public void write(ByteBuffer buffer, LivingEntity entity) {
		buffer.putShort((short) edibleItem.getId());
		buffer.putLong(getStartTimeMillis());
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		setEdibleItem((EdibleItem) entity.getWorld().getContentPack().getItems().get(buffer.getShort()));
		setStartTimeMillis(buffer.getLong());
		
	}

}
