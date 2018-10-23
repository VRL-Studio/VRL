/* 
 * VCodeEditor.java
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
package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.system.VSysUtil;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import org.fife.ui.rsyntaxtextarea.CodeTemplateManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaUI;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.templates.CodeTemplate;
import org.fife.ui.rsyntaxtextarea.templates.StaticCodeTemplate;
import org.fife.ui.rtextarea.RTextAreaUI;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

/**
 * Code editor component with line number view and error notification.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VCodeEditor extends JPanel implements CanvasChild {

    private static final long serialVersionUID = -102409473859342650L;
    private VComponent parent;
    private Style style;
    private RSyntaxTextArea editor;
    private LineNumberView numberView;
    private VRTextScrollPane scrollPane;
    private FindAndReplaceToolbar findAndReplaceToolbar;
    /**
     *
     */
    public static final String BACKGROUND_COLOR_KEY
            = "VCodeEditor:Background:Color";
    /**
     *
     */
    public static final String BACKGROUND_TRANSPARENCY_KEY
            = "VCodeEditor:Background:Transparency";
    /**
     *
     */
    public static final String BORDER_COLOR_KEY
            = "VCodeEditor:Border:Color";
    /**
     *
     */
    public static final String BORDER_THICKNESS_KEY
            = "VCodeEditor:Border:Thickness";
    /**
     *
     */
    public static final String LINE_NUMBER_FIELD_COLOR_KEY
            = "VCodeEditor:LineNumberField:Color";
    /**
     *
     */
    public static final String LINE_NUMBER_COLOR_KEY
            = "VCodeEditor:LineNumber:Color";
    /**
     *
     */
    public static final String COMPILE_ERROR_COLOR_KEY
            = "VCodeEditor:CompileError:Color";
    /**
     *
     */
    public static final String COMPILE_ERROR_BORDER_COLOR_KEY
            = "VCodeEditor:CompileError:Border:Color";
    /**
     *
     */
    public static final String EDITOR_STYLE_KEY
            = "VCodeEditor:EditorStyle";

    public static final String EDITOR_HIGHLIGHTED_LINE_KEY
            = "VCodeEditor:HighlightedLine:Color";

    /**
     *
     */
    public static final String FONT_SIZE_KEY
            = "VCodeEditor:Font:Size";

    static {
        // enable code templates
        RSyntaxTextArea.setTemplatesEnabled(true);

        CodeTemplateManager ctm = RSyntaxTextArea.getCodeTemplateManager();

        // StaticCodeTemplates are templates that insert static text before and
        // after the current caret position. This template is basically shorthand
        // for "System.out.println(".
        CodeTemplate ct = new StaticCodeTemplate("sout", "System.out.println(",
                null);

        ctm.addTemplate(ct);

        // This template is for a for-loop. The caret is placed at the upper
        // bound of the loop.
        ct = new StaticCodeTemplate("fori", "for (int i=0; i<", "; i++) {\n\t\n}\n");
        ctm.addTemplate(ct);

        ct = new StaticCodeTemplate("forj", "for (int j=0; j<", "; j++) {\n\t\n}\n");
        ctm.addTemplate(ct);

        ct = new StaticCodeTemplate("fork", "for (int k=0; k<", "; k++) {\n\t\n}\n");
        ctm.addTemplate(ct);

        ct = new StaticCodeTemplate("forx", "for (int x=0; i<", "; x++) {\n\t\n}\n");
        ctm.addTemplate(ct);

        ct = new StaticCodeTemplate("fory", "for (int y=0; j<", "; y++) {\n\t\n}\n");
        ctm.addTemplate(ct);

        ct = new StaticCodeTemplate("forz", "for (int z=0; k<", "; z++) {\n\t\n}\n");
        ctm.addTemplate(ct);
    }

    /**
     * Constructor.
     *
     * @param parent the main canvas object
     */
    public VCodeEditor(VComponent parent) {
        this.parent = parent;

//        editor = new VCodePane(parent);
        BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(layout);

        editor = new RSyntaxTextArea() {

            {
                setOpaque(false);
            }

            @Override
            public void setOpaque(boolean isOpaque) {

                // this editor ignores opaque request.
                // its paint method makes sure that the component behaves 
                // currectly.
                super.setOpaque(false);

            }

            /**
             * Updates layout on fold toggle. TODO improve design, use standard listener/events!
             */
            @Override
            public void foldToggled(Fold fold) {

                super.foldToggled(fold);

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {

                        getScrollPane().setPreferredSize(null);
                        getScrollPane().revalidate();

                        Container parentWindow
                                = VSwingUtil.getParent(
                                        VCodeEditor.this, CanvasWindow.class);

                        if (parentWindow != null) {
                            CanvasWindow w = (CanvasWindow) parentWindow;
                            w.setPreferredSize(null);
                            w.revalidate();
                        }
                    }
                });
            }

            @Override
            public void paint(Graphics g) {

                // we paint editor augmentations before painting text to
                // ensure everything is in the background
                ((VCodeEditorUI) getUI()).paintEditorAugmentations(g);

                // paint the text and foreground ui as usual
                super.paint(g);
            }

            @Override
            protected RTextAreaUI createRTextAreaUI() {
                // we need a custom ui to support non-opaque components
                return new VCodeEditorUI(this);
            }
        };

        editor.setBracketMatchingEnabled(true);
        editor.setAnimateBracketMatching(true);
        editor.setFadeCurrentLineHighlight(false);

        editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        editor.setBorder(new EmptyBorder(0, 8, 0, 0));
        editor.setLineWrap(false);

        findAndReplaceToolbar
                = new FindAndReplaceToolbar(editor);
        add(findAndReplaceToolbar);
        findAndReplaceToolbar.setVisible(false);

        // add find and replace shortcut
        KeyStroke findAndReplace = null;

        if (!VSysUtil.isMacOSX()) {
            findAndReplace
                    = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK);
        } else {
            findAndReplace
                    = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.META_MASK);
        }

        editor.registerKeyboardAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                toggleFindAndReplaceToolbarVisibility();
            }
        }, findAndReplace, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        KeyStroke incFontSize
                = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK);

        editor.registerKeyboardAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                incFontSize(1f);
            }
        }, incFontSize, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        KeyStroke decFontSize
                = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK);

        editor.registerKeyboardAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                decFontSize(1f);
            }
        }, decFontSize, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // remove code folding menu due to several bugs
        JMenu delMenu = null;

        for (MenuElement e : editor.getPopupMenu().getSubElements()) {
            if (e instanceof JMenu) {
                JMenu m = (JMenu) e;

                if (m.getText().equals("Folding")) {
                    delMenu = m;
                }
            }
        }

        if (delMenu != null) {
            editor.getPopupMenu().remove(delMenu);
        }

        // add Find and Replace menu entry
        JMenuItem findAndReplaceItem = new JMenuItem("Find and Replace");

        findAndReplaceItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                toggleFindAndReplaceToolbarVisibility();
            }
        });

        editor.getPopupMenu().add(findAndReplaceItem);
        
        editor.getPopupMenu().add(new JPopupMenu.Separator());

        // add INC font size menu entry
        JMenuItem fontSizeItemInc = new JMenuItem("Increase Font Size");

        fontSizeItemInc.addActionListener((ActionEvent ae) -> {
            incFontSize(1f);
        });

        editor.getPopupMenu().add(fontSizeItemInc);
        
        // add DEC font size menu entry
        JMenuItem fontSizeItemDec = new JMenuItem("Decrease Font Size");

        fontSizeItemDec.addActionListener((ActionEvent ae) -> {
            decFontSize(1f);
        });

        editor.getPopupMenu().add(fontSizeItemDec);
        
        JMenu fontSizeMenu = new JMenu("Set Font Size");
        
        float[] fSizes = {8,10,12,14,16,18,20,22,24,26,28,30,32};
        for(float fSize : fSizes) {
            JMenuItem item  = new JMenuItem(fSize+"pt");
            item.addActionListener((ActionEvent e) -> {
                Font font1 = editor.getFont().deriveFont(fSize);
                editor.setFont(font1);
            });
            fontSizeMenu.add(item);
        }
        
        editor.getPopupMenu().add(fontSizeMenu);

        // finally add the editor to a scrollpane which is added to this
        scrollPane = new VRTextScrollPane(editor);
        add(scrollPane);
        setOpaque(false);
    }

    public void toggleFindAndReplaceToolbarVisibility() {
        findAndReplaceToolbar.setVisible(!findAndReplaceToolbar.isVisible());
        revalidate();
        findAndReplaceToolbar.getMainCanvas().revalidate();

        getScrollPane().setPreferredSize(null);
        getScrollPane().revalidate();

        Container parentWindow
                = VSwingUtil.getParent(
                        VCodeEditor.this, CanvasWindow.class);

        if (parentWindow != null) {
            CanvasWindow w = (CanvasWindow) parentWindow;
            w.setPreferredSize(null);
            w.revalidate();
        }
    }

    public void hideFindAndReplaceToolbar() {
        findAndReplaceToolbar.setVisible(false);
        revalidate();
        findAndReplaceToolbar.getMainCanvas().revalidate();
    }

    /**
     * Adds an error message.
     *
     * @param line the line the message belongs to
     * @param message the message text
     */
    public void addCodeErrorMessage(int line, String message) {
//        numberView.addCodeErrorMessage(line, message);

        //editor.getParagraphs().get(line - 1).setErrorView(true);
        parent.getMainCanvas().getMessageBox().
                addMessage("Can't compile code:",
                        message, null, MessageType.ERROR);

        BufferedImage errorImage = ImageUtils.createCompatibleImage(10, 10);
        Graphics2D g2 = errorImage.createGraphics();
        g2.fillRect(0, 0, 10, 10);
        g2.dispose();
        ImageIcon icon = new ImageIcon(errorImage);

        try {
            getScrollPane().getGutter().addLineTrackingIcon(line, icon);
        } catch (BadLocationException ex) {
            Logger.getLogger(VCodeEditor.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Removes all error notifiers from this code editor.
     */
    public void removeErrorNotifiers() {
//        numberView.removeErrorNotifiers();
//        editor.removeErrorNotifier();

        getScrollPane().getGutter().removeAllTrackingIcons();
    }

    @Override
    protected void paintComponent(Graphics g) {

        Style newStyle;

        if (getMainCanvas() != null) {
            newStyle = parent.getStyle();
        } else {
            newStyle = new Style("Default");
        }

        if (style == null || !style.equals(newStyle)) {
            style = newStyle;
            Color defaultText
                    = style.getBaseValues().getColor(Canvas.TEXT_COLOR_KEY);
            Color selectedText
                    = style.getBaseValues().getColor(Canvas.SELECTED_TEXT_COLOR_KEY);
            Color selectedTextBackground
                    = style.getBaseValues().getColor(Canvas.TEXT_SELECTION_COLOR_KEY);
            Color caretColor
                    = style.getBaseValues().getColor(Canvas.CARET_COLOR_KEY);

            editor.setCaretColor(caretColor);
            editor.setSelectedTextColor(selectedText);
            editor.setSelectionColor(selectedTextBackground);

            editor.setCurrentLineHighlightColor(style.getBaseValues().
                    getColor(EDITOR_HIGHLIGHTED_LINE_KEY));

            editor.setBackground(VSwingUtil.TRANSPARENT_COLOR);

            SyntaxScheme scheme = style.getBaseValues().getEditorStyle(EDITOR_STYLE_KEY);

            Font baseFont = RSyntaxTextArea.getDefaultFont().deriveFont(style.getBaseValues().getFloat(FONT_SIZE_KEY));
            // set default font
            for (int i = 0; i < scheme.getStyleCount(); i++) {
                if (scheme.getStyle(i) != null) {
                    scheme.getStyle(i).font = baseFont;
                }
            }

            editor.setSyntaxScheme(
                    style.getBaseValues().getEditorStyle(EDITOR_STYLE_KEY));
        }

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        setForeground(style.getBaseValues().getColor(Canvas.TEXT_COLOR_KEY));

        g2.setPaint(style.getBaseValues().getColor(BACKGROUND_COLOR_KEY));

        Composite original = g2.getComposite();

        float alpha = style.getBaseValues().
                getFloat(BACKGROUND_TRANSPARENCY_KEY);

        AlphaComposite ac1
                = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        alpha);
        g2.setComposite(ac1);

        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        g2.setComposite(original);

        Color border = style.getBaseValues().getColor(BORDER_COLOR_KEY);

        g2.setColor(border);

        BasicStroke stroke
                = new BasicStroke(
                        style.getBaseValues().getFloat(BORDER_THICKNESS_KEY));

        g2.setStroke(stroke);

        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        setBackground(VSwingUtil.TRANSPARENT_COLOR);

        g2.setComposite(original);

    }

    /**
     * @return the editor
     */
    public JTextArea getEditor() {
        return editor;
    }

    @Override
    public Canvas getMainCanvas() {
        return parent.getMainCanvas();
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
//        this.parent = mainCanvas;
//
//        if (numberView != null) {
//            numberView.setMainCanvas(mainCanvas);
//        }
//
//        editor.setMainCanvas(mainCanvas);
    }

    /**
     * @return the scrollPane
     */
    public VRTextScrollPane getScrollPane() {
        return scrollPane;
    }

    public void decFontSize(float delta) {
        float size = (float)editor.getFont().getSize() - delta;

        if (size > 8) {
            editor.setFont(editor.getFont().deriveFont(size));
            System.out.println(size);
        }
    }
    
    public void incFontSize(float delta) {
        float size = (float)editor.getFont().getSize() + delta;

        if (size < 32) {
            editor.setFont(editor.getFont().deriveFont(size));
            System.out.println(size);
        }
    }
}

/**
 * Draws a colored rectangle over the code line where an error occured.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ErrorNotifier extends TransparentPanel implements CanvasChild {

    private VComponent parent;

    /**
     * Constructor.
     *
     * @param parent the main canvas object
     */
    public ErrorNotifier(VComponent parent) {
        this.parent = parent;
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public Canvas getMainCanvas() {
        return parent.getMainCanvas();
    }

    /**
     * This method does nothing as the main canvas from the parent vcomponent is used instead
     */
    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        //
    }
}

