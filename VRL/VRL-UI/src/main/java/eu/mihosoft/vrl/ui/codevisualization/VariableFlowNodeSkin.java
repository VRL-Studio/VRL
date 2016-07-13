/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.ui.codevisualization;

import eu.mihosoft.vrl.lang.model.ArgumentType;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeEvent;
import eu.mihosoft.vrl.lang.model.CodeEventType;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.BinaryOperatorInvocation;
import eu.mihosoft.vrl.lang.model.CodeRange;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.UIBinding;
import eu.mihosoft.vrl.v3d.jcsg.CSG;
import eu.mihosoft.vrl.v3d.jcsg.MeshContainer;
import eu.mihosoft.vrl.v3d.jcsg.VFX3DUtil;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.WorkflowUtil;
import eu.mihosoft.vrl.workflow.fx.ConnectorShape;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkinBase;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import eu.mihosoft.vrl.workflow.fx.ScaleBehavior;
import eu.mihosoft.vrl.workflow.fx.TranslateBehavior;
import eu.mihosoft.vrl.workflow.fx.VCanvas;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.text.TextAlignment;
import jfxtras.scene.control.window.Window;
import jfxtras.scene.control.window.WindowUtil;
import org.fxmisc.richtext.CodeArea;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VariableFlowNodeSkin extends CustomFlowNodeSkin {

    private boolean updating;
    private VBox outputs;
    private final ObjectProperty<Object[]> args = new SimpleObjectProperty<>();

    private static CodeArea editor;

    public static void setEditor(CodeArea editor, VFlow flow) {
        VariableFlowNodeSkin.editor = editor;

        Function<CodeEntity, Stream< ? extends Window>> mapFlatToWindow = (cE) -> {

            return flow.getNodeSkinLookup().getById(cE.getNode().getId()).
                    stream().
                    filter((ns) -> {
                        return ns instanceof FXFlowNodeSkinBase;
                    }).
                    map(fns -> ((FXFlowNodeSkinBase) fns).getNode());
        };

//        editor.caretPositionProperty().addListener((ov, oldV, newV) -> {
//            CompilationUnitDeclaration cud
//                    = (CompilationUnitDeclaration) UIBinding.scopes.values().
//                    iterator().next().get(0);
//            cud.pick(new CodeLocation(newV.intValue())).
//                    map(mapFlatToWindow).orElse(Stream.empty()).forEach(w -> {
//                WindowUtil.getDefaultClipboard().deselectAll();
//                WindowUtil.getDefaultClipboard().select(w, true);
//            });
//        });
        EventStream<Change<IndexRange>> selEvents
                = EventStreams.changesOf(editor.selectionProperty());

        selEvents.reduceSuccessions((red1, red2) -> red2,
                // update the model/code after specified time
                // multiple events/changes per specified interval 
                // will be reduced (merged to one event)
                Duration.ofMillis(25)).
                subscribe(newV -> {
                    int start = newV.getNewValue().getStart();
                    int end = newV.getNewValue().getEnd();

//            if (start == end) {
//                return;
//            }
                    WindowUtil.getDefaultClipboard().deselectAll();
//
//            System.out.println("s: " + start + ", end: " + end);

                    Predicate<CodeEntity> contains = (cE) -> {

                        try {
                            return new CodeRange(start, end).contains(
                                    cE.getRange());
                        } catch (Exception ex) {
                            System.out.println(
                                    "EX missing range: " + cE
                                    + ", " + cE.getRange()
                                    + ", ex: " + ex);
                        }

                        return false;
                    };

                    CompilationUnitDeclaration cud
                            = (CompilationUnitDeclaration) UIBinding.scopes.values().
                            iterator().next().get(0);

                    cud.collectScopeAndAllsubElements().stream().
                            filter(contains).flatMap(mapFlatToWindow).forEach(
                            (sce) -> {
                                WindowUtil.getDefaultClipboard().
                                select(sce, true);
                            });

//            WindowUtil.getDefaultClipboard().getSelectedItems().
//                    filtered(contains).forEach(
//                            (sce)->{System.out.println("test");WindowUtil.getDefaultClipboard().
//                                    select(sce, true);});
                });

//        editor.selectionProperty().addListener((ov, oldV, newV) -> {
//
//            int start = newV.getStart();
//            int end = newV.getEnd();
//
////            if (start == end) {
////                return;
////            }
//
//            WindowUtil.getDefaultClipboard().deselectAll();
////
////            System.out.println("s: " + start + ", end: " + end);
//
//            Predicate<CodeEntity> contains = (cE) -> {
//
//                try {
//                    return new CodeRange(start, end).contains(
//                            cE.getRange());
//                } catch (Exception ex) {
//                    System.out.println("EX missing range: " + cE + ", " + cE.getRange());
//                }
//
//                return false;
//            };
//
//            CompilationUnitDeclaration cud
//                    = (CompilationUnitDeclaration) UIBinding.scopes.values().
//                    iterator().next().get(0);
//
//            cud.collectScopeAndAllsubElements().stream().
//                    filter(contains).flatMap(mapFlatToWindow).forEach(
//                    (sce) -> {
//                        WindowUtil.getDefaultClipboard().
//                        select(sce, true);
//                    });
//
////            WindowUtil.getDefaultClipboard().getSelectedItems().
////                    filtered(contains).forEach(
////                            (sce)->{System.out.println("test");WindowUtil.getDefaultClipboard().
////                                    select(sce, true);});
//
//        });
    }

    public VariableFlowNodeSkin(FXSkinFactory skinFactory,
            VNode model, VFlow controller) {
        super(skinFactory, model, controller);

//        getNode().addEventFilter(MouseEvent.ANY, (evt) -> {
//            if (evt.getEventType() == MouseEvent.MOUSE_CLICKED) {
//                Object value = getModel().getValueObject().getValue();
//
//                if (value instanceof CodeEntity) {
//                    CodeEntity cE = (CodeEntity) value;
//
//                    if (cE.getRange() != null) {
//
//                        int anchor = cE.getRange().getBegin().getCharIndex();
//                        int caretPosition = cE.getRange().getEnd().getCharIndex();
////                    WindowUtil.getDefaultClipboard().deselectAll();
//                        editor.selectRange(anchor, caretPosition);
//                    }
//                }
//            };
////            else if (evt.getEventType() == MouseEvent.MOUSE_EXITED) {
////                editor.deselect();
////            }
//        });
//        getNode().selectedProperty().addListener((evt) -> {
//            
//            if (getNode().isSelected()) {
//
////                WindowUtil.getDefaultClipboard().getSelectedItems()
//                Object value = getModel().getValueObject().getValue();
//
//                if (value instanceof CodeEntity) {
//                    CodeEntity cE = (CodeEntity) value;
//
//                    int anchor = cE.getRange().getBegin().getCharIndex();
//                    int caretPosition = cE.getRange().getEnd().getCharIndex();
//
//                    editor.selectRange(anchor, caretPosition);
//                }
//
//            } else {
//                editor.deselect();
//            }
//        });
    }

    @Override
    public void configureCanvas(VCanvas canvas) {
        super.configureCanvas(canvas);

        canvas.setScaleBehavior(ScaleBehavior.ALWAYS);
        canvas.setTranslateBehavior(TranslateBehavior.ALWAYS);
    }

    private List<ConnectorShape> getConnectorShapes() {
        List<ConnectorShape> connectorShapes
                = getModel().getInputs().stream().
                filter(c -> c.getType().equals(WorkflowUtil.DATA_FLOW)).
                map(c -> getConnectorShape(c)).
                collect(Collectors.toList());

        return connectorShapes;
    }

    private boolean isInvocation() {
        return getModel().getValueObject().getValue() instanceof Invocation;
    }

    private Invocation getInvocation() {
        return (Invocation) getModel().getValueObject().getValue();
    }

    @Override
    protected Node createView() {

        ComboBox<Object> box;

        VBox parent = new VBox();

        parent.setAlignment(Pos.CENTER);
//        parent.setStyle("-fx-border-color: red;");

        Object value = getModel().getValueObject().getValue();

        if (value instanceof ScopeInvocation) {

        }

        if (!(value instanceof Invocation)) {

            return parent;
        }

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

        if (invocation instanceof BinaryOperatorInvocation) {
            BinaryOperatorInvocation opInv
                    = (BinaryOperatorInvocation) invocation;

            box = new ComboBox<>();
            box.getItems().addAll(
                    Operator.PLUS,
                    Operator.MINUS,
                    Operator.TIMES,
                    Operator.DIV);

            if (box.getItems().contains(opInv.getOperator())) {
                box.getSelectionModel().select(opInv.getOperator());
            } else {
                box.setVisible(false);
            }

            box.getSelectionModel().selectedItemProperty().
                    addListener((ov, oldV, newV) -> {
                        opInv.setOperator((Operator) newV);
                    });

        } else {
            box = new ComboBox<>();
            box.setVisible(false);
        }

        parent.getChildren().add(box);

//        box.setVisible(!invocation.getArguments().isEmpty());
        VBox inputs = new VBox();
        inputs.setAlignment(Pos.CENTER);

        outputs = new VBox();
        outputs.setAlignment(Pos.CENTER);
        Pane boxPane = new Pane();
        boxPane.setMinWidth(30);
        HBox hbox = new HBox(inputs, boxPane, outputs);
        hbox.setPadding(new Insets(0, 15, 0, 15));

        createArgView(invocation, inputs, false);

        invocation.getArguments().addListener(
                (ListChangeListener.Change<? extends Argument> c) -> {
//                    box.setVisible(!invocation.getArguments().isEmpty());
                    if (!updating) {
                        createArgView(invocation, inputs,
                                invocation.getArguments().size()
                                == inputs.getChildren().size());
                    }
                });

        parent.getChildren().add(hbox);

        if (getModel().getValueObject().getValue() instanceof CodeEntity) {
            CodeEntity ce = (CodeEntity) getModel().getValueObject().getValue();
            updateOutputView(ce.getMetaData().get("VRL:retVal"));
            Object pre = ce.getMetaData().get("VRL:pre-timestamp");
            Object post = ce.getMetaData().get("VRL:post-timestamp");
            if (pre instanceof Long && post instanceof Long) {
                updateProfileData((long) post - (long) pre);
            }

            Object argsObj = ce.getMetaData().get("VRL:args");
            if (argsObj != null) {
                args.set((Object[]) argsObj);
            }

            createArgView(invocation, inputs, true);

            ce.getMetaData().addListener((Observable observable) -> {
                updateOutputView(ce.getMetaData().get("VRL:retVal"));
//                Object pre1 = ce.getMetaData().get("VRL:pre-timestamp");
//                Object post1 = ce.getMetaData().get("VRL:post-timestamp");
//                if (pre1 instanceof Long && post1 instanceof Long) {
//
//                    long duration = Math.abs((long) post1 - (long) pre1);
//
//                    ce.getMetaData().put("VRL:duration", duration);
//
//                    updateProfileData(duration);
//                }

                Object argsObj1 = ce.getMetaData().get("VRL:args");
                if (argsObj1 != null) {
                    args.set((Object[]) argsObj1);
                }

                createArgView(invocation, inputs, true);

            });
        }

        getModel().getValueObject().valueProperty().addListener(
                (ov, oldV, newV) -> {
                    if (newV instanceof CodeEntity) {
                        CodeEntity ce = (CodeEntity) newV;
                        ce.getMetaData().addListener((Observable observable) -> {

                            Object pre = ce.getMetaData().get("VRL:pre-timestamp");
                            Object post = ce.getMetaData().get("VRL:post-timestamp");
                            if (pre instanceof Long && post instanceof Long) {
                                updateProfileData((long) post - (long) pre);
                            }

                            Object argsObj1 = ce.getMetaData().get("VRL:args");
                            if (argsObj1 != null) {
                                args.set((Object[]) argsObj1);
                            }

                            createArgView(invocation, inputs, true);

                            updateOutputView(ce.getMetaData().get("VRL:retVal"));

                        });
                    }
                });

        return parent;
    }

    private void setFieldListener(int argIndex, TextField field,
            Invocation invocation, Argument a) {

        EventStream<Change<String>> textEvents
                = EventStreams.changesOf(field.textProperty());

        textEvents.reduceSuccessions((red1, red2) -> red2,
                // update the model/code after specified time
                // multiple events/changes per specified interval 
                // will be reduced (merged to one event)
                Duration.ofMillis(1000)).
                subscribe(newV -> {
                    // only fire code change events for fields that are changed
                    // by the user
                    if (!field.isFocused()) {
                        return;
                    }
                    if (a.getArgType() == ArgumentType.CONSTANT) {

                        if (a.getType().equals(Type.INT)) {
                            parseInt(newV, invocation, argIndex);
                        } else if (a.getType().equals(Type.DOUBLE)
                                || a.getType().equals(Type.fromClass(BigDecimal.class))) {
                            parseDouble(newV, invocation, argIndex);
                        } else if (a.getType().equals(Type.STRING)) {
                            parseString(newV, invocation, argIndex);
                        }
                        System.out.println("param-type: " + a.getType());
                    } else if (a.getArgType() == ArgumentType.NULL) {
                        parseInt(newV, invocation, argIndex);
                    }

                });
    }

    private void parseInt(Change<String> newV, Invocation invocation, int argIndex) {
        try {
            Integer intValue = Integer.parseInt(newV.getNewValue());
            invocation.getArguments().set(argIndex,
                    Argument.constArg(Type.INT, intValue));
            invocation.getParent().fireEvent(new CodeEvent(
                    CodeEventType.CHANGE,
                    invocation.getParent()));

            if (invocation instanceof ScopeInvocation) {
                ScopeInvocation scopeInv
                        = (ScopeInvocation) invocation;
                Scope scope = scopeInv.getScope();
            }
        } catch (Exception ex) {

        }
    }

    private void parseDouble(Change<String> newV, Invocation invocation, int argIndex) {
        try {
            Double doubleValue = Double.parseDouble(newV.getNewValue());
            invocation.getArguments().set(argIndex,
                    Argument.constArg(Type.DOUBLE, doubleValue));
            invocation.getParent().fireEvent(new CodeEvent(
                    CodeEventType.CHANGE,
                    invocation.getParent()));

            if (invocation instanceof ScopeInvocation) {
                ScopeInvocation scopeInv
                        = (ScopeInvocation) invocation;
                Scope scope = scopeInv.getScope();
            }
        } catch (Exception ex) {

        }
    }

    private void parseString(Change<String> newV, Invocation invocation, int argIndex) {
        try {

            invocation.getArguments().set(argIndex,
                    Argument.constArg(Type.STRING, newV.getNewValue()));
            invocation.getParent().fireEvent(new CodeEvent(
                    CodeEventType.CHANGE,
                    invocation.getParent()));

            if (invocation instanceof ScopeInvocation) {
                ScopeInvocation scopeInv
                        = (ScopeInvocation) invocation;
                Scope scope = scopeInv.getScope();
            }
        } catch (Exception ex) {

        }
    }

    private void createArgView(
            Invocation invocation, VBox inputs, boolean update) {

        updating = true;

        if (!update) {
            inputs.getChildren().clear();
        }

        int argIndex = 0;
        for (Argument a : invocation.getArguments()) {
            if (a.getArgType() == ArgumentType.CONSTANT
                    || a.getArgType() == ArgumentType.NULL) {
                TextField field;

                if (update && inputs.getChildren().get(argIndex) instanceof TextField) {
                    field = (TextField) inputs.getChildren().get(argIndex);

                } else {
                    field = new TextField();
                    field.getStyleClass().add("vtextfield");

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
                label.setMinHeight(15);
                label.setTextFill(Color.LIGHTBLUE);
                if (update) {
                    inputs.getChildren().set(argIndex, label);
                } else {
                    inputs.getChildren().add(label);
                }

                if (args.get() != null) {
                    Object[] currentArgs = args.get();
                    if (argIndex < currentArgs.length) {
                        Object arg = currentArgs[argIndex];
                        if (arg != null) {
                            label.setText(arg.toString());
                            label.setTextFill(Color.LIGHTBLUE);
                        }
                    }
                }

            } else if (a.getArgType() == ArgumentType.INVOCATION) {
                Label label = new Label();
                label.setTextFill(Color.LIGHTBLUE);
                label.setMinHeight(15);
                a.getInvocation().ifPresent(i -> label.setText(i.getMethodName()));
                if (update) {
                    inputs.getChildren().set(argIndex, label);
                } else {
                    inputs.getChildren().add(label);
                }

                if (args.get() != null) {
                    Object[] currentArgs = args.get();
                    if (argIndex < currentArgs.length) {
                        Object arg = currentArgs[argIndex];
                        if (arg != null) {
                            label.setText(arg.toString());
                            label.setTextFill(Color.LIGHTBLUE);
                        }
                    }
                }
            }

            Node argNode = inputs.getChildren().get(argIndex);

            List<ConnectorShape> connectorShapes = getConnectorShapes();

            if (connectorShapes.size() <= argIndex || argNode == null) {
                continue;
            }

            final ConnectorShape connectorShape = connectorShapes.get(argIndex);

            updateConnectorPos(connectorShape, argNode);

            connectorShape.getNode().layoutYProperty().addListener(observable -> {
                if (argNode.getParent() == null
                        || argNode.getParent().getScene() == null) {
                    return;
                }
                if (connectorShape.getNode().getParent() == null
                        || connectorShape.getNode().getParent().getScene() == null) {
                    return;
                }
                updateConnectorPos(connectorShape, argNode);
            });

            getNode().boundsInLocalProperty().addListener(observable -> {
                if (argNode.getParent() == null
                        || argNode.getParent().getScene() == null) {
                    return;
                }
                if (connectorShape.getNode().getParent() == null
                        || connectorShape.getNode().getParent().getScene() == null) {
                    return;
                }
                updateConnectorPos(connectorShape, argNode);
            });

            argIndex++;
        } // end for

        updating = false;
    }

    private void updateConnectorPos(ConnectorShape connectorShape, Node argNode) {
        
        Node connectorNode = connectorShape.getNode();

        Point2D global1 = argNode.getParent().localToScene(
                argNode.getLayoutX(),
                argNode.getLayoutY()
        );

        Point2D global2 = connectorNode.getParent().localToScene(
                connectorNode.getLayoutX(),
                connectorNode.getLayoutY()
        );
        double diffX = global2.getX() - global1.getX()
                - argNode.getLayoutBounds().getWidth()*0.5 
                + connectorShape.getRadius();
        double diffY = global2.getY() - global1.getY()
                - argNode.getLayoutBounds().getHeight()*0.5
                + connectorShape.getRadius();
        
        Point2D diff = new Point2D(diffX, diffY);

//                connectorNode.setTranslateX(+30);
        connectorNode.setTranslateY(-diff.getY());
    }

    private void setMeshScale(MeshContainer meshContainer,
            Bounds t1, final MeshView meshView) {
        double maxDim
                = Math.max(meshContainer.getWidth(),
                        Math.max(meshContainer.getHeight(),
                                meshContainer.getDepth()));

        double minContDim = Math.min(t1.getWidth(), t1.getHeight());

        double scale = minContDim / (maxDim * 2);

        meshView.setScaleX(scale);
        meshView.setScaleY(scale);
        meshView.setScaleZ(scale);
    }

    void updateOutputView(Object retVal) {
        if (outputs == null) {
            return;
        }

        outputs.getChildren().clear();

        CSG csg;

        if (retVal instanceof CSG) {
            csg = (CSG) retVal;

            Group viewGroup = new Group();

            SubScene subScene = new SubScene(viewGroup, 300, 200, true,
                    SceneAntialiasing.DISABLED);
//            subScene.setFill(Color.WHEAT);
            PerspectiveCamera subSceneCamera = new PerspectiveCamera(false);
            subScene.setCamera(subSceneCamera);

            MeshContainer mc = csg.toJavaFXMesh();

            MeshView mv = mc.getAsMeshViews().get(0);
//            mv.setScaleX(10);
//            mv.setScaleY(10);
//            mv.setScaleZ(10);

//Color diffuse = new Color(0.1, 0.99, 0.99, 0.5);
            Color diffuse = new Color(0.99, 0.52, 0.0, 0.7);
            Color spec = new Color(1.0, 1.0, 1.0, 1.0);
            Color ambient = new Color(0.2, 0.15, 0.05, 1.0);

            PhongMaterial mat = new PhongMaterial(diffuse);

            mat.setSpecularColor(spec);

            mv.setMaterial(mat);

            setMeshScale(mc, subScene.getBoundsInLocal(), mv);

            VFX3DUtil.addMouseBehavior(mv, subScene, MouseButton.PRIMARY);

            viewGroup.layoutXProperty().bind(
                    subScene.widthProperty().divide(2));
            viewGroup.layoutYProperty().bind(
                    subScene.heightProperty().divide(2));

            viewGroup.getChildren().addAll(mv);
//            viewGroup.getChildren().add(new AmbientLight(ambient));

            outputs.getChildren().addAll(subScene);
        } else if (retVal instanceof BufferedImage) {
            WritableImage image = SwingFXUtils.toFXImage(
                    (BufferedImage) retVal, null);
            ImageView view = new ImageView(image);
            view.setPreserveRatio(true);
            view.setFitWidth(300);

            outputs.getChildren().add(view);
        } else if (retVal != null) {
            Label l = new Label(retVal.toString());
            l.setTextFill(Color.LIGHTBLUE);
            outputs.getChildren().add(l);
        } else {
            Label l = new Label();
            l.setTextAlignment(TextAlignment.CENTER);
            if (isInvocation()) {
                Invocation invocation = getInvocation();
                if (!invocation.isVoid()
                        && !(invocation instanceof DeclarationInvocation)) {
                    l.setText(" ");
                }
            }
            l.setTextFill(Color.LIGHTBLUE);
            outputs.getChildren().add(l);
        }

    }

    private void updateProfileData(long duration) {

//        double alpha = Math.min(1, Math.abs(duration * 1e-8));
//
//        getNode().setStyle("-fx-background-color: rgb(255,0,0," + alpha + ");");
    }
}
