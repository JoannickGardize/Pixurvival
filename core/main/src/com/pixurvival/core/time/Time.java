package com.pixurvival.core.time;

import com.pixurvival.core.message.TimeSync;
import com.pixurvival.core.system.interest.InterestSubscription;
import com.pixurvival.core.system.interest.InterestSubscriptionSet;
import com.pixurvival.core.system.interest.SecondIntervalInterest;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.VarLenNumberIO;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

public class Time {

    private @Setter
    @Getter long timeMillis = 0;
    private float decimalAccumulator = 0;
    private @Getter DayCycleRun dayCycle;

    private @Getter float deltaTime = 0;
    private @Getter float deltaTimeMillis = 0;
    private long synchronizeTimeCounter = 0;
    private @Getter float averagePing = 0;
    private @Getter long tickCount = 0;
    private float timeDiffAccumulator = 0;
    private long previousSecond = 0;
    private @Getter InterestSubscription<SecondIntervalInterest> timeIntervalSubscription;

    /**
     * Current time to consider for reading / writing relative time based data.
     */
    private @Setter
    @Getter long serializationContextTime;

    public Time(DayCycleRun dayCycle, InterestSubscriptionSet interestSubscriptionSet) {
        this.dayCycle = dayCycle;
        timeIntervalSubscription = interestSubscriptionSet.get(SecondIntervalInterest.class);
    }

    public void update(float deltaTimeMillis) {
        tickCount++;
        this.deltaTimeMillis = deltaTimeMillis;
        deltaTime = deltaTimeMillis / 1000f;
        long integerPart = (long) deltaTimeMillis;
        decimalAccumulator += deltaTimeMillis - integerPart;
        while (decimalAccumulator > 0.5) {
            timeMillis++;
            decimalAccumulator--;
        }
        timeMillis += integerPart;
        dayCycle.update(timeMillis);
        long currentSecond = timeMillis / 1000L;
        while (previousSecond < currentSecond) {
            previousSecond++;
            timeIntervalSubscription.publish(l -> l.tick(1));
        }
    }

    public void synchronizeTime(TimeSync timeResponse) {
        long ping = (timeMillis - timeResponse.getRequesterTime()) / 2;
        if (averagePing == 0) {
            averagePing = ping;
        } else {
            averagePing = MathUtils.linearInterpolate(averagePing, ping, 0.1f);
        }
        long difference = timeResponse.getResponderTime() - timeMillis + ping;
        if (synchronizeTimeCounter < 20) {
            synchronizeTimeCounter++;
        }
        float toAdd = (float) difference / synchronizeTimeCounter + timeDiffAccumulator;
        int toAddInt = (int) toAdd;
        timeDiffAccumulator = toAdd - toAddInt;
        timeMillis += toAddInt;
    }

    public static long secToMillis(float secondes) {
        return (long) (secondes * 1000);
    }

    public void write(ByteBuffer buffer) {
        VarLenNumberIO.writePositiveVarLong(buffer, tickCount);
        buffer.putFloat(decimalAccumulator);
        VarLenNumberIO.writePositiveVarLong(buffer, timeMillis);
        dayCycle.write(buffer);
    }

    public void apply(ByteBuffer buffer) {
        tickCount = VarLenNumberIO.readPositiveVarLong(buffer);
        decimalAccumulator = buffer.getFloat();
        timeMillis = VarLenNumberIO.readPositiveVarLong(buffer);
        previousSecond = timeMillis / 1000L;
        dayCycle.apply(buffer);
    }

    public void setSerializationContextTimeToNow() {
        serializationContextTime = timeMillis;
    }
}
