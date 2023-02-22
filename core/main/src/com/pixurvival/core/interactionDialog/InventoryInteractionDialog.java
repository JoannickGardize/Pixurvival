package com.pixurvival.core.interactionDialog;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.PlayerInventoryInteractions;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.WorldKryo;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamMemberSerialization;
import com.pixurvival.core.util.ByteBufferUtils;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Dialog that contains an inventory.
 *
 * @author SharkHendrix
 */
@Getter
public class InventoryInteractionDialog extends InteractionDialog implements InventoryListener {

    public static final int FILL_ACTION_INDEX = -1;
    public static final int EMPTY_ACTION_INDEX = -2;

    private Inventory inventory;

    public InventoryInteractionDialog(InteractionDialogHolder owner, Inventory inventory) {
        super(owner);
        this.inventory = inventory;
        inventory.addListener(this);
    }

    @Override
    public void interact(PlayerEntity player, int index, boolean splitMode) {
        if (index == FILL_ACTION_INDEX) {
            PlayerInventoryInteractions.fill(player.getInventory(), inventory);
        } else if (index == EMPTY_ACTION_INDEX) {
            PlayerInventoryInteractions.fill(inventory, player.getInventory());
        } else {
            PlayerInventoryInteractions.interact(player.getInventory(), inventory, index, splitMode);
        }
    }

    @Override
    public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
        notifyChanged();
    }

    @Override
    public void set(InteractionDialog other) {
        inventory.set(((InventoryInteractionDialog) other).getInventory());
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<InventoryInteractionDialog> {

        @Override
        public void write(Kryo kryo, Output output, InventoryInteractionDialog object) {
            ByteBuffer bb = ByteBufferUtils.asByteBuffer(output);
            TeamMemberSerialization.write(bb, object.getOwner(), false);
            output.setPosition(bb.position());
            kryo.writeObject(output, object.getInventory());
        }

        @Override
        public InventoryInteractionDialog read(Kryo kryo, Input input, Class<InventoryInteractionDialog> type) {
            ByteBuffer bb = ByteBufferUtils.asByteBuffer(input);
            TeamMember owner = TeamMemberSerialization.read(bb, ((WorldKryo) kryo).getWorld(), false);
            input.setPosition(bb.position());
            if (owner instanceof InteractionDialogHolder) {
                return new InventoryInteractionDialog((InteractionDialogHolder) owner, kryo.readObject(input, Inventory.class));
            } else {
                return new InventoryInteractionDialog(null, kryo.readObject(input, Inventory.class));
            }
        }
    }
}
