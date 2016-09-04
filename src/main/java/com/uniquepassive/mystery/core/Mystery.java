package com.uniquepassive.mystery.core;

import com.uniquepassive.mystery.Configuration;
import com.uniquepassive.mystery.core.obfuscators.renaming.ClassRenaming;
import com.uniquepassive.mystery.core.obfuscators.renaming.MemberRenaming;
import com.uniquepassive.mystery.core.obfuscators.renaming.provider.RandomNumberClassNameProvider;
import com.uniquepassive.mystery.core.obfuscators.renaming.provider.RandomNumberMemberNameProvider;
import com.uniquepassive.mystery.core.obfuscators.shuffling.MemberShuffler;
import com.uniquepassive.mystery.util.JarUtil;

import java.io.File;
import java.io.IOException;

public class Mystery {

    public void run(Configuration configuration)
            throws IOException {

        MemberShuffler memberShuffler = new MemberShuffler();
        memberShuffler.run(configuration.getTargetClasses());

        MemberRenaming memberRenaming = new MemberRenaming(new RandomNumberMemberNameProvider());
        memberRenaming.run(configuration.getInClasses(), configuration.getTargetClasses());

        memberShuffler.run(configuration.getTargetClasses());

        ClassRenaming classRenaming = new ClassRenaming(new RandomNumberClassNameProvider());
        classRenaming.run(configuration.getInClasses(), configuration.getTargetClasses());

        JarUtil.saveToFile(new File(configuration.getOutJar()), configuration.getInClasses().values());
    }
}
