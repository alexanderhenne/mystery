package com.uniquepassive.mystery.core;

import com.uniquepassive.mystery.Configuration;
import com.uniquepassive.mystery.core.obfuscators.modifiers.renaming.ClassRenaming;
import com.uniquepassive.mystery.core.obfuscators.modifiers.renaming.MemberRenaming;
import com.uniquepassive.mystery.core.obfuscators.modifiers.renaming.provider.RandomNumberClassNameProvider;
import com.uniquepassive.mystery.core.obfuscators.modifiers.renaming.provider.RandomNumberMemberNameProvider;
import com.uniquepassive.mystery.core.obfuscators.shuffling.IdentifierShuffler;
import com.uniquepassive.mystery.util.JarUtil;

import java.io.File;
import java.io.IOException;

public class Mystery {

    public void run(Configuration configuration)
            throws IOException {

        MemberRenaming memberRenaming = new MemberRenaming(new RandomNumberMemberNameProvider());
        memberRenaming.run(configuration.getInClasses(), configuration.getTargetClasses());

        ClassRenaming classRenaming = new ClassRenaming(new RandomNumberClassNameProvider());
        classRenaming.run(configuration.getInClasses(), configuration.getTargetClasses());

        IdentifierShuffler identifierShuffler = new IdentifierShuffler();
        identifierShuffler.run(configuration.getTargetClasses());

        JarUtil.saveToFile(new File(configuration.getOutJar()), configuration.getInClasses().values());
    }
}
