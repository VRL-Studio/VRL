/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.io;

import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.system.VSysUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VIOTest {

    private static File testDir;
    private static File projectDir;
    private static File testPropertyFolder;
    private static File projectFile;
    private static VProject project;
    private static VProjectController projectController;

    @BeforeClass
    public static void setUpClass() throws Exception {
        testDir = new File(new File("build"), "test-tmp");
        IOUtil.deleteContainedFilesAndDirs(testDir);
        testDir.mkdirs();

        projectDir = new File(testDir, "projects");
        projectDir.mkdirs();

        testPropertyFolder = new File(testDir, "property-folder");
        testPropertyFolder.mkdirs();

        VRL.initAll(new String[]{"-property-folder", testPropertyFolder.getAbsolutePath()});

        JFrame frame = new JFrame();
        JPanel canvasParent = new JPanel();
        frame.add(canvasParent);
        VisualCanvas canvas = new VisualCanvas();
        canvasParent.add(canvas);

        projectController = new VProjectController(canvasParent, null);

        projectFile = new File(projectDir, "project-01.vrlp");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        
        Assert.assertTrue("closing project must not throw exception!", closeProject());
        IOUtil.deleteContainedFilesAndDirs(testDir);
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void createProjectTest() {

        Assert.assertTrue("saving project must not throw exception!", createProject());
    }

    @Test
    public void openProjectTest() {

        Assert.assertTrue("loading project must not throw exception!", openProject());

        Assert.assertTrue("closing project must not throw exception!", closeProject());

    }

    private boolean createProject() {
        VProjectSessionCreator saver
                = new VProjectSessionCreator(null, false);
        boolean success = false;
        try {
            saver.saveFile(projectController, projectFile, ".vrlp");
            success = true;
        } catch (IOException ex) {
            Logger.getLogger(VIOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    private boolean openProject() {
        boolean success = false;
        try {
            projectController.loadProject(projectFile, false);
            success = true;
        } catch (IOException ex) {
            Logger.getLogger(VIOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    private boolean saveProject() {
        boolean success = false;
        try {
            projectController.saveProject(false);
            success = true;
        } catch (IOException ex) {
            Logger.getLogger(VIOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    private static boolean closeProject() {
        boolean success = false;
        try {
            projectController.closeProject();
            success = true;
        } catch (IOException ex) {
            Logger.getLogger(VIOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    @Test
    public void addComponentToCanvas() {

        Assert.assertTrue("saving project must not throw exception!", createProject());

        Assert.assertTrue("loading project must not throw exception!", openProject());

        boolean success = false;

        try {
            projectController.getCurrentCanvas().addObject(new String("This is a test"));
            success = true;
        } catch (Exception ex) {
            Logger.getLogger(VIOTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue("adding object must not throw exception!", success);

        Assert.assertTrue("saving project must not throw exception!", saveProject());

        Assert.assertTrue("closing project must not throw exception!", closeProject());

        Assert.assertTrue("loading project must not throw exception!", openProject());

        int numObjects = projectController.getCurrentCanvas().getInspector().getObjects().size();

        Assert.assertTrue("project must contain exactly one objects!: contains " + numObjects,
                numObjects == 1);

        int numWindows = projectController.getCurrentCanvas().getWindows().size();

        Assert.assertTrue("canvas must contain exactly 3 windows (start, stop, string)! contains " + numWindows,
                numWindows == 3);

        Assert.assertTrue("closing project must not throw exception!", closeProject());

    }

    public boolean createConsoleApp(VProject project) {

        boolean success = false;

        try {

            Assert.assertTrue("console app project must compile!", projectController.build(true, false));

            class DummySaveAs implements FileSaver {

                public File dest;

                @Override
                public void saveFile(Object o, File file, String ext)
                        throws IOException {
                    // we won't save anything
                    dest = file;
                }

                @Override
                public String getDefaultExtension() {
                    return "zip";
                }
            }

            final DummySaveAs saver = new DummySaveAs();

            saver.dest = new File(projectDir, "console-app-01.zip");

            projectController.exportAsRunnableConsoleApplication(saver.dest, true).join();

            Assert.assertTrue("console app must exist!", saver.dest.exists());

            File consoleApp = new File(projectDir, "console-app-01.dir");
            IOUtil.deleteDirectory(consoleApp);
            consoleApp.mkdirs();

            IOUtil.unzip(saver.dest, consoleApp);

            if (VSysUtil.isWindows()) {
                // not supported
            } else {

                Process p = new ProcessBuilder("/bin/bash", consoleApp.getAbsolutePath() + "/console-app-01/run.sh").start();
                String line;
                BufferedReader serr;

                BufferedReader sout = new BufferedReader(new InputStreamReader(p.getInputStream()));
                serr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = sout.readLine()) != null) {
                    System.out.println(line);
                }

                while ((line = serr.readLine()) != null) {
                    System.err.println(line);
                }
                serr.close();
                p.waitFor();
            }

            success = true;
        } catch (Exception ex) {
            Logger.getLogger(VIOTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;
    }

    @Test
    public void consoleAppTest() {
        Assert.assertTrue("creating project must not throw exception!", createProject());

        System.out.println("a");

        Assert.assertTrue("opening project must not throw exception!", openProject());

        Assert.assertTrue("creating console app must not throw exception!", createConsoleApp(project));

        Assert.assertTrue("closing project must not throw exception!", closeProject());
    }
}
