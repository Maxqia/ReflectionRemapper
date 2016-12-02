package com.maxqia.ReflectionRemapper;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import org.objectweb.asm.Type;

public class RemappedMethods {
    public static ClassLoader loader = RemappedMethods.class.getClassLoader();

    public static Class<?> forName(String className) throws ClassNotFoundException {
        className = Transformer.remapper.map(className.replace('.', '/')).replace('/', '.');
        return Class.forName(className, true, loader);
    }

    public static Field getField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
        return inst.getField(Transformer.remapper.mapFieldName(
                Type.getInternalName(inst), name, null));
    }

    public static String getName(Class<?> inst) {
        String name = inst.getName();
        //return Transformer.remapper.map(Type.getInternalName(inst)).replace('/', '.');
        String check = name.replace('.', '/');
        for (Entry<String, String> entry : Transformer.jarMapping.classes.entrySet()) {
            if (entry.getValue().equals(check))
                return entry.getKey().replace('/', '.');
        }
        System.out.println(name);
        return name;
    }

    public static String getName(Package inst) {
        if (inst == null) return null;
        String name = inst.getName();
        //return Transformer.remapper.map(Type.getInternalName(inst)).replace('/', '.');
        String check = name.replace('.', '/').concat("/");
        for (Entry<String, String> entry : Transformer.jarMapping.packages.entrySet()) {
            if (entry.getValue().equals(check)) {
                String match = entry.getKey().replace('/', '.');
                match = match.substring(0, match.length()-1);
                return match;
            }

        }
        System.out.println(name);
        return name;
    }
}
