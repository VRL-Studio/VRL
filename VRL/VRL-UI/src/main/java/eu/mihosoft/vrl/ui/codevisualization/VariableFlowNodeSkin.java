/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.ui.codevisualization;

import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.ArgumentType;
import eu.mihosoft.vrl.lang.model.CodeEvent;
import eu.mihosoft.vrl.lang.model.CodeEventType;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.FlowNodeWindow;
import eu.mihosoft.vrl.workflow.fx.VCanvas;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VariableFlowNodeSkin extends CustomFlowNodeSkin {

    public VariableFlowNodeSkin(FXSkinFactory skinFactory, VNode model, VFlow controller) {
        super(skinFactory, model, controller);
    }
    

    @Override
    protected Node createView() {
       Object value = getModel().getValueObject().getValue();

       if (value instanceof Invocation) {
           
           FlowNodeWindow w = (FlowNodeWindow) getNode();
           VCanvas canvas = (VCanvas) getContentNode().getParent();
           
           canvas.setMinScaleX(1);
           canvas.setMaxScaleX(1);
           canvas.setMinScaleY(1);
           canvas.setMaxScaleY(1);

           Invocation invocation = (Invocation) value;
           
           
           VBox inputs = new VBox();
           VBox outputs = new VBox();
           HBox hbox = new HBox(inputs,outputs);
           
           createArgView(invocation, inputs);
           
           invocation.getArguments().addListener(
                   (ListChangeListener.Change<? extends IArgument> c) -> {
               createArgView(invocation, inputs);
           });
           
           return hbox;
       }
       
       return null;
    }

    private void createArgView(Invocation invocation, VBox inputs) {
        
        inputs.getChildren().clear();
        
        int argIndex = 0;
        for(IArgument a : invocation.getArguments()) {
            final int finalArgInex = argIndex;
            if (a.getArgType()==ArgumentType.CONSTANT) {
                 TextField field = new TextField();
                 a.getConstant().ifPresent(o->field.setText(o.toString()));
                 inputs.getChildren().add(field);
                 field.textProperty().addListener((ov,oldV,newV) -> {
                     try {
                         Integer intValue = Integer.parseInt(newV);
                         invocation.getArguments().set(finalArgInex, Argument.constArg(Type.INT, intValue));
                         invocation.getParent().fireEvent(new CodeEvent(CodeEventType.CHANGE, invocation.getParent()));
                     } catch(Exception ex) {
                         //
                     }

                 });
            } else if (a.getArgType()==ArgumentType.VARIABLE) {
                 Label label = new Label();
                 a.getVariable().ifPresent(v->label.setText(v.getName()));
                 label.setTextFill(Color.WHITE);
                 inputs.getChildren().add(label);
            } else if (a.getArgType()==ArgumentType.INVOCATION) {
                 Label label = new Label();
                 label.setTextFill(Color.WHITE);
                 a.getInvocation().ifPresent(i->label.setText(i.getMethodName()));
                 inputs.getChildren().add(label);
            }
            
            argIndex++;
        }
    }
}
