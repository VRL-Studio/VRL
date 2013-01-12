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
import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
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

    public VRLUpdater(PluginIdentifier identifier) {
        this.identifier = identifier;
        try {
            this.updateURL = new URL(""
                    + "http://vrl-studio.mihosoft.eu/updates/" + VSysUtil.getOSName() +"/repository.xml");
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public void checkForUpdates(final VRLUpdateAction action) {

        InetAddress adr;
        boolean hostAvailable = false;

        try {
            adr = InetAddress.getByName(updateURL.getHost());
            hostAvailable = adr.isReachable(5000);
        } catch (UnknownHostException e) {
            e.printStackTrace(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        
        if (!hostAvailable) {
            System.out.println(">> VRLUpdater: host unreachable: " + updateURL.toExternalForm());
            action.hostUnreachable(this, updateURL);
            return;
        }

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

        Download download = new Download(updateURL, updateDir, 5000, 60 * 1000);

        if (action != null) {
            action.checkForUpdates(this, download, updateURL);
        }


        download.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object o1) {
                Download d = (Download) o;

                System.out.println(">> VRLUpdater: downloading repository "
                        + d.getProgress() + "%");

                if (d.getStatus() == Download.COMPLETE) {
                    synchronized (updateLock) {
                        System.out.println(" --> repository download finished. "
                                + d.getTargetFile() + ", size: " + d.getSize());
                        readUpdates(action, d);
                    }
                } else if (d.getStatus() == Download.ERROR) {
                    System.err.println(" --> cannot download repository: " + updateURL);
                    
                    if (action!=null) {
                        action.errorOccured(VRLUpdater.this, d, updateURL);
                    }
                }
            }
        });
    }

    private void readUpdates(VRLUpdateAction action, Download d) {

        File repositoryFile = d.getTargetFile();

        XMLDecoder encoder = null;

        try {
            encoder = new XMLDecoder(new FileInputStream(repositoryFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).
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

    public void downloadUpdate(RepositoryEntry update, final VRLDownloadAction action) {
        URL downloadURL = null;

        try {
            downloadURL = new URL(update.getUrl());
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
            if (action != null) {
                action.errorOccured(null, updateURL, "bad URL = " + update.getUrl());
            } else {
                System.err.println(
                        ">> VRLUpdater: bad URL = " + update.getUrl());
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
                Logger.getLogger(VRLUpdater.class.getName()).
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
