/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.io.IOUtil;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import eu.mihosoft.vrl.io.Download;
import eu.mihosoft.vrl.io.VersionInfo;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.VDialog;
import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VRLUpdater {

    private URL updateURL;
    private PluginIdentifier identifier;
    private List<RepositoryEntry> possibleUpdates;
    private final Object updateLock = new Object();

    public static void main(String[] args) {
        VRLUpdater.test();
    }

    public static VRLUpdater test() {
        PluginIdentifier identifier = new PluginIdentifier("VRL-Studio", new VersionInfo("0.4.3"));

        VRLUpdater updater = new VRLUpdater(identifier);

        updater.checkForUpdates(new UpdateActionImpl());

        return updater;
    }

    public VRLUpdater(PluginIdentifier identifier) {
        this.identifier = identifier;
        try {
            this.updateURL = new URL(""
                    + "http://vrl-studio.mihosoft.eu/updates/repository.xml");
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public void checkForUpdates(final UpdateActionImpl action) {

        System.out.println(">> VRLUpdater: checking for updates: " + updateURL.toExternalForm());

        File updateDir;

        try {
            updateDir = IOUtil.createTempDir();
        } catch (IOException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
            System.err.println(
                    ">> VRLUpdater: cannot create tmp update folder");
            return;
        }

        Download download = new Download(updateURL, updateDir, 5000, 60000);

        if (action != null) {
            action.checkForUpdates(this, download, updateURL);
        }


        download.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object o1) {
                Download d = (Download) o;

                System.out.println(">> VRLUpdater: downloading repository " + d.getProgress() + "%");

                if (d.getStatus() == Download.COMPLETE) {
                    synchronized (updateLock) {
                        System.out.println(" --> repository downoad finished. " + d.getTargetFile() + ", size: " + d.getSize());
                        readUpdates(action, d);
                    }
                }
            }
        });
    }

    private void readUpdates(UpdateActionImpl action, Download d) {

        File repositoryFile = d.getTargetFile();

        XMLDecoder encoder = null;

        try {
            encoder = new XMLDecoder(new FileInputStream(repositoryFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UpdateActionImpl.class.getName()).
                    log(Level.SEVERE, null, ex);

            ex.printStackTrace(System.err);

            if (action != null) {
                action.cannotReadRepositoryFile(this, repositoryFile);
            } else {
                System.err.println(
                        ">> VRLUpdater: cannot read repository file: "
                        + repositoryFile);
            }
        }

        Object obj = encoder.readObject();

        if (!(obj instanceof Repository)) {
            VMessage.error("Cannot check for updates:",
                    ">> checking for updates failed! "
                    + "Repository file has wrong format: " + repositoryFile);

            if (action != null) {
                action.cannotReadRepositoryFile(this, repositoryFile);
            } else {
                System.err.println(
                        ">> VRLUpdater: Repository file has wrong format: "
                        + repositoryFile);
            }
        }

        Repository repository = (Repository) obj;


        System.out.println(
                ">> VRLUpdater: searching repository.xml for updates:");

        possibleUpdates = searchForPossibleUpdates(repository);

        RepositoryEntry update = findUpdate();

        if (update != null) {
            System.out.println(">> --> selected: " + update.getName() + "-" + update.getVersion());
        }

        if (update != null && action != null) {
            action.updateAvailable(this, d, updateURL, update);
        }
    }

    public void downloadUpdate(RepositoryEntry update, final DownloadActionImpl action) {
        URL downloadURL = null;

        try {
            downloadURL = new URL(update.getPath());
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
            if (action != null) {
                action.errorOccured(null, updateURL, "bad URL = " + update.getPath());
            } else {
                System.err.println(
                        ">> VRLUpdater: bad URL = " + update.getPath());
            }

            return;
        }

        int connectionTimeout = 5000;
        int readTimeout = 60 * 1000;
        File downloadLocation;

        if (action != null) {
            downloadLocation = action.getDownloadFolder();
            connectionTimeout = action.getConnectionTimeout();
            readTimeout = action.getReadTimeout();
        } else {
            try {
                downloadLocation = IOUtil.createTempDir();
            } catch (IOException ex) {
                Logger.getLogger(DownloadActionImpl.class.getName()).
                        log(Level.SEVERE, null, ex);
                System.err.println(
                        ">> VRLUpdater: cannot create tmp folder!");
                return;
            }
        }

        Download d = new Download(
                downloadURL,
                downloadLocation,
                connectionTimeout,
                readTimeout);

        d.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object o1) {
                Download d = (Download) o;

                System.out.println(">> VRLUpdater: downloading update " + d.getProgress() + "%");

                if (d.getStatus() == Download.COMPLETE) {
                    if (action != null) {
                        action.finished(d, d.getUrl());
                    }

                    System.out.println(
                            " --> finished download: "
                            + d.getTargetFile());
                }
            }
        });
    }

