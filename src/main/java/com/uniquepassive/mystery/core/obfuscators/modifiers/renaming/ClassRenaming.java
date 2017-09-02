package com.uniquepassive.mystery.core.obfuscators.modifiers.renaming;

import com.uniquepassive.mystery.core.obfuscators.modifiers.renaming.provider.ClassNameProvider;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.Map;

public class ClassRenaming {

    private final ClassNameProvider nameProvider;

    public ClassRenaming(ClassNameProvider nameProvider) {
        this.nameProvider = nameProvider;
    }

    public void run(Map<String, ClassNode> inClasses, Map<String, ClassNode> targetClasses) {
        nameProvider.feedData(inClasses, targetClasses);

        Arrays.stream(targetClasses.values().toArray(new ClassNode[targetClasses.size()]))
                .forEach(c -> {
                    String newClassName = nameProvider.getNameForClass(c.name);
                    if (newClassName != null) {
                        inClasses.put(newClassName, inClasses.remove(c.name));
                        targetClasses.put(newClassName, targetClasses.remove(c.name));

                        if (c.sourceFile != null) {
                            c.sourceFile = c.sourceFile.replace(c.name, newClassName);
                        }
                        c.sourceDebug = null;
                        c.name = newClassName;
                    }
                });

        inClasses.forEach((name, c) -> {
            String newSuperName = nameProvider.getNameForClass(c.superName);
            if (newSuperName != null) {
                c.superName = newSuperName;
            }

            String newOuterClassName = nameProvider.getNameForClass(c.outerClass);
            if (newOuterClassName != null) {
                c.outerClass = newOuterClassName;
            }

            if (c.outerMethodDesc != null) {
                c.outerMethodDesc = getNewDesc(c.outerMethodDesc);
            }

            if (c.signature != null) {
                c.signature = getNewDesc(c.signature);
            }

            for (int i = 0; i < c.interfaces.size(); i++) {
                String newInterface = nameProvider.getNameForClass(c.interfaces.get(i));
                if (newInterface != null) {
                    c.interfaces.set(i, newInterface);
                }
            }

            for (FieldNode f : c.fields) {
                String newType = getNewType(f.desc);
                if (newType != null) {
                    f.desc = newType;
                }
            }

            for (MethodNode m : c.methods) {
                m.desc = getNewDesc(m.desc);

                AbstractInsnNode[] instructions = m.instructions.toArray();

                Arrays.stream(instructions)
                        .filter(i -> i instanceof FieldInsnNode)
                        .map(i -> (FieldInsnNode) i)
                        .forEach(i -> {
                            String mapping = nameProvider.getNameForClass(i.owner);
                            if (mapping != null) {
                                i.owner = mapping;
                            }

                            String newType = getNewType(i.desc);
                            if (newType != null) {
                                i.desc = newType;
                            }
                        });

                Arrays.stream(instructions)
                        .filter(i -> i instanceof MethodInsnNode)
                        .map(i -> (MethodInsnNode) i)
                        .forEach(i -> {
                            String mapping = nameProvider.getNameForClass(i.owner);
                            if (mapping != null) {
                                i.owner = mapping;
                            }

                            i.desc = getNewDesc(i.desc);
                        });

                Arrays.stream(instructions)
                        .filter(i -> i instanceof TypeInsnNode)
                        .map(i -> (TypeInsnNode) i)
                        .forEach(i -> {
                            String mapping = getNewType(i.desc);
                            if (mapping != null) {
                                i.desc = mapping;
                            }
                        });

                Arrays.stream(instructions)
                        .filter(i -> i instanceof MultiANewArrayInsnNode)
                        .map(i -> (MultiANewArrayInsnNode) i)
                        .forEach(i -> {
                            String mapping = getNewType(i.desc);
                            if (mapping != null) {
                                i.desc = mapping;
                            }
                        });
            }
        });
    }

    private String getNewDesc(String desc) {
        StringBuilder newDesc = new StringBuilder("(");

        for (Type type : Type.getArgumentTypes(desc)) {
            String newType = getNewType(type.getDescriptor());

            if (newType != null) {
                newDesc.append(newType);
            } else {
                newDesc.append(type.getDescriptor());
            }
        }

        newDesc.append(")");

        String newType = getNewType(Type.getReturnType(desc).getDescriptor());

        if (newType == null) {
            newType = Type.getReturnType(desc).getDescriptor();
        }

        newDesc.append(newType);

        return newDesc.toString();
    }

    private String getNewType(String oldType) {
        Type type = Type.getType(oldType);

        String newType = type.getDescriptor();
        boolean isArray = newType.startsWith("[");
        if (isArray) {
            newType = type.getDescriptor().replace("[", "");
        }

        if (newType.startsWith("L")) {
            newType = nameProvider.getNameForClass(newType.substring(1, newType.length() - 1));

            if (newType != null) {
                newType = "L" + newType + ";";

                if (isArray) {
                    for (int i = 0, dimensions = type.getDimensions(); i < dimensions; i++) {
                        newType = "[" + newType;
                    }
                }

                return newType;
            }
        } else if (newType.contains("/")
                || (!newType.equals("I")
                && !newType.equals("J")
                && !newType.equals("Z")
                && !newType.equals("B")
                && !newType.equals("C")
                && !newType.equals("S"))) {

            newType = nameProvider.getNameForClass(newType);

            if (newType != null) {
                return newType;
            }
        }

        return null;
    }
}
