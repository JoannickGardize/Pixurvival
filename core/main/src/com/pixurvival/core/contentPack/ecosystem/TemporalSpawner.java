package com.pixurvival.core.contentPack.ecosystem;

import java.io.Serializable;

import com.pixurvival.core.contentPack.DoubleInterval;
import com.pixurvival.core.contentPack.IntegerInterval;
import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.creature.Creature;

public class TemporalSpawner implements Serializable {

	private static final long serialVersionUID = 1L;

	private DoubleInterval countdown = new DoubleInterval();

	private CountdownType countdownType = CountdownType.ALL_THE_TIME;

	private boolean multiplyByNumberOfPlayers = true;

	private boolean repeat = true;

	private double numberOfCreatureModifier = 1;

	private IntegerInterval initialNumberOfCreature = new IntegerInterval();

	private WeightedValueProducer<Creature> creatureChooser = new WeightedValueProducer<>();
}
