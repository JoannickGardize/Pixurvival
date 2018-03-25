package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.World;

import lombok.Getter;
import lombok.Setter;

public abstract class WorkActivity extends Activity {
	private @Getter PlayerEntity entity;
	private @Getter @Setter double progressTime;
	private @Getter double duration;

	public WorkActivity(PlayerEntity entity, double duration) {
		this.entity = entity;
		progressTime = 0;
		this.duration = duration;
	}

	public double getProgress() {
		return progressTime / duration;
	}

	@Override
	public boolean canMove() {
		return false;
	}

	@Override
	public void update() {
		World world = entity.getWorld();
		progressTime += world.getTime().getDeltaTime();
		if (progressTime >= duration) {
			entity.setActivity(Activity.NONE);
			onFinished();
		}
	}

	public abstract void onFinished();
}
