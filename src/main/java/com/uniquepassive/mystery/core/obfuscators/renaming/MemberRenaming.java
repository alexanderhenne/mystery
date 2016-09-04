package com.uniquepassive.mystery.core.obfuscators.renaming;

import com.uniquepassive.mystery.core.obfuscators.renaming.provider.MemberNameProvider;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.Map;

public class MemberRenaming {

    private final MemberNameProvider nameProvider;

    public MemberRenaming(MemberNameProvider nameProvider) {
        this.nameProvider = nameProvider;
    }

    public void run(Map<String, ClassNode> inClasses, Map<String, ClassNode> targetClasses) {
        nameProvider.feedData(inClasses, targetClasses);

        inClasses.forEach((name, c) -> {
            for (MethodNode m : c.methods) {
                AbstractInsnNode[] instructions = m.instructions.toArray();

                Arrays.stream(instructions)
                        .filter(i -> i instanceof FieldInsnNode)
                        .map(i -> (FieldInsnNode) i)
                        .forEach(i -> {
                            String mapping = nameProvider.getNameForMember(i.owner, i.name, i.desc, true);
                            if (mapping != null) {
                                i.name = mapping;
                            }
                        });

                Arrays.stream(instructions)
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
