package com.pixurvival.core.entity;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import com.pixurvival.core.Body;
import com.pixurvival.core.CustomDataHolder;
import com.pixurvival.core.Positionnable;
import com.pixurvival.core.World;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.util.Collisions;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Vector2;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe mère de tout objet du monde : joueur, créatures, items, projectiles...
 * 
 * 
 * @author SharkHendrix
 *
 */
@Getter
@EqualsAndHashCode(of = "id")
public abstract class Entity implements Body, CustomDataHolder {

	private @Setter long id;
	private @Setter World world;
	private @Setter Chunk chunk;
	private Vector2 previousPosition = new Vector2();
	private Vector2 position = new Vector2();
	private @Setter boolean alive = true;
	private @Setter Object customData;

	private double speed = 0;
	private double movingAngle = 0;
	private boolean forward = false;
	private double forwardFactor = 1;
	private Vector2 targetVelocity = new Vector2();
	private Vector2 velocity = new Vector2();
	private boolean velocityChanged = false;

	/**
	 * Indicate if the state of this entity has changed, if true, the server will
	 * send data of this entity at the next data send tick to clients that view this
	 * entity. Must be true at initialization to send the new entity data.
	 */
	private @Setter boolean stateChanged = true;

	public abstract void initialize();

	public void setMovingAngle(double movingAngle) {
		if (this.movingAngle != movingAngle) {
			this.movingAngle = movingAngle;
			velocityChanged = true;
		}
	}

	public void setForward(boolean forward) {
		if (this.forward != forward) {
			this.forward = forward;
			velocityChanged = true;
		}
	}

	public void setForwardFactor(double forwardFactor) {
		if (forwardFactor != this.forwardFactor) {
			this.forwardFactor = forwardFactor;
			velocityChanged = true;
		}
	}

	public void setVelocityDirect(Vector2 components) {
		speed = components.length();
		movingAngle = components.angle();
		targetVelocity.set(components);
		velocityChanged = false;
	}

	public void setMovementSameAs(Entity entity) {
		speed = entity.speed;
		forward = entity.forward;
		velocityChanged = false;
		targetVelocity.set(entity.velocity);
		movingAngle = entity.movingAngle;
	}

	public void update() {

		previousPosition.set(position);

		if (forward) {
			setSpeed(getSpeedPotential() * forwardFactor);
			updateVelocity();
			stepX();
			stepY();
		} else {
			setSpeed(0);
			velocity.set(0, 0);
		}
		updateChunk();
	}

	public void updateChunk() {
		// Update current chunk
		if (chunk == null) {
			chunk = getWorld().getMap().chunkAt(position.getX(), position.getY());
			if (chunk != null) {
				chunk.getEntities().add(this);
				setStateChanged(true);
				getWorld().getMap().notifyChangedChunk(null, this);
			}
		} else {
			ChunkPosition previousChunkPosition = chunk.getPosition();
			ChunkPosition newChunkPosition = previousChunkPosition.createIfDifferent(position);
			if (newChunkPosition != previousChunkPosition) {
				chunk.getEntities().remove(this);
				Chunk newChunk = getWorld().getMap().chunkAt(newChunkPosition);
				if (newChunk != null) {
					chunk = newChunk;
					chunk.getEntities().add(this);
					setStateChanged(true);
					getWorld().getMap().notifyChangedChunk(previousChunkPosition, this);
				}
			}
		}
	}

	protected void onDeath() {

	}

	private void stepY() {
		double dy = targetVelocity.getY() * getWorld().getTime().getDeltaTime();
		if (isSolid() && getWorld().getMap().collide(this, 0, dy)) {
			if (targetVelocity.getY() > 0) {
				position.setY(MathUtils.ceil(position.getY() + getCollisionRadius()) - getCollisionRadius());
			} else {
				position.setY(MathUtils.floor(position.getY() - getCollisionRadius()) + getCollisionRadius());
			}
			velocity.setY(0);
		} else {
			position.addY(dy);
			velocity.setY(targetVelocity.getY());
		}
	}

