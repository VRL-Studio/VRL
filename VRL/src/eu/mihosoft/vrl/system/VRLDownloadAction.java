/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.io.Download;
import java.io.File;
import java.net.URL;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface VRLDownloadAction {

    void errorOccured(Download d, URL url, String error);

    void finished(Download d, String url);

    int getConnectionTimeout();

    File getDownloadFolder();

    int getReadTimeout();

    /**
     * @return the targetFile
     */
    File getTargetFile();
    
    public void downloadStateChanged(Download d);
    
}
