package com.pixurvival.core.system.interest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class InterestSubscription<T extends Interest> {

    private @Getter
    @NonNull Class<T> type;
    private List<T> subscribers = new ArrayList<>();

    public void subscribe(T subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(T subscriber) {
        subscribers.remove(subscriber);
    }

    public void publish(Consumer<T> action) {
        subscribers.forEach(action);
    }
}
