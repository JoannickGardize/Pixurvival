package com.pixurvival.core.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerInformation {

    private long id;
    private String name;
    private int roleId;
}
