package com.pixurvival.core.map;

import java.nio.ByteBuffer;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.alteration.DamageAttributes;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.update.DamageableStructureUpdate;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamSet;
import com.pixurvival.core.util.LongSequenceIOHelper;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

public class DamageableMapStructure extends MapStructure implements TeamMember, Damageable {

	private @Getter @Setter float health;

	public DamageableMapStructure(Chunk chunk, Structure definition, int x, int y) {
		super(chunk, definition, x, y);
		health = definition.getMaxHealth();
	}

	@Override
	public void writeData(ByteBuffer buffer, LongSequenceIOHelper idSequence) {
		super.writeData(buffer, idSequence);
		buffer.putFloat(health);
	}

	@Override
	public void applyData(ByteBuffer buffer, LongSequenceIOHelper idSequence) {
		super.applyData(buffer, idSequence);
		health = buffer.getFloat();
	}

	@Override
	public float getMaxHealth() {
		return getDefinition().getMaxHealth();
	}

	@Override
	public void takeDamage(float amount, DamageAttributes attributes) {
		health -= amount;
		if (health < 0) {
			health = 0;
			getChunk().removeStructure(getTileX(), getTileY());
		} else {
			getChunk().getMap().notifyListeners(l -> l.structureChanged(this, new DamageableStructureUpdate(getTileX(), getTileY(), getId(), health)));
		}
	}

	@Override
	public StatSet getStats() {
		// TODO Empty statSet proxy
		return new StatSet();
	}

	@Override
	public Vector2 getTargetPosition() {
		return getPosition();
	}

	@Override
	public TeamMember getOrigin() {
		return this;
	}

	@Override
	public boolean isAlive() {
		return false;
	}

	@Override
	public Team getTeam() {
		return TeamSet.WILD_TEAM;
	}
}
