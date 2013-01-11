/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.system;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Repository {
    private List<RepositoryEntry> entries =
            new ArrayList<RepositoryEntry>();

    public Repository() {
    }
    
    public void addEntry(RepositoryEntry entry) {
        entries.add(entry);
    }
    
    public boolean removeEntry(RepositoryEntry entry) {
        return entries.remove(entry);
    }

    /**
     * @return the entries
     */
    public List<RepositoryEntry> getEntries() {
        return entries;
    }

    /**
     * @param entries the entries to set
     */
    public void setEntries(List<RepositoryEntry> entries) {
        this.entries = entries;
    }
    
    
}
