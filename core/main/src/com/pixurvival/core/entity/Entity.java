package com.pixurvival.core.entity;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
 * Root class of all elements of the game: Players, Creatures, Items, Effects...
 * 
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
	private @Setter ChunkPosition previousUpdateChunkPosition;
	private @Setter Chunk chunk;

	private Vector2 previousPosition = new Vector2();
	private Vector2 position = new Vector2();
	private @Setter boolean alive = true;

	/**
	 * When set to true, the death event will not be fired, resulting to not sharing
	 * to clients the death data. The purpose is to avoid to send death info if the
	 * client can know it by itself
	 */
	private @Setter boolean sneakyDeath = false;
	private @Setter Object customData;

	private float speed = 0;
	private float movingAngle = 0;
	private boolean forward = false;
	private float forwardFactor = 1;
	private Vector2 targetVelocity = new Vector2();
	private Vector2 velocity = new Vector2();
	private boolean velocityChanged = false;
	private boolean collisionLock = false;

	/**
	 * Indicate if the state of this entity has changed, if true, the server will
	 * send data of this entity at the next data send tick to clients that view this
	 * entity. Must be true at initialization to send the new entity data.
	 */
	private @Setter boolean stateChanged = true;

	/**
	 * Always called when added
	 */
	public void initialize() {
		setAlive(true);
		previousPosition.set(position);
	}

	/**
	 * Only called if the entity is "new", after {@link #initialize()}
	 */
	public void initializeAtCreation() {
		// Nothing by default

	}

	public void setMovingAngle(float movingAngle) {
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

	public void setForwardFactor(float forwardFactor) {
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

	protected boolean canForward() {
		return true;
	}

	protected void collisionLockEnded() {

	}

	protected boolean antiCollisionLockEnabled() {
		return true;
	}

	public void update() {

		if (collisionLock) {
			// Currently getting away from collision lock
			if (getWorld().getMap().collide(this)) {
				position.add(targetVelocity.getX() * getWorld().getTime().getDeltaTime(), targetVelocity.getY() * getWorld().getTime().getDeltaTime());
			} else {
				collisionLock = false;
				collisionLockEnded();
			}
		} else if (antiCollisionLockEnabled() && isSolid() && getChunk() != null && getWorld().getMap().collide(this)) {
			// Get away from collision lock
			collisionLock = true;
			if (previousPosition.epsilonEquals(position, MathUtils.EPSILON)) {
				setMovingAngle(getWorld().getRandom().nextAngle());
			} else {
				setMovingAngle(position.angleToward(previousPosition));
			}
			setSpeed(3);
			setForward(true);
			updateVelocity();
			velocity.set(targetVelocity);
			position.add(targetVelocity.getX() * getWorld().getTime().getDeltaTime(), targetVelocity.getY() * getWorld().getTime().getDeltaTime());
			stepX();
			stepY();
		}
		if (!collisionLock) {
			normalPositionUpdate();
		}
		updateChunk();
	}

	protected void normalPositionUpdate() {
		previousPosition.set(position);
		if (forward && canForward()) {
			setSpeed(getSpeedPotential() * forwardFactor);
			updateVelocity();
			stepX();
			stepY();
		} else {
			setSpeed(0);
			velocity.set(0, 0);
		}
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
		float dy = targetVelocity.getY() * getWorld().getTime().getDeltaTime();
		if (isSolid() && getWorld().getMap().collide(this, 0, dy)) {
			if (targetVelocity.getY() > 0) {
				position.setY(MathUtils.ceil(position.getY() + getCollisionRadius()) - getCollisionRadius() - MathUtils.EPSILON);
			} else {
				position.setY(MathUtils.floor(position.getY() - getCollisionRadius()) + getCollisionRadius() + MathUtils.EPSILON);
			}
			velocity.setY(0);
		} else {
			position.addY(dy);
			velocity.setY(targetVelocity.getY());
		}
	}

	private void stepX() {
		float dx = targetVelocity.getX() * getWorld().getTime().getDeltaTime();
		if (isSolid() && getWorld().getMap().collide(this, dx, 0)) {
			if (targetVelocity.getX() > 0) {
				position.setX(MathUtils.ceil(position.getX() + getCollisionRadius()) - getCollisionRadius() - MathUtils.EPSILON);
			} else {
				position.setX(MathUtils.floor(position.getX() - getCollisionRadius()) + getCollisionRadius() + MathUtils.EPSILON);
			}
			velocity.setX(0);
		} else {
			position.addX(dx);
			velocity.setX(targetVelocity.getX());
		}
	}

	public abstract EntityGroup getGroup();

	public abstract float getCollisionRadius();

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
	 * Write data that prequisites a call to {@link this#initialize()}.
	 * 
	 * @param buffer
	 * @param full
	 *            true if all the data should be writen, for clients that discovers
	 *            this entity.
	 */
	public abstract void writeUpdate(ByteBuffer buffer, boolean full);

	public abstract void applyUpdate(ByteBuffer buffer);

	public abstract float getSpeedPotential();

	public abstract boolean isSolid();

	public void writeRepositoryUpdate(ByteBuffer byteBuffer) {
		writeUpdate(byteBuffer, true);
	}

	public void applyRepositoryUpdate(ByteBuffer byteBuffer) {
		applyUpdate(byteBuffer);
	}

	@Override
	public float getHalfWidth() {
		return getCollisionRadius();
	}

	@Override
	public float getHalfHeight() {
		return getCollisionRadius();
	}

	public boolean setSpeed(float speed) {
		if (this.speed != speed) {
			this.speed = speed;
			velocityChanged = true;
			return true;
		}
		return false;
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

	public EntitySearchResult findClosest(EntityGroup group, float maxSquareDistance) {
		return findClosest(group, maxSquareDistance, e -> true);
	}

	public EntitySearchResult findClosest(EntityGroup group, float maxSquareDistance, Predicate<Entity> filter) {
		TiledMap map = world.getMap();

		EntitySearchResult searchResult = new EntitySearchResult();
		map.forEachChunk(position, maxSquareDistance, (Consumer<Chunk>) c -> c.getEntities().get(group).forEach(e -> {
			if (!filter.test(e)) {
				return;
			}
			float distance = distanceSquared(e);
			if (distance < searchResult.getDistanceSquared()) {
				searchResult.setDistanceSquared(distance);
				searchResult.setEntity(e);
			}
		}));
		return searchResult;
	}

	/**
	 * Used to find the closest in all the world. to avoid looping over all
	 * entities, prefer the use of {@link Entity#findClosest(EntityGroup, float)
	 * 
	 * @param group
	 * @param position
	 * @return
	 */
	public Entity findClosest(EntityGroup group) {
		float closestDistanceSquared = Float.POSITIVE_INFINITY;
		Entity closestEntity = null;
		for (Entity e : getWorld().getEntityPool().get(group)) {
			float distanceSquared = e.distanceSquared(this);
			if (e != this && distanceSquared < closestDistanceSquared) {
				closestDistanceSquared = distanceSquared;
				closestEntity = e;
			}
		}
		return closestEntity;
	}

	public float distanceSquared(Positionnable other) {
		return position.distanceSquared(other.getPosition());
	}

	public float nullSafeDistanceSquared(Entity other) {
		return other == null ? Float.POSITIVE_INFINITY : position.distanceSquared(other.position);
	}

	public float distanceSquared(Vector2 position) {
		return this.position.distanceSquared(position);
	}

	public float angleToward(Positionnable other) {
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

	public boolean isInvisible() {
		return false;
	}
}