//    public void installUpdate(UpdateActionImpl action, DownloadActionImpl downloadAction) {
//        RepositoryEntry update = findUpdate();
//        
//        if (downloadAction == null) {
//            downloadAction = new DownloadActionImpl();
//        }
//
//        if (update != null && action != null) {
//            action.installAction(this, update);
//        }
//    }
    private RepositoryEntry findUpdate() {
        synchronized (updateLock) {
            // choose the minimum version
            // (we are careful and don't support direct updates)
            VersionInfo min = null;
            RepositoryEntry minE = null;

            for (RepositoryEntry e : possibleUpdates) {

                VersionInfo vInfo = new VersionInfo(e.getVersion());

                if (min == null) {
                    min = vInfo;
                    minE = e;
                }

                if (vInfo.compareTo(min) < 0) {
                    min = vInfo;
                    minE = e;
                }
            }

            return minE;
        }
    }

    /**
     * @return the updateURL
     */
    public URL getUpdateURL() {
        return updateURL;
    }

    /**
     * @param updateURL the updateURL to set
     */
    public void setUpdateURL(URL updateURL) {
        this.updateURL = updateURL;
    }

    /**
     * @return the identifier
     */
    public PluginIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(PluginIdentifier identifier) {
        this.identifier = identifier;
    }

    private List<RepositoryEntry> searchForPossibleUpdates(Repository repository) {

        List<RepositoryEntry> updates =
                new ArrayList<RepositoryEntry>();

        // search for possible updates
        for (RepositoryEntry e : repository.getEntries()) {

            if (e.getName() == null || e.getVersion() == null) {
                System.err.println(" --> update name or version null!");
                continue;
            }

            if (!e.getName().trim().equals(getIdentifier().getName())) {
                continue;
            }

            VersionInfo vInfo = new VersionInfo(e.getVersion());

            if (!vInfo.isVersionValid()) {
                System.err.println(
                        " --> version invalid: name="
                        + e.getName() + ", version= " + e.getVersion());
                continue;
            }

            if (vInfo.compareTo(getIdentifier().getVersion()) > 0) {
                updates.add(e);
                System.out.println(
                        " --> update = "
                        + e.getName() + "-" + e.getVersion());
            }
        }

        return updates;
    }
    
} // end class VRLUpdater

class DownloadActionImpl {

    private File targetFile;

    public DownloadActionImpl() {
        //
    }

    public void errorOccured(Download d, URL url, String error) {
        VMessage.error("Download failed",
                ">> download failed! Error: " + error);
    }

    public void finished(Download d, String url) {
        VMessage.info("Download finished",
                ">> download finished: " + d.getTargetFile());
        targetFile = d.getTargetFile();
    }

    public int getConnectionTimeout() {
        return 5000;
    }

    public int getReadTimeout() {
        return 60 * 1000;
    }

