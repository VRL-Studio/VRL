/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class IOTests {

    @Test
    public void copyDirTest() throws IOException {
        createCopyDirTest(30, 3);
    }

    private void createCopyDirTest(int numEntries, int maxDepth) throws IOException {
        File baseDir = Files.createTempDirectory("VRL-IOTests-").toFile();

        File srcDir = new File(baseDir, "src");
        srcDir.mkdirs();

        List<String> srcEntries = new ArrayList<>();

        for (int i = 0; i < numEntries; i++) {
            File f;
            File d;

            int depth = i % maxDepth;

            String dPath = null;

            for (int j = 0; j < depth; j++) {
                dPath = "/d" + i + "_" + j;
            }

            if (dPath == null) {
                d = srcDir;
            } else {
                d = new File(srcDir, "d" + i);
                d.mkdirs();
            }

            f = new File(d, "f" + i);

            srcEntries.add(f.getAbsolutePath());

            IOUtil.saveStreamToFile(
                    new ByteArrayInputStream(
                            ("string:" + i).getBytes("UTF-8")), f);
        }

        File dstDir = new File(baseDir, "dst");
        
        System.out.println("dirDst: " + dstDir);

        IOUtil.copyDirectory(srcDir, dstDir);

        List<String> dstEntries = IOUtil.listFiles(dstDir,"").stream().
                map(fe -> fe.getAbsolutePath()).collect(Collectors.toList());

        assertTrue("Expected equal number of entries in src and dst folder,"
                + " got: #src-entries=" + srcEntries.size()
                + ", #dst-entries=" + dstEntries.size(),
                dstEntries.size() == srcEntries.size());

        for(int i = 0; i < srcEntries.size();i++) {
            String src = srcEntries.get(i);
            String dst = dstEntries.get(i);
            
            String normalizedSrc = src.replace(srcDir.getAbsolutePath(),
                    dstDir.getAbsolutePath());
            
            assertEquals("Expected src and dst to be equal", normalizedSrc, dst);
        }
    }
}
