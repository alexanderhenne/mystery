package com.uniquepassive.mystery.util;

import com.google.common.base.Throwables;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarUtil {

    public static Map<String, ClassNode> parseJar(File targetJar)
            throws IOException {

        JarFile jarFile = new JarFile(targetJar);

        return jarFile
                .stream()
                .filter(e -> e.getName().toLowerCase().endsWith(".class"))
                .map(e -> {
                    try (InputStream classStream = jarFile.getInputStream(e)) {
                        ClassReader classReader = new ClassReader(classStream);
                        ClassNode classNode = new ClassNode();
                        classReader.accept(classNode, ClassReader.SKIP_FRAMES);
                        return classNode;
                    } catch (IOException ex) {
                        throw Throwables.propagate(ex);
                    }
                })
                .collect(Collectors.toMap(c -> c.name, Function.identity()));
    }

    public static void saveToFile(File targetJar, Collection<ClassNode> classes)
            throws IOException {

        List<ClassNode> classList = new ArrayList<>(classes);

        /*
            Shuffle the class list
            so that the file order
            in the output jar isn't
            the same as the input order.
         */
        Collections.shuffle(classList);

        try (ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(targetJar))) {
            classList.forEach((c) -> {
                try {
                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    c.accept(classWriter);
                    zipFile.putNextEntry(new ZipEntry(c.name + ".class"));
                    zipFile.write(classWriter.toByteArray());
                    zipFile.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            zipFile.closeEntry();
        }
    }
}
