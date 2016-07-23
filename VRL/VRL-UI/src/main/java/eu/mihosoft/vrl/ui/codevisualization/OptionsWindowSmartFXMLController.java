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

import eu.mihosoft.vrl.workflow.incubating.LayoutGeneratorSmart;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller Class
 * 
 * 
 * @author Tobias Mertz
 */
public class OptionsWindowSmartFXMLController implements Initializable {

    @FXML
    public Pane contentPane;
    
    private LayoutGeneratorSmart generator;
    private final String[] layouts = new String[4];
    private final String[] graphmodes = new String[3];

    /**
     * Initializes the controller class.
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.layouts[0] = "ISOM Layout";
        this.layouts[1] = "FR Layout";
        this.layouts[2] = "KK Layout";
        this.layouts[3] = "DAG Layout";
        this.graphmodes[0] = "VFlowModel";
        this.graphmodes[1] = "jgraph";
        this.graphmodes[2] = "nodelist";
        ObservableList<String> layoutlist = this.selLayout.getItems();
        layoutlist.add(this.layouts[0]);
        layoutlist.add(this.layouts[1]);
        layoutlist.add(this.layouts[2]);
        layoutlist.add(this.layouts[3]);
        ObservableList<String> graphmodelist = this.selGraphmode.getItems();
        graphmodelist.add(this.graphmodes[0]);
        graphmodelist.add(this.graphmodes[1]);
        graphmodelist.add(this.graphmodes[2]);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Menu items">
    @FXML
    private CheckBox checkForcePush;
    
    @FXML
    private CheckBox checkSeparateDisjunctGraphs;
    
    @FXML
    private CheckBox checkSeparateEdgeTypes;
    
    @FXML
    private CheckBox checkDisplaceIdents;
    
    @FXML
    private TextField tfMaxiterations;
    
    @FXML
    private CheckBox checkRecursive;
    
    @FXML
    private TextField tfAspectratio;
    
    @FXML
    private TextField tfScaling;
    
    @FXML
    private ComboBox<String> selLayout;
    
    @FXML
    private CheckBox checkAlignNodes;
    
    @FXML
    private CheckBox checkRemoveCycles;
    
    @FXML
    private CheckBox checkOrigin;
    
    @FXML
    private TextField tfSubflowscale;
    
    @FXML
    private CheckBox checkRotate;
    
    @FXML
    private CheckBox checkPushBack;
    
    @FXML
    private CheckBox checkAutoscaleNodes;
    
    @FXML
    private CheckBox checkJungLayout;
    
    @FXML
    private ComboBox<String> selGraphmode;
    
    @FXML
    private TextField tfDirection;
    
    @FXML
    private TextField tfAlignmentThreshold;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setter">
    public void setGenerator(LayoutGeneratorSmart pgenerator) {
        this.generator = pgenerator;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Getter">
    public LayoutGeneratorSmart getGenerator() {
        return this.generator;
    }
    // </editor-fold>
    
    /**
     * Sets the values of all menu items to the current values of the layout 
     * generator.
     */
    public void set() {
        this.checkForcePush.setSelected(this.generator.getLaunchForcePush());
        this.checkSeparateDisjunctGraphs.setSelected(this.generator
                .getLaunchSeparateDisjunctGraphs());
        this.checkSeparateEdgeTypes.setSelected(this.generator
                .getLaunchSeparateEdgeTypes());
        this.checkDisplaceIdents.setSelected(this.generator
                .getLaunchDisplaceIdents());
        this.tfMaxiterations.setText(Integer.toString(this.generator
                .getMaxiterations()));
        this.checkRecursive.setSelected(this.generator.getRecursive());
        this.tfAspectratio.setText(Double.toString(this.generator
                .getAspectratio()));
        this.tfScaling.setText(Double.toString(this.generator.getScaling()));
        this.selLayout.setValue(this.layouts[this.generator
                .getLayoutSelector()]);
        this.checkAlignNodes.setSelected(this.generator.getLaunchAlignNodes());
        this.checkRemoveCycles.setSelected(this.generator
                .getLaunchRemoveCycles());
        this.checkOrigin.setSelected(this.generator.getLaunchOrigin());
        this.tfSubflowscale.setText(Double.toString(this.generator
                .getSubflowscale()));
        this.checkRotate.setSelected(this.generator.getLaunchRotate());
        this.checkPushBack.setSelected(this.generator.getLaunchPushBack());
        this.checkAutoscaleNodes.setSelected(this.generator
                .getAutoscaleNodes());
        this.checkJungLayout.setSelected(this.generator.getLaunchJungLayout());
        this.selGraphmode.setValue(this.graphmodes[this.generator
                .getGraphmode()]);
        this.tfDirection.setText(Double.toString(this.generator
                .getDirection()));
        this.tfAlignmentThreshold.setText(Double.toString(this.generator
                .getAlignmentThreshold()));
    }

    /**
     * Sets the values of all parameters of the layout generator to the current 
     * values of the menu items.
     */
    public void accept() {
        int i;
        double j;
        String temp;
        this.generator.setLaunchForcePush(this.checkForcePush.isSelected());
        this.generator.setLaunchSeparateDisjunctGraphs(this
                .checkSeparateDisjunctGraphs.isSelected());
        this.generator.setLaunchSeparateEdgeTypes(this.checkSeparateEdgeTypes
                .isSelected());
        this.generator.setLaunchDisplaceIdents(this.checkDisplaceIdents
                .isSelected());
        i = this.generator.getMaxiterations();
        temp = this.tfMaxiterations.getText();
        try {
            i = Integer.parseInt(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setMaxiterations(i);
        this.generator.setRecursive(this.checkRecursive.isSelected());
        j = this.generator.getAspectratio();
        temp = this.tfAspectratio.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setAspectratio(j);
        j = this.generator.getScaling();
        temp = this.tfScaling.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setScaling(j);
        temp = this.selLayout.getValue();
        for(i = 0; i < this.layouts.length; i++) {
            if(this.layouts[i].equals(temp)) {
                this.generator.setLayoutSelector(i);
            }
        }
        this.generator.setLaunchAlignNodes(this.checkAlignNodes.isSelected());
        this.generator.setLaunchRemoveCycles(this.checkRemoveCycles
                .isSelected());
        this.generator.setLaunchOrigin(this.checkOrigin.isSelected());
        j = this.generator.getSubflowscale();
        temp = this.tfSubflowscale.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setSubflowscale(j);
        this.generator.setLaunchRotate(this.checkRotate.isSelected());
        this.generator.setLaunchPushBack(this.checkPushBack.isSelected());
        this.generator.setAutoscaleNodes(this.checkAutoscaleNodes.isSelected());
        this.generator.setLaunchJungLayout(this.checkJungLayout.isSelected());
        temp = this.selGraphmode.getValue();
        for(i = 0; i < this.graphmodes.length; i++) {
            if(this.graphmodes[i].equals(temp)) {
                this.generator.setGraphmode(i);
            }
        }
        j = this.generator.getDirection();
        temp = this.tfDirection.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setDirection(j);
        j = this.generator.getAlignmentThreshold();
        temp = this.tfAlignmentThreshold.getText();
        try {
            j = Double.parseDouble(temp);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.generator.setAlignmentThreshold(j);
    }
}
