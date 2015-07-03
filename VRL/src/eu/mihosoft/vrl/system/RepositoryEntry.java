/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.system;

import java.io.Serializable;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class RepositoryEntry implements Serializable {

    private static final long serialVersionUID = 1L;
    
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

    @Override
    public String toString() {
        return "name:     " + getName() + "\n"
                + "version:  " + getVersion() + "\n"
                + "checksum: " + getSHA1Checksum() + "\n"
                + "url:      " + getUrl() + "\n";
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RepositoryEntry other = (RepositoryEntry) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }
    
    
}
