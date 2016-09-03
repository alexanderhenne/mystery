package com.uniquepassive.mystery.core;

import com.uniquepassive.mystery.core.obfuscators.renaming.MemberRenaming;
import com.uniquepassive.mystery.core.obfuscators.renaming.provider.RandomNumberMemberNameProvider;
import com.uniquepassive.mystery.core.obfuscators.shuffling.MemberShuffler;
import com.uniquepassive.mystery.util.JarUtil;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Mystery {

    public void run(Map<String, ClassNode> inClasses, Map<String, ClassNode> targetClasses, String out)
            throws IOException {

        MemberShuffler memberShuffler = new MemberShuffler();
        memberShuffler.run(targetClasses);

        MemberRenaming memberRenaming = new MemberRenaming(new RandomNumberMemberNameProvider());
        memberRenaming.run(inClasses, targetClasses);

        memberShuffler.run(targetClasses);

        JarUtil.saveToFile(new File(out), inClasses.values());
    }
}
