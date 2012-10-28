/* 
 * VCodePane.java
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

package eu.mihosoft.vrl.visual;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.JTextComponent;
import javax.swing.text.LabelView;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TextAction;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.undo.UndoManager;

/**
 * This is a text pane optimized for code editing and viewing.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VCodePane extends VTextPane implements VComponentChild {

    private static final long serialVersionUID = 1577031239457339507L;
    private CodeDocument document;
    private VComponent parent;
    private UndoManager undoManager;
    private static final String UNDO_ACTION = "undo";
    private static final String REDO_ACTION = "redo";

//    private ArrayList<NoLineWrapParagraphView> paragraphs =
//            new ArrayList<NoLineWrapParagraphView>();
    /**
     * Constructor.
     * @param mainCanvas the main canvas object
     */
    public VCodePane(VComponent mainCanvas) {
        this.setEditorKit(new VCodeEditorKit(this));
        document = new CodeDocument(this);
        this.parent = mainCanvas;
        setDocument(document);

        getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                removeErrorNotifier();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                removeErrorNotifier();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                //
            }
        });

        undoManager = new UndoManager();
        undoManager.setLimit(10000);
        getDocument().addUndoableEditListener(undoManager);
        InputMap im = getInputMap();
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("ctrl Z"), UNDO_ACTION);
        am.put(UNDO_ACTION, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
//		System.out.println("UNDO");
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });
        im.put(KeyStroke.getKeyStroke("ctrl Y"), REDO_ACTION);
        am.put(REDO_ACTION, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
//		System.out.println("REDO");
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });

        setOpaque(false);

        
    }

    /**
     * Returns the paragraph views.
     * @return the paragraph views
     */
    public ArrayList<NoLineWrapParagraphView> getParagraphs() {
        ArrayList<NoLineWrapParagraphView> result =
                new ArrayList<NoLineWrapParagraphView>();

        View rootView = getUI().getRootView(this);

        // TODO we assume the box view to be the root
        // (may change later if multiple boxes etc...)
        rootView = rootView.getView(0);

//        displayView(rootView, 2, System.out);

        int numberOfChildren = rootView.getViewCount();

        for (int i = 0; i < numberOfChildren; i++) {
            View childView = rootView.getView(i);

            if (childView instanceof NoLineWrapParagraphView) {
                result.add((NoLineWrapParagraphView) childView);
            }
        }

        return result;
    }

    /**
     * Removes all error notifiers.
     */
    public void removeErrorNotifier() {
        for (NoLineWrapParagraphView v : getParagraphs()) {
            v.setErrorView(false);
        }
    }

    @Override
    public VComponent getVParent() {
        return parent;
    }

    @Override
    public void setVParent(VComponent parent) {
        this.parent = parent;
    }
    /**
    //     * Returns the main canvas.
    //     * @return the main canvas
    //     */
//    public Canvas getMainCanvas() {
//        return mainCanvas;
//    }
//
//    /**
//     * Defines the main canvas object.
//     * @param mainCanvas the canvas to set
//     */
//    public void setMainCanvas(Canvas mainCanvas) {
//        this.mainCanvas = mainCanvas;
//    }
}

/**
 * Document optimized for code editing.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class CodeDocument extends DefaultStyledDocument {

    private VCodePane codePane;

    /**
     * Constructor.
     * @param codePane the code pane
     */
    public CodeDocument(VCodePane codePane) {
        this.codePane = codePane;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
        str = str.replaceAll("\t", "    ");
        super.insertString(offs, str, a);
    }
}

/**
 * An editor kit optimized for code editing.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class VCodeEditorKit extends StyledEditorKit {

    private VCodePane codePane;

    /**
     * Constructor.
     * @param codePane the code pane
     */
    public VCodeEditorKit(VCodePane codePane) {
        this.codePane = codePane;
        codePane.getActionMap().put(VCodeEditorKit.insertBreakAction,
                new IndentBreakAction());
    }

    @Override
    public ViewFactory getViewFactory() {
        return new CodeViewFactory(codePane);
    }
}

/**
 * View factory optimized for code editing.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class CodeViewFactory implements ViewFactory {

    private VCodePane codePane;

    /**
     * Constructor.
     * @param codePane the code pane
     */
    public CodeViewFactory(VCodePane codePane) {
        this.codePane = codePane;
    }

    @Override
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new LabelView(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
//              return new ParagraphView(elem);
                return new NoLineWrapParagraphView(elem, codePane);
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new BoxView(elem, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
                return new IconView(elem);
            }
        }
        // default to text display
        return new LabelView(elem);
    }
}

/**
 * Inserts white spaces at the beginning of a new line. The number of
 * whitespaces is equal to the number of whitespaces ath the beginning of the
 * previous line.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class IndentBreakAction extends TextAction {

    /**
     * Constructor.
     */
    public IndentBreakAction() {
//        Creates this object with the appropriate identifier.
        super(DefaultEditorKit.insertBreakAction);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);

        if (target == null) {
            return;
        }

        if ((!target.isEditable()) || (!target.isEnabled())) {
            UIManager.getLookAndFeel().
                    provideErrorFeedback(target);
            return;
        }

        try {
            // Determine which line we are on
            Document doc = target.getDocument();
            Element rootElement = doc.getDefaultRootElement();
            int selectionStart = target.getSelectionStart();
            int line = rootElement.getElementIndex(selectionStart);

            // Get the text for this line
            int start = rootElement.getElement(line).getStartOffset();
            int end = rootElement.getElement(line).getEndOffset();
            int length = end - start;
            String text = doc.getText(start, length);
            int offset = 0;

            // Get the number of white spaces characters at the start of the
            // line

            for (offset = 0; offset < length; offset++) {
                char c = text.charAt(offset);

                if (c != ' ' && c != '\t') {
                    break;
                }
            }

            // When splitting the text include white space at start of line
            // else do default processing

            if (selectionStart - start > offset) {
                target.replaceSelection("\n" + text.substring(0, offset));
            } else {
                target.replaceSelection("\n" + text.substring(0, offset));
                //target.replaceSelection("\n");
            }
        } catch (BadLocationException ex) {
        }
    }
}
