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

import eu.mihosoft.vrl.workflow.incubating.LayoutGeneratorNaive;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
public class OptionsWindowNaiveFXMLController implements Initializable {
    
    @FXML
    public Pane contentPane;
    
    private LayoutGeneratorNaive generator;
    private Stage optionsstage;
    private VFlow workflow;
    private final String[] graphmodes = new String[3];

    /**
     * Initializes the controller class.
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.graphmodes[0] = "VFlowModel";
        this.graphmodes[1] = "-";
        this.graphmodes[2] = "nodelist";
        ObservableList<String> graphmodelist = this.selGraphmode.getItems();
        graphmodelist.add(this.graphmodes[0]);
        graphmodelist.add(this.graphmodes[1]);
        graphmodelist.add(this.graphmodes[2]);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Menu items">
    @FXML
    private CheckBox checkCalcVertPos;

    @FXML
    private CheckBox checkRemoveCycles;

    @FXML
    private TextField tfSubflowscale;

    @FXML
    private ComboBox<String> selGraphmode;

    @FXML
    private CheckBox checkAutoscaleNodes;

    @FXML
    private CheckBox checkCreateLayering;

    @FXML
    private TextField tfScaling;

    @FXML
    private CheckBox checkRecursive;

    @FXML
    private CheckBox checkCalcHorPos;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Setter">
    public void setGenerator(LayoutGeneratorNaive pgenerator) {
        this.generator = pgenerator;
    }
    
    public void setStage(Stage poptionsstage) {
        this.optionsstage = poptionsstage;
    }
    
    public void setWorkflow(VFlow pworkflow) {
        this.workflow = pworkflow;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Getter">
    public LayoutGeneratorNaive getGenerator() {
        return this.generator;
    }
    
    public Stage getStage() {
        return this.optionsstage;
    }
    
    public VFlow getWorkflow() {
        return this.workflow;
    }
    // </editor-fold>

    @FXML
    void onOkPress(ActionEvent event) {
        accept();
        this.optionsstage.close();
    }

    @FXML
    void onCancelPress(ActionEvent event) {
        set();
        this.optionsstage.close();
    }

    @FXML
    void onShowPress(ActionEvent event) {
        int i;
        accept();
        switch(this.generator.getGraphmode()) {
            case 0:
                this.generator.setWorkflow(this.workflow.getModel());
                this.generator.generateLayout();
                break;
            case 2:
                ObservableList<VNode> obsnodes = workflow.getNodes();
                LinkedList<VNode> nodelist = new LinkedList<>();
                for(i = 0; i < obsnodes.size(); i++) {
                    VNode curr = obsnodes.get(i);
                    if(curr.isSelected()) {
                        nodelist.add(curr);
                    }
                }
                if(!nodelist.isEmpty()) {
                    this.generator.setNodelist(nodelist);
                    this.generator.generateLayout();
                }
                break;
        }
    }
    
    /**
     * Sets the values of all menu items to the current values of the layout 
     * generator.
     */
    public void set() {
        this.checkAutoscaleNodes.setSelected(this.generator
                .getAutoscaleNodes());
        this.checkCalcHorPos.setSelected(this.generator
                .getLaunchCalculateHorizontalPositions());
        this.checkCalcVertPos.setSelected(this.generator
                .getLaunchCalculateVerticalPositions());
        this.checkCreateLayering.setSelected(this.generator
                .getLaunchCreateLayering());
        this.checkRecursive.setSelected(this.generator.getRecursive());
        this.checkRemoveCycles.setSelected(this.generator
                .getLaunchRemoveCycles());
        this.selGraphmode.setValue(this.graphmodes[this.generator
                .getGraphmode()]);
        this.tfScaling.setText(Double.toString(this.generator.getScaling()));
        this.tfSubflowscale.setText(Double.toString(this.generator
                .getSubflowscale()));
    }
    
    /**
     * Sets the values of all parameters of the layout generator to the current 
     * values of the menu items.
     */
    private void accept() {
        int i;
        double j;
        String temp;
        this.generator.setAutoscaleNodes(this.checkAutoscaleNodes.isSelected());
        this.generator.setLaunchCalculateHorizontalPositions(
                this.checkCalcHorPos.isSelected());
        this.generator.setLaunchCalculateVerticalPositions(
                this.checkCalcVertPos.isSelected());
        this.generator.setLaunchCreateLayering(this.checkCreateLayering
                .isSelected());
        this.generator.setRecursive(this.checkRecursive.isSelected());
        this.generator.setLaunchRemoveCycles(this.checkRemoveCycles
                .isSelected());
        temp = this.selGraphmode.getValue();
        for(i = 0; i < this.graphmodes.length; i++) {
            if(this.graphmodes[i].equals(temp)) {
                if(i != 1) this.generator.setGraphmode(i);
            }
        }
        j = this.generator.getScaling();
        temp = this.tfScaling.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setScaling(j);
        j = this.generator.getSubflowscale();
        temp = this.tfSubflowscale.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setSubflowscale(j);
    }
}
