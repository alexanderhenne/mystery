package com.uniquepassive.mystery.core.obfuscators.renaming.provider;

import org.objectweb.asm.tree.ClassNode;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomNumberClassNameProvider implements ClassNameProvider {

    private static final SecureRandom random = new SecureRandom();

    private final Map<String, String> mappings = new HashMap<>();

    @Override
    public void feedData(Map<String, ClassNode> inClasses, Map<String, ClassNode> targetClasses) {
        mappings.clear();

        Map<String, List<String>> packageClasses = new HashMap<>();

        targetClasses.forEach((name, c) -> {
            String packageName = "";

            int packageDivisor = name.lastIndexOf("/");
            if (packageDivisor > 0) {
                packageName = name.substring(0, name.lastIndexOf("/"));
            }

            List<String> classNodes = packageClasses.get(packageName);
            if (classNodes == null) {
                classNodes = new ArrayList<>();
                packageClasses.put(packageName, classNodes);
            }

            classNodes.add(c.name);
        });

        List<Integer> names = new ArrayList<>();

        packageClasses.forEach((k, v) -> {
            for (int i = 0; i < v.size(); i++) {
                int newName = 0;

                if (names.size() == i) {
                    do {
                        newName = random.nextInt();
                    } while (names.contains(newName));

                    names.add(newName);
                } else {
                    newName = names.get(i);
                }

                mappings.put(v.get(i), k + newName);
            }
        });
    }

    @Override
    public String getNameForClass(String name) {
        return mappings.get(name);
    }
}
