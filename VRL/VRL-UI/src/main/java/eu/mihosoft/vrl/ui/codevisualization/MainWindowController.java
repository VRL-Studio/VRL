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
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.ArgumentType;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.lang.model.ScopeType;
import eu.mihosoft.vrl.lang.model.UIBinding;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.Comment;
import eu.mihosoft.vrl.lang.model.CommentType;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.DataFlow;
import eu.mihosoft.vrl.lang.model.DataRelation;
import eu.mihosoft.vrl.lang.model.ForDeclaration;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connections;
//import eu.mihosoft.vrl.worflow.layout.Layout;
//import eu.mihosoft.vrl.worflow.layout.LayoutFactory;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooserBuilder;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

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
    private Pane rootPane;
    private VFlow flow;
    private final Map<CodeEntity, String> invocationNodes = new HashMap<>();
    private final Map<String, Invocation> nodeInvocations = new HashMap<>();
    private final Map<String, Scope> nodeToScopes = new HashMap<>();
    private final Map<String, Connector> variableConnectors = new HashMap<>();
    private final Map<String, Integer> connectorsToArgIndex = new HashMap<>();
    private final Map<String, Integer> variableArgumentIndex = new HashMap<>();
    private final Map<Invocation, Connector> invocationOutputs = new HashMap<>();
    private final Map<Invocation, Connector> invocationInputs = new HashMap<>();
    private final Map<String, LayoutData> layoutData = new HashMap<>();

    private final Set<String> loadLayoutIds = new HashSet<String>();

    private FileAlterationMonitor fileMonitor;
    private FileAlterationObserver observer;

    private Stage mainWindow;

    private boolean isRunningScopeToFlow;
    private boolean isRunningCodeToScope;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        System.out.println("Init");

        ScalableContentPane canvas = new ScalableContentPane();
        canvas.setStyle("-fx-background-color: rgb(0,0, 0)");

        canvas.setMaxScaleX(1);
        canvas.setMaxScaleY(1);

        view.getChildren().add(canvas);

        Pane root = new Pane();
        canvas.setContentPane(root);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, rgb(10,32,60), rgb(42,52,120));");

        rootPane = root;

        flow = FlowFactory.newFlow();

        fileMonitor = new FileAlterationMonitor(3000);

        try {
            fileMonitor.start();
        } catch (Exception ex) {
            Logger.getLogger(MainWindowController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
//        VCodeEditor vEditor = new VCodeEditor(" the code ");
//        
//        canvas.getContentPane().getChildren().add(vEditor.getNode());
        
        
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
        loadTextFile(null);
    }

    @FXML
    public void onSaveAction(ActionEvent e) {
        updateCode(UIBinding.scopes.values().iterator().next().get(0));

        updateView(false);

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
            fileWriter.write(editor.getText());
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
        updateView(false);
    }

    @FXML
    public void onCloseAction(ActionEvent e) {
    }

    void loadTextFile(File f) {

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

            editor.setText(new String(Files.readAllBytes(
                    Paths.get(currentDocument.getAbsolutePath())), "UTF-8"));

            updateView(false);

        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

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
                        try {
                            editor.setText(new String(
                                    Files.readAllBytes(
                                            Paths.get(currentDocument.getAbsolutePath())),
                                    "UTF-8"));
                            updateView(true);
                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(MainWindowController.class.getName()).
                                    log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(MainWindowController.class.getName()).
                                    log(Level.SEVERE, null, ex);
                        }
                    });
                }
            }
        });

        fileMonitor.addObserver(observer);

    }

    private void updateView(boolean refresh) {

        isRunningCodeToScope = true;

        savePositions();

        if (rootPane == null) {
            System.err.println("UI NOT READY");
            return;
        }

        nodeInvocations.clear();
        invocationNodes.clear();
        nodeToScopes.clear();
        variableConnectors.clear();
        connectorsToArgIndex.clear();
        variableArgumentIndex.clear();

        UIBinding.scopes.clear();

        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(editor.getText(), "Script");

        //if (!refresh) {
            loadUIData();
        //}

        System.out.println("UPDATE UI");

        flow.clear();

        flow.setSkinFactories();

        System.out.println("FLOW: " + flow.getSubControllers().size());

        flow.getModel().setVisible(true);

        if (UIBinding.scopes == null) {
            System.err.println("NO SCOPES");
            isRunningCodeToScope = false;
            return;
        }

        for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
            for (Scope s : scopeList) {
                scopeToFlow(s, flow);
            }
        }

        FXSkinFactory skinFactory = new FXSkinFactory(rootPane);
        flow.setSkinFactories(skinFactory);

        applyPositions();

        saveUIData();

