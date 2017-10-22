/* 
 * LoadFileStringType.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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
import eu.mihosoft.vrl.dialogs.FileDialogManager;
import eu.mihosoft.vrl.io.VFileFilter;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.reflection.LayoutType;
import eu.mihosoft.vrl.reflection.RepresentationType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VTextField;
import groovy.lang.Script;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTextField;

/**
 * TypeRepresentation for
 * <code>Java.lang.String</code>.
 * 
 * Style name: "load-dialog"
 * 
 * @see {@link LoadFileType }
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type = String.class, input = true, output = false, style = "load-dialog")
public class LoadFileStringType extends TypeRepresentationBase {

    private static final long serialVersionUID = 500442740078037574L;
    private VTextField input;
    private File file;
    private VFileFilter fileFilter = new VFileFilter();

    /**
     * Constructor.
     */
    public LoadFileStringType() {
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);

        setLayout(layout);

        setLayoutType(LayoutType.STATIC);

        nameLabel.setText("File Name:");
        nameLabel.setAlignmentX(0.0f);

        this.add(nameLabel);

        input = new VTextField(this, "");
        input.setHorizontalAlignment(JTextField.LEFT);

        setInputDocument(input, input.getDocument());

        int height = (int) this.input.getMinimumSize().getHeight();
        this.input.setSize(new Dimension(120, height));
        this.input.setMinimumSize(new Dimension(120, height));
        this.input.setMaximumSize(new Dimension(120, height));
        this.input.setPreferredSize(new Dimension(120, height));


        input.setAlignmentY(0.5f);
        input.setAlignmentX(0.0f);

        this.add(input);

        final FileDialogManager fileManager = new FileDialogManager();

        JButton button = new JButton("...");

        button.setMaximumSize(new Dimension(50,
                button.getMinimumSize().height));

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File directory = null;
                if (getViewValueWithoutValidation() != null) {
                    directory = new File(
                            getViewValueWithoutValidation().
                            toString());
                    if (!directory.isDirectory()) {
                        directory = directory.getParentFile();
                    }
                }

                file = fileManager.getLoadFile(getMainCanvas(),
                        directory, getFileFilter(), false);
                if (file != null) {
                    input.setText(file.toString());
                    input.setCaretPosition(input.getText().length());
                    input.setToolTipText(file.toString());
                }
            }
        });

        this.add(button);
    }

    @Override
    public void setViewValue(Object o) {
        input.setText(o.toString());
        input.setCaretPosition(input.getText().length());
        input.setToolTipText(o.toString());
    }

    @Override
    public Object getViewValue() {
        Object o = null;

        String inputText = input.getText();
        if (inputText.length() > 0) {
            try {
                o = inputText;
            } catch (Exception e) {
                invalidateValue();
            }
        }

        return o;
    }

    @Override
    public void emptyView() {
        input.setText("");
    }

    /**
     * Returns the file filter of this type representation.
     *
     * @return the file filter of this type representation
     */
    public VFileFilter getFileFilter() {
        return fileFilter;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void evaluationRequest(Script script) {
        super.evaluationRequest(script);

        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("endings")) {
                property = script.getProperty("endings");
            }

            if (property != null) {
                getFileFilter().setAcceptedEndings(
                        (ArrayList<String>) property);
            }

            property = null;

            if (getValueOptions().contains("description")) {
                property = script.getProperty("description");
            }

            if (property != null) {
                getFileFilter().setDescription((String) property);
            }
        }
    }

    @Override
    public String getValueAsCode() {
        
        Object v = getValue();

        if (v == null) {
            return "null";
        }

        return "\""
                + VLangUtils.addEscapesToCode(v.toString()) + "\"";
    }
}
