package com.pixurvival.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ActionTimer implements Comparable<ActionTimer> {

    private Action action;
    private long actionTimeMillis;

    @Override
    public int compareTo(ActionTimer o) {
        return (int) (actionTimeMillis - o.actionTimeMillis);
    }
}
