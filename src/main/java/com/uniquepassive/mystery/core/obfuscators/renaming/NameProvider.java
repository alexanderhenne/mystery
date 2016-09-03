package com.uniquepassive.mystery.core.obfuscators.renaming;

import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public interface NameProvider {

    void feedData(Map<String, ClassNode> classes);

    String getNameForMember(String owner, String name, String desc, boolean isField);
}
