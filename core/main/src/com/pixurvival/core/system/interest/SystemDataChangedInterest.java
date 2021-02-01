package com.pixurvival.core.system.interest;

import com.pixurvival.core.system.SystemData;

public interface SystemDataChangedInterest extends Interest {

	void dataChanged(SystemData data);
}
