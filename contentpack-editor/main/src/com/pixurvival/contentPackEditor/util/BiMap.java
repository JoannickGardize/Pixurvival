package com.pixurvival.contentPackEditor.util;

import java.util.Map;

public interface BiMap<K, V> extends Map<K, V> {

	BiMap<V, K> inverse();
}
