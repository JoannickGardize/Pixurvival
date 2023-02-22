package com.pixurvival.core.contentPack.item;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.ItemStack;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemReward extends NamedIdentifiedElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Data
    public static class Entry implements Serializable {

        private static final long serialVersionUID = 1L;

        @Valid
        private ItemStack itemStack = new ItemStack();

        @Bounds(min = 0, max = 1, maxInclusive = true)
        private float probability = 1;
    }

    // TODO Remove this
    private static ThreadLocal<List<ItemStack>> tmpLists = ThreadLocal.withInitial(ArrayList::new);

    @Valid
    private @Getter
    @Setter List<Entry> entries = new ArrayList<>();

    public ItemStack[] produce(Random random) {
        List<ItemStack> result = tmpLists.get();
        result.clear();
        for (Entry entry : entries) {
            if (random.nextFloat() <= entry.getProbability()) {
                result.add(new ItemStack(entry.getItemStack()));
            }
        }
        return result.toArray(new ItemStack[result.size()]);
    }

}
