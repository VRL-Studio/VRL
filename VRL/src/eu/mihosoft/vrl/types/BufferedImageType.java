/* 
 * BufferedImageType.java
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

package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.PlotPane;
import eu.mihosoft.vrl.reflection.*;
import eu.mihosoft.vrl.visual.FullScreenComponent;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VButton;
import eu.mihosoft.vrl.visual.VContainer;
import eu.mihosoft.vrl.visual.VGraphicsUtil;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.Box;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * TypeRepresentation for <code>java.awt.image.BufferedImage</code>.
 * 
 * Style name: "default"
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@TypeInfo(type=BufferedImage.class, input = true, output = true, style="default")
public class BufferedImageType extends TypeRepresentationBase
        implements TypeRepresentation, FullScreenComponent {

    private static final long serialVersionUID = 1988974656391008424L;
    protected PlotPane plotPane;
    protected Box container;
    protected Dimension plotPaneSize;
    protected Dimension previousPlotPaneSize;
    protected Dimension minimumPlotPaneSize;

    public BufferedImageType() {

        plotPane = new PlotPane(this);

        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
        setLayout(layout);

        setValueName("");
        nameLabel.setAlignmentY(0.5f);

        //this.add(nameLabel);

        setUpdateLayoutOnValueChange(false);


        setMinimumPlotPaneSize(new Dimension(70, 70));

//        plotPane.setAlignmentY(0.5f);
        plotPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1
                        && e.getClickCount() == 2) {
                    if (plotPane.getImage() != null || isInput()) {

                        setDataOutdated();

                        ImageView preview = new ImageView();
                        preview.setMainCanvas(getMainCanvas());
                        preview.setParentPreviewPane(plotPane);
                        preview.getPreviewPane().setImage(plotPane.getImage());
                        preview.pack();

                        if (isInput()) {
                            preview.setLoadButtonEnabled(true);
                            preview.setSaveButtonEnabled(false);
                        }

                        if (isOutput()) {
                            preview.setLoadButtonEnabled(false);
                            preview.setSaveButtonEnabled(true);
                        }

                        preview.setVisible(true);
                    }
                }
            }
        });

        this.add(Box.createHorizontalStrut(3));

        container = Box.createHorizontalBox();

        container.add(nameLabel);

        container.add(plotPane);

        this.add(container);

        this.setInputComponent(container);
//
//        this.add(plotPane);
    }

//    @Override
//    public TypeRepresentationBase copy() {
//        BufferedImageType bType = new BufferedImageType();
//        bType.setMinimumPlotPaneSize(plotPaneSize);
//        return bType;
//    }
    @Override
    public void emptyView() {
        plotPane.setImage(null);
    }

    @Override
    public void setViewValue(Object o) {
        setDataOutdated();
        plotPane.setImage((BufferedImage) o);
    }

    @Override
    public Object getViewValue() {
        return plotPane.getImage();
    }

    public Dimension getPlotPaneSize() {
        return plotPaneSize;
    }

    public boolean isKeepAspectRatio() {
        return plotPane.isFixedAspectRatio();
    }

    protected void setMinimumPlotPaneSize(Dimension plotPaneSize) {
        this.plotPaneSize = plotPaneSize;

        plotPane.setPreferredSize(plotPaneSize);
        plotPane.setMinimumSize(plotPaneSize);
        minimumPlotPaneSize = plotPaneSize;

        setValueOptions("width=" + plotPaneSize.width + ";"
                + "height=" + plotPaneSize.height);
    }

    private void setPlotPaneSizeFromValueOptions(Script script) {
        Object property = null;
        Integer w = null;
        Integer h = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("width")) {
                property = script.getProperty("width");
            }

//            System.out.println("Property:" + property.getClass());

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

            property = null;

            if (getValueOptions().contains("fixedAspectRatio")) {
                property = script.getProperty("fixedAspectRatio");
            }

            if (property != null) {
                plotPane.setFixedAspectRatio((Boolean)property);
            }
        }

        if (w != null && h != null) {
            // TODO find out why offset is 5
            plotPane.setPreferredSize(new Dimension(w - 5, h));
            plotPane.setSize(new Dimension(w - 5, h));
        }
    }

    @Override
    protected void evaluationRequest(Script script) {
        setPlotPaneSizeFromValueOptions(script);
    }

    @Override
    public void enterFullScreenMode(Dimension size) {
        super.enterFullScreenMode(size);
        previousPlotPaneSize = plotPane.getSize();
        container.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MAX_VALUE));
        container.setMinimumSize(null);
        container.setMaximumSize(null);

        plotPane.setPreferredSize(null);
        plotPane.setMinimumSize(null);
        plotPane.setMaximumSize(null);

        revalidate();
    }


    @Override
    public void leaveFullScreenMode() {
        super.leaveFullScreenMode();
        container.setPreferredSize(null);
        container.setMinimumSize(null);
        container.setMaximumSize(null);
        
        plotPane.setSize(previousPlotPaneSize);
        plotPane.setPreferredSize(previousPlotPaneSize);

        plotPane.setMinimumSize(minimumPlotPaneSize);
        

        revalidate();
    }

    @Override
    public JComponent customViewComponent() {
        
//        final BufferedImage img = plotPane.getImage();
//
//        JPanel panel = new JPanel() {
//            @Override
//            public void paintComponent(Graphics g) {
//                g.drawImage(img, 0, 0, 640, 480,  null);
//            }
//        };
//
//        return panel;
        return null;
    }
}

