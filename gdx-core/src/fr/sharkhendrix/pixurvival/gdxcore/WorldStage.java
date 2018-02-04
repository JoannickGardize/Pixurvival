package fr.sharkhendrix.pixurvival.gdxcore;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import fr.sharkhendrix.pixurvival.core.World;
import lombok.Getter;

public class WorldStage extends Stage {

	@Getter
	private World world;

	public WorldStage() {
		super(new FitViewport(20, 15));
	}

	public void setWorld(World world) {
		this.clear();
		this.world = world;
		this.addActor(new EntitiesActor(world.getEntityPool()));
	}
}
