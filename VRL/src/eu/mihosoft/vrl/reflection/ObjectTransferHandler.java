/* 
 * ObjectTransferHandler.java
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

package eu.mihosoft.vrl.reflection;


/*
 * ArrayListTransferHandler.java is used by the 1.4
 * DragListDemo.java example.
 */
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Transferhandler for the object tree.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ObjectTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 6420826506199546349L;
    DataFlavor flavor = new DataFlavor(Object.class, "Object");

//    public boolean importData(JComponent c, Transferable t){
//        JTree tree = null;
//        
//        if (c instanceof JTree){
//            tree = (JTree) c;
//        }
//        else
//        {
//            return false;
//        }
//        
//        return true;
//    }
    @Override
    protected void exportDone(JComponent source,
            Transferable data, int action) {
        // this was only used for objecttree
//        if (action == DnDConstants.ACTION_MOVE) {
//
//            JTree tree = (JTree) source;
//
//            TreePath path = tree.getSelectionPath();
//
//            DefaultMutableTreeNode node =
//                    (DefaultMutableTreeNode) path.getLastPathComponent();
//
//            tree.removeObject(node.getUserObject());
//        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {

        JTree tree = (JTree) c;

        TreePath path = tree.getSelectionPath();

        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) path.getLastPathComponent();

        return new ObjectTransferable(node.getUserObject());
    }
}

  
