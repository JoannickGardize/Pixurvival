package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.system.interest.InteractionDialogRequestInterest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogInteractionActionRequest implements IPlayerActionRequest {

    private int slotIndex;
    private boolean splitMode;

    @Override
    public void apply(PlayerEntity player) {
        player.getWorld().getInterestSubscriptionSet().get(InteractionDialogRequestInterest.class).forEach(i -> i.interactDialogRequest(player, slotIndex, splitMode));
    }

    @Override
    public boolean isClientPreapply() {
        return true;
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<DialogInteractionActionRequest> {

        @Override
        public void write(Kryo kryo, Output output, DialogInteractionActionRequest object) {
            output.writeVarInt(object.slotIndex, true);
            output.writeBoolean(object.splitMode);
        }

        @Override
        public DialogInteractionActionRequest read(Kryo kryo, Input input, Class<DialogInteractionActionRequest> type) {
            return new DialogInteractionActionRequest(input.readVarInt(true), input.readBoolean());
        }
    }
}
