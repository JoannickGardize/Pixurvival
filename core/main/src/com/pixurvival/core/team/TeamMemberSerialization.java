package com.pixurvival.core.team;

import java.nio.ByteBuffer;

import com.pixurvival.core.World;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.util.VarLenNumberIO;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TeamMemberSerialization {

	private static final byte ENTITY_TYPE = 0;
	private static final byte MAP_STRUCTURE_TYPE = 1;
	private static final byte FLAT_TYPE = 2;

	public static void write(ByteBuffer buffer, TeamMember teamMember, boolean safeMode) {
		if (teamMember instanceof Entity) {
			buffer.put(ENTITY_TYPE);
			Entity entity = (Entity) teamMember;
			buffer.put((byte) entity.getGroup().ordinal());
			VarLenNumberIO.writePositiveVarLong(buffer, entity.getId());
			if (safeMode) {
				writeFlatTeamMember(buffer, teamMember);
			}
		} else if (teamMember instanceof StructureEntity) {
			buffer.put(MAP_STRUCTURE_TYPE);
			StructureEntity mapStructure = (StructureEntity) teamMember;
			VarLenNumberIO.writePositiveVarLong(buffer, mapStructure.getId());
			VarLenNumberIO.writePositiveVarInt(buffer, mapStructure.getTileX());
			VarLenNumberIO.writePositiveVarInt(buffer, mapStructure.getTileY());
			if (safeMode) {
				writeFlatTeamMember(buffer, teamMember);
			}
		} else if (teamMember instanceof EntityNotFoundProxy) {
			EntityNotFoundProxy proxy = (EntityNotFoundProxy) teamMember;
			buffer.put(ENTITY_TYPE);
			buffer.put((byte) proxy.getGroup().ordinal());
			VarLenNumberIO.writePositiveVarLong(buffer, proxy.getId());
			if (safeMode) {
				writeFlatTeamMember(buffer, teamMember);
			}
		} else {
			buffer.put(FLAT_TYPE);
			writeFlatTeamMember(buffer, teamMember);
		}
	}

	public static TeamMember read(ByteBuffer buffer, World world, boolean safeMode) {
		switch (buffer.get()) {
		case ENTITY_TYPE:
			return readEntity(buffer, world, safeMode);
		case MAP_STRUCTURE_TYPE:
			return readStructure(buffer, world, safeMode);
		case FLAT_TYPE:
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

	private static TeamMember readStructure(ByteBuffer buffer, World world, boolean safeMode) {
		long id = VarLenNumberIO.readPositiveVarLong(buffer);
		int x = VarLenNumberIO.readPositiveVarInt(buffer);
		int y = VarLenNumberIO.readPositiveVarInt(buffer);
		Chunk chunk = world.getMap().chunkAt(x, y);
		if (chunk != null) {
			StructureEntity structure = chunk.tileAt(x, y).getStructure();
			if (structure != null && structure.getId() == id) {
				if (safeMode) {
					// Read for skipping
					applyFlatTeamMember(buffer, new StructureNotFoundProxy(world, id, x, y));
				}
				return structure;
			}
		}
		StructureNotFoundProxy proxy = new StructureNotFoundProxy(world, id, x, y);
		if (safeMode) {
			applyFlatTeamMember(buffer, proxy);
		}
		return proxy;
	}

	private static TeamMember readEntity(ByteBuffer buffer, World world, boolean safeMode) {
		EntityGroup group = EntityGroup.values()[buffer.get()];
		long id = VarLenNumberIO.readPositiveVarLong(buffer);
		Entity e = world.getEntityPool().get(group, id);
		if (e == null) {
			EntityNotFoundProxy proxy = new EntityNotFoundProxy(world, group, id);
			if (safeMode) {
				applyFlatTeamMember(buffer, proxy);
			}
			return proxy;
		} else {
			if (safeMode) {
				// Read for skipping
				applyFlatTeamMember(buffer, new EntityNotFoundProxy(world, group, id));
			}
			return (TeamMember) e;
		}
	}

	private static void writeFlatTeamMember(ByteBuffer buffer, TeamMember teamMember) {
		VarLenNumberIO.writePositiveVarInt(buffer, teamMember.getTeam().getId());
		teamMember.getStats().writeValues(buffer);
		teamMember.getPosition().write(buffer);
		teamMember.getTargetPosition().write(buffer);
	}

	private static void applyFlatTeamMember(ByteBuffer buffer, FlatTeamMember teamMember) {
		teamMember.setTeam(teamMember.getWorld().getTeamSet().get(VarLenNumberIO.readPositiveVarInt(buffer)));
		teamMember.getStats().applyValues(buffer);
		teamMember.getPosition().apply(buffer);
		teamMember.getTargetPosition().apply(buffer);
	}
}
