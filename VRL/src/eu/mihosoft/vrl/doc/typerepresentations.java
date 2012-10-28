/* 
 * typerepresentations.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2012 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2012 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
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
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.vrl.doc;


/**
 * \page tr_page Type Representation Tutorial
 * \author Michael Hoffer (info@michaelhoffer.de)
 * \date 2009
 * 
 * \section tr_intro_sec Introduction
 *
 * <p>
 * The GUI generation in VRL has three logical levels. The first is the object
 * level. Objects are visualized by object representations
 * (see eu.mihosoft.vrl.reflection.ObjectRepresentation). The second is the
 * method level. Methods are visualized by method prepresentations
 * (see eu.mihosoft.vrl.reflection.MethodRepresentation).
 * The third level is the parameter level. Method parameters and return values
 * are visualized by type representations
 * (see eu.mihosoft.vrl.reflection.TypeRepresentation).
 * </p>
 *
 * \section tr_what_sec What are Type Representations?
 *
 * <p>
 * From a user point of view the third level is the most important one. In Java
 * parameters are usually also objects (except from primitive data types). But
 * VRL visualizes them with object representations because in the context of
 * method parameters these objects are treated like a variable of specific data
 * type.
 * </p>
 * <p>
 * Type representations are the most individual part of the VRL GUI generation.
 * Method representations and Object representations are mainly containers
 * and their visual appearance can't be customized. Type representations however
 * ara individual representations of parameters and return values.
 * </p>
 * <p>
 * One has to distinguish between input and output representations. In many
 * cases it is necessary to provide different interfaces for input and output.
 * Type representations can support both, input and output or only one
 * visualization type.
 * </p>
 *
 * <p>
 * In some situations it is also necessary to provide additional type
 * representations for special cases, e.g., it should be possible to show an
 * <code>Integer</code> value as edit field, where the user can
 * directly input the number. In other cases it is better to show a slider,
 * e.g., if the method takes color values which usually range from 0 to 255.
 * Therefore VRL supports different interface styles.
 * </p>
 *
 * \section tr_write_sec Writing Type Representations
 * <p>
 * As a first approach we will write a custom input type representation for
 * integer values. Usually VRL displays integer values with an edit field. Our
 * custom view will use a radio button instead. With this type representation
 * it will only be possible to input the numbers 1 an 0. This may be usefull in
 * special cases. But in most of them it is better to use boolean values
 * instead.
 * </p>
 * <p>
 * As mentioned earlier type representations are Swing components. That is,
 * writing type representations requires some basic understanding of the Swing
 * API.
 * </p>
 *
 * \subsection tr_code_sec_1 Basic Implementation
 * <p>
 * Our type representation is derived from
 * eu.mihosoft.vrl.reflection.TypeRepresentationBase:
 * \code
 * public class CustomIntegerType extends TypeRepresentationBase {
 * \endcode
 * </p>
 *
 *<p>
 * In the next step we have to add a radio button to our class and initialize
 * it:
 * \code
 * private JRadioButton radioBtn = new JRadioButton();
 * \endcode
 * </p>
 *
 * <p>
 * In the constructor we first have to specify which type we want to
 * visualize, i.e., set the class object of the objects we want to visualize
 * and the default name of the type representation:
 * \code
 * public CustomIntegerType() {
 *   setType(Integer.class);
 *   setValueName("Integer:");
 * \endcode
 * </p>
 *
 * <p>
 * A type representation can provide an interface for data input, output and
 * both. In our case we only provide an interface for data input:
 * \code
 * addSupportedRepresentationType(RepresentationType.INPUT);
 * \endcode
 * </p>
 *
 * <p>
 * Each style has its own identifier. We call our style <code>example#1</code>:
 * \code
 * setSupportedStyle("example#1");
 * \endcode
 * </p>
 *
 * <p>
 * Of course, we also have to specify a layout manager. It is recommended to use
 * the box layout manager. It is very flexible and still easy to use:
 *\code
 * VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
 * setLayout(layout);
 * \endcode
 * </p>
 *
 * <p>
 * If your Java installation uses the standard metal look&feel it is usefull
 * to make the radio button transparent:
 * \code
 * radioBtn.setBackground(new Color(0,0,0,0));
 * \endcode
 * </p>
 * 
 * <p>
 * The last step in the constructor is to add the name label and the radio
 * button to the type representation. The name label is automatically provided
 * by the parent class:
 * \code
 * this.add(nameLabel);
 * this.add(radioBtn);
 * \endcode
 * </p>
 *
 * <p>
 * Except from the constructor we have to implement three methods. In the first
 * one we specify how incoming data is displayed in the interface. We define
 * that integer values <code> in<=0</code> will be represented by an unselected
 * radio button. Values <code>in>0</code> will be represented by a selected
 * radio button. The code is relatively simple:
 * \code
 * @Override
 * public void setViewValue(Object o) {
 *     Integer in = (Integer) o;
 * 
 *     if (in <= 0) {
 *        radioBtn.setSelected(false);
 *     } else if (in > 0) {
 *        radioBtn.setSelected(true);
 *     }
 * }
 * \endcode
 * </p>
 *
 * <p>
 * In the second method we define how outgoing data is to be retrieved from the
 * interface. If the radio button is selected we define this state as
 * <code>1</code>. Thus, the mehtod has to return <code>1</code>. If the radio
 * button is unselected the method returns <code>0</code>. The source code looks
 * like this:
 * \code
 * @Override
 * public Object getViewValue() {
 *     Integer out = 0;
 *
 *     if (radioBtn.isSelected()) {
 *         out = 1;
 *     }
 *     return out;
 * }
 * \endcode
 * </p>
 *
 * <p>
 * There is only one method left. This method defines how our type 
 * representation is to be copied. In our case this only implies instantiation
 * of a new object:
 * \code
 * @Override
 * public TypeRepresentationBase copy() {
 *     return new CustomIntegerType();
 * }
 * \endcode
 * </p>
 *
 * <p>
 * If we put everything together we get the following code:
 * \code
 * import eu.mihosoft.vrl.reflection.RepresentationType;
 * import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
 * import java.awt.Color;
 * 
 * import javax.swing.JRadioButton;
 * import java.awt.event.ActionEvent;
 * import java.awt.event.ActionListener;
 * 
 * public class CustomIntegerType extends TypeRepresentationBase {
 *
 *   private JRadioButton radioBtn = new JRadioButton();
 *
 *   public CustomIntegerType() {
 *
 *       // Defines which objects can be displayed with this type representation.
 *       setType(Integer.class);
 *       // Defines the value name. This will set the caption of the name label.
 *       setValueName("Integer:");
 *
 *       // Defines that this type representation can display a GUI for
 *       // input parameters.
 *       addSupportedRepresentationType(RepresentationType.INPUT);
 *
 *       // Defines the style that is implemented by this type representation.
 *       setSupportedStyle("example#1");
 *
 *       // Sets the layout.
 *       VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
 *       setLayout(layout);
 *
 *       // Makes the background of radio button transparent.
 *       radioBtn.setBackground(new Color(0,0,0,0));
 *
 *       // Adds components to the type representation.
 *       this.add(nameLabel);
 *       this.add(radioBtn);
 *   }
 *
 *   @Override
 *   public void setViewValue(Object o) {
 *       Integer in = (Integer) o;
 *
 *       if (in <= 0) {
 *          radioBtn.setSelected(false);
 *       } else if (in > 0) {
 *          radioBtn.setSelected(true);
 *       }
 *   }
 *
 *   @Override
 *   public Object getViewValue() {
 *       Integer out = 0;
 *
 *       if (radioBtn.isSelected()) {
 *           out = 1;
 *       }
 *       return out;
 *   }
 *
 *   @Override
 *   public TypeRepresentationBase copy() {
 *       return new CustomIntegerType();
 *   }
 * }
 * \endcode
 * </p>
 *
 * \subsection tr_use_sec Using the Type Representation
 * <p>
 * To use the type representation we have to add an instance of it to the type
 * representation factory of the main canvas object. Therefore we reuse the
 * first VRL project example (see \ref use_sec) and add the new type
 * representation class to it. After that we add the following line of code to
 * the constructor of the <code>MainFrame</code> class:
 * \code
 * visualCanvas1.getTypeFactory().addType(new CustomIntegerType());
 * \endcode
 * </p>
 *
 * <p>
 * Also the <code>SimpleClass</code> has to be slightly changed. To use our
 * custom type representation we add parameter annotations to the
 * <code>add()</code> method:
 * \code
 * import eu.mihosoft.vrl.reflection.ParamInfo;
 * 
 * class SimpleClass {
 * 	public Integer add(
 *          @ParamInfo(name="Value A", style="example#1") Integer a,
 *          @ParamInfo(name="Value B", style="example#1") Integer b) {
 * 		return a + b;
 * 	}
 * }
 * \endcode
 * For further information on customizing the GUI generation via annotations
 * please see \ref annotation_page.
 * </p>
 *
 * <p>
 * If we drag a <code>SimpleClass</code> object to the canvas this should look
 * like this:
 * @image html custom_type_sample01.png
 * @image latex custom_type_sample01.eps "Customized Parameters" width=4cm
 * </p>
 *
 * \subsection tr_data_sec Data Consistency
 * <p>
 * Although the type representation works now it is missing an essential
 * feature. It is not able to recognize whether the data it represents is up to
 * date. This is a problem if several methods are connected. Connections between
 * type repreentations define data dependencies. That is, methods depend on the
 * return data of connected methods. Calling a method in
 * VRL implies a data consistency check. Before calling the method VRL computes
 * the data dependencies, i.e., recursively checks whether the return type
 * representations are up to date. The result of a method is defined as up to
 * date if the data of the input type representations has not been changed after
 * the last method call and if all method dependencies are up to date.
 * If methods are up to date they won't be automatically called by VRL.
 * </p>
 * <p>
 * Adding the following code to the constructor adds data consistency
 * functionality to our type representation:
 * </p>
 * 
 * \code
 * radioBtn.addActionListener(new ActionListener(){
 *     public void actionPerformed(ActionEvent e) {
 *         setDataOutdated();
 *     }
 * });
 * \endcode
 * 
 * <p>
 * The code is easy to understand. Whenever the user clicks the radio button
 * the data is defined as outdated. This state is forwarded to all type
 * representations that are affected.
 * </p>
 */
