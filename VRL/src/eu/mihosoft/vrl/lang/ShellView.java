/* 
 * ShellView.java
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

package eu.mihosoft.vrl.lang;

import eu.mihosoft.vrl.system.VSysUtil;
import eu.mihosoft.vrl.visual.VTextPane;
import groovyjarjarantlr.CharBuffer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import org.apache.tools.ant.taskdefs.condition.IsLastModified;

public class ShellView {

    private int startPos = 0;
    private ShellInput shellInputStream;
    private ShellHistory history = new ShellHistory();
    private JEditorPane editor;
    private VRLShell shell;

    // TODO review code 30.06.2014
    @Deprecated
    private void setCompletion(JEditorPane editorPane) {
//        DefaultSyntaxKit.initKit();
//        
//        editorPane.setDocument(new SyntaxDocument(new GroovyLexer()));
//        
//        editorPane.setContentType("text/java");
//        String mimeType = "text/x-java"; // NOI18N
//        editorPane.setEditorKit(MimeLookup.getLookup(mimeType).lookup(EditorKit.class));
        // This will find the Java editor kit and associate it with
        // our editor pane. But that does not give us code completion
        // just yet because we have no Java context (i.e. no class path, etc.).
        // However, this does give us syntax coloring.
//        EditorKit kit = CloneableEditorSupport.getEditorKit("text/x-java");
//        editorPane.setEditorKit(kit);
//        // You can specify any ".java" file.
//        // If the file does not exist, it will be created.
//        // The contents of the file does not matter.
//        // The extension must be ".java", however.
//        String newSourcePath = "tmp.java";
//
//        File tmpFile = new File(newSourcePath);
//        FileObject fob = null;
//
//        DataObject dob = null;
//        try {
//            fob = FileUtil.createData(tmpFile);
//
//            dob = DataObject.find(fob);
//        } catch (DataObjectNotFoundException ex) {
//            Logger.getLogger(ShellView.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(ShellView.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        editorPane.getDocument().putProperty(
//                Document.StreamDescriptionProperty,
//                dob);
//
//        // This sets up a default class path for us so that
//        // we can find all the JDK classes via code completion.
//        DialogBinding.bindComponentToFile(fob, 0, 0, editorPane);
//
//        // Last but not least, we need to fill the editor pane with
//        // some initial dummy code - as it seems somehow required to
//        // kick-start code completion.
//        // A simple dummy package declaration will do.
//        editorPane.setText("package dummy;");
    }

    public ShellView(final JEditorPane editor) {
        this.editor = editor;
        
        // add shortcuts
        if (VSysUtil.isMacOSX()) {

            InputMap keyMap = editor.getInputMap();

            KeyStroke keyC = KeyStroke.getKeyStroke(KeyEvent.VK_C,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            KeyStroke keyV = KeyStroke.getKeyStroke(KeyEvent.VK_V,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            KeyStroke keyX = KeyStroke.getKeyStroke(KeyEvent.VK_X,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            KeyStroke keyA = KeyStroke.getKeyStroke(KeyEvent.VK_A,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());


            keyMap.put(keyC, DefaultEditorKit.copyAction);
            keyMap.put(keyV, DefaultEditorKit.pasteAction);
            keyMap.put(keyX, DefaultEditorKit.cutAction);
            keyMap.put(keyA, DefaultEditorKit.selectAllAction);
        }


        Font font = new Font("Monospaced", Font.PLAIN, 14);

        editor.setFont(font);

        // this is necessary due to ugly GUI bug in most L&F implementations.
        if (editor instanceof VTextPane) {
            VTextPane vEditor = (VTextPane) editor;
            vEditor.setBackgroundColor(Color.black);
            vEditor.setForeground(Color.white);
            vEditor.setCaretColor(Color.white);
        } else {
            editor.setBackground(Color.white);
            editor.setForeground(Color.black);
            editor.setCaretColor(Color.black);
            editor.setOpaque(true);
        }

        shellInputStream = new ShellInput();

        editor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                getEditor().getCaret().setVisible(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {

                editor.setEditable(false);

                if (e.getButton() == MouseEvent.BUTTON2) {
                    updateCaretPos();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    updateCaretPos();
                }
            }
        });

        editor.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {

                char ch = e.getKeyChar();

                boolean isModifyingKey = e.getKeyCode() == KeyEvent.VK_DELETE
                        || e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                        || e.getKeyCode() == KeyEvent.VK_TAB
                        || VLangUtils.isPrintableASCIICharacter(ch);

                if (isModifyingKey) {
                    history.updateBuffer(editor.getDocument(),
                            startPos,
                            editor.getDocument().getLength());
                }


                // enable editor
                if (!editor.isEditable()) {
                    editor.setEditable(true);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

                boolean ctrl_c = e.getModifiers() == KeyEvent.CTRL_MASK
                        && e.getKeyCode() == KeyEvent.VK_C;
                boolean meta_c = e.getModifiers() == KeyEvent.META_MASK
                        && e.getKeyCode() == KeyEvent.VK_C;

                boolean ctrl_v = e.getModifiers() == KeyEvent.CTRL_MASK
                        && e.getKeyCode() == KeyEvent.VK_V;
                boolean meta_v = e.getModifiers() == KeyEvent.META_MASK
                        && e.getKeyCode() == KeyEvent.VK_V;

                boolean onlyControl =
                        e.getKeyCode() == KeyEvent.VK_CONTROL;
                boolean onlyMeta =
                        e.getKeyCode() == KeyEvent.VK_META;
                boolean onlyAlt =
                        e.getKeyCode() == KeyEvent.VK_ALT;

                boolean isNotLeftRightArrow =
                        e.getKeyCode() != KeyEvent.VK_LEFT
                        && e.getKeyCode() != KeyEvent.VK_RIGHT;

                // enable editor if no arrow key used.
                // arrows somehow force the editor to set caret to pos = 0
                if (!editor.isEditable() && isNotLeftRightArrow) {
                    editor.setEditable(true);
                }

                int selectionStart = Math.min(getEditor().getSelectionStart(),
                        getEditor().getSelectionEnd());

                int selectionEnd = Math.max(getEditor().getSelectionStart(),
                        getEditor().getSelectionEnd());

                boolean selected = selectionStart != selectionEnd;

                if (selected && selectionStart <= startPos - 1
                        && !(ctrl_c || meta_c || onlyMeta || onlyControl)) {
                    getEditor().setCaretPosition(selectionEnd);
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        e.consume();
                        if (!history.isEmpty()) {
                            String cmd = history.up();
                            replaceInput(cmd);
                        }
                        
                        setCaretPosToLineEnd();
                        break;

                    case KeyEvent.VK_DOWN:
                        e.consume();

                        String cmd = history.down();
                        replaceInput(cmd);

                        setCaretPosToLineEnd();

                        break;
                    case KeyEvent.VK_ENTER:
                        e.consume();
                        newCommand();
                        break;
                    case KeyEvent.VK_BACK_SPACE:

                        boolean caretPosSmallerThanAllowed =
                                editor.getCaretPosition() <= startPos;

                        if (isSelected()) {
                            caretPosSmallerThanAllowed =
                                    editor.getCaretPosition() <= startPos - 1;
                        }

                        if (caretPosSmallerThanAllowed) {
                            e.consume();
                        }

                        break;

                    default:
                        // if no copy operation
                        if (!(ctrl_c || meta_c || onlyControl || onlyMeta)) {
                            if (isSelected() && !(ctrl_v || meta_v)) {
                                removeSelection();
                                break;
                            }

                            // if no paste operation
                            if (!(ctrl_v || meta_v)
                                    && editor.getCaretPosition() >= startPos) {
                                updateCaretPos(e);
                            } else {
                                // if selected and paste-operation
                                if (isSelected() && (ctrl_v || meta_v)) {
                                    boolean selectionOutOfRange =
                                            selectionStart < startPos
                                            || selectionEnd < startPos;
                                    if (selectionOutOfRange) {
                                        setCaretPosToLineEnd();
                                    }
                                }
                                // if no paste-operation
                                else if (!ctrl_v || !meta_v) {
                                    setCaretPosToLineEnd();
                                }
                            }
                        }


                        break;
                } // end switch
            }
        });

    }

    private boolean isSelected() {
        int selectionStart = Math.min(getEditor().getSelectionStart(),
                getEditor().getSelectionEnd());

        int selectionEnd = Math.max(getEditor().getSelectionStart(),
                getEditor().getSelectionEnd());

        return selectionStart != selectionEnd;
    }

    private void removeSelection() {
        try {
            int selectionStart = Math.min(getEditor().getSelectionStart(),
                    getEditor().getSelectionEnd());

            int selectionEnd = Math.max(getEditor().getSelectionStart(),
                    getEditor().getSelectionEnd());

            getEditor().getDocument().remove(selectionStart, selectionEnd);
        } catch (BadLocationException ex) {
//            Logger.getLogger(ShellView.class.getName()).
//                    log(Level.SEVERE, null, ex);
        }
    }

    private void replaceInput(String s) {

        try {

            int length = getEditor().getDocument().getLength() - startPos;

            getEditor().getDocument().remove(startPos, length);

            getEditor().getDocument().insertString(
                    getEditor().getDocument().getLength(), s, null);
        } catch (BadLocationException ex) {
            Logger.getLogger(ShellView.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    private void updateCaretPos(KeyEvent e) {
        if (getEditor().getCaretPosition() <= startPos) {

            getEditor().setCaretPosition(startPos);

            if (e == null) {
                return;
            }

            if (e.getKeyCode() == KeyEvent.VK_LEFT
                    || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                e.consume();
            }
        }
    }

    private void updateCaretPos() {
        updateCaretPos(null);
    }

    private void setCaretPosToLineEnd() {
        int pos = getEditor().getDocument().getLength();
        getEditor().setCaretPosition(pos);
    }

    private void updateStartPos() {
        startPos = getEditor().getDocument().getLength();
    }

    private void newCommand() {

        int length = getEditor().getDocument().getLength() - startPos;
        try {
            String input = getEditor().getDocument().getText(startPos, length);
            shellInputStream.write(input + "\n");
            history.insert(input);
        } catch (BadLocationException ex) {
            Logger.getLogger(ShellView.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ShellView.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        try {
            if (getEditor().getDocument().getLength() > 0) {
                getEditor().getDocument().insertString(getEditor().getDocument().
                        getLength(), "\n", null);
            }
            getEditor().getDocument().insertString(getEditor().getDocument().
                    getLength(), "", null);
            updateCaretPos();
        } catch (BadLocationException ex) {
            Logger.getLogger(ShellView.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        updateStartPos();
    }

    public void setShell(VRLShell shell) {
        this.shell = shell;
        shell.setShellOutStream(new ShellViewPrintStream(this));
        shell.setShellErrStream(new ShellViewPrintStream(this));
        shell.setShellInputStream(getShellInputStream());
    }

    /*
     * Quits the shell.
     * <p>
     * <b>Note:</b> This implementation is rather suboptimal. It writes data
     * to the input stream of the shell as workaround for the following bug: 
     * {@link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4859836}
     * </p>
     */
    public void quit() {

        try {
            if (shell.isRunning()) {
                boolean quitAllowed = shell.isAllowQuit();
                shell.setAllowQuit(true);
                shellInputStream.write("quit\n");
                while (shell.isRunning()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ShellView.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                } // wait until shell does not run anymore
                shell.setAllowQuit(quitAllowed);
            }
        } catch (IOException ex) {
            Logger.getLogger(ShellView.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ShellView.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ShellView.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ShellView.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ShellView.class.getName()).
                    log(Level.SEVERE, null, ex);
        }


        ShellView shellView = new ShellView(new JEditorPane());
        VRLShell shell = new VRLShell();

        JFrame frame = new JFrame();

        frame.setSize(800, 400);

        frame.add(shellView.getEditor());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);

        shellView.setShell(shell);

        shell.run();
    }

    /**
     * @return the shellInputStream
     */
    public InputStream getShellInputStream() {
        return shellInputStream.getInputStream();
    }

    /**
     * @return the editor
     */
    public JEditorPane getEditor() {
        return editor;
    }

    /**
     * @param editor the editor to set
     */
    public void setEditor(JEditorPane editor) {
        this.editor = editor;
    }

    static class ShellViewPrintStream extends PrintStream {

        private ShellView view;

        ShellViewPrintStream(ShellView view) {
            super(new ByteArrayOutputStream());
            this.view = view;
        }

        @Override
        public void println(String str) {
            process(str + "\n");
        }

        @Override
        public void print(String str) {
            process(str);
        }

        private void process(String str) {
            try {
                view.getEditor().getDocument().insertString(
                        view.getEditor().getDocument().getLength(), str, null);
                view.updateStartPos();
                view.updateCaretPos();
            } catch (BadLocationException ex) {
                Logger.getLogger(ShellViewPrintStream.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    static class ShellInput {

        private PipedInputStream in;
        private PipedOutputStream out;

        public ShellInput() {
            init();
        }

        public void write(String s) throws IOException {
            out.write(s.getBytes());
        }

        public InputStream getInputStream() {
            return in;
        }

        private void init() {
            in = new PipedInputStream();

            try {
                out = new PipedOutputStream(in);
            } catch (IOException ex) {
                Logger.getLogger(ShellInput.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    static class ShellHistory {

        private ArrayList<String> history = new ArrayList<String>();
        private int historyPointer = 0;
        private int maxCapacity = 1000;
        private String currentBuffer = "";

        public ShellHistory() {
            //
        }

        public void insert(String s) {
            if (s.isEmpty()) {
                return;
            }

            history.add(s);

            int diff = history.size() - maxCapacity;

            for (int i = 0; i < diff; i++) {
                history.remove(i);
            }

            historyPointer = history.size();

            // reset current buffer
            currentBuffer = "";
        }

        public String up() {
            historyPointer = Math.max(historyPointer - 1, 0);

            if (history.isEmpty()) {
                return "";
            }

            return history.get(historyPointer);
        }

        public String down() {
            historyPointer = Math.min(historyPointer + 1, history.size());

            if (history.isEmpty() || historyPointer == history.size()) {
                return currentBuffer;
            }

            return history.get(historyPointer);
        }

        /**
         * @param maxCapacity the maxCapacity to set
         */
        public void setMaxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        public boolean isEmpty() {
            return history.isEmpty();
        }

        public void clear() {
            history.clear();
        }

        public void updateBuffer(Document d, int start, int stop) {
            try {
                currentBuffer = d.getText(start, stop - start);
            } catch (BadLocationException ex) {
                //
            }
        }
    }
}
