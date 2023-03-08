package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileAlterationCondition implements AlterationCondition {

    private ElementSet<Tile> tiles = new AllElementSet<>();

    @Override
    public boolean test(TeamMember entity) {
        return tiles.contains(entity.getWorld().getMap().tileAt(entity.getPosition()).getTileDefinition());
    }
}
