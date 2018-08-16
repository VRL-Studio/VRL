/* 
 * JComponentType.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2018 by Michael Hoffer
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
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.ResizableContainer;
import java.awt.event.ActionEvent;
import javax.swing.*;

import java.awt.*;

import eu.mihosoft.vrl.visual.TransparentPanel;
import groovy.lang.Script;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


/**
 * Parent type for UI type representations. Can also be used directly as type
 * representation by returning <code>JComponent</code> objects.
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type = JComponent.class, input = false, output = true, style = "default")
public class JComponentType extends TypeRepresentationBase implements java.io.Serializable {

    private static final long serialVersionUID = 1;
//   
    private JPanel componentContainer;
    private JComponent jComponent;

    public JComponentType() {
     
        setValueName(""); // name of the visualization

        // Set layout
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setPreferredSize(new Dimension(300, 200));
        this.setMinimumSize(new Dimension(200, 120));

        // Get the panel from the xychart
        componentContainer = new TransparentPanel();
        
        // Set layout
        componentContainer.setLayout(new BorderLayout());

        // Add ruler
        ResizableContainer container = new ResizableContainer(componentContainer);

        add(container);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setValueOptions("width=" + getWidth() + ";"
                        + "height=" + getHeight());
            }
        });

    }


    @Override
    public void setViewValue(Object o) {

        if (o instanceof JComponent) {

            final JComponent chart = (JComponent) o;

            updatePanel(componentContainer, chart);

        }
    }

    protected void updatePanel(
            Container container,
            final JComponent panel) {

        if (jComponent != null) {
            componentContainer.remove(jComponent);
        }

        componentContainer.add(panel);

//        JPopupMenu menu = chartPanel.getPopupMenu();
//
//        menu.addSeparator();
//        JMenuItem item1 = new JMenuItem("Export");
//        item1.setActionCommand("export_chart");
//        item1.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                String cmd = e.getActionCommand();
//
//                if (cmd.equals("export_chart")) {
//
//                    try {
//                        new JFExport().openExportDialog(jFreeChart);
//                    } catch (Exception e1) {
//                        e1.printStackTrace(System.err);
//                    }
//
//                }
//            }
//        });
//        menu.add(item1);

        revalidate();
    }

    /**
     * Get the JFXYChartParameters when a VRL session is saved.
     *
     * @return JFXYChartParameters
     */
    @Override
    public Object getViewValue() {
        return null;
    }

    @Override
    public void emptyView() {
        if (jComponent != null) {
            componentContainer.remove(jComponent);
        }
    }

    @Override
    protected void evaluationRequest(Script script) {
        Integer w = null;
        Integer h = null;
        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("width")) {
                property = script.getProperty("width");
            }

            if (property != null) {
                w = (Integer) property;
            }

            property = null;

            if (getValueOptions().contains("height")) {
                property = script.getProperty("height");
            }

            if (property != null) {
                h = (Integer) property;
            }
        }

        if (w != null && h != null) {
            setPreferredSize(new Dimension(w, h));
            setMinimumSize(new Dimension(w, h));
            setSize(new Dimension(w, h));
        }
    }
}
