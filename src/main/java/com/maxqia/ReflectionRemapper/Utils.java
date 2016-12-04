package com.maxqia.ReflectionRemapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import org.objectweb.asm.Type;

public class Utils {

    // Classes
    public static String reverseMapExternal(Class<?> name) {
        return reverseMap(name).replace('$', '.').replace('/', '.');
    }

    public static String reverseMap(Class<?> name) {
        return reverseMap(Type.getInternalName(name));
    }

    public static String reverseMap(String check) {
        for (Entry<String, String> entry : Transformer.jarMapping.classes.entrySet()) {
            if (entry.getValue().equals(check))
                return entry.getKey();
        }
        return check;
    }


    // Fields



    // Methods
    public static String mapMethod(Class<?> inst, String name, Class<?>... parameterTypes) {
        String result = mapMethodInternal(inst, name, parameterTypes);
        if (result != null) {
            return result;
        }

        System.out.println("Could not find method : " + name);
        return name;
    }

    private static String mapMethodInternal(Class<?> inst, String name, Class<?>... parameterTypes) {
        String match = reverseMap(inst) + "/" + name + " ";

        for (Entry<String, String> entry : Transformer.jarMapping.methods.entrySet()) {
            if (entry.getKey().startsWith(match)) {
                System.out.println(entry.getValue());

                // Check type to see if it matches
                String[] str = entry.getKey().split("\\s+");
                int i = 0;
                boolean failed = false;
                for (Type type : Type.getArgumentTypes(str[1])) {
                    if (!type.getClassName().equals(reverseMapExternal(parameterTypes[i]))) {
                        failed = true;
                        break;
                    }
                }

                if (!failed)
                    return entry.getValue();
            }
            //System.out.println(entry.getKey());
        }

        // Search interfaces
        ArrayList<Class<?>> parents = new ArrayList<Class<?>>();
        parents.add(inst.getSuperclass());
        parents.addAll(Arrays.asList(inst.getInterfaces()));

        for (Class<?> superClass : parents) {
            if (superClass == null) continue;
            mapMethodInternal(superClass, name, parameterTypes);
        }

        return null;
    }
}
