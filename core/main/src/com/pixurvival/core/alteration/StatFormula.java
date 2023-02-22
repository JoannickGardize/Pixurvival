package com.pixurvival.core.alteration;

import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a formula depending on a {@link StatSet}. It is formed by a base
 * value, a list of {@link StatMultiplier}, and a random value.
 *
 * @author SharkHendrix
 */
@Getter
@Setter
public class StatFormula implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Useful to reference this StatAmount in translation texts.
     */
    private long id = -1;

    private float base;

    @Valid
    private List<StatMultiplier> statMultipliers = new ArrayList<>();

    private float randomValue;

    /**
     * Returns the value of this StatAmount for the given StatSet, the value will be
     * set to zero if the result is negative.
     *
     * @param statSet
     * @return
     */
    public float getValue(StatSet statSet, Random random) {
        float result = base;
        for (StatMultiplier multiplier : statMultipliers) {
            result += statSet.getValue(multiplier.getStatType()) * multiplier.getMultiplier();
        }
        if (randomValue != 0f) {
            result += random.nextFloat() * randomValue;
        }
        return result;
    }

    public float getValue(TeamMember sourceProvider) {
        return getValue(sourceProvider.getStats(), sourceProvider.getWorld().getRandom());
    }
}
