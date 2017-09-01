package com.uniquepassive.mystery.core.obfuscators.modifiers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;

import java.io.*;
import java.security.SecureRandom;
import java.util.ListIterator;
import java.util.Map;

public class LineNumberModifier {

    private static final SecureRandom random = new SecureRandom();

    private Action action = Action.REMOVE;
    private String infoOut;

    public LineNumberModifier(String lineNumbersAction, String infoOut) {
        if (lineNumbersAction != null) {
            if (lineNumbersAction.equalsIgnoreCase("disable")) {
                this.action = Action.DISABLED;
            } else if (lineNumbersAction.equalsIgnoreCase("random")) {
                this.action = Action.RANDOM;
            }
        }

        this.infoOut = infoOut;
    }

    public void run(Map<String, ClassNode> targetClasses) throws IOException {
        if (action == Action.REMOVE) {
            targetClasses
                    .values()
                    .stream()
                    .flatMap(c -> c.methods.stream())
                    .forEach(m -> {
                        ListIterator<AbstractInsnNode> iterator = m.instructions.iterator();

                        while (iterator.hasNext()) {
                            AbstractInsnNode i = iterator.next();

                            if (i instanceof LineNumberNode) {
                                iterator.remove();
                            }
                        }
                    });
        } else if (action == Action.RANDOM) {
            File lineNumberMappingsFile = new File(infoOut, "lineNumbers.csv");

            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(lineNumberMappingsFile)));

            writer.println("Method,Original Line Number,New Line Number");

            targetClasses
                    .values()
                    .forEach(c -> c.methods.forEach(m -> {
                        ListIterator<AbstractInsnNode> iterator = m.instructions.iterator();

                        while (iterator.hasNext()) {
                            AbstractInsnNode i = iterator.next();

                            if (i instanceof LineNumberNode) {
                                LineNumberNode i2 = (LineNumberNode) i;

                                int line = i2.line;
                                i2.line = random.nextInt();

                                writer.println(c.name + "." + m.name + m.desc + "," + line + "," + i2.line);
                            }
                        }
                    }));

            writer.close();
        }
    }

    private enum Action {
        DISABLED,
        REMOVE,
        RANDOM
    }
}
