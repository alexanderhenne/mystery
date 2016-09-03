package com.uniquepassive.mystery.core.obfuscators.renaming.provider;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.security.SecureRandom;
import java.util.*;

public class RandomNumberMemberNameProvider implements MemberNameProvider {

    private static final SecureRandom random = new SecureRandom();

    private final Map<String, String> mappings = new HashMap<>();

    @Override
    public void feedData(Map<String, ClassNode> classes) {
        mappings.clear();

        Set<Integer> usedFieldNames = new HashSet<>();
        Set<Integer> usedMethodNames = new HashSet<>();

        classes.forEach((name, c) -> {
            Set<String> usedDescriptors = new HashSet<>();
            int[] newName = {generateRandomInt(0)};

            // Fields

            for (FieldNode f : c.fields) {
                if (usedDescriptors.contains(f.desc)) {
                    usedFieldNames.add(newName[0]);

                    do {
                        newName[0] = generateRandomInt(newName[0]);
                    } while (usedFieldNames.contains(newName[0]));
                }

                if (!mappings.containsKey(name + "." + f.name + f.desc)) {
                    classes.entrySet()
                            .stream()
                            .filter(e -> {
                                ClassNode c2 = classes.get(e.getValue().superName);
                                while (c2 != null) {
                                    if (c2 == c) {
                                        return true;
                                    }
                                    c2 = classes.get(c2.superName);
                                }
                                return false;
                            })
                            .forEach(e -> {
                                ClassNode c2 = e.getValue();

                                Optional<FieldNode> optionalField = c2.fields
                                        .stream()
                                        .filter(f2 -> (f2.access & Opcodes.ACC_STATIC) == 0)
                                        .filter(f2 -> f2.name.equals(f.name))
                                        .filter(f2 -> f2.desc.equals(f.desc))
                                        .findAny();

                                if (!optionalField.isPresent()) {
                                    mappings.put(c2.name + "." + f.name + f.desc, "" + newName[0]);
                                }
                            });

                    mappings.put(name + "." + f.name + f.desc, "" + newName[0]);
                    f.name = "" + newName[0];

                    usedDescriptors.add(f.desc);
                }
            }

            // Methods

            usedDescriptors.clear();
            newName[0] = generateRandomInt(newName[0]);

            // TODO: Implement method functionality
        });
    }

    @Override
    public String getNameForMember(String owner, String name, String desc, boolean isField) {
        return mappings.get(owner + "." + name + desc);
    }

    private int generateRandomInt(int oldInt) {
        int newInt;

        do {
            newInt = random.nextInt();
        } while (oldInt == newInt);

        return newInt;
    }
}
