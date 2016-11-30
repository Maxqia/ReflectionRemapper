package com.maxqia.ReflectionRemapper;

import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.repo.RuntimeRepo;

public class Transformer { // This is kinda like RemapperProcessor from SpecialSource

    protected static JarMapping jarMapping;
    protected static JarRemapper remapper;

    /**
     * Only call this once, this library only supports one mapping.
     * @throws IllegalArgumentException if there's already a mapping
     */
    public static void loadMapping(JarMapping mapping) throws IllegalArgumentException {
            if (jarMapping != null) throw new IllegalArgumentException("Already loaded a mapping");
            jarMapping = mapping;
            remapper = new JarRemapper(mapping);
    }

    /**
     * Remap code using the provided jarMapping and
     * convert code from using Class.X methods to our remapped versions
     */
    public static byte[] transformSS(byte[] code) {
        return remapper.remapClassFile(
                transform(code),
                RuntimeRepo.getInstance());
    }

    /**
     * Convert code from using Class.X methods to our remapped versions
     */
    public static byte[] transform(byte[] code) {
        ClassReader reader = new ClassReader(code); // Turn from bytes into visitor
        ClassNode node = new ClassNode();
        reader.accept(node, 0); // Visit using ClassNode

        for (MethodNode method : node.methods) { // Taken from SpecialSource
            ListIterator<AbstractInsnNode> insnIterator = method.instructions.iterator();
            while (insnIterator.hasNext()) {
                AbstractInsnNode insn = insnIterator.next();
                switch (insn.getOpcode()) {
                    case Opcodes.INVOKEVIRTUAL:
                        remapVirtual(insn);
                        break;

                    case Opcodes.INVOKESTATIC:
                        remapForName(insn);
                        break;
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        node.accept(writer); // Convert back into bytes
        return writer.toByteArray();
    }

    public static void remapVirtual(AbstractInsnNode insn) {
        MethodInsnNode method = (MethodInsnNode) insn;
        if (method.owner != "java/lang/Class" || method.name != "getField") return;
        method.owner = "com/maxqia/ReflectionRemapper/RemappedMethods";
    }

    public static void remapForName(AbstractInsnNode insn) {
        MethodInsnNode method = (MethodInsnNode) insn;
        if (method.owner != "java/lang/Class" || method.name != "forName") return;
        method.owner = "com/maxqia/ReflectionRemapper/RemappedMethods";
    }
}
