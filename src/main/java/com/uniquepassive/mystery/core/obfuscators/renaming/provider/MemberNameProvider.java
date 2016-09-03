package com.uniquepassive.mystery.core.obfuscators.renaming.provider;

import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public interface MemberNameProvider {

    void feedData(Map<String, ClassNode> classes);

    String getNameForMember(String owner, String name, String desc, boolean isField);
}
