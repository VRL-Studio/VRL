/* 
 * VisualObjects.java
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
package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.io.vrlx.AbstractWindow;
import eu.mihosoft.vrl.io.vrlx.AbstractWindows;
import eu.mihosoft.vrl.effects.EffectPainter;
import eu.mihosoft.vrl.effects.FadeEffect;
import eu.mihosoft.vrl.io.Base64;
import eu.mihosoft.vrl.types.Shape3DType;
import java.util.ArrayList;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.CanvasWindows;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.RemoveObjectTask;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.Serializable;
import java.util.Collections;

/**
 * Represents a list of all visual objects inside a VisualCanvas object.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VisualObjects extends CanvasWindows {

    private static final long serialVersionUID = -2042499969012562841L;

    public VisualObjects(VisualCanvas mainCanvas) {
        super(mainCanvas);
    }

    @Override
    public void removeObjectWithoutEffect(int ID) {
        if (getObject(ID) instanceof VisualObject) {
            VisualObject vObj = (VisualObject) getObject(ID);

            VisualCanvas mainCanvas = (VisualCanvas) getMainCanvas();

            int inspectorID = vObj.getObjectRepresentation().getObjectID();
            int visualID = vObj.getObjectRepresentation().getID();

            VisualObjectInspector inspector = mainCanvas.getInspector();

            ObjectTree objectTree = mainCanvas.getObjectTree();

            Object o = inspector.getObject(inspectorID);

            if (objectTree != null && !(o instanceof IgnoreObjectTree)) {
                objectTree.addObject(o);
            }

            // was used before multiple visualizations
            // inspector.removeObject(inspectorID);

            inspector.removeObjectRepresentation(inspectorID, visualID);
        }

        super.removeObjectWithoutEffect(ID);
    }

    /**
     * Removes object from list.
     *
     * @param ID the ID value of the object that is to be removed, valid range:
     * [0,MAX_INT]
     */
    public void removeObjectOnlyFromCanvas(int ID) {
        super.removeObject(ID);
    }

    /**
     * Removes all objects from list.
     */
    public void removeAllOnlyFromCanvas() {
        ArrayList<Integer> delList = new ArrayList<Integer>();

        for (CanvasWindow o : this) {
            delList.add(o.getID());
        }

        for (Integer i : delList) {
            removeObjectOnlyFromCanvas(i);
        }
    }

//    /**
//     * Saves this visual object list.
//     * @param e the xml encoder that is to be used for serialization
//     */
//    public void save(XMLEncoder e) {
//        deleteNonSerializableObjects();
//        AbstractObjects objects = new AbstractObjects();
//        Collections.sort(this);
//        for (CanvasWindow o : this) {
//            objects.add(new AbstractWindow(o));
//        }
//        e.writeObject(objects);
//    }
    /**
     * Load visual objects from file.
     *
     * @param d the xml decoder that is to be used for deserialization
     */
    // TODO maybe move this method to VisualCanvas or VisualObjectInspector
    public void load(XMLDecoder d) {

        Object result = d.readObject();

        AbstractWindows objects = (AbstractWindows) result;
        VisualCanvas canvas = (VisualCanvas) getMainCanvas();
        objects.addToCanvas(canvas);
    }

    /**
     * Deletes object that are not serializable from this list.
     */
    public void deleteNonSerializableObjects() {

        ArrayList<VisualObject> delList = new ArrayList<VisualObject>();

        for (CanvasWindow w : this) {

            if (w instanceof VisualObject) {
                VisualObject o = (VisualObject) w;

                VisualCanvas mainCanvas = (VisualCanvas) getMainCanvas();

                int inspectorID = o.getObjectRepresentation().getObjectID();

                Object result = mainCanvas.getInspector().getObject(inspectorID);

                if (!(result instanceof Serializable)) {
                    delList.add(o);
                } else {
                    String value = Base64.encodeObject((Serializable) result);
                    if (value == null) {
                        delList.add(o);
                    }
                }
            }
        }

        for (VisualObject vObj : delList) {

            if (vObj == null) {
                getMainCanvas().getMessageBox().addMessage(
                        "Error: object does not exist",
                        "Error: null obj in visual-object-list",
                        MessageType.ERROR);
                continue;
            }

            VisualCanvas canvas = (VisualCanvas) getMainCanvas();

            Object o = canvas.getInspector().
                    getObject(vObj.getObjectRepresentation().getObjectID());

            removeObjectWithoutEffect(vObj.getID());

            if (o == null) {
                getMainCanvas().getMessageBox().addMessage(
                        "Error: object does not exist",
                        "Error: object with id "
                        + vObj.getObjectRepresentation().getObjectID()
                        + " is null!",
                        MessageType.ERROR);

                System.err.println("Error: object with id "
                        + vObj.getObjectRepresentation().getObjectID()
                        + " is null!");

                continue;
            }
            
            boolean implementsIgnoreMarkerInterface = (o instanceof IgnoreNotSerializableWarnings);
            boolean serializableEnabledInObjectAnnotation = ComponentUtil.isSerializationEnabled(o.getClass());

            if (!implementsIgnoreMarkerInterface && serializableEnabledInObjectAnnotation) {

                getMainCanvas().getMessageBox().addMessage(
                        "Warning: Can't save some objects",
                        "Some objects are not serializable and had to be"
                        + " removed.<br><br> <p>Please ensure that all "
                        + "objects implement the <b><i>Serializable</i></b> "
                        + "interface and that properties don't prevent"
                        + " serialization. "
                        + "Use <b><i>transient</i></b> in this case.<p>"
                        + "Classes added from shell cannot be deserialized"
                        + " because source code definition is not available."
                        + " This can be fixed by adding a new "
                        + "<b><i>AbstractCode</i></b> to the canvas before"
                        + " saving the session.",
                        MessageType.WARNING);
            }
        }
    }
//    public void updateContentProviders(){
//        for (CanvasWindow window : this){
//
//        }
//    }
}
