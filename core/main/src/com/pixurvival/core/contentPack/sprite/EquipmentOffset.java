package com.pixurvival.core.contentPack.sprite;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EquipmentOffset extends NamedIdentifiedElement implements Serializable {

    private static final long serialVersionUID = 1L;

    // TODO 2D array instead of frames
    @Valid
    @Length(min = 1)
    private List<FrameOffset> frameOffsets = new ArrayList<>();

}
