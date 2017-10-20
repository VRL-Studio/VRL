/* 
 * Trajectory.java
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

package eu.mihosoft.vrl.math;

import eu.mihosoft.vrl.annotation.ObjectInfo;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Trajectory class.
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@ObjectInfo(serializeParam=false)
public class Trajectory extends ArrayList<double[]> implements Serializable {

    private static final long serialVersionUID=1;
    
    private String title;
    private String label;
    private String xAxisLabel;
    private String yAxisLabel;
    private boolean xAxisLogarithmic;
    private boolean yAxisLogarithmic;

    public Trajectory() {
    }

    public Trajectory(String label) {
        this.title = "Plot";
        this.label = label;
        this.xAxisLabel = "X";
        this.yAxisLabel = "Y";
    }

    public Trajectory(
            String title,
            String label,
            String xAxisLabel,
            String yAxisLabel) {
        this.title = title;
        this.label = label;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    // add your code here
    public void add(double x, double y) {
        super.add(new double[]{x, y});
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the xAxisLabel
     */
    public String getxAxisLabel() {
        return xAxisLabel;
    }

    /**
     * @param xAxisLabel the xAxisLabel to set
     */
    public void setxAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    /**
     * @return the yAxisLabel
     */
    public String getyAxisLabel() {
        return yAxisLabel;
    }

    /**
     * @param yAxisLabel the yAxisLabel to set
     */
    public void setyAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    /**
     * @return the xAxisLogarithmic
     */
    public boolean isxAxisLogarithmic() {
        return xAxisLogarithmic;
    }

    /**
     * @param xAxisLogarithmic the xAxisLogarithmic to set
     */
    public void setxAxisLogarithmic(boolean xAxisLogarithmic) {
        this.xAxisLogarithmic = xAxisLogarithmic;
    }

    /**
     * @return the yAxisLogarithmic
     */
    public boolean isyAxisLogarithmic() {
        return yAxisLogarithmic;
    }

    /**
     * @param yAxisLogarithmic the yAxisLogarithmic to set
     */
    public void setyAxisLogarithmic(boolean yAxisLogarithmic) {
        this.yAxisLogarithmic = yAxisLogarithmic;
    }
}
