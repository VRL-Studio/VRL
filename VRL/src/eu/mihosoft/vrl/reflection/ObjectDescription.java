/* 
 * ObjectDescription.java
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
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.annotation.ObjectInfo;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Describes an object with all informations accessible from the Java Reflection 
 * API, except annotations.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class ObjectDescription implements Serializable {

    private static final long serialVersionUID = -6183131209730973578L;
    /**
     * Object Info
     */
    private ObjectInfo info;
    /**
     * Methods of the object
     */
    private ArrayList<MethodDescription> methods =
            new ArrayList<MethodDescription>();
    /**
     * Name of the object
     */
    private String name = "";
    /**
     * ID of the object
     */
    private int ID = 0;

    /**
     * Constructor.
     */
    public ObjectDescription() {
        // code
    }

    /**
     * Constructor.
     * @param name name of the object
     * @param ID ID of the object
     * @param info the object info
     * @param methods methods of the object
     */
    public ObjectDescription(String name, int ID,
            ObjectInfo info,
            ArrayList<MethodDescription> methods) {
        setName(name);
        setID(ID);
        setMethods(methods);
    }

    /**
     * Adds a method to the object
     * @param m method that is to be added to the object
     */
    public void addMethod(MethodDescription m) {
        m.setObjectID(getID());
        methods.add(m);
    }

    /**
     * Returns all methods of the object.
     * @return all methods of the object
     */
    public ArrayList<MethodDescription> getMethods() {
        return methods;
    }

    /**
     * Defines all methods of the object.
     * @param methods all methods that are to be added to the object
     */
    public void setMethods(ArrayList<MethodDescription> methods) {
        this.methods = methods;
    }

    /**
     * Returns the object ID.
     * @return the object ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Defines the object ID.
     * @param ID the object ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Returns the name of the object.
     * @return  the name of the object
     */
    public String getName() {
        return name;
    }

    /**
     * Defines the name of the object.
     * @param name the name of the object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the info
     */
    public ObjectInfo getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(ObjectInfo info) {
        this.info = info;
    }
    
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
    
    /**
     * Returns a signature string that is unique for each interface except for
     * modifiers. VRL generally ignores private methods. Visualizations of 
     * static and non-static methods are identical. Thus, modifiers are
     * not part of the signature.
     * @return a unique signature string
     */
    public String getSignature() {
        String infoString = ";INFO=";
        
        if (info!=null) {
            infoString = info.toString();
        }
        String result="TYPE=CLASS;ID=" + ID + ";NAME=" + name + infoString
                + ";METHODS=[";
        
        for(MethodDescription m : methods) {
            result += m.toString() + ";";
        }
        
        result+="];";
        
        return result;
    }
    
    @Override
    public String toString() {
        
        return getSignature();
    }

}
