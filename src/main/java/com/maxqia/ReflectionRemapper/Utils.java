package com.maxqia.ReflectionRemapper;

import java.util.Map.Entry;

import org.objectweb.asm.Type;

public class Utils {

    public static String reverseMapExternal(Class<?> name) {
        return reverseMap(name.getName()).replace('$', '.').replace('/', '.');
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



    public static String mapMethod(Class<?> inst, String name, Class<?>... parameterTypes) {
        String match = reverseMap(inst) + "/" + name + " ";

        for (Entry<String, String> entry : Transformer.jarMapping.methods.entrySet()) {
            if (entry.getKey().startsWith(match)) {
                System.out.println(entry.getValue());
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
        System.out.println("Could not find method : " + match);
        return name;
    }
}
