/* 
 * LoadFileObservable.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.types.observe;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Stores the data info to a file. Several files are possible and destinguished
 * via tags. Implementation is via a Singleton and the Observer Pattern.
 * Observer can register at this Observable using a tag and will be notified
 * once the data info associated with a tag has changed.
 *
 * @author andreasvogel
 */
public class LoadFileObservable {

    /**
     * private constructor for singleton
     */
    private LoadFileObservable() {
    }
    private static volatile LoadFileObservable instance = null;

    /**
     * return of singleton instance
     *
     * @return instance of singleton
     */
    public static LoadFileObservable getInstance() {
        if (instance == null) {
            synchronized (LoadFileObservable.class) {
                if (instance == null) {
                    instance = new LoadFileObservable();
                }
            }
        }
        return instance;
    }

    /**
     * Info class for a tag. Stored are all observer listening to the tag and
     * the file data associated with a tag.
     */
    private class FileTag {

        public Collection<LoadFileObserver> observers = new HashSet<LoadFileObserver>();
        public File data = null;
    }

    /**
     * Identifier for grid loader strings. If several loaders exist, they are
     * destinguished by tag, objectID and windowID
     */
    private class Identifier {

        public Identifier(String tag, Object object, int windowID) {
            this.tag = tag;
            this.object = object;
            this.windowID = windowID;
        }
        private String tag;
        private Object object;
        private int windowID;

        @Override
        public int hashCode() {
            int result = 17;
            result = 37 * result + tag.hashCode();
            result = 37 * result + object.hashCode();
            result = 37 * result + windowID;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }

            Identifier rhs = (Identifier) obj;

            return (tag.equals(rhs.tag)) && (object == rhs.object) && (windowID == rhs.windowID);
        }
    }
    /**
     * Map storing data and observers associated with a tag
     */
    private transient Map<Identifier, FileTag> tags = new HashMap<Identifier, FileTag>();

    /**
     * returns the file tag info for a tag. If create is set to true a new tag
     * entry is create, else not. If no tag exists and create is set to false
     * null is returned.
     *
     * @param tag tag name
     * @param create flag indicating if tag should be created if it does not
     * exists
     * @return file tag info
     */
    private synchronized FileTag getTag(String tag, Object object, int windowID, boolean create) {
        Identifier id = new Identifier(tag, object, windowID);

        if (tags.containsKey(id)) {
            return tags.get(id);
        }

        if (create) {
            tags.put(id, new FileTag());
            return getTag(tag, object, windowID, false);
        }

        return null;
    }

    /**
     * Add an observer to this Observable. The observer listens to a tag. The
     * observer will be updated with the current data automatically.
     *
     * @param obs the observer to add
     * @param tag the tag
     */
    public synchronized void addObserver(LoadFileObserver obs, String tag, Object object, int windowID) {
        getTag(tag, object, windowID, true).observers.add(obs);
        obs.update(getTag(tag, object, windowID, false).data);
    }

    /**
     * Removes an observer from this Observable.
     *
     * @param obs the observer to remove
     * @param tag the tag
     */
    public synchronized void deleteObserver(LoadFileObserver obs, String tag, Object object, int windowID) {
        Identifier id = new Identifier(tag, object, windowID);
        if (tags.containsKey(id)) {
            tags.get(id).observers.remove(obs);
        }
    }

    /**
     * Removes an observer from this Observable.
     *
     * @param obs the observer to remove
     * @param tag the tag
     */
    public synchronized void deleteObserver(LoadFileObserver obs) {

        for (Map.Entry<Identifier, FileTag> entry : tags.entrySet()) {
            entry.getValue().observers.remove(obs);
        }
    }

    /**
     * Removes all observer of a tag from this Observable.
     *
     * @param tag the tag
     */
    public synchronized void deleteObservers(String tag, Object object, int windowID) {
        Identifier id = new Identifier(tag, object, windowID);
        if (tags.containsKey(id)) {
            tags.get(id).observers.clear();
        }
    }

    /**
     * Notifies all observers of a tag about the currently given data
     *
     * @param tag the tag
     */
    public synchronized void notifyObservers(String tag, Object object, int windowID) {
        // get data for tag
        FileTag fileTag = getTag(tag, object, windowID, false);

        // if no such tag present, return (i.e. no observer)
        if (fileTag == null) {
            return;
        }

        // notify observers of this tag
        for (LoadFileObserver b : fileTag.observers) {
            b.update(fileTag.data);
        }
    }

    /**
     * sets a filename for a tag. The file will be analysed and the contained
     * data will be broadcasted to all observer of the tag.
     *
     * @param file the file
     * @param analyser the analyser for the file
     * @param tag the tag
     * @return empty string if successful, error-msg if error occured
     */
    public synchronized String setSelectedFile(File file, String tag, Object object, int windowID) {

        FileTag fileTag = getTag(tag, object, windowID, true);

        fileTag.data = file; 

        // now we notify the obersver of this tag
        notifyObservers(tag, object, windowID);

        return "";
    }

    public synchronized File getSelectedFile(String tag, Object object, int windowID) {

        FileTag fileTag = getTag(tag, object, windowID, true);

        return fileTag.data;
    }

    /**
     * Sets that a tag has an invalid file.
     *
     * @param tag the tag
     */
    public synchronized void setInvalidFile(String tag, Object object, int windowID) {
        FileTag fileTag = getTag(tag, object, windowID, true);

        //  set to new (empty) data
        fileTag.data = null;

        // now we notify the obersver of this tag
        notifyObservers(tag, object, windowID);
    }
}
