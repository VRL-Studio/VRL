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
    private String path; 

    public RepositoryEntry() {
    }

    public RepositoryEntry(String name, String version, String sha1Checksum, String path) {
        this.name = name;
        this.version = version;
        this.SHA1Checksum = sha1Checksum;
        this.path = path;
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
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
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
    
    
}
