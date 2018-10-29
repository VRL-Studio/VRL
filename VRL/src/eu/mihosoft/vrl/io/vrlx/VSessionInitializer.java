/* 
 * VSessionInitializer.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.Canvas;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.util.ArrayList;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VSessionInitializer implements SessionInitializer {

    private String preInitCode;
    private String postInitCode;
    private String codesLoadedCode;
    private String disposeCode;
    private String preSaveCode;
    private String postSaveCode;
    private ArrayList<Class<TypeRepresentationBase>> typeRepresentations =
            new ArrayList<Class<TypeRepresentationBase>>();

    @Override
    public void preInit(Canvas canvas) throws InstantiationException,
            IllegalAccessException {
        if (getPreInitCode() != null) {
            Script script = createScript((VisualCanvas) canvas, getPreInitCode());
            script.run();
        }

        for (Class<TypeRepresentationBase> t : typeRepresentations) {
            ((VisualCanvas)canvas).getTypeFactory().addType(t);
        }
    }

    @Override
    public void postInit(Canvas canvas) throws InstantiationException,
            IllegalAccessException {
        if (getPostInitCode() != null) {
            Script script = createScript((VisualCanvas) canvas, getPostInitCode());
            script.run();
        }
    }

    @Override
    public void preSave(Canvas canvas) throws Exception {
        if (getPreSaveCode() != null) {
            Script script = createScript((VisualCanvas) canvas, getPreSaveCode());
            script.run();
        }
    }

    @Override
    public void postSave(Canvas canvas) throws Exception {
        if (getPostSaveCode() != null) {
            Script script = createScript((VisualCanvas) canvas, getPostSaveCode());
            script.run();
        }
    }


    @Override
    public void codesLoaded(Canvas canvas) throws Exception {
        if (getCodesLoadedCode() != null) {
            Script script = createScript((VisualCanvas) canvas, getCodesLoadedCode());
            script.run();
        }
    }

    @Override
    public void dispose(Canvas canvas) throws Exception {
        if (getDisposeCode() != null) {
            Script script = createScript((VisualCanvas) canvas, getDisposeCode());
            script.run();
        }
    }

    /**
     * @return the typeRepesentations
     */
    public ArrayList<Class<TypeRepresentationBase>> getTypeRepresentations() {
        return typeRepresentations;
    }

    /**
     * @param typeRepresentations the typeRepesentations to set
     */
    public void setTypeRepresentations(
            ArrayList<Class<TypeRepresentationBase>> typeRepresentations) {
        this.typeRepresentations = typeRepresentations;
    }

    public void addType(Class<TypeRepresentationBase> t) {
        typeRepresentations.add(t);
    }

        /**
     * Creates a Groovy script with specified code.
     * @param canvas main canvas
     * @param code code to execute
     * @return the script
     */
    private Script createScript(VisualCanvas canvas, String code) {
        GroovyShell shell = new GroovyShell();

        Script script = shell.parse(code);

        script.setProperty("canvas", canvas);
        script.setProperty("session", canvas.getSessionFileName());
        script.setProperty("typeFactory", canvas.getTypeFactory());

        return script;
    }

    /**
     * @return the preInitCode
     */
    public String getPreInitCode() {
        return preInitCode;
    }

    /**
     * @param preInitCode the preInitCode to set
     */
    public void setPreInitCode(String preInitCode) {
        this.preInitCode = preInitCode;
    }

    /**
     * @return the postInitCode
     */
    public String getPostInitCode() {
        return postInitCode;
    }

    /**
     * @param postInitCode the postInitCode to set
     */
    public void setPostInitCode(String postInitCode) {
        this.postInitCode = postInitCode;
    }

    /**
     * @return the codesLoadedCode
     */
    public String getCodesLoadedCode() {
        return codesLoadedCode;
    }

    /**
     * @param codesLoadedCode the codesLoadedCode to set
     */
    public void setCodesLoadedCode(String codesLoadedCode) {
        this.codesLoadedCode = codesLoadedCode;
    }

    /**
     * @return the disposeCode
     */
    public String getDisposeCode() {
        return disposeCode;
    }

    /**
     * @param disposeCode the disposeCode to set
     */
    public void setDisposeCode(String disposeCode) {
        this.disposeCode = disposeCode;
    }

    /**
     * @return the preSaveCode
     */
    public String getPreSaveCode() {
        return preSaveCode;
    }

    /**
     * @param preSaveCode the preSaveCode to set
     */
    public void setPreSaveCode(String preSaveCode) {
        this.preSaveCode = preSaveCode;
    }

    /**
     * @return the postSaveCode
     */
    public String getPostSaveCode() {
        return postSaveCode;
    }

    /**
     * @param postSaveCode the postSaveCode to set
     */
    public void setPostSaveCode(String postSaveCode) {
        this.postSaveCode = postSaveCode;
    }
}
