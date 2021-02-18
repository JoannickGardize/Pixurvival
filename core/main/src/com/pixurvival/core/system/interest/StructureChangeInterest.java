package com.pixurvival.core.system.interest;

import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.update.StructureUpdate;

public interface StructureChangeInterest extends Interest {

	void structureChanged(StructureEntity structureEntity, StructureUpdate structureUpdate);

	void structureAdded(StructureEntity structureEntity);

	void structureRemoved(StructureEntity structureEntity);
}
