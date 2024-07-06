package com.pixurvival.core.map;

import com.pixurvival.core.Action;
import com.pixurvival.core.World;
import com.pixurvival.core.map.chunk.update.RemoveStructureUpdate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Collections;

@NoArgsConstructor
@AllArgsConstructor
public class RemoveDurationStructureAction implements Action {

    private int x;
    private int y;
    private long id;

    @Override
    public void perform(World world) {
        MapTile tile = world.getMap().tileAt(x, y);
        if (tile instanceof TileAndStructure) {
            StructureEntity mapStructure = ((TileAndStructure) tile).getStructure();
            long duration = mapStructure.getDefinition().getDuration();
            if (mapStructure.getId() == id && duration > 0 && world.getTime().getTimeMillis() - mapStructure.getCreationTime() >= duration) {
                mapStructure.getChunk().removeStructure(x, y);
            }
        } else {
            world.getMap().applyUpdate(Collections.singleton(new RemoveStructureUpdate(x, y, id)));
        }
    }

}