/**
 * Displays line numbers on the left side of the editor component.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class LineNumberView extends TransparentPanel {

    private VComponent parent;
    private FontMetrics fontMetrics;
    private int charHeight;
    private int charAscent;
    private int digitWidth;
    private int numberCharLength;
    private int minNumberCharLength = 2;
    private int leftRightMargin = 5;
    private JComponent textComponent;
    private ErrorNotifier errorNotifier;
    private int errorLine;

    /**
     * Constructor.
     *
     * @param parent the main canvas object
     * @param textComponent the text component
     */
    public LineNumberView(VComponent parent, JComponent textComponent) {
        this.setLayout(null);
        this.parent = parent;
        this.textComponent = textComponent;
        errorNotifier = new ErrorNotifier(parent);
        this.add(errorNotifier);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        fontMetrics = getFontMetrics(getFont());
        charHeight = fontMetrics.getHeight();
        charAscent = fontMetrics.getAscent();
        digitWidth = fontMetrics.charWidth('0');
    }

    /**
     * Sets an error notifier at specific line.
     *
     * @param line the position of the notifier
     */
    private void setErrorNotifierAt(int line) {
        errorLine = line;
        int charWidth = fontMetrics.charWidth('0') * getNumberOfChars(line);

        errorNotifier.setLocation(getWidth() - leftRightMargin - charWidth / 2,
                getCoordinateOfLine(line));
    }

    /**
     * Adds a code error message to the message box of the main canvas.
     *
     * @param line the line number where the error occured
     * @param message the error message
     */
    public void addCodeErrorMessage(int line, String message) {
        setErrorNotifierAt(line);

        parent.getMainCanvas().getMessageBox().
                addMessage("Can't compile code:",
                        message, errorNotifier, MessageType.ERROR);
    }

    /**
     * Removes all error notifiers.
     */
    public void removeErrorNotifiers() {
        errorLine = 0;
    }

    /**
     * Calculate the width needed to display the maximum line number
     */
    private void updateWidth() {
        int maxLineNumber = getMaxLineNumber();
        int charLength
                = Math.max(getNumberOfChars(maxLineNumber), minNumberCharLength);

        //  Update sizes when number of digits in the line number changes
        if (charLength != numberCharLength && charLength > 1) {
            numberCharLength = charLength;
            int width = digitWidth * charLength;
            Dimension d = getPreferredSize();
            d.setSize(2 * leftRightMargin + width, d.height);
            setPreferredSize(d);
            setSize(d);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
        Style style = null;

        if (parent != null) {
            style = parent.getStyle();
        }

        float borderThickness = style.getBaseValues().getFloat(
                VCodeEditor.BORDER_THICKNESS_KEY);

        int insetValue = (int) borderThickness;

        g2.setPaint(style.getBaseValues().getColor(
                VCodeEditor.LINE_NUMBER_FIELD_COLOR_KEY));

        int arcWidth = 20;
        int arcHeight = 20;

        Shape shape = new RoundRectangle2D.Double(insetValue, insetValue,
                arcWidth, getHeight() - 1 - insetValue,
                arcWidth, arcHeight);

        // define the shape that is created by adding rectangular shape to
        // round rectangle
        Shape shape2 = new Rectangle2D.Double(
                arcWidth / 2,
                insetValue,
                getWidth() - 1 - insetValue,
                getHeight() - 1 - insetValue);

        Area objectOne = new Area(shape);
        Area objectTwo = new Area(shape2);

        objectOne.add(objectTwo);
        shape = objectOne;

        g2.fill(shape);

        g2.setColor(style.getBaseValues().getColor(VCodeEditor.BORDER_COLOR_KEY));

        g2.drawLine(getWidth() - 1, insetValue,
                getWidth() - 1, getHeight() - 1 - insetValue);

        paintNumbers(g);
    }

    /**
     * Returns the line number at specific y value.
     *
     * @param y the y value
     * @return the line number at y
     */
    private int getLineNumberAt(int y) {
        return y / charHeight;
    }

    /**
     * Returns the string width (depending on font metrics of the current graphics context.)
     *
     * @param s the string
     * @return the width of the string
     */
    public int getStringWidth(String s) {
        return fontMetrics.stringWidth(s);
    }

    /**
     * Returns the number of characters (digits) of a number.
     *
     * @param n the number
     * @return the number of characters of n
     */
    public int getNumberOfChars(int n) {
        return String.valueOf(n).length();
    }

    /**
     * Returns the maximum line number.
     *
     * @return the maximum line number
     */
    private int getMaxLineNumber() {
        return getLineNumberAt(getHeight());
    }

    /**
     * Returns the y position of a line.
     *
     * @param line the line
     * @return the y position of line
     */
    private int getCoordinateOfLine(int line) {
        return getStartOffset() + charHeight * (line - 1) - charHeight / 4;
    }

    /**
     * Paints the number view.
     *
     * @param g the graphics context to use
     */
    private void paintNumbers(Graphics g) {

        Rectangle visibleRect = g.getClipBounds();

        int startLineNumber = getLineNumberAt(visibleRect.y);

        int stopIndex = startLineNumber + getLineNumberAt(visibleRect.height);

        int startY
                = getLineNumberAt(visibleRect.y) * charHeight + getStartOffset();

        Style style = parent.getStyle();

        for (int i = startLineNumber; i <= stopIndex; i++) {
            if (i + 1 == errorLine) {
                g.setColor(Color.red);
            } else {
                g.setColor(style.getBaseValues().getColor(
                        VCodeEditor.LINE_NUMBER_COLOR_KEY));
            }
            String lineNumber = String.valueOf(i + 1);
            int stringWidth = getStringWidth(lineNumber);
            int rowWidth = getSize().width;
            g.drawString(lineNumber,
                    rowWidth - stringWidth - leftRightMargin, startY);
            startY += charHeight;
        }

        updateWidth();
    }

    /**
     * Returns the start offset (including char ascend).
     *
     * @return the start offset
     */
    private int getStartOffset() {
        return textComponent.getInsets().top + charAscent;
    }
}

