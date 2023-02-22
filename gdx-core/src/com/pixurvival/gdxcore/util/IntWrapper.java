package com.pixurvival.gdxcore.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntWrapper {
    private int value;

    public void increment() {
        ++value;
    }
}
