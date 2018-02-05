package fr.sharkhendrix.pixurvival.core;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

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
	public void writeUpdate(Output output) {
		output.writeDouble(getPosition().x);
		output.writeDouble(getPosition().y);
		output.writeDouble(getHealth());
		output.writeDouble(getMovingAngle());
		output.writeBoolean(isForward());
	}

	@Override
	public void applyUpdate(Input input) {
		getPosition().set(input.readDouble(), input.readDouble());
		setHealth(input.readDouble());
		setMovingAngle(input.readDouble());
		setForward(input.readBoolean());
	}

}
