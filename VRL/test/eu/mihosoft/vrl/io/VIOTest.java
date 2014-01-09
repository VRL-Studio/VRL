/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.io;

import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VRL;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
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

    File testDir;
    File projectDir;
    File testPropertyFolder;
    VProjectController projectController;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        testDir = new File(new File("build"), "test-tmp");
        testDir.mkdirs();

        projectDir = new File(testDir, "projects");
        projectDir.mkdirs();

        testPropertyFolder = new File(testDir, "property-folder");
        testPropertyFolder.mkdirs();

        VRL.initAll(new String[]{"-property-folder", testPropertyFolder.getAbsolutePath()});
        
        JPanel canvasParent = new JPanel();
        VisualCanvas canvas = new VisualCanvas();
        canvasParent.add(canvas);

        projectController = new VProjectController(canvasParent, null);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void createProjectTest() {

        VProjectSessionCreator saver
                = new VProjectSessionCreator(null);

        File project = new File(projectDir, "project-01.vrlp");

        boolean throwsExcepion = true;

        try {
            saver.saveFile(projectController, project, ".vrlp");
            throwsExcepion = false;
        } catch (IOException ex) {
            Logger.getLogger(VIOTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue("saving project must not throw exception!", throwsExcepion == false);
    }
}
