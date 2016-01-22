/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.io.Download;
import eu.mihosoft.vrl.io.IOUtil;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DownloadTest {

    @Test
    public void dowloadFileTest() {
        String urlOne = "http://vrl-studio.mihosoft.eu/releases/v0.4.5.7/VRL-Studio-Linux.zip";
        String sha1One = "17ca58114de926b9b90521defaf180c5bf460618";

        createDownloadTest(urlOne, sha1One);

        String urlTwo = "http://vrl-studio.mihosoft.eu/releases/v0.4.5.7/VRL-Studio-Windows.zip";
        String sha1Two = "1ccbeee55f818915b3e04c865290eea795806c77";

        createDownloadTest(urlTwo, sha1Two);

    }

    private void createDownloadTest(String url, String sha1) {

        Download d = null;
        boolean exception = false;
        try {
            d = new Download(
                    new URL(url), new File("."), 5000, 60 * 1000);

            d.addObserver((Observable o, Object arg) -> {
                System.out.println(" -> " + ((Download) o).getProgress());
            });

            d.run();
        } catch (MalformedURLException ex) {
            Logger.getLogger(DownloadTest.class.getName()).
                    log(Level.SEVERE, null, ex);
            exception = true;
        }
        
        IOUtil.enableDebugging(true);

        Assert.assertNotNull("Download object must not be null.", d);
        Assert.assertFalse("Download must not fail with exception.", exception);

        Assert.assertTrue("Download must not fail SHA1 verification.",
                d.verifySHA1(sha1));

        Assert.assertFalse("Download must fail with wrong SHA1 verification.",
                d.verifySHA1("1ccbeee55f818915b3e04c865290eea795806c78"));
    }
}
