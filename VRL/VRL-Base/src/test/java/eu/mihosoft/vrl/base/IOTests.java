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
        createCopyDirTest(134, 7);
        createCopyDirTest(250, 1);
        createCopyDirTest(0, 1);
        createCopyDirTest(1, 1);
        createCopyDirTest(1, 1);
    }

    @Test
    public void zipUnzipDirTest() throws IOException {
        createZipUnzipTest(30, 3);
        createZipUnzipTest(134, 7);
        createZipUnzipTest(250, 1);
        createZipUnzipTest(0, 1);
        createZipUnzipTest(1, 1);
        createZipUnzipTest(1, 1);
    }

    private void createZipUnzipTest(int numEntries, int maxDepth) throws IOException {
        File baseDir = Files.createTempDirectory("VRL-IOTests-").toFile();

        File srcDir = new File(baseDir, "src");
        srcDir.mkdirs();

        List<String> srcEntries = createDirWithContent(numEntries, maxDepth, srcDir);

        File dstDir = new File(baseDir, "dst");
        File archive = new File(baseDir, "dst.zip");

        boolean throwsIllegalaRgumentException = false;
        try {
            IOUtil.zipContentOfFolder(srcDir, archive);
        } catch (IllegalArgumentException ex) {
            throwsIllegalaRgumentException = true;
        }

        if (numEntries == 0 || maxDepth == 0) {
            assertTrue("Expecting OUtil.zipContentOfFolder() to throw an "
                    + "IllegalArgumentException since the source folder"
                    + " is empty", throwsIllegalaRgumentException);
        } else {

            IOUtil.unzip(archive, dstDir);

            List<String> dstEntries = IOUtil.listFiles(dstDir, "").stream().
                    map(fe -> fe.getAbsolutePath()).collect(Collectors.toList());

            compareEntries(srcEntries, dstEntries, srcDir, dstDir);
        }
    }

    private void createCopyDirTest(int numEntries, int maxDepth) throws IOException {
        File baseDir = Files.createTempDirectory("VRL-IOTests-").toFile();

        File srcDir = new File(baseDir, "src");
        srcDir.mkdirs();

        List<String> srcEntries = createDirWithContent(numEntries, maxDepth, srcDir);

        File dstDir = new File(baseDir, "dst");

        IOUtil.copyDirectory(srcDir, dstDir);

        List<String> dstEntries = IOUtil.listFiles(dstDir, "").stream().
                map(fe -> fe.getAbsolutePath()).collect(Collectors.toList());

        compareEntries(srcEntries, dstEntries, srcDir, dstDir);
    }

    private void compareEntries(List<String> srcEntries, List<String> dstEntries, File srcDir, File dstDir) {
        assertTrue("Expected equal number of entries in src and dst folder,"
                + " got: #src-entries=" + srcEntries.size()
                + ", #dst-entries=" + dstEntries.size() + " in " + dstDir.getAbsolutePath(),
                dstEntries.size() == srcEntries.size());

        for (int i = 0; i < srcEntries.size(); i++) {
            String src = srcEntries.get(i);
            String dst = dstEntries.get(i);

            String normalizedSrc = src.replace(srcDir.getAbsolutePath(),
                    dstDir.getAbsolutePath());

            assertTrue("Expected dst-entries to contain "
                    + normalizedSrc, dstEntries.contains(normalizedSrc));

        }

    }

    private List<String> createDirWithContent(int numEntries, int maxDepth, File srcDir) throws IOException {
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
        return srcEntries;
    }
}