    public File getDownloadFolder() {
        try {
            return IOUtil.createTempDir();
        } catch (IOException ex) {
            Logger.getLogger(DownloadActionImpl.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the targetFile
     */
    public File getTargetFile() {
        return targetFile;
    }
}

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class UpdateActionImpl {

    public UpdateActionImpl() {
        //
    }

    public void checkForUpdates(VRLUpdater updater, Download d, URL location) {

        VMessage.info("Checking for updates:",
                ">> checking for updates from " + location.toExternalForm());

        VisualCanvas canvas =
                VRL.getCurrentProjectController().getCurrentCanvas();
    }

    public void errorOccured(VRLUpdater updater, Download d, URL location) {
        VMessage.error("Cannot check for updates:",
                ">> checking for updates failed! Location: "
                + location.toExternalForm());
    }

    public void cannotReadRepositoryFile(VRLUpdater updater, File repositoryFile) {
        VMessage.error("Cannot check for updates:",
                ">> checking for updates failed! "
                + "Cannot read repository file: " + repositoryFile);
    }

    public void repositoryFileHasWrongFormat(VRLUpdater updater, File repositoryFile) {
        VMessage.error("Cannot check for updates:",
                ">> checking for updates failed! "
                + "Repository file has wrong format: " + repositoryFile);
    }

    public void downloadFinished(VRLUpdater updater, Download d, URL location) {

        VMessage.info("Donloaded updates repository:",
                ">> checking for updates finished!");

    }

    public void updateAvailable(final VRLUpdater updater, Download d, URL location, final RepositoryEntry update) {

        VisualCanvas canvas =
                VRL.getCurrentProjectController().getCurrentCanvas();

        if (VDialog.YES == VDialog.showConfirmDialog(canvas,
                "Update available!",
                "Shall the update "
                + update.getName() + "-" + update.getVersion() + " be installed?", VDialog.YES_NO)) {

            updater.downloadUpdate(update, new DownloadActionImpl() {
                @Override
                public void finished(Download d, String url) {
                    installAction(updater, update, d.getTargetFile());
                }
            });
        }
    }

    public void installAction(VRLUpdater updater, RepositoryEntry update, File updateFile) {
        System.out.println(">> VRLUpdater: install " + updateFile);
    }
}
//File repositoryFile = d.getTargetFile();
//
//        XMLDecoder encoder = null;
//
//        try {
//            encoder = new XMLDecoder(new FileInputStream(repositoryFile));
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(UpdateActionImpl.class.getName()).
//                    log(Level.SEVERE, null, ex);
//            VMessage.error("Cannot check for updates:",
//                    ">> checking for updates failed! "
//                    + "Cannot read repository file: " + repositoryFile);
//        }
//
//        Object obj = encoder.readObject();
//
//        if (!(obj instanceof Repository)) {
//            VMessage.error("Cannot check for updates:",
//                    ">> checking for updates failed! "
//                    + "Repository file has wrong format: " + repositoryFile);
//        }
//
//        Repository repository = (Repository) obj;
//
//        List<RepositoryEntry> possibleUpdates =
//                new ArrayList<RepositoryEntry>();
//
//        // search for possible updates
//        for (RepositoryEntry e : repository.getEntries()) {
//
//            if (e.getName() == null || e.getVersion() == null) {
//                System.err.println(">> update name or version null!");
//                continue;
//            }
//
//            if (!e.getName().trim().equals(updater.getIdentifier().getName())) {
//                continue;
//            }
//
//            VersionInfo vInfo = new VersionInfo(e.getVersion());
//
//            if (!vInfo.isVersionValid()) {
//                System.err.println(
//                        ">> update version invalid: name="
//                        + e.getName() + ", version= " + e.getVersion());
//                continue;
//            }
//
//            if (vInfo.compareTo(updater.getIdentifier()) > 0) {
//                possibleUpdates.add(e);
//            }
//        }
//
//        // choose the minimum version
//        // (we are careful and don't support direct updates)
//        VersionInfo min = updater.getIdentifier().getVersion();
//        RepositoryEntry minE = null;
//
//        for (RepositoryEntry e : possibleUpdates) {
//
//            VersionInfo vInfo = new VersionInfo(e.getVersion());
//
//            if (vInfo.compareTo(min) < 0) {
//                min = vInfo;
//                minE = e;
//            }
//        }
//
//        // we found and update
//        if (minE != null) {
//            VDialog.showConfirmDialog(canvas, null, null, VDialog.DialogType.OK)
//        }