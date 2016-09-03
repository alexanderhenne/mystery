package com.uniquepassive.mystery.core.obfuscators.shuffling;

import org.objectweb.asm.tree.ClassNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MemberShuffler {

    public void run(Map<String, ClassNode> targetClasses) {
        targetClasses.forEach((name, c) -> {

            List<?>[] targets = {
                    c.fields, c.methods,
                    c.attrs, c.innerClasses,
                    c.visibleAnnotations, c.invisibleAnnotations,
                    c.visibleTypeAnnotations, c.invisibleTypeAnnotations
            };

            Arrays.stream(targets)
                    .filter(l -> l != null)
                    .forEach(Collections::shuffle);
        });
    }
}
