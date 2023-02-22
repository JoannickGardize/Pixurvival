package com.pixurvival.core.system;

import com.pixurvival.core.AdditionalAttribute;
import com.pixurvival.core.World;
import com.pixurvival.core.system.interest.Interest;
import com.pixurvival.core.system.interest.InterestSubscription;
import com.pixurvival.core.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SuppressWarnings("unchecked")
public class WorldAttributesAccessor {

    private static class TypeKey {
        private Class<?>[] types;

        public TypeKey(Class<?> type, Class<?>... genericTypes) {
            this.types = new Class<?>[genericTypes.length + 1];
            types[0] = type;
            if (genericTypes.length > 0) {
                System.arraycopy(genericTypes, 0, types, 1, genericTypes.length);
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(types);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            return obj instanceof TypeKey && Arrays.equals(types, ((TypeKey) obj).types);
        }
    }

    private static Map<TypeKey, BiFunction<Field, World, Object>> staticAccessors = new HashMap<>();

    static {
        for (Field field : World.class.getDeclaredFields()) {
            staticAccessors.put(new TypeKey(field.getType(), ReflectionUtils.getGenericTypeArguments(field)), (f, w) -> ReflectionUtils.getByGetter(w, field));
        }
        staticAccessors.put(new TypeKey(InterestSubscription.class), (f, w) -> w.getInterestSubscriptionSet().get((Class<Interest>) ReflectionUtils.getGenericTypeArgument(f)));
    }

    private World world;

    private Map<TypeKey, BiFunction<Field, World, Object>> accessors;

    public WorldAttributesAccessor(World world) {
        super();
        this.world = world;
        accessors = new HashMap<>(staticAccessors);
        for (AdditionalAttribute additionalAttribute : world.getAdditionalAttributes()) {
            accessors.put(new TypeKey(additionalAttribute.getType(), additionalAttribute.getGenericTypes()), (f, w) -> additionalAttribute.getValue());
        }
    }

    public void inject(GameSystem system) {
        for (Field field : ReflectionUtils.getAllFields(system.getClass())) {
            if (field.isAnnotationPresent(Inject.class)) {
                inject(system, field);
            }
        }
    }

    private void inject(GameSystem system, Field field) {
        ReflectionUtils.setBySetter(system, field, accessors.get(buildTypeKey(field)).apply(field, world));
    }

    private TypeKey buildTypeKey(Field field) {
        if (field.getType() == InterestSubscription.class) {
            return new TypeKey(field.getType());
        } else {
            return new TypeKey(field.getType(), ReflectionUtils.getGenericTypeArguments(field));
        }
    }
}
