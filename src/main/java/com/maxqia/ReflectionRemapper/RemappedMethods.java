package com.maxqia.ReflectionRemapper;

import java.lang.reflect.Field;

import org.objectweb.asm.Type;

public class RemappedMethods {
    public static Class<?> forName(String className) throws ClassNotFoundException {
        className = Transformer.remapper.map(className);
        return Class.forName(className);
    }

    public static Field getField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
        return inst.getField(Transformer.remapper.mapFieldName(
                Type.getInternalName(inst), name, null));
    }
}
