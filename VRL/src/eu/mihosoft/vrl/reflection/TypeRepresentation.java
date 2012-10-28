/* 
 * TypeRepresentation.java
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

//import java.util.Observable;
import eu.mihosoft.vrl.visual.Canvas;

/**
 * Defines an interface for type representation.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface TypeRepresentation {

    /**
     * Returns the main canvas.
     *
     * @return the main canvas
     */
    public Canvas getMainCanvas();

//    public void addListener(InputChangedListener l);
//    public void removeListener(InputChangedListener l);
    /**
     * Returns the type of the representation.
     *
     * @return the type of the representation
     */
//    public Class getType();

    /**
     * Returns the name of the value.
     *
     * @return the name of the value
     */
    public String getValueName();

    /**
     * Defines the name of the value.
     *
     * @param name the name of the value
     */
    public void setValueName(String name);

//    /**
//     * Returns a deep copy of the type representation.
//     *
//     * @return
//     */
//    public TypeRepresentation copy();

//    public void refresh(); 
    /**
     * Assigns a new value to the type representation.
     *
     * @param o a new value to the type representation
     */
    public void setValue(Object o);

    /**
     * Returns the value that is assigned to the type representation.
     *
     * @return the value that is assigned to the type representation
     */
    public Object getValue();

    /**
     * Defines whether this type representation shall be an input
     * representation.
     *
     * @param representationType the representation type to set
     */
    public void setCurrentRepresentationType(RepresentationType representationType);

//    /**
//     * Adds a representation type to the supported representation types of this
//     * type representation.
//     *
//     * @param representationType the representation type to add
//     */
//    public void addSupportedRepresentationType(RepresentationType representationType);

    /**
     * Indicates whether this type representation is an input.
     *
     * @return
     * <code>true</code> if this type representation is an input;
     * <code>false</code> otherwise
     */
    public boolean isInput();

    /**
     * Indicates whether this type representation is an output.
     *
     * @return
     * <code>true</code> if this type representation is an output;
     * <code>false</code> otherwise
     */
    public boolean isOutput();

//    /**
//     * Indicates whether this type representation supports input representation
//     * type.
//     *
//     * @return
//     * <code>true</code> if this type representation supports input
//     * representation;
//     * <code>false</code> otherwise
//     */
//    public boolean supportsInput();
//
//    /**
//     * Indicates whether this type representation supports output representation
//     * type.
//     *
//     * @return
//     * <code>true</code> if this type representation supports output
//     * representation;
//     * <code>false</code> otherwise
//     */
//    public boolean supportsOutput();

//    /**
//     * Indicates whether this type representation supports the given
//     * representation type.
//     *
//     * @return
//     * <code>true</code> if this type representation supports supports the given
//     * representation;
//     * <code>false</code> otherwise
//     */
//    public boolean supports(RepresentationType representationType);

    /**
     * Empties the representation view, e.g., a input text field.
     * <p><b>Warning:</b> this method must not change the data of the type
     * representation as this operation is performed on the ui thread.
     * Synchronization with other threads/methods manipulating the data of this
     * type representation cannot be guaranteed.</p>
     */
    public void emptyView();

    /**
     * Defines the content of the representation view, e.g. the content of a
     * text field.
     *
     * <p><b>Warning:</b> this method must not change the data of the type
     * representation as this operation is performed on the ui thread.
     * Synchronization with other threads/methods manipulating the data of this
     * type representation cannot be guaranteed.</p>
     *
     * @param o the view value (view content)
     */
    public void setViewValue(Object o);

    /**
     * Returns the view value, e.g. the content of a text field.
     * 
     * <p><b>Warning:</b> this method must not change the data of the type
     * representation as this operation is performed on the ui thread.
     * Synchronization with other threads/methods manipulating the data of this
     * type representation cannot be guaranteed.</p>
     *
     * @return the view value (view content)
     */
    public Object getViewValue();
}
