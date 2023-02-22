package com.pixurvival.core.contentPack.validation.annotation;

import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum AnimationTemplateRequirementSet {
    CHARACTER(new ActionAnimation[][]{
            {ActionAnimation.MOVE_RIGHT, ActionAnimation.MOVE_UP, ActionAnimation.MOVE_LEFT, ActionAnimation.MOVE_DOWN, ActionAnimation.STAND_RIGHT, ActionAnimation.STAND_UP,
                    ActionAnimation.STAND_LEFT, ActionAnimation.STAND_DOWN, ActionAnimation.WORK_RIGHT, ActionAnimation.WORK_UP, ActionAnimation.WORK_LEFT, ActionAnimation.WORK_DOWN}}),
    DEFAULT(new ActionAnimation[][]{{ActionAnimation.DEFAULT}}),
    DEFAULT_OR_BEFORE_DEFAULT(new ActionAnimation[][]{{ActionAnimation.DEFAULT}, {ActionAnimation.BEFORE_DEFAULT}});

    private String toString;

    @Getter
    private ActionAnimation[][] actionAnimations;

    private AnimationTemplateRequirementSet(ActionAnimation[][] actionAnimations) {
        this.actionAnimations = actionAnimations;
        toString = String.join(" OR ", Arrays.stream(actionAnimations).map(Arrays::toString).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return toString;
    }

    public boolean test(Set<ActionAnimation> actionSet) {
        for (ActionAnimation[] andGroup : actionAnimations) {
            boolean ok = true;
            for (ActionAnimation andElement : andGroup) {
                if (!actionSet.contains(andElement)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return true;
            }
        }
        return false;
    }
}
