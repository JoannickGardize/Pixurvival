package fr.sharkhendrix.pixurvival.core;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import fr.sharkhendrix.pixurvival.core.util.Vector2;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Entity {

	private @Setter long id;
	private @Setter(AccessLevel.PACKAGE) World world;
	private Vector2 position = new Vector2();
	private @Setter boolean alive = true;

	public abstract void update();

	public abstract EntityGroup getGroup();

	public abstract double getBoundingRadius();

	public abstract void writeUpdate(Output output);

	public abstract void applyUpdate(Input input);

}
