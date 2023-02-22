package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.VarLenNumberIO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.ByteBuffer;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeconstructAbilityData extends WorkAbilityData {

    private StructureEntity structure;

    public void setStructure(StructureEntity structure) {
        this.structure = structure;
        setDurationMillis(structure.getDefinition().getDeconstructionDuration());
    }

    @Override
    public void write(ByteBuffer buffer, LivingEntity entity) {
        ByteBufferUtils.writePastTime(buffer, entity.getWorld(), getStartTimeMillis());
        if (structure == null) {
            buffer.put((byte) -1);
        } else {
            buffer.put((byte) 0);
            VarLenNumberIO.writeVarInt(buffer, structure.getTileX());
            VarLenNumberIO.writeVarInt(buffer, structure.getTileY());
        }
    }

    @Override
    public void apply(ByteBuffer buffer, LivingEntity entity) {
        setStartTimeMillis(ByteBufferUtils.readPastTime(buffer, entity.getWorld()));
        if (buffer.get() == -1) {
            structure = null;
        } else {
            StructureEntity mapStructure = entity.getWorld().getMap().tileAt(VarLenNumberIO.readVarInt(buffer), VarLenNumberIO.readVarInt(buffer)).getStructure();
            setStructure(mapStructure);
        }
    }

}
