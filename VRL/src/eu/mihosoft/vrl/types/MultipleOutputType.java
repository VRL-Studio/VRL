/* 
 * MultipleOutputType.java
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
import eu.mihosoft.vrl.reflection.TypeRepresentationContainer;
import groovy.lang.Script;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type = Object[].class, style = "multi-out", input = false, output = true)
public class MultipleOutputType extends ArrayBaseType {

    private Object valueFromOptions;
    private String MINIMIZED_KEY = "MultiOutType:minimized";
    private boolean isMinimized;

    public MultipleOutputType() {
        setUseRealElementType(false);

        setValueName(" ");

        setHideConnector(true);

        nameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {

                    toggleMinMax();

                    if (MultipleOutputType.super.getCustomData() != null) {
                        getCustomData().put(MINIMIZED_KEY, isMinimized);
                    }
                }
            }
        });
    }

    @Override
    public void emptyView() {
        for (TypeRepresentationContainer tC : getTypeContainers()) {
            tC.getTypeRepresentation().emptyView();
            tC.getTypeRepresentation().deleteViewValue();
            tC.getTypeRepresentation().deleteValue();
        }
    }

    private void toggleMinMax() {
        isMinimized = !isMinimized;
        updateMinimizeState();
    }

    @Override
    public void setUpToDate(boolean upToDate) {
        super.setUpToDate(upToDate);
        
        for (TypeRepresentationContainer tCont : getTypeContainers()) {
            tCont.getTypeRepresentation().setUpToDate(upToDate);
        }
    }
    
    @Override
    public void setReturnTypeOutdated() {
        for (TypeRepresentationContainer tCont : getTypeContainers()) {
            tCont.getTypeRepresentation().setReturnTypeOutdated();
        }
        super.setReturnTypeOutdated();
    }
    

    private void updateMinimizeState() {
        if (isMinimized) {
            minimize();
        } else {
            maximize();
        }
    }

    private void minimize() {
        for (TypeRepresentationContainer tCont : getTypeContainers()) {
            tCont.getTypeRepresentation().setVisible(false);
        }
    }

    private void maximize() {
        for (TypeRepresentationContainer tCont : getTypeContainers()) {
            tCont.getTypeRepresentation().setVisible(true);
        }
    }

    @Override
    public void addedToMethodRepresentation() {
        setHideButtonBox(true);

        if (valueFromOptions == null && getOutputInfo() != null) {
            valueFromOptions = new Object[getOutputInfo().elemTypes().length];
        }

        if (valueFromOptions != null) {
            setViewValue(valueFromOptions);
        }
        
        setUpToDate(false);
    }

    @Override
    public void evaluationRequest(Script script) {
        super.evaluationRequest(script);

        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("value")) {
                property = script.getProperty("value");
            }

            if (property != null && !getMainCanvas().isLoadingSession()) {

                valueFromOptions = property;
            }
        }
    }

    @Override
    public void evaluateCustomParamData() {
        super.evaluateCustomParamData();

        if (super.getCustomData() != null) {
            Object minimzedValue = getCustomData().get(MINIMIZED_KEY);

            if (minimzedValue instanceof Boolean) {
                isMinimized = (Boolean) minimzedValue;

                updateMinimizeState();
            }
        }
    }
    
    @Override
    public boolean noSerialization() {
        return true;
    }
}
