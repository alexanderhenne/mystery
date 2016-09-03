package com.uniquepassive.mystery;

import com.uniquepassive.mystery.core.Mystery;
import com.uniquepassive.mystery.util.JarUtil;
import org.apache.commons.cli.*;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Boot {

    public static void main(String[] args)
        throws ParseException, IOException {

        parseCmdArgs(args);
    }

    private static void parseCmdArgs(String[] args)
            throws ParseException, IOException {

        Options options = new Options();

        options.addOption("help", "print possible command line options");

        options.addOption(Option
                .builder("in")
                .hasArg()
                .required()
                .argName("jar")
                .desc("input jar")
                .build());

        options.addOption(Option
                .builder("t")
                .hasArg()
                .argName("jar/class file")
                .desc("jar or class file to obfuscate")
                .build());

        options.addOption(Option
                .builder("out")
                .hasArg()
                .required()
                .argName("jar")
                .desc("output jar")
                .build());

        CommandLine parse = new DefaultParser()
                .parse(options, args);

        if (parse.hasOption("help")) {
            new HelpFormatter().printHelp("mystery", options);
        } else {
            Map<String, ClassNode> inClasses
                    = getInClasses(parse.getOptionValues("in"));

            Map<String, ClassNode> targetClasses;
            if (parse.hasOption("t")) {
                targetClasses = getTargetClasses(parse.getOptionValues("t"), inClasses);
            } else {
                targetClasses = inClasses
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }

            new Mystery().run(inClasses, targetClasses, parse.getOptionValue("out"));
        }
    }

    private static Map<String, ClassNode> getTargetClasses(String[] target, Map<String, ClassNode> inClasses)
            throws IOException {

        Map<String, ClassNode> targetClasses = new HashMap<>();

        for (String s : target) {
            File targetFile = new File(s);
            if (targetFile.exists()) {
                if (targetFile.isFile()) {
                    JarUtil.parseJar(targetFile)
                            .forEach((name, c) -> {
                                inClasses.put(name, c);
                                targetClasses.put(name, c);
                            });
                }
            } else {
                int indexOfExtension = s.lastIndexOf(".");
                if (indexOfExtension > 0) {
                    s = s.substring(0, indexOfExtension);
                }

                s = s.replace(".", "/");

                ClassNode targetClass = inClasses.get(s);
                if (targetClass != null) {
                    targetClasses.put(s, targetClass);
                }
            }
        }

        return targetClasses;
    }

    private static Map<String, ClassNode> getInClasses(String[] in)
            throws IOException {

        Map<String, ClassNode> inClasses = new HashMap<>();

        for (String s : in) {
            File inFile = new File(s);
            if (!inFile.exists()) {
                throw new FileNotFoundException("File " + inFile + " passed to -in could not be resolved");
            }

            if (inFile.isFile()) {
                JarUtil.parseJar(inFile)
                        .forEach(inClasses::put);
            }
        }

        return inClasses;
    }
}
