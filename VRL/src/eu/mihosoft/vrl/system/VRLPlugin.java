/* 
 * VRLPlugin.java
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

package eu.mihosoft.vrl.system;

import eu.mihosoft.g4j.lang.G4J;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.devel.LibraryMerger;
import eu.mihosoft.vrl.devel.LibraryPluginCreator;
import eu.mihosoft.vrl.devel.NativePluginCreator;
import eu.mihosoft.vrl.devel.PluginContentAdder;
import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.lang.CompilerProvider;
import eu.mihosoft.vrl.lang.Keywords;
import eu.mihosoft.vrl.lang.groovy.GroovyCodeEditorComponent;
import eu.mihosoft.vrl.lang.visual.*;
import eu.mihosoft.vrl.logging.MessageLogger;
import eu.mihosoft.vrl.math.UserFunction;
import eu.mihosoft.vrl.media.VideoCreator;
import eu.mihosoft.vrl.reflection.ComponentManagementPanel;
import eu.mihosoft.vrl.reflection.ControlFlowConnection;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.visual.VTextField;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VRLPlugin extends VPluginConfigurator {

    public static final PluginIdentifier PLUGIN_IDENTIFIER =
            new PluginIdentifier("VRL", Constants.VERSION_BASE);
    private File templateProjectSrc;

    public VRLPlugin() {
        setIdentifier(PLUGIN_IDENTIFIER);
        setDescription(Message.generateHTMLSpace(20)
                + "Visual Reflection Library"
                + Message.generateHTMLSpace(20));
        setCopyrightInfo("VRL", "(c) 2006-2012 by Michael Hoffer"
                + "<br><br>"
                + "(c) 2009-2012 Steinbeis Forschungszentrum "
                + "(STZ Ölbronn)<br>",
                "http://vrl.mihosoft.eu", "LGPL v3",
                "This version includes  copyright notice and attribution requirements."
                + "According to the LGPL this information must be displayed even if you modify the source code of VRL. "
                + "Neither the VRL Canvas attribution icon nor any copyright statement/attribution may be removed.<br><br>"
                + "<b><font size=5>Attribution Requirements:</font></b><br><br>"
                + "If you create derived work you must do three things regarding copyright notice and author attribution.<br><br>"
                + "<b>First</b>, the following text must be displayed on the Canvas: <b>\"based on VRL source code\"</b>.<br>"
                + "In this case the VRL canvas icon must be removed.<br><br>"
                + "<b>Second</b>, keep the links to \"About VRL-Studio\"and \"About VRL\".<br>"
                + "The copyright notice must remain.<br><br>"
                + "<b>Third</b>, add an additional notice, stating that you modified VRL and/or VRL-Studio. In addition<br>"
                + "you must cite the publications listed below. A suitable notice might read<br>"
                + "\"VRL source code modified by YourName 2012\".<br><br>"
                + "<b>Note</b>, that these requirements are in full accordance with the LGPL v3 (see 7. Additional Terms, b).<br><br>"
                + "Please cite the following publication:<br>"
                + "<pre>"
                + "M. Hoffer, C.Poliwoda, G.Wittum.\n"
                + "Visual Reflection Library -\n"
                + "A Framework for Declarative GUI Programming on the Java Platform.\n"
                + "Computing and Visualization in Science, 2011, in press.\n"
                + "</pre><br>"
                + "<pre><br>" + readLicense() + "</pre>");

        addThirdPartyCopyrightInfo("Groovy",
                "2003-2012 The Codehaus",
                "http://groovy.codehaus.org",
                "Apache Software License 2",
                "http://groovy.codehaus.org/License+Information");

        addThirdPartyCopyrightInfo("Jav3d",
                "Copyright (c) 1997-2007 Sun Microsystems",
                "http://java3d.java.net/",
                "BSD 2-Clause & GPL v2, with CLASSPATH exception",
                "http://www.opensource.org/licenses/bsd-license.php");

        addThirdPartyCopyrightInfo("JNA",
                "Copyright (c) 2007 Timothy Wall",
                "https://github.com/twall/jna",
                "LGPL, version 2.1",
                "http://www.opensource.org/licenses/LGPL-2.1");

        addThirdPartyCopyrightInfo("Batik",
                "Copyright (c) 2008 Apache Software Foundation",
                "http://xmlgraphics.apache.org/batik/",
                "Apache Software License 2",
                "http://www.opensource.org/licenses/Apache-2.0");

        addThirdPartyCopyrightInfo("JGit",
                "Copyright (c) 2011 The Eclipse Foundation",
                "http://eclipse.org/jgit/",
                "Eclipse Distribution License - v 1.0 (BSD)",
                "http://www.eclipse.org/org/documents/edl-v10.php");

        addThirdPartyCopyrightInfo("RSyntaxTextArea",
                "Copyright (C) 2003 - 2012 Robert Futrell",
                "http://fifesoft.com/rsyntaxtextarea/",
                "LGPL, version 2.1",
                "http://www.opensource.org/licenses/LGPL-2.1");

        addThirdPartyCopyrightInfo("Ant",
                "Copyright (C) 2010 Apache Software Foundation",
                "http://ant.apache.org/",
                "Apache Software License 2",
                "http://www.opensource.org/licenses/Apache-2.0");

        addThirdPartyCopyrightInfo("Foxtrot",
                "Copyright (c) 2002-2008, Simone Bordet",
                "http://foxtrot.sourceforge.net/",
                "BSD License",
                "http://www.opensource.org/licenses/bsd-license.php");

    }

    @Override
    public void register(final PluginAPI api) {

        Style defaultStyle = new Style("Default");
        updateDialogStyle(defaultStyle);
        api.addStyle(defaultStyle);

        Style lightStyle = LightStyle.newInstance();
        updateDialogStyle(lightStyle);
        api.addStyle(lightStyle);

        Style darkStyle = DarkStyle.newInstance();
        updateDialogStyle(darkStyle);
        api.addStyle(darkStyle);


        VisualCanvas vCanvas = (VisualCanvas) api.getCanvas();
//        vCanvas.addClass(IconCreator.class);

        vCanvas.setStyle(defaultStyle);

        if (api instanceof VPluginAPI) {

            // obtain visual api object
            VPluginAPI vapi = (VPluginAPI) api;

            // initialize logging
            MessageLogger.getInstance((VisualCanvas) vapi.getCanvas());

            // register default component search filter
            vapi.addComponentSearchFilter(new ComponentManagementPanel.ComponentFilter());

            // editor
            vapi.addEditorConfiguration(new GroovyEditorConfiguration());
            vapi.addComponent(GroovyCodeEditorComponent.class);

            // control-flow objects
            vapi.addComponent(StartObject.class);
            vapi.addComponent(StopObject.class);
            vapi.addComponent(InputObject.class);
            vapi.addComponent(OutputObject.class);

            // media
            vapi.addComponent(VideoCreator.class);

            // devel
            vapi.addComponent(LibraryPluginCreator.class);
            vapi.addComponent(LibraryMerger.class);
            vapi.addComponent(NativePluginCreator.class);
            vapi.addComponent(PluginContentAdder.class);

            // language
            vapi.addComponent(G4J.class);

            // math
            vapi.addComponent(UserFunction.class);
        }

// EXPERIMENTAL
//        api.addAction(new VAction("SVG Test") {
//
//            @Override
//            public void actionPerformed(ActionEvent e, Object owner) {
//                api.getCanvas().addWindow(
//                        SVGWindowCreator.getWindow(api.getCanvas()));
//            }
//        }, ActionDelelator.CANVAS_MENU);
    }

    @Override
    public void unregister(PluginAPI api) {
        //
    }

    @Override
    public void init(InitPluginAPI iApi) {

        setPreferencePane(new PreferencePane() {

            private PreferencePaneControl control;

            @Override
            public String getTitle() {
                return "VRL Preferences";
            }

            @Override
            public JComponent getInterface() {
                VRLPluginPreferencePanel panel = new VRLPluginPreferencePanel();
                panel.setPreferences(control, getInitAPI().getConfiguration());
                return panel;
            }

            @Override
            public String getKeywords() {
                return "VRL, Preferences";
            }

            @Override
            public void setControl(PreferencePaneControl ctrl) {
                this.control = ctrl;
            }
        });


        initTemplateProject(iApi);
    }
    
    private String readLicense() {
        
        String codeName = "/eu/mihosoft/vrl/resources/license/lgplv3.txt";
        
        // load Sample Code
        InputStream iStream = Keywords.class.getResourceAsStream(
                codeName);

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(iStream));

        String code = "";

        try {
            while (reader.ready()) {
                String line = reader.readLine();
                code += line + "\n";
            }
        } catch (IOException ex) {
            Logger.getLogger(Keywords.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Keywords.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
        return code;
    }

    @Override
    public void install(InitPluginAPI iApi) {
        // ensure template projects are updated
        new File(iApi.getResourceFolder(), "VRL-Tutorial-1.vrlp").delete();
    }

    private void saveProjectTemplate() {
        InputStream in = VRLPlugin.class.getResourceAsStream(
                "/eu/mihosoft/vrl/resources/projects/VRL-Tutorial-1.vrlp");
        try {
            IOUtil.saveStreamToFile(in, templateProjectSrc);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VRLPlugin.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VRLPlugin.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    private void initTemplateProject(InitPluginAPI iApi) {
        templateProjectSrc = new File(iApi.getResourceFolder(), "VRL-Tutorial-1.vrlp");

        if (!templateProjectSrc.exists()) {
            saveProjectTemplate();
        }

        iApi.addProjectTemplate(new ProjectTemplate() {

            @Override
            public String getName() {
                return "VRL-Tutorial 1";
            }

            @Override
            public File getSource() {
                return templateProjectSrc;
            }

            @Override
            public String getDescription() {
                return "VRL Tutorial Project 1";
            }

            @Override
            public BufferedImage getIcon() {
                return null;
            }
        });
    }

    private static void updateDialogStyle(Style parent) {
        Style s = parent.getBaseValues().
                getStyle(VDialogWindow.DIALOG_STYLE_KEY);

        s.getBaseValues().set(DefaultMethodRepresentation.METHOD_TITLE_UPPER_COLOR_KEY,
                new Color(75, 75, 75));
        s.getBaseValues().set(
                DefaultMethodRepresentation.METHOD_TITLE_LOWER_COLOR_KEY,
                new Color(40, 40, 40));

        s.getBaseValues().set(Connector.INACTIVE_COLOR_ERROR_KEY,
                new Color(85, 85, 85));
        s.getBaseValues().set(Connector.INACTIVE_COLOR_VALID_KEY,
                new Color(85, 85, 85));

        s.getBaseValues().set(Connector.BORDER_COLOR_KEY,
                new Color(85, 85, 85));

        s.getBaseValues().set(Connection.CONNECTION_COLOR_KEY, new Color(70, 70, 70));
        s.getBaseValues().set(Connection.CONNECTION_THICKNESS_KEY, 2f);
        s.getBaseValues().set(Connection.ACTIVE_CONNECTION_COLOR_KEY, new Color(47, 110, 47));
        s.getBaseValues().set(Connection.ACTIVE_CONNECTION_THICKNESS_KEY, 3f);

        s.getBaseValues().set(ControlFlowConnection.CONTROLFLOW_CONNECTION_COLOR_KEY,
                new Color(100, 100, 100));
        s.getBaseValues().set(ControlFlowConnection.ACTIVE_CONTROLFLOW_CONNECTION_COLOR_KEY,
                new Color(70, 70, 70));
        s.getBaseValues().set(
                ControlFlowConnection.CONTROLFLOW_CONNECTION_THICKNESS_KEY, 5.f);
        s.getBaseValues().set(
                ControlFlowConnection.ACTIVE_CONTROLFLOW_CONNECTION_THICKNESS_KEY, 2.f);

        s.getBaseValues().set(VTextField.TEXT_FIELD_COLOR_KEY, new Color(80, 80, 80));

        s.getBaseValues().set(VCodeEditor.LINE_NUMBER_COLOR_KEY, new Color(160, 160, 160, 160));
        s.getBaseValues().set(VCodeEditor.LINE_NUMBER_FIELD_COLOR_KEY, new Color(160, 160, 160, 20));
        s.getBaseValues().set(VCodeEditor.BACKGROUND_COLOR_KEY, new Color(85, 85, 85));
        s.getBaseValues().set(VCodeEditor.BORDER_COLOR_KEY, new Color(100, 100, 100));
    }
}

class DarkStyle {

    public static Style newInstance() {
        Style s = new Style("Dark");

        s.getBaseValues().set(CanvasGrid.GRID_COLOR_KEY, new Color(50, 50, 50));
        s.getBaseValues().set(Canvas.BACKGROUND_COLOR_KEY, new Color(30, 30, 30));
        s.getBaseValues().set(Canvas.TEXT_COLOR_KEY, new Color(160, 160, 160));
        s.getBaseValues().set(Canvas.SELECTED_TEXT_COLOR_KEY, new Color(160, 160, 160));
        s.getBaseValues().set(Canvas.CARET_COLOR_KEY, new Color(160, 160, 160));
        s.getBaseValues().set(Canvas.TEXT_SELECTION_COLOR_KEY, new Color(130, 145, 180, 60));

        s.getBaseValues().set(CanvasWindow.ICON_COLOR_KEY, new Color(180, 180, 180));
//        s.getBaseValues().set(CanvasWindow.ACTIVE_ICON_COLOR_KEY, new Color(180, 180, 180));
        s.getBaseValues().set(CanvasWindow.UPPER_BACKGROUND_COLOR_KEY, new Color(85, 85, 85));
        s.getBaseValues().set(CanvasWindow.LOWER_BACKGROUND_COLOR_KEY, new Color(35, 35, 35));
        s.getBaseValues().set(CanvasWindow.UPPER_TITLE_COLOR_KEY, new Color(55, 55, 55));
        s.getBaseValues().set(CanvasWindow.LOWER_TITLE_COLOR_KEY, new Color(35, 35, 35));
        s.getBaseValues().set(CanvasWindow.UPPER_ACTIVE_TITLE_COLOR_KEY, new Color(35, 55, 75));
        s.getBaseValues().set(CanvasWindow.LOWER_ACTIVE_TITLE_COLOR_KEY, new Color(45, 35, 45));
        s.getBaseValues().set(CanvasWindow.BORDER_COLOR_KEY, new Color(100, 100, 100));

        s.getBaseValues().set(DefaultMethodRepresentation.METHOD_TITLE_UPPER_COLOR_KEY, new Color(75, 75, 75));
        s.getBaseValues().set(DefaultMethodRepresentation.METHOD_TITLE_LOWER_COLOR_KEY, new Color(40, 40, 40));

        s.getBaseValues().set(MessageBox.BOX_COLOR_KEY, new Color(40, 45, 60));

        s.getBaseValues().set(Connector.INACTIVE_COLOR_ERROR_KEY,
                new Color(85, 85, 85));
        s.getBaseValues().set(Connector.INACTIVE_COLOR_VALID_KEY,
                new Color(85, 85, 85));

        s.getBaseValues().set(Connector.BORDER_COLOR_KEY,
                new Color(85, 85, 85));

        s.getBaseValues().set(Connection.CONNECTION_COLOR_KEY, new Color(70, 70, 70));
        s.getBaseValues().set(Connection.CONNECTION_THICKNESS_KEY, 2f);
        s.getBaseValues().set(Connection.ACTIVE_CONNECTION_COLOR_KEY, new Color(47, 110, 47));
        s.getBaseValues().set(Connection.ACTIVE_CONNECTION_THICKNESS_KEY, 3f);


        s.getBaseValues().set(ControlFlowConnection.CONTROLFLOW_CONNECTION_COLOR_KEY,
                new Color(100, 100, 100));
        s.getBaseValues().set(ControlFlowConnection.ACTIVE_CONTROLFLOW_CONNECTION_COLOR_KEY,
                new Color(70, 70, 70));
        s.getBaseValues().set(
                ControlFlowConnection.CONTROLFLOW_CONNECTION_THICKNESS_KEY, 5.f);
        s.getBaseValues().set(
                ControlFlowConnection.ACTIVE_CONTROLFLOW_CONNECTION_THICKNESS_KEY, 2.f);


        s.getBaseValues().set(VTextField.TEXT_FIELD_COLOR_KEY, new Color(80, 80, 80));

        s.getBaseValues().set(VCodeEditor.LINE_NUMBER_COLOR_KEY, new Color(160, 160, 160, 160));
        s.getBaseValues().set(VCodeEditor.LINE_NUMBER_FIELD_COLOR_KEY, new Color(160, 160, 160, 20));
        s.getBaseValues().set(VCodeEditor.BACKGROUND_COLOR_KEY, new Color(85, 85, 85));
        s.getBaseValues().set(VCodeEditor.BACKGROUND_TRANSPARENCY_KEY, 0.6f);
        s.getBaseValues().set(VCodeEditor.BORDER_COLOR_KEY, new Color(100, 100, 100));


        s.getBaseValues().set(
                SelectionRectangle.FILL_COLOR_KEY, new Color(80, 80, 80, 120));
        s.getBaseValues().set(
                SelectionRectangle.BORDER_COLOR_KEY, new Color(120, 120, 120, 160));

        // Nimbus related
        s.getLookAndFeelValues().set("nimbusBase", new Color(24, 24, 24));
        s.getLookAndFeelValues().set("nimbusBlueGrey", new Color(24, 24, 24));
        s.getLookAndFeelValues().set("control", new Color(40, 40, 40));
        s.getLookAndFeelValues().set("text", new Color(160, 160, 160));
        s.getLookAndFeelValues().set("menuText", new Color(160, 160, 160));
        s.getLookAndFeelValues().set("infoText", new Color(160, 160, 160));
        s.getLookAndFeelValues().set("controlText", new Color(160, 160, 160));
        s.getLookAndFeelValues().set("nimbusSelectedText",
                new Color(160, 160, 160));
        s.getLookAndFeelValues().set("nimbusLightBackground",
                new Color(40, 40, 40));

        // EDITOR STYLE
        SyntaxScheme scheme = new SyntaxScheme(true);

        // set default color to text color
        for (int i = 0; i < scheme.getStyleCount(); i++) {
            if (scheme.getStyle(i) != null) {
                scheme.getStyle(i).foreground = s.getBaseValues().getColor(
                        Canvas.TEXT_COLOR_KEY);
            }
        }

//        StyleContext sc = StyleContext.getDefaultStyleContext();
        Font baseFont = RSyntaxTextArea.getDefaultFont();
        Font boldFont = baseFont.deriveFont(Font.BOLD);
        Font italicFont = baseFont.deriveFont(Font.ITALIC);

        scheme.getStyle(Token.COMMENT_DOCUMENTATION).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_DOCUMENTATION).font = italicFont;
        scheme.getStyle(Token.COMMENT_MULTILINE).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_MULTILINE).font = italicFont;
        scheme.getStyle(Token.COMMENT_EOL).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_EOL).font = italicFont;

        scheme.getStyle(Token.RESERVED_WORD).font = baseFont;
        scheme.getStyle(Token.RESERVED_WORD).foreground = new Color(145, 145, 240);
        scheme.getStyle(Token.DATA_TYPE).foreground = new Color(145, 145, 240);

        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(220, 180, 100);
        scheme.getStyle(Token.LITERAL_CHAR).foreground = new Color(220, 180, 100);

        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = new Color(160, 200, 180);
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = new Color(160, 200, 180);
        scheme.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = new Color(160, 200, 180);

        scheme.getStyle(Token.ERROR_STRING_DOUBLE).foreground = new Color(230, 0, 30);
        scheme.getStyle(Token.ERROR_CHAR).foreground = new Color(230, 0, 30);
        scheme.getStyle(Token.ERROR_NUMBER_FORMAT).foreground = new Color(230, 0, 30);

        s.getBaseValues().setEditorStyle(VCodeEditor.EDITOR_STYLE_KEY, scheme);

        return s;
    }
}

class LightStyle {

    public static Style newInstance() {
        Style s = new Style("Light");

        s.getBaseValues().set(Canvas.BACKGROUND_COLOR_KEY,
                new Color(0.980f, 0.980f, 0.980f));
        s.getBaseValues().set(CanvasWindow.TRANSPARENCY_KEY, 0.92f);

        s.getBaseValues().set(CanvasGrid.GRID_COLOR_KEY,
                new Color(220, 220, 220));


        s.getBaseValues().set(MessageBox.BOX_COLOR_KEY,
                new Color(240, 240, 240));
        s.getBaseValues().set(MessageBox.TOP_TRANSPARENCY_KEY, 1.f);
        s.getBaseValues().set(MessageBox.BOTTOM_TRANSPARENCY_KEY, 1.f);
        s.getBaseValues().set(MessageBox.ICON_COLOR_KEY,
                new Color(70, 70, 70));
        s.getBaseValues().set(MessageBox.ACTIVE_ICON_COLOR_KEY,
                new Color(80, 120, 200));
        s.getBaseValues().set(MessageBox.TEXT_COLOR_KEY,
                new Color(0, 0, 0));


        s.getBaseValues().set(Connection.CONNECTION_COLOR_KEY,
                new Color(70, 70, 70));
        s.getBaseValues().set(Connection.ACTIVE_CONNECTION_COLOR_KEY,
                new Color(0, 120, 20));
        s.getBaseValues().set(Connection.CONNECTION_THICKNESS_KEY, 3.f);
        s.getBaseValues().set(Connection.ACTIVE_CONNECTION_THICKNESS_KEY, 4.f);


        s.getBaseValues().set(ControlFlowConnection.CONTROLFLOW_CONNECTION_COLOR_KEY,
                new Color(70, 70, 70));
        s.getBaseValues().set(ControlFlowConnection.ACTIVE_CONTROLFLOW_CONNECTION_COLOR_KEY,
                new Color(0, 120, 20));
        s.getBaseValues().set(
                ControlFlowConnection.CONTROLFLOW_CONNECTION_THICKNESS_KEY, 6.f);
        s.getBaseValues().set(
                ControlFlowConnection.ACTIVE_CONTROLFLOW_CONNECTION_THICKNESS_KEY, 3.f);


        s.getBaseValues().set(CanvasWindow.UPPER_BACKGROUND_COLOR_KEY,
                new Color(222, 222, 222));
        s.getBaseValues().set(CanvasWindow.LOWER_BACKGROUND_COLOR_KEY,
                new Color(192, 192, 192));
        s.getBaseValues().set(CanvasWindow.UPPER_TITLE_COLOR_KEY,
                new Color(212, 212, 212));
        s.getBaseValues().set(CanvasWindow.LOWER_TITLE_COLOR_KEY,
                new Color(200, 200, 200));
        s.getBaseValues().set(CanvasWindow.UPPER_ACTIVE_TITLE_COLOR_KEY,
                new Color(176, 192, 255));
        s.getBaseValues().set(CanvasWindow.LOWER_ACTIVE_TITLE_COLOR_KEY,
                new Color(160, 192, 220));

        s.getBaseValues().set(VCodeEditor.BACKGROUND_TRANSPARENCY_KEY, 0.8f);
        s.getBaseValues().set(VCodeEditor.BACKGROUND_COLOR_KEY, new Color(248, 248, 248));


        return s;
    }
}

class GroovyEditorConfiguration implements EditorConfiguration {

    private CompletionProvider cp;
    private VisualCanvas canvas;

    @Override
    public void init(VisualCanvas canvas) {
        this.cp = createCompletionProvider(canvas.getClassLoader());
        this.canvas = canvas;
    }

    @Override
    public void configure(RSyntaxTextArea editor) {
        editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
        editor.setCodeFoldingEnabled(true);
        editor.setTabsEmulated(true);
        editor.setTabSize(2);

        final VAutoCompletion ac = new VAutoCompletion(cp);
        ac.addReplacementRule(new AddImportReplacementRule());
        ac.setChoicesWindowSize(600, 250);
        ac.install(editor);
        ac.setTriggerKey(KeyStroke.getKeyStroke(
                KeyEvent.VK_SPACE,
                KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK));

        canvas.addDisposable(new Disposable() {

            @Override
            public void dispose() {
                ac.uninstall();
                EditorProvider.removeConfiguration(
                        GroovyEditorConfiguration.this);
            }
        });
    }

    @Override
    public String getLanguage() {
        return CompilerProvider.LANG_GROOVY;
    }

    /**
     * Create a simple provider that adds some Java-related completions.
     *
     * @return The completion provider.
     */
    private CompletionProvider createCompletionProvider(VClassLoader loader) {

        CompletionProviderGroup provider =
                new CompletionProviderGroup();

//        TestCompletionProvider p = new TestCompletionProvider();
//        p.addCompletion(new ImportTestCompletion(p));
//        p.addCompletion(new BasicCompletion(p, "aaaaa"));
//        
//        provider.addProvider(p);

        provider.addProvider(new ClassLoaderCompletionProvider(loader));

        DefaultCompletionProvider p2 = new DefaultCompletionProvider();

        provider.addProvider(p2);

        // Add completions for all Java keywords.  A BasicCompletion is just
        // a straightforward word completion.
        p2.addCompletion(new VBasicCompletion(p2, "abstract"));
        p2.addCompletion(new BasicCompletion(p2, "assert"));
        p2.addCompletion(new VBasicCompletion(p2, "break"));
        p2.addCompletion(new VBasicCompletion(p2, "case"));
        p2.addCompletion(new VBasicCompletion(p2, "catch"));
        p2.addCompletion(new VBasicCompletion(p2, "class"));
        p2.addCompletion(new VBasicCompletion(p2, "const"));
        p2.addCompletion(new VBasicCompletion(p2, "continue"));
        p2.addCompletion(new VBasicCompletion(p2, "default"));
        p2.addCompletion(new VBasicCompletion(p2, "do"));
        p2.addCompletion(new VBasicCompletion(p2, "else"));
        p2.addCompletion(new VBasicCompletion(p2, "enum"));
        p2.addCompletion(new VBasicCompletion(p2, "extends"));
        p2.addCompletion(new VBasicCompletion(p2, "final"));
        p2.addCompletion(new VBasicCompletion(p2, "finally"));
        p2.addCompletion(new VBasicCompletion(p2, "for"));
        p2.addCompletion(new VBasicCompletion(p2, "goto"));
        p2.addCompletion(new VBasicCompletion(p2, "if"));
        p2.addCompletion(new VBasicCompletion(p2, "implements"));
        p2.addCompletion(new VBasicCompletion(p2, "import"));
        p2.addCompletion(new VBasicCompletion(p2, "instanceof"));
        p2.addCompletion(new VBasicCompletion(p2, "interface"));
        p2.addCompletion(new VBasicCompletion(p2, "native"));
        p2.addCompletion(new VBasicCompletion(p2, "new"));
        p2.addCompletion(new VBasicCompletion(p2, "package"));
        p2.addCompletion(new VBasicCompletion(p2, "private"));
        p2.addCompletion(new VBasicCompletion(p2, "protected"));
        p2.addCompletion(new VBasicCompletion(p2, "public"));
        p2.addCompletion(new VBasicCompletion(p2, "return"));
        p2.addCompletion(new VBasicCompletion(p2, "static"));
        p2.addCompletion(new VBasicCompletion(p2, "strictfp"));
        p2.addCompletion(new VBasicCompletion(p2, "super"));
        p2.addCompletion(new VBasicCompletion(p2, "switch"));
        p2.addCompletion(new VBasicCompletion(p2, "synchronized"));
        p2.addCompletion(new VBasicCompletion(p2, "this"));
        p2.addCompletion(new VBasicCompletion(p2, "throw"));
        p2.addCompletion(new VBasicCompletion(p2, "throws"));
        p2.addCompletion(new VBasicCompletion(p2, "transient"));
        p2.addCompletion(new VBasicCompletion(p2, "try"));
        p2.addCompletion(new VBasicCompletion(p2, "void"));
        p2.addCompletion(new VBasicCompletion(p2, "volatile"));
        p2.addCompletion(new VBasicCompletion(p2, "while"));

        // Add a couple of "shorthand" completions.  These completions don't
        // require the input text to be the same thing as the replacement text.
        p2.addCompletion(new VShortHandCompletion(p2,
                "sout", "System.out.println(", "System.out.println("));
        p2.addCompletion(new VShortHandCompletion(p2,
                "serr", "System.err.println(", "System.err.println("));
        p2.addCompletion(new VShortHandCompletion(p2,
                "fori", "for (int i = 0; i < i_n; i++) { ",
                "for (int i = 0; i < n;i++)"));
        p2.addCompletion(new VShortHandCompletion(p2,
                "forj", "for (int j = 0; j < j_n; j++) { ",
                "for (int j = 0; j < m;j++)"));
        p2.addCompletion(new VShortHandCompletion(p2,
                "fork", "for (int k = 0; k < k_n; k++) { ",
                "for (int k = 0; k < n; k++)"));
        p2.addCompletion(new VShortHandCompletion(p2,
                "forx", "for (int x = 0; x < x_n; x++) { ",
                "for (int x = 0; x < n; x++)"));
        p2.addCompletion(new VShortHandCompletion(p2,
                "fory", "for (int y = 0; y < y_n; y++) { ",
                "for (int y = 0; y < n; y++)"));
        p2.addCompletion(new VShortHandCompletion(p2,
                "forz", "for (int z = 0; z < z_n; z++) { ",
                "for (int z = 0; z < n; z++)"));
        p2.addCompletion(new VShortHandCompletion(p2,
                "fort", "for (int t = 0; t < t_n; t++) { ",
                "for (int t = 0; t < t_n; t++)"));
        p2.addCompletion(new VShortHandCompletion(p2, "vcomp",
                "@ComponentInfo(name=\"NAME\", category=\"Custom\")\n"
                + "public class CLSNAME implements java.io.Serializable {\n\t"
                + "private static final long serialVersionUID=1L;\n\n\t"
                + "// add your code here\n\n}",
                "@ComponentInfo(name=\"NAME\", category=\"Custom\")\n"
                + "public class CLSNAME implements java.io.Serializable {\n\t"
                + "private static final long serialVersionUID=1L;\n\n\t"
                + "// add your code here\n\n}"));
        p2.addCompletion(new VShortHandCompletion(p2, "cinf",
                "@ComponentInfo(name=\"NAME\", category=\"Custom\")",
                "@ComponentInfo(name=\"NAME\", category=\"Custom\")"));
        p2.addCompletion(new VShortHandCompletion(p2, "oinf",
                "@ObjectInfo(name=\"NAME\")",
                "@ObjectInfo(name=\"NAME\")"));
        p2.addCompletion(new VShortHandCompletion(p2, "minf",
                "@MethodInfo(name=\"\", valueName=\"\", valueStyle=\"default\", valueOptions=\"\")",
                "@MethodInfo(name=\"\", valueName=\"\", valueStyle=\"default\", valueOptions=\"\")"));
        p2.addCompletion(new VShortHandCompletion(p2, "pinf",
                "@ParamInfo(name=\"\", style=\"default\", options=\"\")",
                "@ParamInfo(name=\"\", style=\"default\", options=\"\")"));
        p2.addCompletion(new VShortHandCompletion(p2, "tinf",
                "@TypeInfo(type=Object.class,input=true, output=true, style=\"default\")",
                "@TypeInfo(type=Object.class,input=true, output=true, style=\"default\")"));
        p2.addCompletion(new VShortHandCompletion(p2, "pginf",
                "@ParamGroupInfo(group=\"default|true|no description\")",
                "@ParamGroupInfo(group=\"default|true|no description\")"));

        return provider;
    }
}
