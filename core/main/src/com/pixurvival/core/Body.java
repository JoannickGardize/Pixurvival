package com.pixurvival.core;

/**
 * An interface that represents a 'physical body' with a position and a bounding box.
 * The purpose of this interface is to provide an abstraction for any object that needs to interact with others,
 * based on its position and/or its bounding box.
 *
 * @author SharkHendrix
 */
public interface Body extends Positionnable {

    /**
     * @return half of the bounding box width
     */
    float getHalfWidth();

    /**
     * @return half of the bounding box height
     */
    float getHalfHeight();

    default float getDisplayDeath() {
        return getPosition().getY();
    }
}