class FindAndReplaceToolbar extends VComponent implements ActionListener {

    private final RSyntaxTextArea textArea;
    private VTextField searchField;
    private final VTextField replaceField;
    private final JCheckBox regexCB;
    private final JCheckBox matchCaseCB;
    private JButton nextButton;
    private JButton prevButton;
    private JButton replaceButton;

    public FindAndReplaceToolbar(final RSyntaxTextArea textArea) {
        this.textArea = textArea;

//      textArea = new RSyntaxTextArea(20, 60);
//      textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
//      textArea.setCodeFoldingEnabled(true);
//      textArea.setAntiAliasingEnabled(true);
        // Create a toolbar with searching options.
//        JToolBar toolBar = new JToolBar();
        VComponent toolbar = new VComponent();

//        toolbar.setStyle(VDialogWindow.createDialogStyle());
//        toolbar.setPainterKey(CanvasWindow.BACKGROUND_PAINTER_KEY);
        searchField = new VTextField("", 8);
//        searchField.setInvalidStateColor(Color.RED);
        searchField.selectAll();
        toolbar.add(searchField);
        replaceField = new VTextField("replacement", 8);
//        replaceField.setInvalidStateColor(Color.RED);
        toolbar.add(replaceField);

        prevButton = new VButton("<-");
        prevButton.setActionCommand("FindPrev");
        prevButton.addActionListener(this);
        toolbar.add(prevButton);

        nextButton = new VButton("->");
        nextButton.setActionCommand("FindNext");
        nextButton.addActionListener(this);
        toolbar.add(nextButton);

        searchField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent ke) {
                //
            }

