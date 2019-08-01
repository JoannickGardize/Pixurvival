package com.pixurvival.core.contentPack.ecosystem;

import java.io.Serializable;

import com.pixurvival.core.ActionTimer;
import com.pixurvival.core.contentPack.IntegerInterval;
import com.pixurvival.core.contentPack.LongInterval;
import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.creature.Creature;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreaturesWave implements Serializable {

	private static final long serialVersionUID = 1L;

	private CountdownType countdownType = CountdownType.ALL_THE_TIME;

	private LongInterval countdown = new LongInterval();

	private double maxDistanceToPlayer;

	private long individualInterval;

	private IntegerInterval initialNumberOfCreature = new IntegerInterval();

	private boolean repeat = true;

	private double numberOfCreatureIncrement = 1;

	private boolean multiplyByNumberOfPlayers = true;

	private WeightedValueProducer<Creature> creatureChooser = new WeightedValueProducer<>();

	public ActionTimer getActionTimer() {
		// TODO
		return null;
	}
}
