/* 
 * ColorType.java
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
package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.RepresentationType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.VBoxLayout;
import groovy.lang.Script;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Box;

/**
 * TypeRepresentation for
 * <code>java.awt.Color</code>. <p>Sample:</p> <br/> <img
 * src="doc-files/color-default-01.png"/> <br/>
 * 
 * Style name: "default"
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@TypeInfo(type = Color.class, input = true, output = true, style = "default")
public class ColorType extends TypeRepresentationBase {

    private static final long serialVersionUID = -1432892124007526692L;
    private Color color;
    protected StaticPlotPane container;
    private Dimension plotPaneSize;

    public ColorType() {

        container = new StaticPlotPane(this);

        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.X_AXIS);
        setLayout(layout);

        nameLabel.setText("Color:");
        nameLabel.setAlignmentY(0.5f);

        this.add(nameLabel);

        setPlotPaneSize(new Dimension(50, 50));

        container.setAlignmentY(0.5f);

        this.add(Box.createHorizontalStrut(3));
        this.add(container);

        setViewValue(color);
        setUpdateLayoutOnValueChange(false);
    }

    @Override
    public void setViewValue(Object o) {

        if (o != null) {
            color = (Color) o;
        }
//        else {
//	    color = VSwingUtil.TRANSPARENT_COLOR;
//	}

        BufferedImage img = new BufferedImage(50, 50,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = img.createGraphics();

        if (color != null) {
            g2.setPaint(color);
            g2.fillRect(0, 0, img.getWidth(), img.getHeight());
        }

        g2.dispose();

        container.setImage(img);
        container.contentChanged();
    }

    @Override
    public void emptyView() {
        container.setImage(null);
        color = null;
    }

    @Override
    public Object getViewValue() {
        return color;
    }

    protected void setPlotPaneSize(Dimension dimension) {
        this.plotPaneSize = dimension;

        int nameWidth = 0;

        if (!nameLabel.getText().equals("")) {
            nameWidth = nameLabel.getPreferredSize().width;
        }
//        int nameHeight = nameLabel.getPreferredSize().height;

        this.setSize(new Dimension(plotPaneSize.width
                + nameWidth, plotPaneSize.height));
        this.setPreferredSize(new Dimension(plotPaneSize.width
                + nameWidth, plotPaneSize.height));
        this.setMinimumSize(new Dimension(plotPaneSize.width
                + nameWidth, plotPaneSize.height));
        this.setMaximumSize(new Dimension(plotPaneSize.width
                + nameWidth, plotPaneSize.height));
    }

    @Override
    public void setValueName(String name) {
        super.setValueName(name);
        setPlotPaneSize(plotPaneSize);
    }

    @Override
    protected void evaluationRequest(Script script) {
        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("value")) {
                property = script.getProperty("value");
            }

            if (property != null && !getMainCanvas().isLoadingSession()) {

                setViewValue((Color) property);

            }
        }
    }
}
