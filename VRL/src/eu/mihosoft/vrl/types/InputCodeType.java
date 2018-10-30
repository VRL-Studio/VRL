/* 
 * InputCodeType.java
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
import eu.mihosoft.vrl.lang.CompilerProvider;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.visual.EditorProvider;
import eu.mihosoft.vrl.reflection.RepresentationType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.ResizableContainer;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VCodeEditor;
import eu.mihosoft.vrl.visual.VContainer;
import eu.mihosoft.vrl.visual.VScrollPane;
import groovy.lang.Script;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.BoxLayout;

/**
 * TypeRepresentation for
 * <code>java.lang.String</code>.
 * 
 * Style name: "code"
 *
 * <p>Sample:</p> <br/> <img src="doc-files/string-text-01.png"/> <br/>
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type = String.class, input = true, output = false, style = "code")
public class InputCodeType extends TypeRepresentationBase {

    private VCodeEditor editor;
    private int minimumEditorWidth = 120;
    private int minimumEditorHeight = 24;
    private VContainer editorContainer = new VContainer();

    public InputCodeType() {

        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);

        setLayout(layout);

        nameLabel.setText("Code:");
        nameLabel.setAlignmentX(0.5f);
        this.add(nameLabel);

        editor = createEditor();

        final ResizableContainer resizeCont = new ResizableContainer(editor);

        editorContainer.add(resizeCont);

        resizeCont.addExternalListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                editor.getScrollPane().setMaxWidth(resizeCont.getWidth());
                editor.getScrollPane().setMaxHeight(resizeCont.getHeight());
            }
        });

        editor.getScrollPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                InputCodeType.this.setValueOptions("width=" + getWidth() + ";"
                        + "height=" + getHeight());
            }
        });

//        editorContainer.add(editor);

        // add minimum editor width functionality
        editorContainer.setMinPreferredWidth(minimumEditorWidth);
        editorContainer.setMinPreferredHeight(minimumEditorHeight);

        add(editorContainer);

        setInputDocument(editor.getEditor().getDocument());
    }
    
    protected VCodeEditor createEditor() {
        return EditorProvider.getEditor(CompilerProvider.LANG_GROOVY, this);
    }

    @Override
    public void emptyView() {
        editor.getEditor().setText("");
    }

    @Override
    public void setViewValue(Object o) {
        emptyView();
        editor.getEditor().setText(o.toString());
        editor.setPreferredSize(null);
        editor.getEditor().revalidate();
    }

    @Override
    public Object getViewValue() {
        Object o = null;
        try {
            o = editor.getEditor().getText();
        } catch (Exception ex) {
        }
        return o;
    }

    /**
     * @return the editor
     */
    public VCodeEditor getEditor() {
        return editor;
    }

//    /**
//     * @param editor the editor to set
//     */
//    public void setEditor(VCodeEditor editor) {
//        this.editor = editor;
//    }
    /**
     * @return the minimumEditorWidth
     */
    public Integer getMinimumEditorWidth() {
        return minimumEditorWidth;
    }

    /**
     * @param minimumEditorWidth the minimumEditorWidth to set
     */
    public void setMinimumEditorWidth(Integer minimumEditorWidth) {
        this.minimumEditorWidth = minimumEditorWidth;
        editorContainer.setMinPreferredWidth(minimumEditorWidth);
        editor.revalidate();
    }

    @Override
    public void evaluationRequest(Script script) {

        super.evaluationRequest(script);

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

        }

        if (w != null && h != null) {
            // TODO find out why offset is 5
            editor.getScrollPane().setMaxWidth(w-5);
            editor.getScrollPane().setMaxHeight(h);
            
            
            // TODO find out why offset is 26
            editor.setPreferredSize(new Dimension(w-26, h-5));
            editor.setSize(new Dimension(w-26, h-5));
        }
    }

//    @Override()
//    public String getValueAsCode() {
//        String result = "null";
//
//        Object o = getValue();
//
//        if (o != null) {
//            result = "\"" + o.toString() + "\"";
//        }
//
//        return result;
//    }
    @Override
    public String getValueAsCode() {
        return "\""
                + VLangUtils.addEscapesToCode(getValue().toString()) + "\"";
    }
}
