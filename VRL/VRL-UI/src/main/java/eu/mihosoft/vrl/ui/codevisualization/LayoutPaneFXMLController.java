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

import edu.uci.ics.jung.graph.DirectedGraph;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.incubating.LayoutGeneratorNaive;
import eu.mihosoft.vrl.workflow.incubating.LayoutGeneratorSmart;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.Node;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller Class
 * 
 * @author Tobias Mertz
 */
public class LayoutPaneFXMLController implements Initializable {
    
    public static enum layoutGeneratorSelect {
        SMART, NAIVE
    }
    
    @FXML
    public Pane contentPane;
    
    @FXML
    public Pane layoutMenuParent;
    
    public layoutGeneratorSelect layoutSelected;
    private VFlowModel flow;
    
    private LayoutGeneratorSmart layoutSmart;
    private FXMLLoader layoutSmartFXMLLoader;
    public OptionsWindowSmartFXMLController layoutSmartFXMLController;
    private LayoutGeneratorNaive layoutNaive;
    private FXMLLoader layoutNaiveFXMLLoader;
    public OptionsWindowNaiveFXMLController layoutNaiveFXMLController;
    
    //<editor-fold defaultstate="collapsed" desc="Menu Items">
    @FXML
    private ComboBox<layoutGeneratorSelect> selLayout;
    
    @FXML
    private CheckBox checkLayoutDebug;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getter">
    public VFlowModel getFlow() {
        return this.flow;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setter">
    public void setFlow(VFlowModel pflow) {
        this.flow = pflow;
    }
    //</editor-fold>

    /**
     * Initializes the controller class.
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // initialize selection
        ObservableList<layoutGeneratorSelect> layoutItems = selLayout.getItems();
        layoutItems.add(layoutGeneratorSelect.SMART);
        layoutItems.add(layoutGeneratorSelect.NAIVE);
        selLayout.setValue(layoutGeneratorSelect.SMART);
        layoutSelected = layoutGeneratorSelect.SMART;
        checkLayoutDebug.setSelected(false);
        
        // initialize change listeners
        selLayout.valueProperty().addListener(new ChangeListener<layoutGeneratorSelect>() {
            @Override
            public void changed(ObservableValue ov, layoutGeneratorSelect t, layoutGeneratorSelect t1) {
                ObservableList<Node> children = layoutMenuParent.getChildren();
                children.remove(0);
                layoutSelected = t1;
                switch(t1) {
                    case SMART:
                        children.add(layoutSmartFXMLController.contentPane);
                        break;
                    case NAIVE:
                        children.add(layoutNaiveFXMLController.contentPane);
                        break;
                }
            }
        });
        checkLayoutDebug.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ov, Boolean t, Boolean t1) {
                layoutSmart.setDebug(t1);
                layoutNaive.setDebug(t1);
            }
        });
        
        // initialize controller menus
        this.layoutSmart = new LayoutGeneratorSmart();
        this.layoutSmartFXMLLoader = new FXMLLoader(getClass()
                .getResource("OptionsWindowSmartFXML.fxml"));
        try {
            this.layoutSmartFXMLLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.layoutSmartFXMLController = this.layoutSmartFXMLLoader.getController();
        this.layoutSmartFXMLController.setGenerator(this.layoutSmart);
        this.layoutSmartFXMLController.set();
        
        this.layoutNaive = new LayoutGeneratorNaive();
        this.layoutNaiveFXMLLoader = new FXMLLoader(getClass().
                getResource("OptionsWindowNaiveFXML.fxml"));
        try {
            this.layoutNaiveFXMLLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.layoutNaiveFXMLController = this.layoutNaiveFXMLLoader.getController();
        this.layoutNaiveFXMLController.setGenerator(this.layoutNaive);
        this.layoutNaiveFXMLController.set();
        
        this.layoutMenuParent.getChildren().add(this.layoutSmartFXMLController.contentPane);
    }
    
    /*
     * can only be implemented with changes to the layout generators.
     * initialize() is currently a private method.
     */
    @FXML
    void onResetAction(ActionEvent e) {
        switch(this.layoutSelected) {
            case SMART:
                // this.layoutSmart.initialize();
                break;
            case NAIVE:
                // this.layoutNaive.initialize();
                break;
        }
    }
    
    @FXML
    void onLayoutApplyAction(ActionEvent e) {
        switch(this.layoutSelected) {
            case SMART:
                this.layoutSmartFXMLController.accept();
                runSmartLayout();
                this.layoutSmart.setWorkflow(this.flow);
                this.layoutSmart.generateLayout();
                break;
            case NAIVE:
                this.layoutNaiveFXMLController.accept();
                runNaiveLayout();
                this.layoutNaive.setWorkflow(this.flow);
                this.layoutNaive.generateLayout();
                break;
        }
    }
    
    private void runSmartLayout() {
        int i;
        switch(this.layoutSmart.getGraphmode()) {
            case 0:
                this.layoutSmart.setWorkflow(this.flow);
                this.layoutSmart.generateLayout();
                break;
            case 1:
                LayoutGeneratorSmart altlay = new LayoutGeneratorSmart();
                altlay.setWorkflow(this.flow);
                altlay.generateLayout();
                DirectedGraph<VNode, Connection> jgraph = 
                        altlay.getModelGraph();
                this.layoutSmart.setModelGraph(jgraph);
                this.layoutSmart.generateLayout();
                break;
            case 2:
                ObservableList<VNode> obsnodes = flow.getNodes();
                LinkedList<VNode> nodelist = new LinkedList<>();
                for(i = 0; i < obsnodes.size(); i++) {
                    VNode curr = obsnodes.get(i);
                    if(curr.isSelected()) {
                        nodelist.add(curr);
                    }
                }
                if(!nodelist.isEmpty()) {
                    this.layoutSmart.setNodelist(nodelist);
                    this.layoutSmart.generateLayout();
                }
                break;
        }
    }
    
    private void runNaiveLayout() {
        int i;
        switch(this.layoutNaive.getGraphmode()) {
            case 0:
                this.layoutNaive.setWorkflow(this.flow);
                this.layoutNaive.generateLayout();
                break;
            case 2:
                ObservableList<VNode> obsnodes = flow.getNodes();
                LinkedList<VNode> nodelist = new LinkedList<>();
                for(i = 0; i < obsnodes.size(); i++) {
                    VNode curr = obsnodes.get(i);
                    if(curr.isSelected()) {
                        nodelist.add(curr);
                    }
                }
                if(!nodelist.isEmpty()) {
                    this.layoutNaive.setNodelist(nodelist);
                    this.layoutNaive.generateLayout();
                }
                break;
        }
    }

}
