package com.pixurvival.core.system.mapLimits;

import com.pixurvival.core.system.interest.Interest;

// TODO Generic data change interest instead
public interface MapLimitsAnchorInterest extends Interest {

	void anchorChanged(MapLimitsSystemData data);
}
