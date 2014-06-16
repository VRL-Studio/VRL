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
import eu.mihosoft.vrl.io.NetUtil;
import eu.mihosoft.vrl.io.VersionInfo;
import eu.mihosoft.vrl.security.PGPUtil;
import eu.mihosoft.vrl.visual.ProceedRequest;
import eu.mihosoft.vrl.visual.VSwingUtil;
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
    private URL updateSignatureURL;
    private PluginIdentifier identifier;
    private List<RepositoryEntry> possibleUpdates;
    private final Object updatesLock = new Object();
    private Download updateDownload;
    private final Object updateDownloadLock = new Object();
    private Download repositoryDownload;
    private Download repositorySignatureDownload;
    private final Object repositoryDownloadLock = new Object();
    private boolean verificationEnabled;
    private boolean verificationSuccessful;
    private File customPublicKey;

    public VRLUpdater(PluginIdentifier identifier) {
        this.identifier = identifier;
        try {
            this.updateURL = new URL(""
                    + "http://vrl-studio.mihosoft.eu/updates/"
                    + VSysUtil.getOSName() + "/repository.xml");
            this.updateSignatureURL = new URL(""
                    + "http://vrl-studio.mihosoft.eu/updates/"
                    + VSysUtil.getOSName() + "/repository.xml.asc");
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public boolean isDownloadingUpdate() {
        synchronized (updateDownloadLock) {
            return updateDownload != null;
        }
    }

    public boolean isDownloadingRepository() {
        synchronized (repositoryDownloadLock) {
            return repositoryDownload != null;
        }
    }

    public void checkForUpdates(final VRLUpdateAction action) {
        if (isDownloadingUpdate() || isDownloadingRepository()) {
            System.out.println(
                    ">> VRLUpdater: "
                    + "currently downloading repository. Please wait!");
            return;
        }

        boolean hostAvailable = NetUtil.isHostReachable(
                updateURL.getHost(), 80, 5000);

        if (!hostAvailable) {
            System.out.println(">> VRLUpdater: host unreachable: "
                    + updateURL.toExternalForm());
            action.hostUnreachable(this, updateURL);
            return;
        }

        System.out.println(">> VRLUpdater: checking for updates: "
                + updateURL.toExternalForm());

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

        synchronized (repositoryDownloadLock) {

            // downloading signature
            repositorySignatureDownload = new Download(
                    updateSignatureURL, updateDir, 5000, 60 * 1000);

            repositorySignatureDownload.addObserver(new Observer() {
                private long timestamp;

                @Override
                public void update(Observable o, Object o1) {
                    Download d = (Download) o;

                    long currentTime = System.currentTimeMillis();

                    if (timestamp == 0 || currentTime - timestamp > 1000) {
                        System.out.println(
                                ">> VRLUpdater:"
                                + " downloading repository signature "
                                + d.getProgress() + "%");
                        timestamp = currentTime;
                    }

                    if (d.getStatus() == Download.COMPLETE) {
                        System.out.println(
                                ">> VRLUpdater:"
                                + " downloading repository signature"
                                + d.getProgress() + "%");
                        synchronized (updatesLock) {
                            System.out.println(
                                    " --> repository signature download finished. "
                                    + d.getTargetFile()
                                    + ", size: " + (d.getSize() / 1024.0) + " KB");
                        }
                    } else if (d.getStatus() == Download.ERROR) {
                        System.err.println(
                                " --> cannot download repository signature: "
                                + updateSignatureURL);
                        repositorySignatureDownload = null;
                        if (action != null) {
                            action.errorOccured(
                                    VRLUpdater.this, d, updateSignatureURL);
                        }
                    }
                }
            });

            // wait for signature to complete
            VSwingUtil.newWaitController().requestConcurrentWait(
                    new ProceedRequest() {
                        @Override
                        public boolean proceed() {
                            if (repositorySignatureDownload != null) {
                                return repositorySignatureDownload.getStatus()
                                != Download.DOWNLOADING;
                            } else {
                                return true;
                            }
                        }
                    });

            // downloading repository
            repositoryDownload = new Download(
                    updateURL, updateDir, 5000, 60 * 1000);

            if (action != null) {
                action.checkForUpdates(this, repositoryDownload, updateURL);
            }

            repositoryDownload.addObserver(new Observer() {
                private long timestamp;

                @Override
                public void update(Observable o, Object o1) {
                    Download d = (Download) o;

                    long currentTime = System.currentTimeMillis();

                    if (timestamp == 0 || currentTime - timestamp > 1000) {
                        System.out.println(
                                ">> VRLUpdater: downloading repository "
                                + d.getProgress() + "%");
                        timestamp = currentTime;
                    }

                    if (d.getStatus() == Download.COMPLETE) {
                        System.out.println(
                                ">> VRLUpdater: downloading repository "
                                + d.getProgress() + "%");
                        synchronized (updatesLock) {
                            System.out.println(
                                    " --> repository download finished. "
                                    + d.getTargetFile()
                                    + ", size: " + (d.getSize() / 1024.0) + " KB");
                            readUpdates(action, d);
                            synchronized (repositoryDownloadLock) {
                                repositoryDownload = null;
                            }
                        }
                    } else if (d.getStatus() == Download.ERROR) {
                        System.err.println(" --> cannot download repository: "
                                + updateURL);

                        repositoryDownload = null;

                        if (action != null) {
                            action.errorOccured(VRLUpdater.this, d, updateURL);
                        }
                    }
                }
            });
        } // end synchronize
    }

    private void readUpdates(VRLUpdateAction action, Download d) {

        boolean repositoryVerified = false;
        File repositoryFile = d.getTargetFile();
        try {

            File keyFile;

            if (customPublicKey != null) {
                keyFile = customPublicKey;
            } else {
                keyFile = PGPUtil.loadPublicVRLKey();
            }

            repositoryVerified = PGPUtil.verifyFile(keyFile,
                    repositoryFile,
                    repositorySignatureDownload.getTargetFile());
        } catch (IOException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).log(
                    Level.SEVERE, null, ex);
        } catch (RuntimeException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        if (repositoryVerified) {
            System.out.println(
                    ">> VRLUpdater: repository successfully verified.");
        } else {
            if (action != null) {
                VMessage.warning("Cannot check for updates:",
                        ">> checking for updates failed! "
                        + "Repository cannot be verified " + repositoryFile);
            }
            System.out.println(
                    ">> VRLUpdater: repository cannot be verified!");
        }

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
            VMessage.warning("Cannot check for updates:",
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
            System.out.println(
                    " --> selected possible update: " + update.getName()
                    + "-" + update.getVersion());
        }

        if (update != null && action != null) {
            action.updateAvailable(this, d, updateURL, update);
        }
    }

    public void downloadUpdate(final RepositoryEntry update,
            final VRLDownloadAction action) {

        if (isDownloadingUpdate() || isDownloadingRepository()) {
            System.out.println(">> VRLUpdater: download in progress."
                    + " Please wait!");
            return;
        }

        URL downloadURL = null;

        try {
            downloadURL = new URL(update.getUrl());
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
            if (action != null) {
                action.errorOccured(null, updateURL,
                        "bad URL = " + update.getUrl());
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

        synchronized (updateDownloadLock) {
            updateDownload = new Download(
                    downloadURL,
                    downloadLocation,
                    connectionTimeout,
                    readTimeout);

            updateDownload.addObserver(new Observer() {
                private long timestamp;

                @Override
                public void update(Observable o, Object o1) {
                    Download d = (Download) o;

                    long currentTime = System.currentTimeMillis();

                    if (timestamp == 0 || currentTime - timestamp > 1000) {
                        System.out.println(
                                ">> VRLUpdater: downloading update "
                                + d.getProgress() + "%");
                        timestamp = currentTime;

                        action.downloadStateChanged(d);
                    }

                    if (d.getStatus() == Download.COMPLETE) {
                        System.out.println(
                                ">> VRLUpdater: downloading update "
                                + d.getProgress() + "%");

                        action.downloadStateChanged(d);

                        synchronized (updateDownloadLock) {
                            updateDownload = null;
                        }

                        System.out.println(
                                " --> finished download: "
                                + d.getTargetFile());

                        action.startVerification(d);

                        if (isVerificationEnabled()) {
                            verificationSuccessful
                                    = d.verifySHA1(update.getSHA1Checksum());
                        }

                        action.stopVerification(d, verificationSuccessful);

                        if (action != null) {
                            action.finished(d, d.getUrl());
                        }
                    }
                }
            });
        }
    }

    private RepositoryEntry findUpdate() {
        synchronized (updatesLock) {
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

                boolean lessThan = vInfo.compareTo(min) < 0;
                boolean equal = vInfo.compareTo(min) == 0;

                // check whether less or equal + shorter, i.e.,
                // 0.1.2.0 and 0.1.2
                // the compare method compares only the min number of digits, in
                // this case 0.1.2
                if (lessThan
                        || (equal && vInfo.getVersion().length()
                        < min.getVersion().length())) {
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

        try {
            this.updateSignatureURL = new URL(this.updateURL.toExternalForm() + ".asc");
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRLUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private List<RepositoryEntry> searchForPossibleUpdates(
            Repository repository) {

        List<RepositoryEntry> updates
                = new ArrayList<RepositoryEntry>();

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

            boolean bigger = vInfo.compareTo(getIdentifier().getVersion()) > 0;
            boolean equal = vInfo.compareTo(getIdentifier().getVersion()) == 0;

            // check whether bigger or equal + longer, i.e.,
            // 0.1.2.0 and 0.1.2
            // the compare method compares only the min number of digits, in
            // this case 0.1.2
            if (bigger
                    || (equal && vInfo.getVersion().length()
                    > getIdentifier().getVersion().getVersion().length())) {
                updates.add(e);
                System.out.println(
                        " --> update = "
                        + e.getName() + "-" + e.getVersion());
            }
        }

        return updates;
    }

    /**
     * @return the verificationEnabled
     */
    public boolean isVerificationEnabled() {
        return verificationEnabled;
    }

    /**
     * @param verificationEnabled the verificationEnabled to set
     */
    public void setVerificationEnabled(boolean verificationEnabled) {
        this.verificationEnabled = verificationEnabled;
    }

    /**
     * @return the verificationSuccessful
     */
    public boolean isVerificationSuccessful() {
        return verificationSuccessful;
    }

    /**
     * @return the customPublicKey
     */
    public File getCustomPublicKey() {
        return customPublicKey;
    }

    /**
     * @param customPublicKey the customPublicKey to set
     */
    public void setCustomPublicKey(File customPublicKey) {
        this.customPublicKey = customPublicKey;
    }
} // end class VRLUpdater
