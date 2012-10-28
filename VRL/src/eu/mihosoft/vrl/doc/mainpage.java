/* 
 * mainpage.java
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

/** \mainpage VRL: Visual Reflection Library
 * \author Michael Hoffer (info@michaelhoffer.de)
 * \date 2009
 *
 * \section intro_sec Introduction
 * <p>
 * VRL is a flexible Swing based library for automatic and interactive object
 * visualization.
 *
 * The project was started as a job for student assistants at
 * <a href="http://www.iwr.uni-heidelberg.de/">IWR, University of Heidelberg</a>
 * (supervision: A. Heusel).<br>
 * </p>
 * \subsection why_sec Why VRL?
 * <p>
 * The <b>Simulation in Technology Center of Prof. Wittum</b> needs a plattform
 * independent client for their simulation system
 * <a href="http://129.206.120.227/~ug/">UG</a> that provides an interactive
 * graphical user interface as extension to the already existing shell
 * environment.
 * </p>
 * <p>
 * In addition to that VRL is a visual development environment for the Java
 * platform.
 * </p>
 * <p>
 * The aims of VRL are:
 * <ul>
 * 	<li> flexibility and plattform independence
 * 	<li> automated and interactive visualization of Java objects
 * 	<li> visual programming interface (with Groovy support)
 *      <li> visual meta programming
 *      <li> application deployment
 * </ul>
 * </p>
 *
 * \subsection dev_status Development status
 * <p>
 * In its current state VRL is able to generate user interfaces from any local
 * Java object. The VRL-Studio application attempts to give easy access to most
 * of the features.
 * </p>
 * \subsection ug_support UG Support
 * <p>
 * Christian Poliwoda is working on the UG visualization support. The
 * visualization support is available as VRL extension and can be integrated to
 * the VRL-Studio.
 * </p>
 * <p>
 * As the XML based network interface for UG by Leo Reich is still in
 * development only future version will have network transparent UG support.
 * </p>
 *
 * \section use_sec Using VRL
 * <p>
 * Using VRL is straightforward. In combination with a modern IDE such as
 * Netbeans 6.5 or higher the following example can be implemented very easily.
 * </p>
 * \subsection req_sec Requirements
 * <p>
 * For the examples you need at least:
 * <ul>
 * 	<li> Java 6
 *      <li> Java3D 1.5.2
 * 	<li> Netbeans 6.5 (with Groovy and Grails)
 * 	<li> VRL 0.3
 * </ul>
 * <p>
 * You can get the VRL and example projects from
 * <a href="http://www.mihosoft.eu/software-projects/vrl.html">
 * www.mihosoft.de/software-projects/vrl.html</a>
 * </p>
 * \subsection tut_seq Basic Example
 * <p>
 * After downloading and unpacking VRL start NetBeans and create a new
 * Project (VRLExample):
 * </p>
 * @image html netbeans_create_project_01.png
 * @image latex netbeans_create_project_01.eps "Create New Project" width=10cm
 * <p>
 * Make sure "Create Main Class" is unselected:
 * </p>
 * @image html netbeans_create_project_02.png
 * @image latex netbeans_create_project_02.eps "Project Properties" width=10cm
 * <p>
 * Add the <code>VRL-0.3.jar</code>
 * library file to the project by right-clicking on the "Libraries" folder
 * of your project and clicking "Add JAR/Folder".
 * </p>
 *
 * @image html netbeans_add_libs_01.png
 * @image latex netbeans_add_libs_01.eps "Add Libraries (1)" width=6cm
 * <p>
 * After selecting the file click "Open".
 * </p>
 * @image html netbeans_add_libs_02.png
 * @image latex netbeans_add_libs_02.eps "Add Libraries (2)" width=10cm
 * <p>
 * Do the same for the Java 3D Jar files and Groovy. For Groovy choose
 * "Add Library" instead of "Add Jar/Folder". Select Groovy 1.5.5 from the list.
 * If Groovy does not appear the Groovy and Grails plugin is not installed. This
 * can be done with the Netbeans plugin manager. Please make sure that Java 3D
 * is installed on your system. The example will only run if you have
 * installed Java 3D properly.
 * </p>
 * <p>
 * Add a new <code>JFrame</code> form:
 * </p>
 * @image html netbeans_new_jframe_01.png
 * @image latex netbeans_new_jframe_01.eps "Add new JFrame (1)" width=10cm
 * <p>
 * and call it "MainFrame":
 * </p>
 * @image html netbeans_new_jframe_02.png
 * @image latex netbeans_new_jframe_02.eps "Add new JFrame (2)" width=10cm
 * <p>
 * In the Swing palette right-click and select "Create New Category"; call it VRL.
 * </p>
 * @image html netbeans_add_beans_01.png
 * @image latex netbeans_add_beans_01.eps "Add New Palette Category" width=8cm
 * <p>
 * Now right-click again, select "Palette Manager" and click on "Add from JAR".
 * </p>
 * @image html netbeans_add_beans_02.png
 * @image latex netbeans_add_beans_02.eps "Add New Beans to Palette (1)" width=8cm
 * <p>
 * There you have to load the VRL library file
 * <code>VRL-0.3.jar</code> by clicking on "Add from JAR".
 * After clicking "Next" you can choose which components to add. Select
 * "ObjectTree" and "VisualCanvas".
 * </p>
 * @image html netbeans_add_beans_03.png
 * @image latex netbeans_add_beans_03.eps "Add New Palette Category (2)" width=10cm
 * <p>
 * Then click "Next" and select the "VRL" category before clicking "Finish".
 * </p>
 * <p>
 * After following the previous steps you should see "VisualCavas" and
 * "ObjectTree" in the Swing Palette. Drag them to the MainFrame. You can also
 * use other Swing containers such as <code>SplitPane</code> and
 * <code>ScrollPane</code> to create a custom layout.
 * </p>
 * @image html netbeans_add_beans_04.png
 * @image latex netbeans_add_beans_04.eps "Add Components to JFrame" width=10cm
 * <p>
 * Now comes the interesting part. You have to write the classes you want to
 * display. In our case it will be the following:
 * </p>
 * \code
 * class SimpleClass {
 * 	public Integer add(Integer a, Integer b) {
 * 		return a + b;
 * 	}
 * }
 * \endcode
 * <p>
 * Go to the MainFrame class and add the following to the constructor:
 * </p>
 * \code
 * objectTree1.addObject(new SimpleClass());
 * objectTree1.addObject(new SimpleClass());
 *
 * objectTree1.expandRow(0);
 * \endcode
 * <p>
 * That's it. Now you can run the application. Drag the two objects from the
 * tree to the canvas. You can connect the objects and invoke there methods.
 * </p>
 * @image html netbeans_app_01.png
 * @image latex netbeans_app_01.eps "Run the Example" width=10cm
 *
 * \subsection concl_seq Conclusion
 * This first example shows the basic concept of VRL GUI design. Very important
 * is the strict separation between functionality and design. The objects who
 * contain the code are completely independent of their visual appearance. On
 * the other hand a very essential feature is the possibility to manually
 * specify the visual appearance of more complicated parameter types. Please
 * read the \ref tr_page for further information.
 */



