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
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface VRLUpdateAction {

    void cannotReadRepositoryFile(VRLUpdater updater, File repositoryFile);

    void checkForUpdates(VRLUpdater updater, Download d, URL location);
    
    void hostUnreachable(VRLUpdater updater, URL location);

//    void downloadFinished(VRLUpdater updater, Download d, URL location);

    void errorOccured(VRLUpdater updater, Download d, URL location);

    void installAction(VRLUpdater updater, RepositoryEntry update,
            File updateFile);

    void repositoryFileHasWrongFormat(
            VRLUpdater updater, File repositoryFile);

    void updateAvailable(
            final VRLUpdater updater,
            Download d, URL location,
            final RepositoryEntry update);
    
    public void updateDownloadStateChanged(Download d);

    public void startVerification(Download d);

    public void stopVerification(Download d, boolean verificationSuccessful);
    
}
