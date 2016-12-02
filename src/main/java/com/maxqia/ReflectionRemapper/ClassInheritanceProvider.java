package com.maxqia.ReflectionRemapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;

import org.objectweb.asm.Type;
import net.md_5.specialsource.provider.InheritanceProvider;

public class ClassInheritanceProvider implements InheritanceProvider {

    @Override
    public Collection<String> getParents(String className) {
        className = Transformer.remapper.map(className);

        try {
            Collection<String> parents = new HashSet<String>();
            Class<?> reference = Class.forName(className.replace('/', '.').replace('$', '.'), false, this.getClass().getClassLoader()/*RemappedMethods.loader*/);
            Class<?> extend = reference.getSuperclass();
            if (extend != null) {
                parents.add(reverseMap(Type.getInternalName(extend)));
            }

            for (Class<?> inter : reference.getInterfaces()) {
                if (inter != null) {
                    parents.add(reverseMap(Type.getInternalName(inter)));
                }
            }

            return parents;
        } catch (Exception e) {
            // Empty catch block
        }
        return null;
    }

    public static String reverseMap(String check) {
        for (Entry<String, String> entry : Transformer.jarMapping.classes.entrySet()) {
            if (entry.getValue().equals(check))
                return entry.getKey();
        }
        return check;
    }

}
