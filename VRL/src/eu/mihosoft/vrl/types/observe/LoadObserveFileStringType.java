/* 
 * LoadObserveFileStringType.java
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

package eu.mihosoft.vrl.types.observe;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.reflection.LayoutType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.types.SelectionInputType;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VBoxLayout;
import groovy.lang.Script;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;

/**
 *
 * @author Andreas Vogel <andreas.vogel@gcsc.uni-frankfurt.de>
 */
@TypeInfo(type = String.class, input = true, output = false, style = "observe-load-dialog")
public class LoadObserveFileStringType extends TypeRepresentationBase implements LoadFileObserver {

    // the possible selection to display
    JComboBox selection = new JComboBox();
//    /// filter to restrict to ugx file
//    private VFileFilter fileFilter = new VFileFilter();
    /// the current tag
    private String tag = null;
//    
    FileAnalyzer analyzer = null;

    /**
     * Constructor.
     */
    public LoadObserveFileStringType() {

        // create a Box and set it as layout
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
        setLayout(layout);
        setLayoutType(LayoutType.STATIC);

        // set the name label
        nameLabel.setText("File Name:");
        nameLabel.setAlignmentX(0.0f);
        add(nameLabel);

        add(selection);

        // hide connector, since no external data allowed
        setHideConnector(true);

        // START evaluate registered Listeners

//        //         init fire to evaluate default selection
//        // init DO NOT WORK because ActionListeners are added if the coresponding
//        // component (component that uses this typerepresentation) is added to canvas
//        // therefore you need to make in these component an init fireAction
//        // (dirty solution but needed at the moment)
//            fireAction(new ActionEvent(this, 0, SELECTION_CHANGED_ACTION));
//        

        // now react on changes
        selection.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ie) {

                fireAction(new ActionEvent(this, 0, SelectionInputType.SELECTION_CHANGED_ACTION));

            }
        });

        selection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean isLoading = false;

                // check whether this action event is a mouse event or if it is
                // caused by session loading
                if (getMainCanvas() != null) {
                    VisualCanvas canvas = (VisualCanvas) getMainCanvas();
                    isLoading = canvas.isLoadingSession();
                }

                // if the event is a mouse event and the selection model exists
                // change the model according to the view
                if (!isLoading) {
                    setDataOutdated();
                }
            }
        });

        //END evaluate registered Listeners
    }

    @Override
    public void setViewValue(Object o) {

        super.setViewValue(o);

        selection.setSelectedItem(o);
    }

    @Override
    public Object getViewValue() {
        if (selection.getSelectedItem() == null) {
            return "";
        } else {

            return selection.getSelectedItem();
        }
//        return input.getText();
    }

    @Override
    public void emptyView() {
//        input.setText("");
        selection.removeAllItems();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void evaluationRequest(Script script) {
        super.evaluationRequest(script);

        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("tag")) {
                property = script.getProperty("tag");
            }

            if (property != null) {
                tag = (String) property;
            }

            // check if an analyser name is set in ParamInfo

            if (getValueOptions().contains("fileAnalyzer")) {
                property = script.getProperty("fileAnalyzer");

                if (property != null) {
                    String analyserName = (String) property;

                    analyzer = VTypeObserveUtil.getFileAnanlyzerByName(analyserName);
                }
            }
        }

        if (tag == null) {
            getMainCanvas().getMessageBox().addMessage("Invalid ParamInfo option",
                    "ParamInfo for ugx-subset-selection requires tag in options",
                    getConnector(), MessageType.ERROR);
        }

        if (analyzer == null) {
            getMainCanvas().getMessageBox().addMessage("Invalid ParamInfo option",
                    "ParamInfo for observable-file-selection requires 'fileAnalyzer' in options",
                    getConnector(), MessageType.ERROR);
        }

    }

    @Override
    public void addedToMethodRepresentation() {
        super.addedToMethodRepresentation();

        // register at the observable for loaded file if tag given
        if (tag != null) {
            int id = this.getParentMethod().getParentObject().getObjectID();
            Object o = ((VisualCanvas) getMainCanvas()).getInspector().getObject(id);
            int windowID = 0;
            LoadFileObservable.getInstance().addObserver(this, tag, o, windowID);
        }

    }

    @Override
    public String getValueAsCode() {
        return "\"" + VLangUtils.addEscapesToCode(getValue().toString()) + "\"";
    }

    @Override
    // reasons for @SuppressWarnings("unchecked")
    // selection uses no generics because we want to be compatible with
    // java 1.6 which does not support the necessary generics in swing classes
    @SuppressWarnings("unchecked")
    public void update(File data) {
//        System.out.println("Update called: " + data);

        String selectedItem = (String) selection.getSelectedItem();
        if (selectedItem == null) {
            selectedItem = "";
        }
        selection.removeAllItems();

        List<String> list = new ArrayList<String>();
        list.addAll(analyzer.analyzeFile(data));
        for (String s : list) {

            selection.addItem(s);
            if (selectedItem.equals(s)) {
                selection.setSelectedItem(selectedItem);
            }
        }
    }
}
