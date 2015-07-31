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
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.ScaleBehavior;
import eu.mihosoft.vrl.workflow.fx.TranslateBehavior;
import eu.mihosoft.vrl.workflow.fx.VCanvas;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VariableFlowNodeSkin extends CustomFlowNodeSkin {

    private boolean updating;

    public VariableFlowNodeSkin(FXSkinFactory skinFactory, VNode model, VFlow controller) {
        super(skinFactory, model, controller);
    }

    @Override
    public void configureCanvas(VCanvas canvas) {
        super.configureCanvas(canvas);

        canvas.setScaleBehavior(ScaleBehavior.ALWAYS);
        canvas.setTranslateBehavior(TranslateBehavior.ALWAYS);
    }

    @Override
    protected Node createView() {
        
        ComboBox<MethodDeclaration> box = new ComboBox<>();
        
        VBox parent = new VBox(box);
        
        parent.setAlignment(Pos.CENTER);

        Object value = getModel().getValueObject().getValue();

        if (value instanceof ScopeInvocation) {

        }

        if (value instanceof Invocation) {

//            if (value instanceof ScopeInvocation) {
//                ScopeInvocation inv = (ScopeInvocation) value;
//                Scope s = inv.getScope();
//                
//                if (s instanceof ForDeclaration) {
//                    System.out.println("SCOPE: " + s);
//                    System.exit(1);
//                }
//            }
            Invocation invocation = (Invocation) value;

            VBox inputs = new VBox();
            VBox outputs = new VBox();
            HBox hbox = new HBox(inputs, outputs);
            hbox.setPadding(new Insets(0, 15, 0, 15));

            createArgView(invocation, inputs, false);

            invocation.getArguments().addListener(
                    (ListChangeListener.Change<? extends IArgument> c) -> {
                        if (!updating) {
                            createArgView(invocation, inputs,
                                    invocation.getArguments().size()
                                    == inputs.getChildren().size());
                        }
                    });
            parent.getChildren().add(hbox);
        }

        return parent;
    }

    private void setFieldListener(int argIndex, TextField field, Invocation invocation, IArgument a) {
        field.textProperty().addListener((ov, oldV, newV) -> {
            try {
                Integer intValue = Integer.parseInt(newV);

                invocation.getArguments().set(argIndex,
                        Argument.constArg(Type.INT, intValue));
                invocation.getParent().fireEvent(new CodeEvent(
                        CodeEventType.CHANGE, invocation.getParent()));

                if (invocation instanceof ScopeInvocation) {
                    ScopeInvocation scopeInv = (ScopeInvocation) invocation;
                    Scope scope = scopeInv.getScope();

                }
            } catch (Exception ex) {

            }
        });
//        EventStream<Change<String>> textEvents
//                = EventStreams.changesOf(field.textProperty());
//
//        textEvents.reduceSuccessions((red1, red2) -> red2, Duration.ofMillis(1000)).
//                subscribe(text -> {
//                    try {
//                        Integer intValue = Integer.parseInt(text.getNewValue());
//
//                        invocation.getArguments().set(argIndex,
//                                Argument.constArg(Type.INT, intValue));
//                        invocation.getParent().fireEvent(new CodeEvent(
//                                        CodeEventType.CHANGE, invocation.getParent()));
//
//                    } catch (Exception ex) {
//                        //
//                    }
//                });
    }

    private void createArgView(Invocation invocation, VBox inputs, boolean update) {

        updating = true;

        if (!update) {
            inputs.getChildren().clear();
        }

        int argIndex = 0;
        for (IArgument a : invocation.getArguments()) {
            if (a.getArgType() == ArgumentType.CONSTANT || a.getArgType() == ArgumentType.NULL) {
                TextField field;

                if (update && inputs.getChildren().get(argIndex) instanceof TextField) {
                    field = (TextField) inputs.getChildren().get(argIndex);

                } else {
                    field = new TextField();

                    setFieldListener(argIndex, field, invocation, a);

                    if (update) {
                        inputs.getChildren().set(argIndex, field);

                    } else {
                        inputs.getChildren().add(field);
                    }
                }

                a.getConstant().ifPresent(o -> {

                    if (!field.getText().equals(o.toString())) {
                        field.setText(o.toString());
                    }
                });

            } else if (a.getArgType() == ArgumentType.VARIABLE) {
                Label label = new Label();
                a.getVariable().ifPresent(v -> label.setText(v.getName()));
                label.setTextFill(Color.WHITE);
                if (update) {
                    inputs.getChildren().set(argIndex, label);
                } else {
                    inputs.getChildren().add(label);
                }
            } else if (a.getArgType() == ArgumentType.INVOCATION) {
                Label label = new Label();
                label.setTextFill(Color.WHITE);
                a.getInvocation().ifPresent(i -> label.setText(i.getMethodName()));
                if (update) {
                    inputs.getChildren().set(argIndex, label);
                } else {
                    inputs.getChildren().add(label);
                }
            }

            argIndex++;
        } // end for

        updating = false;
    }
}
