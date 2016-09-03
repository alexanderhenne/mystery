package com.uniquepassive.mystery.core.obfuscators.renaming;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.Map;

public class MemberRenaming {

    private final NameProvider nameProvider;

    public MemberRenaming(NameProvider nameProvider) {
        this.nameProvider = nameProvider;
    }

    public void run(Map<String, ClassNode> inClasses, Map<String, ClassNode> targetClasses) {
        nameProvider.feedData(targetClasses);

        inClasses.forEach((name, c) -> {
            for (MethodNode m : c.methods) {
                Arrays.stream(m.instructions.toArray())
                        .filter(i -> i instanceof FieldInsnNode)
                        .map(i -> (FieldInsnNode) i)
                        .forEach(i -> {
                            String mapping = nameProvider.getNameForMember(i.owner, i.name, i.desc, true);
                            if (mapping != null) {
                                i.name = mapping;
                            }
                        });

                Arrays.stream(m.instructions.toArray())
                        .filter(i -> i instanceof MethodInsnNode)
                        .map(i -> (MethodInsnNode) i)
                        .forEach(i -> {
                            String mapping = nameProvider.getNameForMember(i.owner, i.name, i.desc, false);
                            if (mapping != null) {
                                i.name = mapping;
                            }
                        });
            }
        });
    }
}
