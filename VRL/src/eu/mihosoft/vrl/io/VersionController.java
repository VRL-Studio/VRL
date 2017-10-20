/* 
 * VersionController.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2012 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2012 by Michael Hoffer
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

package eu.mihosoft.vrl.io;

import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.jgit.revwalk.RevCommit;




/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface VersionController {

    /**
     * Checkout first version, i.e., version 1.
     * @throws IOException
     */
    void checkoutFirstVersion() throws IOException;

    /**
     * Checkout latest version, i.e., version with highest version number.
     * @throws IOException
     */
    void checkoutLatestVersion() throws IOException;

    /**
     * Checks out next version. Throws an {@link IllegalStateException}
     * if if such a version does not exist.
     * @return this file
     * @throws IOException
     * @throws IllegalStateException if the specified version does not exist
     */
    void checkoutNextVersion() throws IOException;

    /**
     * Checks out previous version. Throws an {@link IllegalStateException}
     * if if such a version does not exist.
     * @return this file
     * @throws IOException
     * @throws IllegalStateException if the specified version does not exist
     */
    void checkoutPreviousVersion() throws IOException;

    /**
     * Checks out the specified version.
     * @param i version to checkout (version counting starts with <code>1</code>)
     * @return this file
     * @throws IOException
     * @throws IllegalArgumentException if the specified version does not exist
     */
    void checkoutVersion(int i) throws IOException;

    /**
     * <p>Deletes the complete history of this file keeping only the latest
     * version, i.e., the version with the highest version number.</p>
     * <p><b>Warning:</b>Uncommited
     * changes will be lost. This action cannot be undone!</p>
     * @return this file
     * @throws IllegalStateException if this file is currently not open
     */
    void deleteHistory() throws IOException;

    /**
     * Returns the number of the current version.
     * @return the number of the current version
     * @throws IllegalStateException if this file is currently not open
     */
    int getCurrentVersion();

    /**
     * Returns the number of versions.
     * @return the number of versions or <code>-1</code> if an error occured
     * @throws IllegalStateException if this file is currently not open
     */
    int getNumberOfVersions();

    /**
     * Returns a list containing commit objects of all versions. This method
     * can be used to show the version messages, e.g., for creating a ui that
     * does allow the selection of the version that shall be checked out.
     * @return a list containing commit objects of all versions
     * @throws IOException
     * @throws IllegalStateException if this file is currently not open
     */
    ArrayList<RevCommit> getVersions() throws IOException;

    /**
     * Determines whether a version with version number
     * <code>currentVersion+1</code> exists.
     * @return <code>true</code> if a next version exists
     * @throws IllegalStateException if this file is currently not open
     */
    boolean hasNextVersion();

    /**
     * Determines whether a version with version number
     * <code>currentVersion-1</code>
     * exists. Version counting starts with <code>1</code>.
     * Version <code>0</code> is for internal use only and cannot be accessed.
     * @return <code>true</code> if a previous version exists
     * @throws IllegalStateException if this file is currently not open
     */
    boolean hasPreviousVersion();
    
    public void addVersionEventListener(VersionEventListener l );
    
    public void removeVersionEventListener(VersionEventListener l );
    
    public void removeAllVersionEventListeners();
    
    public Iterable<VersionEventListener> getVersionEventListeners();
    
}
