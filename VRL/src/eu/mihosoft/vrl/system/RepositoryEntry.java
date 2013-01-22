/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.system;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class RepositoryEntry {

    private String name;
    private String version;
    private String SHA1Checksum;
    private String url;

    public RepositoryEntry() {
    }

    public RepositoryEntry(
            String name, String version, String sha1Checksum, String url) {
        this.name = name;
        this.version = version;
        this.SHA1Checksum = sha1Checksum;
        this.url = url;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the sha1Checksum
     */
    public String getSHA1Checksum() {
        return SHA1Checksum;
    }

    /**
     * @param sha1Checksum the sha1Checksum to set
     */
    public void setSHA1Checksum(String sha1Checksum) {
        this.SHA1Checksum = sha1Checksum;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
