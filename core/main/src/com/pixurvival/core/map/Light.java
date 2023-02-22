package com.pixurvival.core.map;

import com.pixurvival.core.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Light {

    private Vector2 position;
    private float radius;
}
