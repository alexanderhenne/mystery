package com.uniquepassive.mystery;

import com.uniquepassive.mystery.core.Mystery;
import org.apache.commons.cli.*;

import java.io.IOException;

public class Boot {

    public static void main(String[] args)
        throws ParseException, IOException {

        Configuration configuration = parseCmdArgs(args);
        if (configuration != null) {
            new Mystery().run(configuration);
        }
    }

    private static Configuration parseCmdArgs(String[] args)
            throws ParseException, IOException {

        Options options = new Options();

        options.addOption(Option
                .builder("in")
                .hasArg()
                .required()
                .argName("jars")
                .desc("input jar paths, separated using ','")
                .build());

        options.addOption(Option
                .builder("targets")
                .hasArg()
                .required()
                .argName("jar/class files")
                .desc("jars/class files to obfuscate, separated using ',' - prepend with ':' to make an exception for a class")
                .build());

        options.addOption(Option
                .builder("out")
                .hasArg()
                .required()
                .argName("jar")
                .desc("output jar path")
                .build());

        options.addOption(Option
                .builder("infoOut")
                .hasArg()
                .argName("path")
                .desc("info output folder path (default: -out path parent folder)")
                .build());

        options.addOption(Option
                .builder("linenumbers")
                .hasArg()
                .argName("action")
                .desc("'remove' to remove method debug line numbers (default), 'random' to make random, 'disable' to disable")
                .build());

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-help")) {
                new HelpFormatter().printHelp("mystery", options, true);
                return null;
            }
        }

        CommandLine parse = new DefaultParser()
                .parse(options, args);

        return Configuration
                .builder()
                .inJars(parse.getOptionValue("in").split(","))
                .targets(parse.getOptionValue("targets").split(","))
                .outJar(parse.getOptionValue("out"))
                .infoOut(parse.getOptionValue("infoOut"))
                .lineNumbersAction(parse.getOptionValue("linenumbers"))
                .build();
    }
}
