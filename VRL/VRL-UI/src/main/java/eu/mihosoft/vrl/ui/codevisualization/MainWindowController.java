/* 
 * MainWindowController.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
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
 * Computing and Visualization in Science, in press.
 */
package eu.mihosoft.vrl.ui.codevisualization;

import com.thoughtworks.xstream.XStream;
import eu.mihosoft.vrl.base.VSysUtil;
import eu.mihosoft.vrl.instrumentation.InstrumentationEvent;
import eu.mihosoft.vrl.instrumentation.InstrumentationEventType;
import eu.mihosoft.vrl.instrumentation.VRLInstrumentationUtil;
import eu.mihosoft.vrl.instrumentation.VRLVisualizationTransformation;
import eu.mihosoft.vrl.lang.VCommentParser;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.lang.model.UIBinding;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeEventType;
import eu.mihosoft.vrl.lang.model.Comment;
import eu.mihosoft.vrl.lang.model.CommentType;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;
import eu.mihosoft.vrl.lang.model.transform.InstrumentCode;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.WorkflowUtil;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkin;
import eu.mihosoft.vrl.workflow.fx.FXValueSkinFactory;
import eu.mihosoft.vrl.workflow.fx.VCanvas;
import eu.mihosoft.vrl.workflow.skin.VNodeSkin;
import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooserBuilder;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.scene.control.window.WindowUtil;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;

