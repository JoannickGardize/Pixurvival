package com.pixurvival.core.logic;

import com.pixurvival.core.contentPack.tag.Tag;
import com.pixurvival.core.contentPack.tag.TagValue;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.tag.RemoveTagAction;
import com.pixurvival.core.tag.TagInstance;
import com.pixurvival.core.util.IndexMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TagLogic {

    public static void applyTag(LivingEntity entity, TagValue tagValue, long duration) {
        Tag tag = tagValue.getTag();
        int id = tag.getId();
        IndexMap<TagInstance> tags = entity.getTags();
        TagInstance tagInstance = tags.get(id);
        if (tagInstance == null) {
            tagInstance = new TagInstance(tagValue.getValue());
            tagInstance.setModCount(entity.nextTagInstanceModCount());
            tags.put(id, tagInstance);
            if (duration > 0) {
                tagInstance.setExpirationTime(entity.getWorld().getActionTimerManager().addActionTimer(
                        new RemoveTagAction(entity.getId(), id, entity.getTagInstanceModCount()), duration));
            }
        } else {
            switch (tag.getValueStackPolicy()) {
                case ADD:
                    if (tagValue.getValue() != 0) {
                        tagInstance = tags.captureValueChange(id, tagInstance);
                        tagInstance.setValue(tagInstance.getValue() + tagValue.getValue());
                        tagInstance.setModCount(entity.nextTagInstanceModCount());
                    }
                    break;
                case REPLACE:
                    if (tagValue.getValue() != tagInstance.getValue()) {
                        tagInstance = tags.captureValueChange(id, tagInstance);
                        tagInstance.setValue(tagInstance.getValue() + tagValue.getValue());
                        tagInstance.setModCount(entity.nextTagInstanceModCount());
                    }
                    break;
            }
            switch (tag.getDurationStackPolicy()) {
                case ADD:
                    if (duration != 0) {
                        tagInstance = tags.captureValueChange(id, tagInstance);
                        tagInstance.setModCount(entity.nextTagInstanceModCount());
                        tagInstance.setExpirationTime(entity.getWorld().getActionTimerManager().addActionTimer(
                                new RemoveTagAction(entity.getId(), id, tagInstance.getModCount()),
                                tagInstance.getExpirationTime() - entity.getWorld().getTime().getTimeMillis() + duration));
                    }
                    break;
                case REPLACE:
                    // Unchanged case not checked because it's highly improbable
                    tagInstance = tags.captureValueChange(id, tagInstance);
                    tagInstance.setModCount(entity.nextTagInstanceModCount());
                    tagInstance.setExpirationTime(entity.getWorld().getActionTimerManager().addActionTimer(
                            new RemoveTagAction(entity.getId(), id, tagInstance.getModCount()), duration));
                    break;
            }
        }
    }
}
