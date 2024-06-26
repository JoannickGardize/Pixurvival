package com.pixurvival.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ArgsUtils {

    private Map<Class<?>, TriConsumer<Field, Object, String>> primitiveSetters = new HashMap<>();

    static {
        primitiveSetters.put(byte.class, (field, instance, string) -> ReflectionUtils.setField(field, instance, Integer.parseInt(string)));
        primitiveSetters.put(short.class, (field, instance, string) -> ReflectionUtils.setField(field, instance, Short.parseShort(string)));
        primitiveSetters.put(int.class, (field, instance, string) -> ReflectionUtils.setField(field, instance, Integer.parseInt(string)));
        primitiveSetters.put(long.class, (field, instance, string) -> ReflectionUtils.setField(field, instance, Long.parseLong(string)));
        primitiveSetters.put(float.class, (field, instance, string) -> ReflectionUtils.setField(field, instance, Float.parseFloat(string)));
        primitiveSetters.put(double.class, (field, instance, string) -> ReflectionUtils.setField(field, instance, Double.parseDouble(string)));
        primitiveSetters.put(char.class, (field, instance, string) -> ReflectionUtils.setField(field, instance, string.charAt(0)));
        primitiveSetters.put(boolean.class, (field, instance, string) -> ReflectionUtils.setField(field, instance, Boolean.parseBoolean(string)));
    }

    @Getter
    @AllArgsConstructor
    private static class Arg {
        private String name;
        private String value;
    }

    @SneakyThrows
    public static <T> T readArgs(String[] args, Class<T> wrapperClass) {
        T result = wrapperClass.newInstance();
        Map<String, Field> fieldMap = ReflectionUtils.getAllFieldsMap(wrapperClass);
        for (String argString : args) {
            Arg arg = readArg(argString);
            Field field = fieldMap.get(arg.getName());
            setValue(result, field, arg.getValue());
        }
        return result;
    }

    private static Arg readArg(String argString) {
        String[] split = argString.split("=");
        if (split.length != 2) {
            throw new IllegalArgumentException("Wrong arg format : must have only one '=' separator");
        }
        String name = split[0].trim();
        String value = split[1].trim();
        Preconditions.notEmpty(name, "name");
        Preconditions.notEmpty(value, "value");
        return new Arg(name, value);
    }

    @SneakyThrows
    public static void setValue(Object instance, Field field, String stringValue) {
        if (field == null) {
            return;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        if (field.getType() == String.class) {
            field.set(instance, stringValue);
        } else if (field.getType().isPrimitive()) {
            primitiveSetters.get(field.getType()).accept(field, instance, stringValue);
        } else { // At this point we assume that the field has a valueOf method like primitive wrappers or enums
            field.set(instance, field.getType().getMethod("valueOf", String.class).invoke(null, stringValue));
        }
    }

}
