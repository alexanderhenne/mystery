package com.uniquepassive.mystery.core.obfuscators.modifiers.renaming.provider;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomNumberMemberNameProvider implements MemberNameProvider {

    private static final SecureRandom random = new SecureRandom();

    private final Map<String, String> mappings = new HashMap<>();

    @Override
    public void feedData(Map<String, ClassNode> inClasses, Map<String, ClassNode> targetClasses) {
        mappings.clear();

        Set<Integer> usedFieldNames = new HashSet<>();
        Set<Integer> usedMethodNames = new HashSet<>();

        targetClasses.forEach((name, c) -> {
            Set<String> usedDescriptors = new HashSet<>();
            AtomicInteger newName = new AtomicInteger(generateRandomInt());

            // Fields

            for (FieldNode f : c.fields) {
                if (usedDescriptors.contains(f.desc)) {
                    usedFieldNames.add(newName.get());

                    do {
                        newName.set(generateRandomInt());
                    } while (usedFieldNames.contains(newName.get()));
                }

                if (!mappings.containsKey(name + "." + f.name + f.desc)) {
                    inClasses.values()
                            .stream()
                            .filter(c2 -> {
                                ClassNode c3 = inClasses.get(c2.superName);
                                while (c3 != null) {
                                    if (c3 == c) {
                                        return true;
                                    }
                                    c3 = inClasses.get(c3.superName);
                                }
                                return false;
                            })
                            .forEach(c2 -> {
                                Optional<FieldNode> optionalField = c2.fields
                                        .stream()
                                        .filter(f2 -> (f2.access & Opcodes.ACC_STATIC) == 0)
                                        .filter(f2 -> f2.name.equals(f.name))
                                        .filter(f2 -> f2.desc.equals(f.desc))
                                        .findAny();

                                if (!optionalField.isPresent()) {
                                    mappings.put(c2.name + "." + f.name + f.desc, "" + newName.get());
                                }
                            });

                    mappings.put(name + "." + f.name + f.desc, "" + newName.get());

                    usedDescriptors.add(f.desc);
                }
            }

            // Methods

            usedDescriptors.clear();
            newName.set(generateRandomInt());

            // TODO: Implement method functionality
        });
    }

    @Override
    public String getNameForMember(String owner, String name, String desc, boolean isField) {
        return mappings.get(owner + "." + name + desc);
    }

    private int generateRandomInt() {
        return random.nextInt();
    }
}
