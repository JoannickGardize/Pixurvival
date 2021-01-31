package com.pixurvival.core.system;

import com.pixurvival.core.World;
import com.pixurvival.core.system.interest.Interest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class BaseSystem implements Interest {

	private World world;

	/**
	 * Called before {@link #initialize()} to check if this system is required for
	 * this world run. If not, this system will be removed from the world. True by
	 * default.
	 * 
	 * @return true if this system is required for the starting world run, false
	 *         otherwise.
	 */
	public boolean isRequired() {
		return true;
	}

	public void initialize() {
		world.getInterestSubscriptionSet().subscribeAll(this);
	}

	public void accept(SystemData data) {
		// No data by default
	}
}
