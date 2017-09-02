package com.uniquepassive.mystery.core.obfuscators;

import org.objectweb.asm.tree.ClassNode;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;

public class BogusExceptionAdder {

    private static final int MIN_ITEMS_TO_ADD = 1;
    private static final int MAX_ITEMS_TO_ADD = 3;

    private static final SecureRandom random = new SecureRandom();

    public void run(Map<String, ClassNode> inClasses, Map<String, ClassNode> targetClasses) {
        targetClasses
                .values()
                .stream()
                .flatMap(c -> c.methods.stream())
                .forEach(m -> {
                    int count = random.nextInt(MAX_ITEMS_TO_ADD + 1 - MIN_ITEMS_TO_ADD)
                            + MIN_ITEMS_TO_ADD;

                    for (int i = 0; i < count; i++) {
                        m.exceptions.add(getRandomClassName(inClasses));
                    }
                });
    }

    private String getRandomClassName(Map<String, ClassNode> inClasses) {
        // Random from 0 to 1
        int type = random.nextInt(2);

        if (type == 0) {
            // Get a random class name from the in pool
            return new ArrayList<>(inClasses.values())
                    .get(random.nextInt(inClasses.size()))
                    .name;
        } else {
            String[] classes = new String[] {
                    "java/lang/Integer",
                    "java/lang/IllegalStateException",
                    "java/lang/ArrayIndexOutOfBoundsException",
                    "java/net/UnknownHostException"
            };

            return classes[random.nextInt(classes.length)];
        }
    }
}
