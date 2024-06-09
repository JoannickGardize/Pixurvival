package com.pixurvival.core.livingEntity.tag;

import com.pixurvival.core.Action;
import com.pixurvival.core.World;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.LivingEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RemoveTagAction implements Action {

    private long entityId;
    private int tagId;
    private int modCount;

    @Override
    public void perform(World world) {
        LivingEntity entity = world.getEntityPool().get(EntityGroup.CREATURE, entityId);
        if (entity == null) {
            // Maybe it's ad dead player
            entity = world.getPlayerEntities().get(entityId);
        }
        if (entity == null) {
            // It's a dead creature,
            // or it's a stored creature and the remove will occur in the applyUpdate method,
            // nothing to do.
            return;
        }
        TagInstance tagInstance = entity.getTags().get(tagId);
        if (tagInstance.getModCount() != modCount) {
            return;
        }
        entity.removeTag(tagId);
    }
}
