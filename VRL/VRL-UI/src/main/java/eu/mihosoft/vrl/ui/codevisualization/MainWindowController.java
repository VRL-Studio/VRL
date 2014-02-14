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

import eu.mihosoft.vrl.instrumentation.Scope2Code;
import eu.mihosoft.vrl.instrumentation.ScopeInvocation;
import eu.mihosoft.vrl.instrumentation.ScopeType;
import eu.mihosoft.vrl.instrumentation.UIBinding;
import eu.mihosoft.vrl.instrumentation.Variable;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.DataFlow;
import eu.mihosoft.vrl.lang.model.DataRelation;
import eu.mihosoft.vrl.lang.model.ForDeclaration;
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
import eu.mihosoft.vrl.workflow.io.PersistentNode;
import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooserBuilder;

/**
 * FXML Controller class
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MainWindowController implements Initializable {

    File currentDocument;
    @FXML
    TextArea editor;
    @FXML
    Pane view;
    private Pane rootPane;
    private VFlow flow;
    private final Map<CodeEntity, VNode> invocationNodes = new HashMap<>();
    private final Map<String, Invocation> nodeInvocations = new HashMap<>();
    private final Map<String, Scope> nodeToScopes = new HashMap<>();
    private final Map<String, Connector> variableConnectors = new HashMap<>();
    private final Map<String, LayoutData> layoutData = new HashMap<>();

    /**
     * Initializes the controller class.
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
        saveDocument(false);
        updateView();
    }

    private void saveDocument(boolean askForLocationIfAlreadyOpened) {

        if (askForLocationIfAlreadyOpened || currentDocument == null) {
            FileChooser.ExtensionFilter mdFilter
                    = new FileChooser.ExtensionFilter(
                            "Text Files (*.groovy, *.txt)", "*.groovy", "*.txt");

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
        updateView();
    }

    @FXML
    public void onCloseAction(ActionEvent e) {
    }

    void loadTextFile(File f) {

        try {
            if (f == null) {
                FileChooser.ExtensionFilter mdFilter
                        = new FileChooser.ExtensionFilter(
                                "Text Files (*.groovy, *.txt)", "*.groovy", "*.txt");

                FileChooser.ExtensionFilter allFilesfilter
                        = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

                currentDocument
                        = FileChooserBuilder.create().title("Open Groovy File").
                        extensionFilters(mdFilter, allFilesfilter).build().
                        showOpenDialog(null).getAbsoluteFile();
            } else {
                currentDocument = f;
            }

            editor.setText(new String(Files.readAllBytes(
                    Paths.get(currentDocument.getAbsolutePath())), "UTF-8"));

//            CompilationUnitDeclaration cu = Scope2Code.demoScope();
//            editor.setText(Scope2Code.getCode(cu));
            updateView();

        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    private void updateView() {

        savePositions();

        if (rootPane == null) {
            System.err.println("UI NOT READY");
            return;
        }

        UIBinding.scopes.clear();

        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(editor.getText(), "Script");

        System.out.println("UPDATE UI");

        flow.clear();

        flow.setSkinFactories();

        System.out.println("FLOW: " + flow.getSubControllers().size());

        flow.getModel().setVisible(true);

        if (UIBinding.scopes == null) {
            System.err.println("NO SCOPES");
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

//        Layout layout = LayoutFactory.newDefaultLayout();
//        layout.doLayout(flow);
    }

    public void dataFlowToFlow(Scope scope, VFlow parent) {

        DataFlow dataflow = scope.getDataFlow();
        dataflow.create(scope.getControlFlow());

        for (Invocation i : scope.getControlFlow().getInvocations()) {

//            Variable retValue = scope.getVariable(i.getReturnValueName());
            List<DataRelation> relations = dataflow.getRelationsForReceiver(i);

//            System.out.println("relations: " + relations.size());
            for (DataRelation dataRelation : relations) {

                VNode sender = invocationNodes.get(dataRelation.getSender());
                VNode receiver = invocationNodes.get(dataRelation.getReceiver());

//                System.out.println("SENDER: " + sender.getId() + ", receiver: " + receiver.getId());
                String retValueName
                        = dataRelation.getSender().getReturnValueName();

//                System.out.println(" --> sender: " + retValueName);
                Connector senderConnector = getVariableById(sender, retValueName);

                int inputIndex = 0;

                for (Variable var : dataRelation.getReceiver().getArguments()) {
//                    System.out.println(" --> receiver: " + var.getName() + ", (possible receiver)");
                    if (var.getName().equals(retValueName)) {
                        Connector receiverConnector = getVariableById(receiver, var.getName());

                        ConnectionResult result = parent.connect(
                                senderConnector, receiverConnector);

//                        System.out.println(" -> connected: " + result.getStatus().isCompatible());
//                        System.out.println(" -> " + result.getStatus().getMessage());
//                        System.out.println(inputIndex + " = connect: " + senderConnector.getType() + ":" + senderConnector.isOutput() + " -> " + receiverConnector.getType() + ":" + receiverConnector.isInput());
                    }
                    inputIndex++;
                }
            }
        }
    }

    public VFlow scopeToFlow(Scope scope, VFlow parent) {

        boolean isClassOrScript = scope.getType() == ScopeType.CLASS
                || scope.getType() == ScopeType.COMPILATION_UNIT
                || scope.getType() == ScopeType.NONE;

        VFlow result = parent.newSubFlow();

        addCloseNodeListener(result.getModel());
        addControlflowListener(scope, result);
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

            } else {
                n = result.newNode();
                String mTitle = "" + i.getVariableName() + "."
                        + i.getMethodName() + "(): " + i.getId();
                n.setTitle(mTitle);

                invocationNodes.put(i, n);
                nodeInvocations.put(n.getId(), i);
            }

            n.setMainInput(n.addInput("control"));
            n.setMainOutput(n.addOutput("control"));

            if (prevNode != null) {
                result.connect(prevNode, n, "control");
            }

            for (Variable v : i.getArguments()) {
                Connector input = n.addInput("data");
//                System.out.println(" > Write Connector: ");
                variableConnectors.put(getVariableId(n, v), input);
            }

            if (!i.isVoid()) {
                Connector output = n.addOutput("data");
                Variable v = scope.getVariable(i.getReturnValueName());
//                System.out.println(" > Write Connector: ");
                variableConnectors.put(getVariableId(n, v), output);
            }

            if (!applyPosition(n)) {
                n.setWidth(400);
                n.setHeight(100);
            }

            prevNode = n;
        }

        if (isClassOrScript) {
            for (Scope s : scope.getScopes()) {
                scopeToFlow(s, result);
            }
        }

        dataFlowToFlow(scope, result);

        return result;
    }

    private void savePositions() {
        nodeToScopes.keySet().
                forEach(id -> savePosition(flow.getNodeLookup().getById(id)));
    }

    private void applyPositions() {
        nodeToScopes.keySet().
                forEach(id -> applyPosition(flow.getNodeLookup().getById(id)));
    }

    private CodeEntity nodeToCodeEntity(VNode n) {
        boolean isScope = nodeToScopes.get(n.getId()) != null;
        boolean isInvocation = nodeInvocations.get(n.getId()) != null;

        CodeEntity cE = null;
        
        if (isScope) {
            cE = nodeToScopes.get(n.getId());
        } else if (isInvocation) {
            cE =  nodeInvocations.get(n.getId());
        }
        
        System.out.println("ce: " + cE.getId());

        return cE;
    }

    private boolean applyPosition(VNode n) {

        CodeEntity cE = nodeToCodeEntity(n);

        if (cE == null) {
            return false;
        }

        LayoutData d = layoutData.get(codeId(cE));

        if (d == null) {
            return false;
        }

        d.apply(n);

        return true;
    }

    private void savePosition(VNode n) {

        CodeEntity cE = nodeToCodeEntity(n);

        if (cE == null) {
            return;
        }

        layoutData.put(codeId(cE), new LayoutData(n));

    }

    private void saveUIData(Path p) {

    }

    private void loadUIData(Path p) {

    }

    private String codeId(CodeEntity cE) {

        List<String> parts = new ArrayList<>();

        parts.add(entityId(cE));

        while (cE.getParent() != null) {
            cE = cE.getParent();
            parts.add(entityId(cE));
        }

        Collections.reverse(parts);

        String str = String.join(":", parts);

        System.out.println("id " + cE.getId() + " -> " + str);

        return str;
    }

    private String entityId(CodeEntity cE) {
        if (cE instanceof Invocation) {
            return "inv:"+((Invocation) cE).getMethodName();
        } else if (cE instanceof ForDeclaration) {
            return "for";
        } else if (cE instanceof WhileDeclaration) {
            return "while";
        } else if (cE instanceof Scope) {
            return ((Scope) cE).getName();
        }

        return "";
    }

    private void addCloseNodeListener(VFlowModel flow) {
        flow.getNodes().addListener(
                (ListChangeListener.Change<? extends VNode> change) -> {
                    while (change.next()) {

                        change.getRemoved().forEach(removedN -> {

                            Scope s = nodeToScopes.get(removedN.getId());

                            if (s.getParent() != null) {
                                s.getParent().removeScope(s);
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

    private boolean isRoot(VNode n, String connectionType) {
        return n.getInputs().stream().filter(
                (Connector c) -> {
                    return c.getType().equals(connectionType)
                    && !c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).isEmpty();
                }).count() == 0;
    }

    private void addControlflowListener(Scope rootScope, VFlow result) {
        result.getConnections("control").getConnections().addListener(
                (ListChangeListener.Change<? extends Connection> change) -> {

                    List<VNode> roots = result.getNodes().filtered(
                            n -> isRoot(n, "control"));

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
                            });

                    paths.forEach(path
                            -> path.forEach(node
                                    -> rootScope.getControlFlow().
                                    getInvocations().add(nodeInvocations.get(node.getId()))
                            )
                    );
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
}

class LayoutData {

    private double x;
    private double y;
    private double width;
    private double height;

    public LayoutData() {
    }

    public LayoutData(VNode n) {
        this.x = n.getX();
        this.y = n.getY();
        this.width = n.getWidth();
        this.height = n.getHeight();
    }

    public LayoutData(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
    }
}
