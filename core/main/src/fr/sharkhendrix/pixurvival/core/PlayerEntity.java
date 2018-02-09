package fr.sharkhendrix.pixurvival.core;

import java.nio.ByteBuffer;

import fr.sharkhendrix.pixurvival.core.message.PlayerActionRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerEntity extends AliveEntity {

	private String name;

	public void apply(PlayerActionRequest actionRequest) {
		setMovingAngle(actionRequest.getDirection().getAngle());
		setForward(actionRequest.isForward());
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public double getMaxHealth() {
		return 100;
	}

	@Override
	public double getSpeedPotential() {
		return 4;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.PLAYER;
	}

	@Override
	public double getBoundingRadius() {
		return 1;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer) {
		buffer.putDouble(getPosition().x);
		buffer.putDouble(getPosition().y);
		buffer.putDouble(getHealth());
		buffer.putDouble(getMovingAngle());
		buffer.put(isForward() ? (byte) 1 : (byte) 0);
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		getPosition().set(buffer.getDouble(), buffer.getDouble());
		setHealth(buffer.getDouble());
		setMovingAngle(buffer.getDouble());
		setForward(buffer.get() == 1 ? true : false);
	}

}