//        Layout layout = LayoutFactory.newDefaultLayout();
//        layout.doLayout(flow);
        isRunningCodeToScope = false;
    }

    public void dataFlowToFlow(Scope scope, VFlow parent) {

        DataFlow dataflow = scope.getDataFlow();
        dataflow.create(scope.getControlFlow());

        for (Invocation i : scope.getControlFlow().getInvocations()) {

//            Variable retValue = scope.getVariable(i.getReturnValueName());
            List<DataRelation> relations = dataflow.getRelationsForReceiver(i);

//            System.out.println("relations: " + relations.size());
            for (DataRelation dataRelation : relations) {

                Connector output = invocationOutputs.get(dataRelation.getSender());
                Connector input = invocationInputs.get(dataRelation.getSender());

                ConnectionResult result = parent.connect(output, input);

            }
        }
    }

    public VFlow scopeToFlow(Scope scope, VFlow parent) {

        isRunningScopeToFlow = true;

        boolean isClassOrScript = scope.getType() == ScopeType.CLASS
                || scope.getType() == ScopeType.COMPILATION_UNIT
                || scope.getType() == ScopeType.NONE;

        VFlow result = parent.newSubFlow();

        addCloseNodeListener(result.getModel());
        addControlflowListener(scope, result);
        addDataflowListener(scope, result);
//        invocationNodes.put(scope, result.getModel());
        nodeToScopes.put(result.getModel().getId(), scope);

        String title = "" + scope.getType() + " "
                + scope.getName() + "(): " + scope.getId();

        if (isClassOrScript) {
            result.getModel().setWidth(550);
            result.getModel().setHeight(800);
            result.setVisible(true);
        } else {
            result.getModel().setWidth(400);
            result.getModel().setHeight(300);
        }

        result.getModel().setTitle(title);

//        System.out.println("Title: " + title + ", " + scope.getType());
        VNode prevNode = null;

        List<Invocation> invocations = new ArrayList<>();
        invocations.addAll(scope.getControlFlow().getInvocations());

        for (Invocation i : invocations) {

            VNode n;

            if (i.isScope() && !isClassOrScript) {

                ScopeInvocation sI = (ScopeInvocation) i;
                n = scopeToFlow(sI.getScope(), result).getModel();

                invocationNodes.put(i, n.getId());
                nodeInvocations.put(n.getId(), i);

            } else {
                n = result.newNode();
                String mTitle = "" + i.getVariableName() + "."
                        + i.getMethodName() + "(): " + i.getId();
                n.setTitle(mTitle);

                invocationNodes.put(i, n.getId());
                nodeInvocations.put(n.getId(), i);
            }

            n.setMainInput(n.addInput("control"));
            n.setMainOutput(n.addOutput("control"));

            if (prevNode != null) {
                result.connect(prevNode, n, "control");
            }

            int index = 0;
            for (IArgument a : i.getArguments()) {

                Connector input = n.addInput("data");
//                System.out.println(" > Write Connector: ");
//                variableConnectors.put(getVariableId(n, v), input);
//                System.out.println("-> adding input: " + getVariableId(n, v));
//                variableArgumentIndex.put(getVariableId(n, v), index);
                connectorsToArgIndex.put(input.getId(), index);

                if (a.getArgType() == ArgumentType.INVOCATION) {
                    invocationInputs.put(a.getInvocation().get(), input);
                } else if (a.getArgType() == ArgumentType.VARIABLE) {
                    variableConnectors.put(getVariableId(n, a.getVariable().get()), input);
                }

                index++;
            }

            if (!i.isVoid()) {
                Connector output = n.addOutput("data");
                invocationOutputs.put(i, output);
//                Variable v = scope.getVariable(i.getReturnValue().get().getName());
//                System.out.println(" > Write Connector: ");
//                variableConnectors.put(getVariableId(n, v), output);

            }

//            if (!applyPosition(n)) {
            n.setWidth(400);
            n.setHeight(100);
//            }

            prevNode = n;
        }

        if (isClassOrScript) {
            for (Scope s : scope.getScopes()) {
                scopeToFlow(s, result);
            }
        }

        dataFlowToFlow(scope, result);

        isRunningScopeToFlow = false;

        return result;
    }

    private void savePositions() {

        layoutData.clear();

        nodeToScopes.keySet().
                forEach(id -> savePosition(flow.getNodeLookup().getById(id)));
        nodeInvocations.keySet().
                forEach(id -> savePosition(flow.getNodeLookup().getById(id)));
    }

    private void applyPositions() {
        nodeToScopes.keySet().
                forEach(id -> applyPosition(flow.getNodeLookup().getById(id)));
        nodeInvocations.keySet().
                forEach(id -> applyPosition(flow.getNodeLookup().getById(id)));
    }

    private CodeEntity nodeToCodeEntity(VNode n) {
        boolean isScope = nodeToScopes.get(n.getId()) != null;
        boolean isInvocation = nodeInvocations.get(n.getId()) != null;

        CodeEntity cE = null;

        if (isScope) {
            cE = nodeToScopes.get(n.getId());
        } else if (isInvocation) {
            cE = nodeInvocations.get(n.getId());
        }

//        System.out.println("ce: " + cE.getId());
        return cE;
    }

    private boolean applyPosition(VNode n) {

        CodeEntity cE = nodeToCodeEntity(n);

        if (cE == null) {
            return false;
        }

        String id = codeId(cE, true);

        LayoutData d = layoutData.get(id);

        if (d == null) {
            System.out.println("cannot load pos for [" + cE.getId() + ", " + id + "]");
            return false;
        }

        d.apply(n);

        return true;
    }

    private void savePosition(VNode n) {

        CodeEntity cE = nodeToCodeEntity(n);

        if (cE == null) {
            System.out.println("cannot save pos for " + n.getTitle());
            return;
        }

        layoutData.put(codeId(cE, false), new LayoutData(n));

    }

    private void saveUIData() {
        XStream xstream = new XStream();
        xstream.alias("layout", LayoutData.class);
        String data = xstream.toXML(layoutData);

        CompilationUnitDeclaration cud
                = (CompilationUnitDeclaration) UIBinding.scopes.values().
                iterator().next().get(0);

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

        toBeRemoved.forEach(c -> System.out.println("out: " + c.getType()));

        cud.getComments().removeAll(toBeRemoved);

        updateCode(cud);
        editor.setText(editor.getText()
                + "// <editor-fold defaultstate=\"collapsed\" desc=\"VRL-Data\">\n"
                + "/*<!VRL!><Type:VRL-Layout>\n" + data + "\n*/\n"
                + "// </editor-fold>");
    }

    private void loadUIData() {
        try {
            CompilationUnitDeclaration cud
                    = (CompilationUnitDeclaration) UIBinding.scopes.values().
                    iterator().next().get(0);

            Predicate<Comment> vrlLayoutType = (Comment c) -> {
                return c.getType()
                        == CommentType.VRL_MULTI_LINE
                        && c.getComment().contains("<!VRL!><Type:VRL-Layout>");
            };

            Optional<Comment> vrlC = cud.getComments().stream().
                    filter(vrlLayoutType).findFirst();

            if (vrlC.isPresent()) {
                String vrlComment = vrlC.get().getComment();
                vrlComment = vrlComment.substring(26, vrlComment.length() - 2);
                XStream xstream = new XStream();
                xstream.alias("layout", LayoutData.class);
                layoutData.clear();
                loadLayoutIds.clear();
                layoutData.putAll(
                        (Map<String, LayoutData>) xstream.fromXML(vrlComment));
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
        } else if (cE instanceof ForDeclaration) {
            return "for:var=" + ((ForDeclaration) cE).getVarName();
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

    private void addCloseNodeListener(VFlowModel flow) {
        flow.getNodes().addListener(
                (ListChangeListener.Change<? extends VNode> change) -> {
                    while (change.next()) {

                        change.getRemoved().forEach(removedN -> {

                            Scope s = nodeToScopes.get(removedN.getId());

                            if (s != null) {
                                if (s.getParent() != null) {
                                    s.getParent().removeScope(s);
                                }
                            } else {
                                Invocation i = nodeInvocations.get(removedN.getId());

                                if (i != null) {
                                    if (i.getParent() != null) {
                                        i.getParent().getControlFlow().
                                        getInvocations().remove(i);
                                    }
                                }
                            }

                        });

                        if (change.wasRemoved()) {
                            Scope rootScope = nodeToScopes.get(flow.getId());
                            updateCode(rootScope);
                        }
                    }
                }
        );
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

    private void addControlflowListener(Scope rootScope, VFlow result) {
        result.getConnections("control").getConnections().addListener(
                (ListChangeListener.Change<? extends Connection> change) -> {

                    try {

                        if (isRunningCodeToScope) {
                            return;
                        }

                        List<VNode> roots = result.getNodes().filtered(WorkflowUtil.nodeNotConnected("control"));

                        // clear current control flow
                        rootScope.getControlFlow().getInvocations().clear();

                        List<List<VNode>> paths = new ArrayList<>();

                        // follow controlflow from roots to end
                        roots.forEach(
                                r -> {
//                                System.out.println("-- root " + r.getTitle() + " --");

                                    List<VNode> path = getPath(r, "control");

//                                path.forEach(
//                                        n -> System.out.println("n->" + n.getTitle()));
                                    paths.add(path);
                                }
                        );

                        paths.forEach(path
                                -> path.forEach(node
                                        -> {
                            System.out.println("-->adding inv: " + nodeInvocations.get(node.getId()) + " :: " + isRunningCodeToScope);
                            rootScope.getControlFlow().
                            getInvocations().add(nodeInvocations.get(node.getId()));
                        }
                                )
                        );
                        updateCode(rootScope);
                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    }
                });

    }

    private void addDataflowListener(Scope rootScope, VFlow result) {
        result.getConnections("data").getConnections().addListener(
                (ListChangeListener.Change<? extends Connection> change) -> {

                    if (isRunningCodeToScope) {
                        return;
                    }

                    while (change.next()) {
                        if (change.wasAdded()) {
                            for (Connection conn : change.getAddedSubList()) {
                                VNode senderN = conn.getSender().getNode();
                                VNode receiverN = conn.getReceiver().getNode();

                                Invocation senderInv = nodeInvocations.get(senderN.getId());
                                Invocation receiverInv = nodeInvocations.get(receiverN.getId());

                                int numConnections
                                = senderN.getOutputs().filtered(
                                        WorkflowUtil.moreThanConnections(1, "data")).size();

                                if (numConnections > 0) {
                                    // TODO introduce var declaration to prevent multiple method calls for each dataflow connection 19.02.2014
                                    System.out.println("NORE THAN ONE DATAFLOW CONN!!! for invocation " + senderInv);
                                }

//                                Connector output = variableConnectors.get(
//                                        getVariableId(senderN, senderInv.getReturnValue().get().getName()));
//                                Connector input = variableConnectors.get(
//                                        getVariableId(receiverN, senderInv.getReturnValue().get().getName()));
                                // TODO if (senderInv equals variableinvocation) only invocations supported 18.02.2014
                                try {

//                                    Variable retVal = senderInv.getReturnValue().get();
//                                    String varId = getVariableId(receiverN, retVal.getName());
//                                    System.out.println("varId: " + varId);
                                    int argIndex = connectorsToArgIndex.get(conn.getReceiver().getId());

                                    receiverInv.getArguments().set(
                                            argIndex,
                                            Argument.invArg(senderInv));
//
//                                    System.out.println("argIndex: " + argIndex + "argument: " + senderInv + ", recInv: " + receiverInv);

                                    senderInv.getParent().generateDataFlow();
                                } catch (Exception ex) {
                                    ex.printStackTrace(System.err);
                                }
                            }
                        }
                        if (change.wasRemoved()) {
                            for (Connection conn : change.getRemoved()) {
                                VNode senderN = conn.getSender().getNode();
                                VNode receiverN = conn.getReceiver().getNode();

                                Invocation senderInv = nodeInvocations.get(senderN.getId());
                                Invocation receiverInv = nodeInvocations.get(receiverN.getId());

                                try {

                                    int argIndex = connectorsToArgIndex.get(conn.getReceiver().getId());

                                    receiverInv.getArguments().set(
                                            argIndex,
                                            Argument.NULL);

//                                    System.out.println("argIndex: " + argIndex + "argument: " + senderInv + ", recInv: " + receiverInv);
                                    senderInv.getParent().generateDataFlow();
                                } catch (Exception ex) {
                                    ex.printStackTrace(System.err);
                                }

                            }
                        }
                    }
                    updateCode(rootScope);
                });
    }

    private void updateCode(Scope rootScope) {
        System.out.println("Scope: UPDATED");
        String code = Scope2Code.getCode(
                (CompilationUnitDeclaration) getRootScope(rootScope));
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

    public Connector getVariableById(VNode n, String varName) {
        return variableConnectors.get(getVariableId(n, varName));
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
                fileMonitor.stop();
            } catch (Exception ex) {
                Logger.getLogger(MainWindowController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        });
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

    public LayoutData(double x, double y, double width, double height, boolean contentVisible) {
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
