package com.pixurvival.core.alteration;

import com.pixurvival.core.livingEntity.stats.StatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatMultiplier implements Serializable {

    private static final long serialVersionUID = 1L;

    private StatType statType;
    private float multiplier;
}
