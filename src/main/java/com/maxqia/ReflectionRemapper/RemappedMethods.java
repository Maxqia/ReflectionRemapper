package com.maxqia.ReflectionRemapper;

import static com.maxqia.ReflectionRemapper.Utils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map.Entry;

import org.objectweb.asm.Type;

public class RemappedMethods {
    public static ClassLoader loader = RemappedMethods.class.getClassLoader();

    // Classes
    public static Class<?> forName(String className) throws ClassNotFoundException {
        className = Transformer.remapper.map(className.replace('.', '/')).replace('/', '.');
        return Class.forName(className, true, loader);
    }

    public static String getSimpleName(Class<?> inst) {
        String[] name = getName(inst).split("\\.");
        return name[name.length-1];
    }

    public static String getName(Class<?> inst) {
        return reverseMapExternal(inst);
    }


    // Fields
    public static Field getField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
        return inst.getField(Transformer.remapper.mapFieldName(
                reverseMap(inst), name, null));
    }

    public static Field getDeclaredField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
        return inst.getDeclaredField(Transformer.remapper.mapFieldName(
                reverseMap(inst), name, null));
    }

    public static String getName(Field field) {
        String name = field.getName();
        String match = reverseMap(field.getDeclaringClass());

        for (Entry<String, String> entry : Transformer.jarMapping.fields.entrySet()) {
            if (entry.getKey().startsWith(match) && entry.getValue().equals(name)) {
                String[] matched = entry.getKey().split("\\/");
                String rtr =  matched[matched.length-1];
                System.out.println(matched[matched.length-1] + " field matched " + name);
                return rtr;
            }
        }

        System.out.println("Could not get field reverse of : " + name);
        return name;
    }


    // Methods
    public static Method getMethod(Class<?> inst, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        try {
            if (inst.getName().equals("net.minecraft.server.MinecraftServer") && name.equals("getServer"))
                return Class.forName("org.spongepowered.common.SpongeImpl").getMethod("getServer");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inst.getMethod(mapMethod(inst, name, parameterTypes), parameterTypes);
    }

    public static Method getDeclaredMethod(Class<?> inst, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        try {
            if (inst.getName().equals("net.minecraft.server.MinecraftServer") && name.equals("getServer"))
                return Class.forName("org.spongepowered.common.SpongeImpl").getMethod("getServer");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inst.getDeclaredMethod(mapMethod(inst, name, parameterTypes), parameterTypes);
    }


    // Package names
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
