package com.pixurvival.core.livingEntity;

import com.pixurvival.core.contentPack.tag.TagValue;
import com.pixurvival.core.tag.TagInstance;
import com.pixurvival.core.util.IndexMap;
import com.pixurvival.core.util.Serializer;

import java.nio.ByteBuffer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Proxy class of creature's tag map.<br/>
 * Until any tag change occurs, use the static map from the content pack,
 * to avoid creating mutable IndexMaps for each creature without the knowledge of its usefulness.
 */
public class CreatureDefaultTagMapProxy extends IndexMap<TagInstance> {

    private CreatureEntity entity;
    private IndexMap<TagInstance> map;

    public CreatureDefaultTagMapProxy(CreatureEntity e) {
        entity = e;
        map = e.getWorld().getContentPack().getDefaultCreatureTagMaps().get(e.getDefinition().getId());
    }

    @Override
    public TagInstance put(int key, TagInstance value) {
        return createRegularMap().put(key, value);
    }

    @Override
    public TagInstance get(int key) {
        return map.get(key);
    }

    @Override
    public TagInstance remove(int key) {
        return createRegularMap().remove(key);
    }

    @Override
    public void forEachValues(Consumer<? super TagInstance> action) {
        map.forEachValues(action);
    }

    @Override
    public TagInstance merge(int key, TagInstance value, BiFunction<? super TagInstance, ? super TagInstance, ? extends TagInstance> remappingFunction) {
        return null;
    }

    @Override
    public void write(ByteBuffer buffer, Serializer<TagInstance> valueSerializer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TagInstance captureValueChange(int index, TagInstance value) {
        IndexMap<TagInstance> regularMap = createRegularMap();
        return regularMap.get(index);
    }

    private IndexMap<TagInstance> createRegularMap() {
        // TODO create and IndexMap.clone for this ? (so contentpack stores a prototype)
        IndexMap<TagInstance> regularMap = IndexMap.create(entity.getWorld().getContentPack().getTags().size() - 1);
        for (TagValue tv : entity.getDefinition().getTags()) {
            regularMap.put(tv.getTag().getId(), new TagInstance(tv.getValue()));
        }
        entity.setTags(regularMap);
        return regularMap;
    }
}
