package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.livingEntity.PlayerEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CraftItemRequest implements IPlayerActionRequest {

    private int craftId;

    // TODO use quantity
    private int quantity;

    @Override
    public void apply(PlayerEntity player) {
        ItemCraft craft = player.getWorld().getContentPack().getItemCrafts().get(craftId);
        player.craft(craft);
    }

    @Override
    public boolean isClientPreapply() {
        return false;
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<CraftItemRequest> {

        @Override
        public void write(Kryo kryo, Output output, CraftItemRequest object) {
            output.writeVarInt(object.craftId, true);
            output.writeVarInt(object.quantity, true);
        }

        @Override
        public CraftItemRequest read(Kryo kryo, Input input, Class<CraftItemRequest> type) {
            return new CraftItemRequest(input.readVarInt(true), input.readVarInt(true));
        }
    }
}
