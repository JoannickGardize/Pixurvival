package com.pixurvival.core.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class Cache<K, V> {

    @Getter
    private static class Entry<K, V> extends SoftReference<V> {

        private K key;

        public Entry(K key, V referent, ReferenceQueue<? super V> q) {
            super(referent, q);
            this.key = key;
        }

    }

    private Map<K, Entry<K, V>> map = new HashMap<>();
    private @NonNull Function<K, V> producer;
    private ReferenceQueue<V> queue = new ReferenceQueue<>();

    public V get(K key) {
        collect();
        Reference<V> ref = map.get(key);
        V result;
        if (ref == null || (result = ref.get()) == null) {
            result = producer.apply(key);
            map.put(key, new Entry<>(key, result, queue));
            return result;
        } else {
            return result;
        }
    }

    public void clear() {
        map.clear();
    }

    @SuppressWarnings("unchecked")
    private void collect() {
        Reference<? extends V> ref;
        while ((ref = queue.poll()) != null) {
            map.remove(((Entry<K, V>) ref).getKey());
        }
    }
}
