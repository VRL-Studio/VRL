/* 
 * FileType.java
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
import eu.mihosoft.vrl.dialogs.FileDialogManager;
import eu.mihosoft.vrl.reflection.LayoutType;
import eu.mihosoft.vrl.reflection.RepresentationType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.TransparentLabel;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VTextField;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;

/**
 * TypeRepresentation for <code>java.io.File</code>.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@TypeInfo(type=File.class, input = true, output = true, style="default")
public class FileType extends TypeRepresentationBase {
    private static final long serialVersionUID = 6141652211554743393L;

    private VTextField input;
    private File file;
    private FileDialogManager fileManager;

    public FileType() {
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);

        setLayout(layout);

        setLayoutType(LayoutType.STATIC);

        nameLabel.setText("FileName:");
        nameLabel.setAlignmentX(0.0f);

        this.add(nameLabel);

        input = new VTextField(this, "");

        setInputDocument(input, input.getDocument());

        int height = (int) this.input.getMinimumSize().getHeight();
        this.input.setSize(new Dimension(120, height));
        this.input.setMinimumSize(new Dimension(120, height));
        this.input.setMaximumSize(new Dimension(120, height));
        this.input.setPreferredSize(new Dimension(120, height));

        input.setAlignmentY(0.5f);
        input.setAlignmentX(0.0f);

        input.setAlignmentY(0.5f);
        input.setAlignmentX(0.0f);

        this.add(input);

//        fileManager = new FileDialogManager();
//
//        JButton button = new JButton("...");
//
//        button.setMaximumSize(new Dimension(50, button.getMinimumSize().height));
//
//        button.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                file = fileManager.getLoadFile(getMainCanvas(), null);
//                if (file != null) {
//                    output.setText(file.toString());
//                }
//            }
//        });
//
//        this.add(button);
    }

    @Override
    public void setViewValue(Object o) {
        File f = (File) o;
        input.setText(f.getPath());
        super.setViewValue(o);
    }

    @Override
    public void setCurrentRepresentationType(
            RepresentationType representationType) {
        if (representationType == RepresentationType.OUTPUT) {
            input.setEditable(false);
            int height = (int) this.input.getMinimumSize().getHeight();
            this.input.setMinimumSize(new Dimension(50, height));
            this.input.setPreferredSize(new Dimension(120, height));
            this.input.setMaximumSize(new Dimension(120, height));
        } else {
            setInputDocument(input, input.getDocument());
        }
        super.setCurrentRepresentationType(representationType);
    }

    @Override
    public void emptyView() {
        input.setText("");
    }
}