            @Override
            public void keyPressed(KeyEvent ke) {

                if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    FindAndReplaceToolbar.this.setVisible(false);
                    revalidate();
                    getMainCanvas().revalidate();
                } else {
                    searchField.setInvalidState(false);
                    searchField.repaint();

                    nextButton.setEnabled(true);
                    prevButton.setEnabled(true);
                    replaceButton.setEnabled(true);
                }

            }

            @Override
            public void keyReleased(KeyEvent ke) {
                findFirst();
            }
        });

        searchField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                nextButton.doClick(0);
            }
        });

        replaceButton = new VButton("Replace");
        replaceButton.setActionCommand("Replace");
        replaceButton.addActionListener(this);
        toolbar.add(replaceButton);

        regexCB = new JCheckBox("Regex");
        regexCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                findFirst();
            }
        });

        toolbar.add(regexCB);

        matchCaseCB = new JCheckBox("Match Case");
        matchCaseCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                findFirst();
            }
        });

        toolbar.add(matchCaseCB);

        CloseIcon closeIcon = new CloseIcon();
        closeIcon.setPreferredSize(new Dimension(20, 20));
        closeIcon.setActionListener(new CanvasActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                VSwingUtil.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        FindAndReplaceToolbar.this.setVisible(false);
                        revalidate();
                        getMainCanvas().revalidate();
                    }
                });
            }
        });
        toolbar.add(closeIcon);

        this.add(toolbar);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (isVisible()) {
            searchField.requestFocus();
            searchField.selectAll();
        } else {
            SearchContext context = new SearchContext();
            SearchEngine.find(textArea, context);
        }
    }

    private void findFirst() {
        textArea.setCaretPosition(0);
        // Create an object defining our search parameters.
        SearchContext context = new SearchContext();
        String text = searchField.getText();

        if (text.length() == 0) {
            return;
        }

        context.setSearchFor(text);
        context.setMatchCase(matchCaseCB.isSelected());
        context.setRegularExpression(regexCB.isSelected());
        context.setSearchForward(true);
        context.setWholeWord(false);

        // if not found
        if (!SearchEngine.find(textArea, context).wasFound()) {
            searchField.setInvalidState(true);
            searchField.repaint();
            nextButton.setEnabled(false);
            prevButton.setEnabled(false);
            replaceButton.setEnabled(false);

        } else {
            searchField.setInvalidState(false);
            searchField.repaint();
            nextButton.setEnabled(true);
            prevButton.setEnabled(true);
            replaceButton.setEnabled(true);
        }

        revalidate();
        getMainCanvas().revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // "FindNext" => search forward, "FindPrev" => search backward
        String command = e.getActionCommand();

        boolean forward = "FindNext".equals(command);

        boolean find = command != null && command.contains("Find");
        boolean replace = command != null && command.contains("Replace");

        // Create an object defining our search parameters.
        SearchContext context = new SearchContext();
        String text = searchField.getText();

        if (text.length() == 0) {
            return;
        }

        context.setSearchFor(text);
        context.setMatchCase(matchCaseCB.isSelected());
        context.setRegularExpression(regexCB.isSelected());
        context.setSearchForward(forward);
        context.setWholeWord(false);

        boolean found = false;

        if (replace) {
            if (textArea.getSelectedText().length() > 0) {
                textArea.replaceSelection(replaceField.getText());

                context.setSearchForward(true);
                SearchEngine.find(textArea, context);
            }
        }

        if (find) {
            found = SearchEngine.find(textArea, context).wasFound();
        }

        if (find && !found) {
            if (forward) {
                textArea.setCaretPosition(0);
            } else {
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }

            if (find) {
                found = SearchEngine.find(textArea, context).wasFound();
            }
        }

        // if still not found
        if (find && !found) {
            searchField.setInvalidState(true);
            searchField.repaint();
        }

        revalidate();
        getMainCanvas().revalidate();
    }
}

/**
 * Custom editor ui that does support non-opaque components.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class VCodeEditorUI extends RSyntaxTextAreaUI {

    public VCodeEditorUI(JComponent rSyntaxTextArea) {
        super(rSyntaxTextArea);
    }

    /**
     * Paints editor augmentations such as matching brackets etc.
     *
     * @param g graphics context that shall be used for painting
     */
    public void paintEditorAugmentations(Graphics g) {
        paintLineHighlights(g);

        Rectangle visibleRect = textArea.getVisibleRect();

        paintCurrentLineHighlight(g, visibleRect);
        paintMarginLine(g, visibleRect);

        paintMatchedBracket(g);
    }

}
