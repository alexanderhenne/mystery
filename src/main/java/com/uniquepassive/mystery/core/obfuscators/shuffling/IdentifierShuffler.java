package com.uniquepassive.mystery.core.obfuscators.shuffling;

import org.objectweb.asm.tree.ClassNode;

import java.util.*;

public class IdentifierShuffler {

    public void run(Map<String, ClassNode> targetClasses) {
        targetClasses.forEach((name, c) -> {
            Set<List<?>> targets = new HashSet<>();

            targets.addAll(Arrays.asList(
                    c.fields, c.methods,
                    c.attrs, c.innerClasses,
                    c.visibleAnnotations, c.invisibleAnnotations,
                    c.visibleTypeAnnotations, c.invisibleTypeAnnotations)
            );

            c.fields.forEach(f -> targets.addAll(Arrays.asList(
                    f.visibleAnnotations, f.invisibleAnnotations,
                    f.visibleTypeAnnotations, f.invisibleTypeAnnotations)
            ));

            c.methods.forEach(m -> targets.addAll(Arrays.asList(
                    m.exceptions, m.localVariables,
                    m.visibleAnnotations, m.invisibleAnnotations,
                    m.visibleTypeAnnotations, m.invisibleTypeAnnotations,
                    m.visibleLocalVariableAnnotations, m.invisibleLocalVariableAnnotations)
            ));

            targets.remove(null);

            targets.forEach(Collections::shuffle);
        });
    }
}
