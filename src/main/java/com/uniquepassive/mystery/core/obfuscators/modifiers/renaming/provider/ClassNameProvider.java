package com.uniquepassive.mystery.core.obfuscators.modifiers.renaming.provider;

import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public interface ClassNameProvider {

    void feedData(Map<String, ClassNode> inClasses, Map<String, ClassNode> targetClasses);

    String getNameForClass(String name);
}
