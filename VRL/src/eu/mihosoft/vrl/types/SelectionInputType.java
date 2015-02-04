/* 
 * SelectionInputType.java
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

package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.RepresentationType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;

/**
 * JComboBox based type representation for
 * {@link eu.mihosoft.vrl.types.Selection}.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type = Selection.class, input = true, output = false, style = "default")
public class SelectionInputType extends TypeRepresentationBase {

    private final JComboBox selectionView = new JComboBox();
    private Selection selectionModel;
    public static final String SELECTION_CHANGED_ACTION =
            "selection-input-type:selection-changed";

    /**
     * Constructor
     */
    public SelectionInputType() {
        setValueName("Selection: ");

        add(nameLabel);
        add(selectionView);

        // START evaluate registered Listeners
        
//        //         init fire to evaluate default selection
//        // init DO NOT WORK because ActionListeners are added if the coresponding
//        // component (component that uses this typerepresentation) is added to canvas
//        // therefore you need to make in these component an init fireAction
//        // (dirty solution but needed at the moment)
//            fireAction(new ActionEvent(this, 0, SELECTION_CHANGED_ACTION));
//        
        
        // now react on changes
        selectionView.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ie) {

                fireAction(new ActionEvent(this, 0, SELECTION_CHANGED_ACTION));

            }
        });
        //END evaluate registered Listeners

        selectionView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                getMainCanvas().getMessageBox().addMessage("Debug",
//                        "Action: " + e.getActionCommand(),
//                        MessageType.INFO);

                boolean isLoading = false;

                // check whether this action event is a mouse event or if it is
                // caused by session loading
                if (getMainCanvas() != null) {
                    VisualCanvas canvas = (VisualCanvas) getMainCanvas();
                    isLoading = canvas.isLoadingSession();
                }

                // if the event is a mouse event and the selection model exists
                // change the model according to the view
                if (selectionModel != null && !isLoading) {
                    selectionModel.setSelectedIndex(
                            getSelectionView().getSelectedIndex());
                    selectionModel.setSelectedObject(
                            getSelectionView().getSelectedItem());
                    setDataOutdated();
                }
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked") // we must be compatible with 1.6
    public void setViewValue(Object o) {
        emptyView();


        super.setViewValue(o);

        if (o instanceof Selection) {
            selectionModel = (Selection) o;
            for (Object object : selectionModel.getCollection()) {
                getSelectionView().addItem(object);
            }

            int index = 0;

            if (selectionModel.getSelectedIndex() != null) {
                index = selectionModel.getSelectedIndex();
            }

            getSelectionView().
                    setSelectedIndex(index);
            
            //notify Listeners that selection has may changed
            fireAction(new ActionEvent(this, 0, SELECTION_CHANGED_ACTION));
        }


    }

    @Override
    public Object getViewValue() {
        return selectionModel;
    }

    @Override
    public void emptyView() {
        selectionModel = null;
        getSelectionView().removeAllItems();
    }

    /**
     * Returns the selection view.
     *
     * @return the selection view
     */
    public JComboBox getSelectionView() {
        return selectionView;
    }
}
