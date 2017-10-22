/* 
 * AbstractCodes.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.MessageType;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A list of abstract codes.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class AbstractCodes extends ArrayList<AbstractCode> {

    private static final long serialVersionUID = 6305325062600541128L;
    public static final String NAME_KEY = "abstract-codes";

    /**
     * Compiles the codes and adds the classes to the class loader of
     * the specified main canvas.
     * @param mainCanvas the main canvas
     */
    public void addToClassPath(VisualCanvas mainCanvas) {
        for (AbstractCode code : this) {
            try {
                code.addToClassPath(mainCanvas);
            } catch (Exception ex) {
                mainCanvas.getMessageBox().addUniqueMessage(
                        "Error while loading codes!",
                        ex.toString(), null, MessageType.ERROR);

                if (ex.getCause() != null) {

                    mainCanvas.getMessageBox().addUniqueMessage("Error Message!",
                            ex.getCause().toString(), null, MessageType.ERROR);
                }

                Logger.getLogger(AbstractCodes.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Removes unused codes, i.e., codes that are no components and that don't
     * have instances that are added to the main canvas.
     * @param mainCanvas the main canvas
     */
    public void removeUnusedCodes(VisualCanvas mainCanvas) {
        ArrayList<AbstractCode> delList = new ArrayList<AbstractCode>();

        for (AbstractCode a : this) {

            int numberOfInstances = mainCanvas.getInspector().
                    getObjectsByClassName(a.getName()).size();

            boolean classInUse = numberOfInstances > 0 || a.isComponent();

//            System.out.println("N: " + a.getName() + " I: " +
//                    numberOfInstances);

            if (!classInUse) {
                delList.add(a);
            }
        }

        for (AbstractCode a : delList) {
            remove(a);
        }
    }

//    public boolean providesClass(Class c) {
//        boolean result = false;
//        for (AbstractCode a : this) {
//            if (a.getName().equals(c.getName())) {
//                result = true;
//                break;
//            }
//        }
//        return result;
//    }
    /**
     * Returns abstract code by class.
     * @param c the class of the code that is to be returned
     * @return the code that defines the specified class if the corresponding
     * code is an element of this list;<code>null</code> otherwise
     */
    public AbstractCode getByClass(Class c) {
        AbstractCode result = null;
        for (AbstractCode a : this) {
            if (a.getName().equals(c.getName())) {
                result = a;
            }
        }
        return result;
    }

    /**
     * Returns abstract code by name.
     * @param name the name (class name) of the code that is to be returned
     * @return the code that defines the specified class if such a code exists;
     * <code>null</code> otherwise
     */
    public AbstractCode getByName(String name) {
        AbstractCode result = null;

        for (AbstractCode a : this) {
            if (a.getName().equals(name)) {
                result = a;
                break;
            }
        }

        return result;
    }

    /**
     * Removes code from the list by name.
     * @param name the name of the code that is to be deleted
     * @return <code>true</code> if the code could be found and deleted;
     * <code>false</code> otherwise
     */
    public boolean removeByName(String name) {
        AbstractCode a = getByName(name);
        boolean result = a != null;

        if (a != null) {
            result = remove(a);
            
            System.out.println("Remove Code: " + name);
        }
        return result;
    }

    @Override
    public boolean add(AbstractCode a) {
        if (a.getName() != null) {
            removeByName(a.getName());
        }
        return super.add(a);
    }

    public void setPackageName(String packageName) {
        for (AbstractCode c : this) {
            c.setPackageName(packageName);
        }
    }
}