/**
 * FXML Controller class
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MainWindowController implements Initializable {

    private File currentDocument;
    @FXML
    private TextArea editor;
    @FXML
    private Pane view;

    @FXML
    private MenuItem menuSaveItem;

    @FXML
    private MenuItem menuLoadItem;

    @FXML
    private MenuItem menuCloseItem;

    @FXML
    private MenuItem menuRunItem;

    private Pane rootPane;
    private VFlow flow;

    private final Map<String, LayoutData> layoutData = new HashMap<>();

    private final Set<String> loadLayoutIds = new HashSet<>();

    private FileAlterationMonitor fileMonitor;
    private FileAlterationObserver observer;

    private Stage mainWindow;

    private String uiData;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        VCanvas canvas = new VCanvas();
//        canvas.setStyle("-fx-background-color: rgb(0,0, 0)");

        canvas.setMinScaleX(0.2);
        canvas.setMinScaleY(0.2);
        canvas.setMaxScaleX(1);
        canvas.setMaxScaleY(1);

        canvas.setOnMouseClicked((evt) -> {
            WindowUtil.getDefaultClipboard().deselectAll();
            editor.deselect();
        });

//        canvas.setScaleBehavior(ScaleBehavior.IF_NECESSARY);
//        canvas.setTranslateBehavior(TranslateBehavior.IF_NECESSARY);
        addResetViewMenu(canvas);

        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        view.getChildren().add(scrollPane);

        rootPane = canvas.getContent();

        //rootPane.setStyle("-fx-background-color: linear-gradient(to bottom, rgb(10,32,60), rgb(42,52,120));");
        flow = FlowFactory.newFlow();
        flow.setVisible(true);
        flow.getModel().getVisualizationRequest().set(
                VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
        UIBinding.setRootFlow(flow);

        VariableFlowNodeSkin.setEditor(editor, flow);

        FXValueSkinFactory skinFactory = new FXValueSkinFactory(rootPane);
        skinFactory.setDefaultNodeSkin(VariableFlowNodeSkin.class);
        flow.setSkinFactories(skinFactory);

        fileMonitor = new FileAlterationMonitor(3000);

        try {
            fileMonitor.start();
        } catch (Exception ex) {
            Logger.getLogger(MainWindowController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        flow.getConnections(WorkflowUtil.CONTROL_FLOW).getConnections().addListener(
                (ListChangeListener.Change<? extends Connection> c) -> {
                    updateCode((Scope) UIBinding.getRootFlow().getModel().getValueObject().getValue());
                    System.out.println("changed cflow: " + c);
                });

        flow.getConnections(WorkflowUtil.DATA_FLOW).getConnections().addListener(
                (ListChangeListener.Change<? extends Connection> c) -> {
                    updateCode((Scope) UIBinding.getRootFlow().getModel().getValueObject().getValue());
                    System.out.println("changed dflow: " + c);
                });

//        VCodeEditor vEditor = new VCodeEditor(" the code ");
//        
//        canvas.getContentPane().getChildren().add(vEditor.getNode());
        Modifier cmdModifier;

        if (VSysUtil.isMacOSX()) {
            cmdModifier = KeyCodeCombination.META_DOWN;
        } else {
            cmdModifier = KeyCodeCombination.CONTROL_DOWN;
        }

        menuSaveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, cmdModifier));
        menuCloseItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, cmdModifier));
        menuLoadItem.setAccelerator(new KeyCodeCombination(KeyCode.L, cmdModifier));
        menuRunItem.setAccelerator(new KeyCodeCombination(KeyCode.R, cmdModifier));
    }

    @FXML
    public void onKeyTyped(KeyEvent evt) {
//        String output = editor.getText();
//
//        output = MultiMarkdown.convert(output);
//
//        System.out.println(output);
//
//        
//
//        URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
//
//            @Override
//            public URLStreamHandler createURLStreamHandler(String protocol) {
//                
//            }
//        });
//        
//        
//        outputView.getEngine().s
    }

    @FXML
    public void onLoadAction(ActionEvent e) {
        loadTextFile(null, true);
    }

    @FXML
    public void onSaveAction(ActionEvent e) {
        //updateCode(UIBinding.scopes.values().iterator().next().get(0));

        //updateView();
        saveDocument(false);
    }

    private void saveDocument(boolean askForLocationIfAlreadyOpened) {

        if (askForLocationIfAlreadyOpened || currentDocument == null) {
            FileChooser.ExtensionFilter mdFilter
                    = new FileChooser.ExtensionFilter(
                            "Text Files (*.groovy, *.java, *.txt)",
                            "*.groovy", "*.txt", "*.java");

            FileChooser.ExtensionFilter allFilesfilter
                    = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

            currentDocument
                    = FileChooserBuilder.create().title("Save Groovy File").
                    extensionFilters(mdFilter, allFilesfilter).build().
                    showSaveDialog(null).getAbsoluteFile();
        }

        try (FileWriter fileWriter = new FileWriter(currentDocument)) {
            if (compiles(editor.getText())) {
                savePositions();
                saveUIData();
            }
            fileWriter.write(editor.getText() + "\n" + uiData);
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

    }

    private void insertStringAtCurrentPosition(String s) {
        editor.insertText(editor.getCaretPosition(), s);
    }

    @FXML
    public void onSaveAsAction(ActionEvent e) {
        saveDocument(true);
//        updateView();
    }

    @FXML
    public void onCloseAction(ActionEvent e) {
    }

    @FXML
    public void onRunCodeAction(ActionEvent e) {
        runCode();
    }

    public void loadTextFile(File f, boolean loadUIData) {

        try {
            if (f == null) {
                FileChooser.ExtensionFilter mdFilter
                        = new FileChooser.ExtensionFilter(
                                "Text Files (*.groovy, *.java, *.txt)",
                                "*.groovy", "*.txt", "*.java");

                FileChooser.ExtensionFilter allFilesfilter
                        = new FileChooser.ExtensionFilter(
                                "All Files (*.*)", "*.*");

                currentDocument
                        = FileChooserBuilder.create().title("Open Groovy File").
                        extensionFilters(mdFilter, allFilesfilter).build().
                        showOpenDialog(null).getAbsoluteFile();
            } else {
                currentDocument = f;
            }

            String loadedText = new String(Files.readAllBytes(
                    Paths.get(currentDocument.getAbsolutePath())), "UTF-8");

            editor.setText(loadedText);

            if (loadUIData) {
                uiData = loadedText;
                loadUIData();
            }

        } catch (Exception ex) {
            Logger.getLogger(MainWindowController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        updateView();
        CompilationUnitDeclaration cud
                = (CompilationUnitDeclaration) UIBinding.scopes.values().
                iterator().next().get(0);
        updateCode(cud);

        if (observer != null) {
            fileMonitor.removeObserver(observer);
        }

        observer = new FileAlterationObserver(
                currentDocument.getAbsoluteFile().getParentFile());
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {

                if (file.equals(currentDocument.getAbsoluteFile())) {

                    Platform.runLater(() -> {
//                        try {

                        try {
                            savePositions();
                            saveUIData();
                        } catch (Exception ex) {
                            ex.printStackTrace(System.err);
                        }

                        loadTextFile(file, false);
//                            editor.setText(new String(
//                                    Files.readAllBytes(
//                                            Paths.get(currentDocument.getAbsolutePath())),
//                                    "UTF-8"));
//                            updateView();
//                        } catch (UnsupportedEncodingException ex) {
//                            Logger.getLogger(MainWindowController.class.getName()).
//                                    log(Level.SEVERE, null, ex);
//                        } catch (IOException ex) {
//                            Logger.getLogger(MainWindowController.class.getName()).
//                                    log(Level.SEVERE, null, ex);
//                        }
                    });
                }
            }
        });

        fileMonitor.addObserver(observer);

    }

    private Optional<VNode> getNodeByCodeId(VFlowModel flow, String id) {

//        System.out.println("-> searching for: " + id);
        for (VNode vn : flow.getNodes()) {

            boolean valObjectIsCodeEntity
                    = vn.getValueObject().getValue() instanceof CodeEntity;

            if (valObjectIsCodeEntity) {
                CodeEntity ce = (CodeEntity) vn.getValueObject().getValue();

                if (Objects.equals(ce.getId(), id)) {
                    return Optional.of(vn);
                }
            }

            if (vn instanceof VFlowModel) {
                VFlowModel subFlow = (VFlowModel) vn;
                Optional<VNode> res = getNodeByCodeId(subFlow, id);

                if (res.isPresent()) {
                    return res;
                }
            }

        }

        return Optional.empty();
    }

//    private Optional<VNode> _getNodeByCodeId(VFlow flow, String id) {
//        return flow.getNodes().stream().
//                filter((vn) -> {
//                    return vn.getValueObject() instanceof CodeEntity;
//                }).
//                map((vn) -> {
//                    return (CodeEntity) vn.getValueObject();
//                }).
//                filter((ce) -> {
//                    return Objects.equals(ce.getId(),
//                            id);
//                }).findFirst().map(ce -> ce.getNode());
//    }
    private void runCode() {

        // copy cu
        // TODO 10.08.2015 add copy/cloning functionality (see todos in model)
        VFlow origFlow = flow;
        CompilationUnitDeclaration origCU
                = (CompilationUnitDeclaration) UIBinding.scopes.values().
                iterator().next().get(0);
        VFlow instrFlow = FlowFactory.newFlow();
        UIBinding.setRootFlow(instrFlow);
        updateView();
        CompilationUnitDeclaration clonedCU
                = (CompilationUnitDeclaration) UIBinding.scopes.values().
                iterator().next().get(0);

        UIBinding.setRootFlow(origFlow);
        UIBinding.scopes.values().iterator().next().clear();
        UIBinding.scopes.values().iterator().next().add(origCU);

        String clonedCode = Scope2Code.getCode(clonedCU);
        System.out.println("cloned: " + clonedCode);

        clonedCU.disableParentUpdate();
        InstrumentCode instrumentCode = new InstrumentCode();
        CompilationUnitDeclaration newCu = instrumentCode.transform(clonedCU);

        String instrCode = Scope2Code.getCode(newCu);
        System.out.println("instr:\n\n" + instrCode);

//        if (true) {
//            return;
//        }
        String instrumentedCode = Scope2Code.getCode(newCu);

//        VRLInstrumentationUtil.addEventHandler(
//                InstrumentationEventType.PRE_INVOCATION,
//                (evt) -> {
//                    System.out.println("pre-evt:\t" + evt.toString());
//                });
//        VRLInstrumentationUtil.addEventHandler(
//                InstrumentationEventType.POST_INVOCATION,
//                (evt) -> {
//                    System.out.println("post-evt:\t" + evt.toString());
//                });
        VRLInstrumentationUtil.addEventHandler(
                InstrumentationEventType.INVOCATION,
                (evt) -> {
                    getNodeByCodeId(
                            flow.getModel(),
                            evt.getSource().getId()).ifPresent(
                            vn -> {
                                visualizeEvent(evt, vn);
                            });
                });

        try {
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class<?> instrumentedCodeClass = gcl.parseClass(instrumentedCode);
            instrumentedCodeClass.getMethod("main", String[].class).
                    invoke(instrumentedCodeClass, (Object) new String[0]);

            visualizeProfiling(origCU);

        } catch (CompilationFailedException | NoSuchMethodException |
                SecurityException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(MainWindowController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    private void visualizeProfiling(CompilationUnitDeclaration cu) {

        AtomicLong minDuration = new AtomicLong();
        AtomicLong maxDuration = new AtomicLong();

        cu.visitScopeAndAllSubElements((cE) -> {
            Object durationObj = cE.getMetaData().get("VRL:duration");
            if (durationObj instanceof Long
                    && !(cE instanceof ScopeInvocation)
                    && !(cE instanceof Scope)) {
                minDuration.set(
                        Math.min(minDuration.get(),
                                (long) durationObj));
                maxDuration.set(
                        Math.max(maxDuration.get(),
                                (long) durationObj));
//                System.out.println("max: " + maxDuration + " : "
//                        + durationObj + " : " + cE);
            }
        });

        cu.visitScopeAndAllSubElements((cE) -> {
            Object durationObj = cE.getMetaData().get("VRL:duration");
            
            
            if (durationObj instanceof Long
                    && !(cE instanceof ScopeInvocation)
                    && !(cE instanceof Scope)) {

                VNode n = cE.getNode();
                visualizeProfilingData(
                        (long) durationObj,
                        minDuration.get(), maxDuration.get(), n);
            }
        });
    }

    private boolean compiles(String s) {

        // copy cu
        // TODO 10.08.2015 add copy/cloning functionality (see todos in model)
        VFlow origFlow = flow;

        CompilationUnitDeclaration origCU = null;

        try {

            origCU = (CompilationUnitDeclaration) UIBinding.scopes.values().
                    iterator().next().get(0);
            VFlow tmpFlow = FlowFactory.newFlow();
            UIBinding.setRootFlow(tmpFlow);

            CompilerConfiguration ccfg = new CompilerConfiguration();

            ccfg.addCompilationCustomizers(new ASTTransformationCustomizer(
                    new VRLVisualizationTransformation()));

            GroovyClassLoader gcl = new GroovyClassLoader(
                    new GroovyClassLoader(), ccfg);
        } catch (Exception ex) {
            return false;
        } finally {
            UIBinding.setRootFlow(origFlow);

            if (origCU != null) {
                UIBinding.scopes.values().iterator().next().clear();
                UIBinding.scopes.values().iterator().next().add(origCU);
            }
        }

        return true;
    }

    private void updateView() {

        savePositions();

        if (rootPane == null) {
            System.err.println("UI NOT READY");
            return;
        }

        UIBinding.scopes.clear();
        UIBinding.getRootFlow().clear();

        CompilerConfiguration ccfg = new CompilerConfiguration();

        ccfg.addCompilationCustomizers(new ASTTransformationCustomizer(
                new VRLVisualizationTransformation()));

        GroovyClassLoader gcl = new GroovyClassLoader(
                new GroovyClassLoader(), ccfg);

        try {
            gcl.parseClass(editor.getText(), "Script");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        loadUIData();

        if (UIBinding.scopes == null) {
            System.err.println("NO SCOPES");
            return;
        }

        for (List<Scope> sList : UIBinding.scopes.values()) {
            for (Scope scope : sList) {
                scope.generateDataFlow();
                scope.addEventHandler(CodeEventType.CHANGE, evt -> {
                    updateCode((CompilationUnitDeclaration) UIBinding.scopes.
                            values().
                            iterator().next().get(0));
                });
            }
        }

        applyPositions();

        saveUIData();

    }

    private void savePositions() {

        layoutData.clear();

//        nodeToScopes.keySet().
//                forEach(id -> savePosition(flow.getNodeLookup().getById(id)));
//        nodeInvocations.keySet().
//                forEach(id -> savePosition(flow.getNodeLookup().getById(id)));
        if (UIBinding.scopes.values().
                iterator().hasNext()) {
            CompilationUnitDeclaration cud
                    = (CompilationUnitDeclaration) UIBinding.scopes.values().
                    iterator().next().get(0);

            cud.visitScopeAndAllSubElements(s -> {
                savePosition(s);
            });
        }

    }

    private void applyPositions() {

        if (UIBinding.scopes.values().
                iterator().hasNext()) {
            CompilationUnitDeclaration cud
                    = (CompilationUnitDeclaration) UIBinding.scopes.values().
                    iterator().next().get(0);

            cud.visitScopeAndAllSubElements(s -> {
                applyPosition(s);
            });
        }
    }

    private boolean applyPosition(CodeEntity cE) {
        Objects.requireNonNull(cE);

        if (cE.getNode() == null) {
            return false;
        }

        String id = codeId(cE, true);

        LayoutData d = layoutData.get(id);

        // auto layout for nodes without previous layout data
        if (d == null) {

            VNode n = cE.getNode();
            VFlowModel nParent = n.getFlow();
            Collection<Connection> connections
                    = nParent.getConnections(
                            WorkflowUtil.CONTROL_FLOW).
                    getAllWithNode(n);

            Optional<VNode> previousNode = connections.stream().
                    filter(pn -> pn.getReceiver().getNode().equals(n)).
                    map(conn -> conn.getSender().getNode()).
                    findFirst();

            int gap = 50;

            if (previousNode.isPresent()) {
                VNode prevN = previousNode.get();
                n.setX(prevN.getX() + prevN.getWidth() + gap);
                n.setY(prevN.getY());
            }

            return false;
        }

        d.apply(cE.getNode());

        return true;
    }

    private void savePosition(CodeEntity cE) {

        Objects.requireNonNull(cE);

        if (cE.getNode() == null) {
            return;
        }

        layoutData.put(
                codeId(cE, false),
                new LayoutData(cE.getNode()));

    }

    private void saveUIData() {

        XStream xstream = new XStream();
        xstream.alias("layout", LayoutData.class);
        String data = xstream.toXML(layoutData);

        CompilationUnitDeclaration cud
                = (CompilationUnitDeclaration) UIBinding.scopes.values().
                iterator().next().get(0);
        removeUIDataComment(cud);

        uiData = "// <editor-fold defaultstate=\"collapsed\" desc=\"VRL-Data\">\n"
                + "/*<!VRL!><Type:VRL-Layout>\n" + data + "\n*/\n"
                + "// </editor-fold>";
    }

    private void removeUIDataComment(CompilationUnitDeclaration cud) {

        Predicate<Comment> vrlLayoutType = (Comment c) -> {
            return c.getType() == CommentType.VRL_MULTI_LINE
                    && c.getComment().contains("<!VRL!><Type:VRL-Layout>");
        };

        Predicate<Comment> editorFoldType = (Comment c) -> {
            return c.getComment().contains("<editor-fold")
                    || c.getComment().contains("</editor-fold");
        };

        List<Comment> toBeRemoved = cud.getComments().stream().
                filter(vrlLayoutType.or(editorFoldType)).
                collect(Collectors.toList());

        cud.getComments().removeAll(toBeRemoved);
    }

    private void loadUIData() {
        try {
//            CompilationUnitDeclaration cud
//                    = (CompilationUnitDeclaration) UIBinding.scopes.values().
//                    iterator().next().get(0);

            List<Comment> comments = VCommentParser.parse(new StringReader(uiData));

            Predicate<Comment> vrlLayoutType = (Comment c) -> {
                return c.getType()
                        == CommentType.VRL_MULTI_LINE
                        && c.getComment().contains("<!VRL!><Type:VRL-Layout>");
            };

            Optional<Comment> vrlC = comments.stream().
                    filter(vrlLayoutType).findFirst();

            if (vrlC.isPresent()) {
                String vrlComment = vrlC.get().getComment();
                vrlComment = vrlComment.substring(26, vrlComment.length() - 2);
                XStream xstream = new XStream();
                xstream.alias("layout", LayoutData.class);
                layoutData.clear();
                loadLayoutIds.clear();
                layoutData.putAll((Map<String, LayoutData>) xstream.fromXML(vrlComment));
            } else {
                System.err.println("-> cannot load layout - not present!");
            }
        } catch (Exception ex) {
            System.err.println(
                    "-> cannot load layout - exception while loading!");
        }
    }

    private String codeId(CodeEntity cE, boolean load) {

        List<String> parts = new ArrayList<>();

        parts.add(entityId(cE));

        while (cE.getParent() != null) {
            cE = cE.getParent();
            parts.add(entityId(cE));
        }

        Collections.reverse(parts);

        String str = String.join(":", parts);

        Set<String> set;

        if (load) {
            set = loadLayoutIds;
        } else {
            set = layoutData.keySet();
        }

        str = incCodeId(set, str);

        if (load) {
            set.add(str);
        }

//        System.out.println("id " + cE.getId() + " -> " + str);
        return str;
    }

    private String entityId(CodeEntity cE) {
        if (cE instanceof Invocation) {
            if (cE instanceof ScopeInvocation) {
                ScopeInvocation scopeInv = (ScopeInvocation) cE;

                entityId(scopeInv.getScope());
            }

            return "inv:" + ((Invocation) cE).getMethodName();
        } else if (cE instanceof SimpleForDeclaration) {
            return "for:var=" + ((SimpleForDeclaration) cE).getVarName();
        } else if (cE instanceof WhileDeclaration) {
            return "while";
        } else if (cE instanceof Scope) {
            return ((Scope) cE).getName();
        }

        return "";
    }

    public String incCodeId(Set<String> ids, String id) {

        if (!ids.contains(id)) {

            return id;
        }

        int counter = 0;
        String result = id + ":" + counter;

        while (ids.contains(result)) {
            counter++;
            result = id + ":" + counter;
        }

        return result;
    }

    private boolean isRoot(VNode node, String connectionType) {

        Predicate<Connector> notConnected = (Connector c) -> {
            return c.getType().equals(connectionType)
                    && !c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).isEmpty();
        };

        Predicate<VNode> rootNode = (VNode n) -> {
            return n.getInputs().filtered(notConnected).isEmpty();
        };

        return rootNode.test(node);
    }

    private void updateCode(Scope rootScope) {
        System.out.println("Scope: UpdateCode");
        CompilationUnitDeclaration cud
                = (CompilationUnitDeclaration) getRootScope(rootScope);
        removeUIDataComment(cud);
        String code = Scope2Code.getCode(cud);

        editor.setText(code);
    }

    private Scope getRootScope(Scope s) {
        Scope root = s;

        while (s.getParent() != null) {
            root = s.getParent();
            s = root;
        }

        return root;
    }

    private List<VNode> getPath(VNode sender, String connectionType) {

        List<VNode> result = new ArrayList<>();

        if (!isRoot(sender, connectionType)) {
            System.err.println("sender is no root!");
            return result;
        }

        result.add(sender);

        Connections connections = sender.getFlow().getConnections(connectionType);
        Collection<Connection> connectionsWithSender
                = connections.getAllWith(sender.getMainOutput(connectionType));

        while (!connectionsWithSender.isEmpty()) {

            VNode newSender = null;

            for (Connection c : connectionsWithSender) {

                if (newSender == c.getReceiver().getNode()) {
                    System.err.println("circular flow!");
                    return result;
                }

                newSender = c.getReceiver().getNode();

                result.add(newSender);
                break; // we only support one connection per controlflow conector
            }

            if (newSender != null) {
                connectionsWithSender
                        = connections.getAllWith(
                                newSender.getMainOutput(connectionType));
            } else {
                connectionsWithSender.clear();
            }
        }

        return result;
    }

    public static String getVariableId(VNode n, Variable v) {
        String id = n.getId() + ":" + v.getName();

        System.out.println("id: " + id);

        return id;
    }

    public static String getVariableId(VNode n, String varName) {
        String id = n.getId() + ":" + varName;

        System.out.println("id: " + id);

        return id;
    }

    /**
     * @return the mainWindow
     */
    public Stage getMainWindow() {
        return mainWindow;
    }

    /**
     * @param mainWindow the mainWindow to set
     */
    public void setMainWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;

        mainWindow.setOnCloseRequest((WindowEvent event) -> {
            try {
                fileMonitor.stop(1);
            } catch (Exception ex) {
                Logger.getLogger(MainWindowController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        });
    }

    static void addResetViewMenu(VCanvas canvas) {
        final ContextMenu cm = new ContextMenu();
        MenuItem resetViewItem = new MenuItem("Reset View");
        resetViewItem.setOnAction((ActionEvent e) -> {
            canvas.resetScale();
            canvas.resetTranslation();
        });
        cm.getItems().add(resetViewItem);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                cm.show(canvas, e.getScreenX(), e.getScreenY());
            }
        });
    }

    private void visualizeProfilingData(
            long duration, long minDuration, long maxDuration, VNode vn) {

        if (vn.getValueObject().getValue() instanceof CodeEntity) {

            List<VNodeSkin> skins
                    = flow.getNodeSkinLookup().getById(vn.getId());

            double a = 0.45;

            Color maxColor = Color.rgb(255, 0, 0);
            Color midColor = Color.rgb(255, 255, 0);
            Color minColor = Color.rgb(0, 255, 0);
            
            double t = (double) (duration-minDuration)
                    / (double) (maxDuration-minDuration);

            Color c;

            if (t < 0.5) {
//                System.out.println("g-y: " + (t*2) + ", " + t);
                c = minColor.interpolate(midColor, t * 2);
            } else {
//                System.out.println("y-r: " + ((t-0.5)*2) + ", " + t);
                c = midColor.interpolate(maxColor, (t - 0.5) * 2);
            }

            double r = c.getRed(), g = c.getGreen(), b = c.getBlue();

            skins.stream().filter((s)
                    -> (s instanceof FXFlowNodeSkin)).forEach((s) -> {
                ((FXFlowNodeSkin) s).getNode().
                        setStyle("-fx-background-color: "
                                + "rgba("
                                + (int) (r * 255) + ","
                                + (int) (g * 255) + ","
                                + (int) (b * 255) + ","
                                + a + ");"
                        );
            });
        }
    }

    private void visualizeEvent(InstrumentationEvent evt, VNode vn) {
//        System.out.println("vn: " + vn.getId());

        if (vn.getValueObject().getValue() instanceof CodeEntity) {
            CodeEntity ce = (CodeEntity) vn.getValueObject().getValue();

            if (evt.getType() == InstrumentationEventType.POST_INVOCATION) {
                ce.getMetaData().put("VRL:retVal", evt.getSource().
                        getReturnValue().orElse(null));
                ce.getMetaData().put("VRL:post-timestamp",
                        evt.getTimeStamp());
                Object prevDurationObj = ce.getMetaData().get("VRL:duration");
                long prevDuration;
                if (prevDurationObj instanceof Long) {
                    prevDuration = (long) prevDurationObj;
                } else {
                    prevDuration = 0;
                }

                // add duration
                ce.getMetaData().put("VRL:duration",
                        prevDuration
                        + Math.abs(evt.getTimeStamp()
                                - (long) ce.getMetaData().
                                get("VRL:pre-timestamp")));

            } else if (evt.getType()
                    == InstrumentationEventType.PRE_INVOCATION) {
                ce.getMetaData().put("VRL:args",
                        evt.getSource().getArguments());
                ce.getMetaData().put("VRL:pre-timestamp",
                        evt.getTimeStamp());
            }
        }

//        List<VNodeSkin> skins = flow.getNodeSkinLookup().getById(vn.getId());
//
//        skins.stream().filter(sk -> sk instanceof VariableFlowNodeSkin).
//                map((sk) -> {
//                    return (VariableFlowNodeSkin) sk;
//                }).
//                forEach(sk -> {
//                    sk.updateOutputView(evt.getSource().getReturnValue());
//                });
    }
}

class LayoutData {

    private double x;
    private double y;
    private double width;
    private double height;
    private boolean contentVisible;

    public LayoutData() {
    }

    public LayoutData(VNode n) {

        Objects.requireNonNull(n);

        this.x = n.getX();
        this.y = n.getY();
        this.width = n.getWidth();
        this.height = n.getHeight();

        if (n instanceof VFlowModel) {
            this.contentVisible = ((VFlowModel) n).isVisible();
        } else {
            this.contentVisible = true;
        }
    }

    public LayoutData(
            double x, double y,
            double width, double height,
            boolean contentVisible) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.contentVisible = contentVisible;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    public void apply(VNode n) {
        n.setX(x);
        n.setY(y);
        n.setWidth(width);
        n.setHeight(height);

        if (n instanceof VFlowModel) {
            ((VFlowModel) n).setVisible(contentVisible);
        }
    }
}
