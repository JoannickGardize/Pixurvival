package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.interactionDialog.InteractionDialogHolder;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.logic.ActionPreconditions;
import com.pixurvival.core.map.HarvestableStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.system.interest.InteractionDialogRequestInterest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InteractStructureRequest implements IPlayerActionRequest {

    private int x;
    private int y;
    private boolean secondaryAction;

    @Override
    public void apply(PlayerEntity player) {
        StructureEntity structure = player.getWorld().getMap().tileAt(x, y).getStructure();
        if (secondaryAction) {
            if (ActionPreconditions.canDeconstruct(player, structure)) {
                player.deconstruct(structure);
            }
        } else {
            if (ActionPreconditions.canInteract(player, structure)) {
                if (structure instanceof HarvestableStructureEntity) {
                    player.harvest((HarvestableStructureEntity) structure);
                } else if (structure instanceof InteractionDialogHolder) {
                    player.getWorld().getInterestSubscriptionSet().get(InteractionDialogRequestInterest.class)
                            .publish(i -> i.openDialogRequest(player, ((InteractionDialogHolder) structure).getInteractionDialog()));
                } else {
                    player.deconstruct(structure);
                }
            }
        }
    }

    @Override
    public boolean isClientPreapply() {
        return false;
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<InteractStructureRequest> {

        @Override
        public void write(Kryo kryo, Output output, InteractStructureRequest object) {
            output.writeInt(object.x);
            output.writeInt(object.y);
            output.writeBoolean(object.secondaryAction);
        }

        @Override
        public InteractStructureRequest read(Kryo kryo, Input input, Class<InteractStructureRequest> type) {
            return new InteractStructureRequest(input.readInt(), input.readInt(), input.readBoolean());
        }

    }

}