	private void stepX() {
		double dx = targetVelocity.getX() * getWorld().getTime().getDeltaTime();
		if (isSolid() && getWorld().getMap().collide(this, dx, 0)) {
			if (targetVelocity.getX() > 0) {
				position.setX(MathUtils.ceil(position.getX() + getCollisionRadius()) - getCollisionRadius());
			} else {
				position.setX(MathUtils.floor(position.getX() - getCollisionRadius()) + getCollisionRadius());
			}
			velocity.setX(0);
		} else {
			position.addX(dx);
			velocity.setX(targetVelocity.getX());
		}
	}

	public abstract EntityGroup getGroup();

	public abstract double getCollisionRadius();

	/**
	 * Write data required before a call to {@link this#initialize()}.
	 * 
	 * @param buffer
	 */
	public void writeInitialization(ByteBuffer buffer) {
		// Nothing by default
	}

	/**
	 * Apply data required before a call to {@link this#initialize()}.
	 * 
	 * @param buffer
	 */
	public void applyInitialization(ByteBuffer buffer) {
		// Nothing by default
	}

	/**
	 * Write data that prequisites a call {@link this#initialize()}.
	 * 
	 * @param buffer
	 */
	public abstract void writeUpdate(ByteBuffer buffer);

	public abstract void applyUpdate(ByteBuffer buffer);

	public abstract double getSpeedPotential();

	public abstract boolean isSolid();

	@Override
	public double getHalfWidth() {
		return getCollisionRadius();
	}

	@Override
	public double getHalfHeight() {
		return getCollisionRadius();
	}

	public void setSpeed(double speed) {
		if (this.speed != speed) {
			this.speed = speed;
			velocityChanged = true;
		}
	}

	public void updateVelocity() {
		if (velocityChanged) {
			targetVelocity.setFromEuclidean(speed, movingAngle);
			velocityChanged = false;
		}
	}

	// *******************
	// * Utility methods *
	// *******************

	public EntitySearchResult findClosest(EntityGroup group, double maxSquareDistance) {
		TiledMap map = world.getMap();

		EntitySearchResult searchResult = new EntitySearchResult();
		map.forEachChunk(position, maxSquareDistance, (Consumer<Chunk>) c -> c.getEntities().get(group).forEach(e -> {
			double distance = distanceSquared(e);
			if (distance < searchResult.getDistanceSquared()) {
				searchResult.setDistanceSquared(distance);
				searchResult.setEntity(e);
			}
		}));
		return searchResult;
	}

	/**
	 * Used to find the closest in all the world. to avoid looping over all
	 * entities, prefer the use of {@link Entity#findClosest(EntityGroup, double)
	 * 
	 * @param group
	 * @param position
	 * @return
	 */
	public Entity findClosest(EntityGroup group) {
		double closestDistanceSquared = Double.POSITIVE_INFINITY;
		Entity closestEntity = null;
		for (Entity e : getWorld().getEntityPool().get(group)) {
			double distanceSquared = e.distanceSquared(this);
			if (e != this && distanceSquared < closestDistanceSquared) {
				closestDistanceSquared = distanceSquared;
				closestEntity = e;
			}
		}
		return closestEntity;
	}

	public double distanceSquared(Entity other) {
		return position.distanceSquared(other.position);
	}

	public double nullSafeDistanceSquared(Entity other) {
		return other == null ? Double.POSITIVE_INFINITY : position.distanceSquared(other.position);
	}

	public double distanceSquared(Vector2 position) {
		return this.position.distanceSquared(position);
	}

	public double angleToward(Positionnable other) {
		return position.angleToward(other.getPosition());
	}

	public boolean collide(Entity other) {
		return Collisions.circleCircle(position, getCollisionRadius(), other.position, other.getCollisionRadius());
	}

	public boolean collideDynamic(Entity other) {
		return Collisions.dynamicCircleCircle(position, getCollisionRadius(), velocity.copy().mul(world.getTime().getDeltaTime()), other.position, other.getCollisionRadius());
	}

	public ChunkPosition chunkPosition() {
		if (chunk == null) {
			return ChunkPosition.fromWorldPosition(position);
		} else {
			return chunk.getPosition();
		}
	}
}
