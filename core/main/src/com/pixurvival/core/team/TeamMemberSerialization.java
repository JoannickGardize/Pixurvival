package com.pixurvival.core.team;

import java.nio.ByteBuffer;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.gameMode.event.EffectEventTeamMember;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.map.DamageableMapStructure;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TeamMemberSerialization {

	private static final byte ENTITY_TYPE = 0;
	private static final byte DAMAGEABLE_MAP_STRUCTURE_TYPE = 1;
	private static final byte EFFECT_EVENT_TYPE = 2;

	public static void write(ByteBuffer buffer, TeamMember teamMember, boolean safeMode) {
		if (teamMember instanceof Entity) {
			buffer.put(ENTITY_TYPE);
			Entity entity = (Entity) teamMember;
			buffer.put((byte) entity.getGroup().ordinal());
			buffer.putLong(entity.getId());
			if (safeMode) {
				writeFlatTeamMember(buffer, teamMember);
			}
		} else if (teamMember instanceof DamageableMapStructure) {
			buffer.put(DAMAGEABLE_MAP_STRUCTURE_TYPE);
			// TODO structure as origin
		} else if (teamMember instanceof EffectEventTeamMember) {
			buffer.put(EFFECT_EVENT_TYPE);
			writeFlatTeamMember(buffer, teamMember);
		} else if (teamMember instanceof EntityNotFoundProxy) {
			EntityNotFoundProxy proxy = (EntityNotFoundProxy) teamMember;
			buffer.put(ENTITY_TYPE);
			buffer.put((byte) proxy.getGroup().ordinal());
			buffer.putLong(proxy.getId());
			if (safeMode) {
				writeFlatTeamMember(buffer, teamMember);
			}
		} else {
			throw new IllegalArgumentException();
		}

	}

	public static TeamMember read(ByteBuffer buffer, World world, boolean safeMode) {
		switch (buffer.get()) {
		case ENTITY_TYPE:
			EntityGroup group = EntityGroup.values()[buffer.get()];
			long id = buffer.getLong();
			Entity e = world.getEntityPool().get(group, id);
			if (e == null) {
				EntityNotFoundProxy proxy = new EntityNotFoundProxy(world, group, id);
				if (safeMode) {
					applyFlatTeamMember(buffer, proxy);
				}
				return proxy;
			} else {
				if (safeMode) {
					skipFlatTeamMember(buffer);
				}
				return (TeamMember) e;
			}
		case EFFECT_EVENT_TYPE:
			FlatTeamMember result = new FlatTeamMember(world);
			applyFlatTeamMember(buffer, result);
			return result;
		default:
			return new FlatTeamMember(world);
		}
	}

	public static void writeNullSafe(ByteBuffer buffer, TeamMember teamMember, boolean safeMode) {
		if (teamMember == null) {
			buffer.put((byte) 0);
		} else {
			buffer.put((byte) 1);
			write(buffer, teamMember, safeMode);
		}
	}

	public static TeamMember readNullSafe(ByteBuffer buffer, World world, boolean safeMode) {
		byte isNull = buffer.get();
		if (isNull == 0) {
			return null;
		} else {
			return read(buffer, world, safeMode);
		}
	}

	private static void writeFlatTeamMember(ByteBuffer buffer, TeamMember teamMember) {
		buffer.putShort((short) teamMember.getTeam().getId());
		teamMember.getStats().writeValues(buffer);
		teamMember.getPosition().write(buffer);
		teamMember.getTargetPosition().write(buffer);
	}

	private static void applyFlatTeamMember(ByteBuffer buffer, FlatTeamMember teamMember) {
		teamMember.setTeam(teamMember.getWorld().getTeamSet().get(buffer.getShort()));
		teamMember.getStats().applyValues(buffer);
		teamMember.getPosition().apply(buffer);
		teamMember.getTargetPosition().apply(buffer);
	}

	private static void skipFlatTeamMember(ByteBuffer buffer) {
		buffer.position(buffer.position() + 2 + 4 * 6 + 8 + 8);
	}
}
