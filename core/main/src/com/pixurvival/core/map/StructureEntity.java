package com.pixurvival.core.map;

import java.nio.ByteBuffer;

import com.pixurvival.core.Body;
import com.pixurvival.core.CustomDataHolder;
import com.pixurvival.core.Damageable;
import com.pixurvival.core.World;
import com.pixurvival.core.alteration.DamageAttributes;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.update.DamageableStructureUpdate;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamSet;
import com.pixurvival.core.util.LongSequenceIOHelper;
import com.pixurvival.core.util.VarLenNumberIO;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
public class StructureEntity implements Body, CustomDataHolder, TeamMember, Damageable {

	@FunctionalInterface
	private static interface StructureSupplier {
		StructureEntity apply(Chunk chunk, Structure structure, int x, int y);
	}

	private @Setter long id;
	private Chunk chunk;
	private Structure definition;
	private int tileX;
	private int tileY;
	private Vector2 position;
	private @Setter long creationTime;
	private @Setter Object customData;
	private @Getter @Setter float health;

	public StructureEntity(Chunk chunk, Structure definition, int x, int y) {
		this.chunk = chunk;
		this.definition = definition;
		tileX = x;
		tileY = y;
		position = new Vector2(x + definition.getDimensions().getWidth() / 2f, y + definition.getDimensions().getHeight() / 2f);
		health = definition.getMaxHealth();
	}

	public void initiliazeNewlyCreated() {
		World world = chunk.getMap().getWorld();
		if (world.isServer()) {
			id = chunk.getMap().nextStructureId();
			if (definition.getDuration() > 0) {
				creationTime = chunk.getMap().getWorld().getTime().getTimeMillis();
				world.getActionTimerManager().addActionTimer(new RemoveDurationStructureAction(tileX, tileY, id), definition.getDuration());
			}
		}
	}

	@Override
	public float getHalfWidth() {
		return definition.getDimensions().getWidth() / 2f;
	}

	@Override
	public float getHalfHeight() {
		return definition.getDimensions().getHeight() / 2f;
	}

	public int getWidth() {
		return definition.getDimensions().getWidth();
	}

	public int getHeight() {
		return definition.getDimensions().getHeight();
	}

	/**
	 * Write data for chunk serialization
	 * 
	 * @param buffer
	 */
	public void writeData(ByteBuffer buffer, LongSequenceIOHelper idSequence) {
		idSequence.write(buffer, id);
		if (definition.getDuration() > 0) {
			VarLenNumberIO.writePositiveVarLong(buffer, (getChunk().getUpdateTimestamp() - creationTime));
		}
		if (definition.getMaxHealth() > 0) {
			buffer.putFloat(health);
		}
	}

	/**
	 * Read data from serialized chunk
	 * 
	 * @param buffer
	 */
	public void applyData(ByteBuffer buffer, LongSequenceIOHelper idSequence) {
		id = idSequence.read(buffer);
		if (definition.getDuration() > 0) {
			creationTime = getChunk().getUpdateTimestamp() - VarLenNumberIO.readPositiveVarLong(buffer);
		}
		if (definition.getMaxHealth() > 0) {
			health = buffer.getFloat();
		}
	}

	public void onDeath() {
		// Nothing by default
	}

	@Override
	public World getWorld() {
		return getChunk().getMap().getWorld();
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
			onDeath();
		} else {
			getChunk().notifyStructureChanged(this, new DamageableStructureUpdate(getTileX(), getTileY(), getId(), health));
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
		return getChunk().tileAt(tileX, tileY).getStructure() == this;
	}

	@Override
	public Team getTeam() {
		return TeamSet.WILD_TEAM;
	}
}
