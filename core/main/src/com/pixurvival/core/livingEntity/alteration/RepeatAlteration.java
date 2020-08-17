package com.pixurvival.core.livingEntity.alteration;

import java.nio.ByteBuffer;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepeatAlteration extends PersistentAlteration {

	private static final long serialVersionUID = 1L;

	private int numberOfRepeat;
	private long interval;
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
	public void writeData(ByteBuffer buffer, Object data) {
		buffer.putLong((long) data);
	}

	@Override
	public Object readData(ByteBuffer buffer) {
		return buffer.getLong();
	}
}
