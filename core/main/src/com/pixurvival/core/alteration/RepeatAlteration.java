package com.pixurvival.core.alteration;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.ByteBufferUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepeatAlteration extends PersistentAlteration {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 1)
	private int numberOfRepeat;
	@Positive
	private long interval;
	@Valid
	private Alteration alteration;

	@Override
	public Object begin(TeamMember source, LivingEntity entity) {
		// TODO set this at ContentPack initialization
		setDuration(numberOfRepeat * interval);
		return source.getWorld().getTime().getTimeMillis();
	}

	@Override
	public Object update(TeamMember source, LivingEntity entity, Object data) {
		long nextTrigger = (long) data;
		long currentTime = source.getWorld().getTime().getTimeMillis();
		if (currentTime >= nextTrigger) {
			alteration.apply(source, entity);
			nextTrigger += interval;
		}
		return nextTrigger;
	}

	@Override
	public void writeData(ByteBuffer buffer, LivingEntity entity, Object data) {
		ByteBufferUtils.writeFutureTime(buffer, entity.getWorld(), (long) data);
	}

	@Override
	public Object readData(ByteBuffer buffer, LivingEntity entity) {
		return ByteBufferUtils.readFutureTime(buffer, entity.getWorld());
	}
}
