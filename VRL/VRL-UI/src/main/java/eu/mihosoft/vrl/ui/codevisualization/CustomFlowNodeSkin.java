/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.ui.codevisualization;

import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkinBase;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * Custom flownode skin for leaf nodes. In addition to the basic node
 * visualization from VWorkflows this skin adds custom visualization of value
 * objects.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public abstract class CustomFlowNodeSkin extends FXFlowNodeSkinBase {

    public CustomFlowNodeSkin(FXSkinFactory skinFactory,
            VNode model, VFlow controller) {
        super(skinFactory, model, controller);
    }

    protected abstract Node createView();

    @Override
    public void updateView() {

        super.updateView();
        
         // we don't create a custom view if no value has been defined
        if (getModel().getValueObject().getValue() == null) {
            return;
        }

        if (getModel() instanceof VFlowModel) {

            if (!(getModel().getValueObject().getValue() instanceof ScopeInvocation)) {
                return;
            }
            
            ScopeInvocation scopeInv = 
                    (ScopeInvocation) getModel().getValueObject().getValue();

            // we create custom views for control flow scopes (for, while etc.)
            if (scopeInv.getScope() instanceof ControlFlowScope) {
                // ?
            } else {
                // we don't create custom view for regular flows
                return;
            }
        }

        Pane contentPane = getNode().getContentPane();

        // create the view
        Node view = createView();

        // add the view to scalable content pane
        if (view != null) {

            GridPane nodePane = new GridPane();
            nodePane.setAlignment(Pos.CENTER);
            nodePane.getChildren().add(view);

            getNode().setContentPane(nodePane);
        }
    }
}
