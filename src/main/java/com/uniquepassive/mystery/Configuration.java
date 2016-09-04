package com.uniquepassive.mystery;

import com.uniquepassive.mystery.util.JarUtil;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Configuration {

    private Map<String, ClassNode> inClasses;
    private Map<String, ClassNode> targetClasses;
    private String outJar;

    private Configuration(Builder builder)
            throws IOException {

        this.inClasses = getInClasses(builder.inJars);
        this.targetClasses = getTargetClasses(builder.targets, this.inClasses);

        this.outJar = builder.outJar;
    }

    public Map<String, ClassNode> getInClasses() {
        return inClasses;
    }

    public void setInClasses(Map<String, ClassNode> inClasses) {
        this.inClasses = inClasses;
    }

    public Map<String, ClassNode> getTargetClasses() {
        return targetClasses;
    }

    public void setTargetClasses(Map<String, ClassNode> targetClasses) {
        this.targetClasses = targetClasses;
    }

    public String getOutJar() {
        return outJar;
    }

    public void setOutJar(String outJar) {
        this.outJar = outJar;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String[] inJars;
        private String[] targets;
        private String outJar;

       public Builder inJars(String[] inJars) {
           this.inJars = inJars;
           return this;
       }

        public Builder targets(String[] targets) {
            this.targets = targets;
            return this;
        }

        public Builder outJar(String outJar) {
            this.outJar = outJar;
            return this;
        }

        public Configuration build()
                throws IOException {

            return new Configuration(this);
        }
    }

    private static Map<String, ClassNode> getInClasses(String[] inJars)
            throws IOException {

        Map<String, ClassNode> inClasses = new HashMap<>();

        for (String inJar : inJars) {
            File inFile = new File(inJar);
            if (!inFile.exists()) {
                throw new FileNotFoundException("In file " + inFile + " could not be resolved");
            }

            if (inFile.isFile()) {
                JarUtil.parseJar(inFile)
                        .forEach(inClasses::put);
            }
        }

        return inClasses;
    }

    private static Map<String, ClassNode> getTargetClasses(String[] targets, Map<String, ClassNode> inClasses)
            throws IOException {

        Map<String, ClassNode> targetClasses = new HashMap<>();

        Set<String> classesToSkip = new HashSet<>();

        for (String target : targets) {
            File targetFile = new File(target);

            if (targetFile.exists()) {
                if (targetFile.isFile()) {
                    JarUtil.parseJar(targetFile)
                            .forEach((name, c) -> {
                                inClasses.put(name, c);
                                targetClasses.put(name, c);
                            });
                }
            } else {
                boolean toAdd = true;

                if (target.startsWith(":")) {
                    toAdd = false;
                    target = target.substring(1);
                }

                int indexOfExtension = target.lastIndexOf(".");
                if (indexOfExtension > 0) {
                    target = target.substring(0, indexOfExtension);
                }

                target = target.replace(".", "/");

                if (toAdd) {
                    ClassNode targetClass = inClasses.get(target);
                    if (targetClass != null) {
                        targetClasses.put(target, targetClass);
                    }
                } else {
                    classesToSkip.add(target);
                }
            }
        }

        classesToSkip.forEach(targetClasses::remove);

        return targetClasses;
    }
}
