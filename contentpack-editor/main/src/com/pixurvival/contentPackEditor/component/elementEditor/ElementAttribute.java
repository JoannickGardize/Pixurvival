package com.pixurvival.contentPackEditor.component.elementEditor;

import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @param <E> the element container type
 * @param <A> the attribute type
 * @author SharkHendrix
 */
@Getter
@RequiredArgsConstructor
public class ElementAttribute<E, A> {

    @NonNull
    private ValueComponent<A> component;
    private Function<E, A> getter;
    private BiConsumer<E, A> setter;
    private Predicate<E> condition;

    public ElementAttribute<E, A> getter(Function<E, A> getter) {
        this.getter = getter;
        return this;
    }

    public ElementAttribute<E, A> setter(BiConsumer<E, A> setter) {
        this.setter = setter;
        return this;
    }

    public ElementAttribute<E, A> condition(Predicate<E> condition) {
        this.condition = condition;
        return this;
    }

    public ElementAttribute<E, A> condition(Class<?> typeCondition) {
        this.condition = typeCondition::isInstance;
        return this;
    }
}
