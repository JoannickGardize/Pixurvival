package com.pixurvival.core.tag;

import com.pixurvival.core.World;
import com.pixurvival.core.util.IndexMap;

public interface TagHolder {

    IndexMap<TagInstance> getTags();

    long getId();
    
    World getWorld();
}
