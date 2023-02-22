package com.pixurvival.core.contentPack.item;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.*;
import com.pixurvival.core.item.ItemStack;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ItemCraft extends NamedIdentifiedElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Positive
    private long duration;

    @Valid
    private ItemStack result = new ItemStack();

    @Valid
    @Unique
    private List<ItemStack> recipes = new ArrayList<>();

    @ElementReference
    private List<Item> discoveryItems = new ArrayList<>();

    @Nullable
    @ElementReference
    private Structure requiredStructure;

    public boolean discover(Set<Item> discoveredItems) {
        for (Item item : discoveryItems) {
            if (!discoveredItems.contains(item)) {
                return false;
            }
        }
        return true;
    }
}
