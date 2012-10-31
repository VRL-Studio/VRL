/* 
 * CompletionUtil.java
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
package eu.mihosoft.vrl.lang.visual;

import eu.mihosoft.vrl.io.VJarUtil;
import eu.mihosoft.vrl.system.PluginConfigurator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CompletionUtil {
    
    private final static Collection<ClassCompletionList> completionLists =
            new ArrayList<ClassCompletionList>();
    private static ClassCompletionList completionListGroup =
            new ClassCompletionListGroupImpl(completionLists);
    
    static {
        String[] bootPaths = System.getProperty("sun.boot.class.path").split(":");
        for (String path : bootPaths) {
            if (path.endsWith("rt.jar")) {
                registerClassesFromJar(new File(path));
            }
        }
    }

    // no instanciation allowed
    private CompletionUtil() {
        throw new AssertionError(); // not in this class either!
    }
    
    public static void register(ClassCompletionList completions) {
        completionLists.add(completions);
        
        completionListGroup = new ClassCompletionListGroupImpl(completionLists);
    }
    
    public static void unregister(ClassCompletionList completions) {
        completionLists.remove(completions);
        
        completionListGroup = new ClassCompletionListGroupImpl(completionLists);
    }
    
    public static ClassCompletionList getCompletionList() {
        return completionListGroup;
    }
    
    public static void registerClassesFromJar(File f) {
        register(new JarClassCompletionList(f));
    }
}

class ClassCompletionListImpl implements ClassCompletionList {
    
    private Collection<String> classes = new ArrayList<String>();
    
    public void addClass(String cls) {
        classes.add(cls);
    }
    
    @Override
    public Collection<String> getClassNames() {
        return classes;
    }
}

class JarClassCompletionList implements ClassCompletionList {
    
    private Collection<String> classes = new ArrayList<String>();
    
    public JarClassCompletionList(File jarFile) {
        try {
            classes =
                    VJarUtil.getClassNamesFromStream(
                    new JarInputStream(new FileInputStream(jarFile)));
            
            
        } catch (IOException ex) {
            Logger.getLogger(JarClassCompletionList.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public Collection<String> getClassNames() {
        return classes;
    }
}

class ClassCompletionListGroupImpl implements ClassCompletionList {
    
    private Collection<ClassCompletionList> completionLists;
    
    public ClassCompletionListGroupImpl(Collection<ClassCompletionList> completionLists) {
        this.completionLists = completionLists;
    }
    
    @Override
    public Collection<String> getClassNames() {
        Collection<String> classNames = new ArrayList<String>();
        
        for (ClassCompletionList list : completionLists) {
            for (String n : list.getClassNames()) {
                if (!n.contains("$")) {
                    classNames.add(n);
                }
            }
        }
        
        return classNames;
    }
}
