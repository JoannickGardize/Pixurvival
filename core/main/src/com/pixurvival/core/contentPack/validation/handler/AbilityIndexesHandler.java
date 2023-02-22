package com.pixurvival.core.contentPack.validation.handler;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.AbilityIndexes;
import com.pixurvival.core.reflection.visitor.VisitNode;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

public class AbilityIndexesHandler implements AnnotationHandler {

    @Override
    public Collection<Class<? extends Annotation>> getHandledAnnotations() {
        return Collections.singleton(AbilityIndexes.class);
    }

    @Override
    public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
        if (!test((Creature) node.getParent().getObject())) {
            errors.add(node, annotation);
        }
    }

    public static boolean test(Creature creature) {
        if (creature.getBehaviorSet() == null) {
            return true;
        }
        int abilityCount = creature.getAbilitySet() == null ? 0 : creature.getAbilitySet().getAbilities().size();
        for (Behavior behavior : creature.getBehaviorSet().getBehaviors()) {
            if (behavior.getAbilityToUseId() > abilityCount) {
                return false;
            }
        }
        return true;
    }
}
