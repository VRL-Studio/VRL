/* 
 * VClassFileManager.java
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
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.lang.java;

import eu.mihosoft.vrl.lang.ClassEntry;
import eu.mihosoft.vrl.lang.Patterns;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.JavaFileObject.Kind;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VClassFileManager
        extends ForwardingJavaFileManager<JavaFileManager> {

//    private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject
    HashSet<ClassFileObject> custom = new HashSet<ClassFileObject>();
    private ClassEntryClassLoader loader = null;
    private VisualCanvas mainCanvas;

    public VClassFileManager(JavaCompiler compiler, VisualCanvas canvas) {

        super(compiler.getStandardFileManager(null, null, null));
        this.mainCanvas = canvas;

        try {
            if (canvas != null) {
                loader = new ClassEntryClassLoader(canvas.getClassLoader());
            } else {
                loader = new ClassEntryClassLoader();
            }
        } catch (Exception ex) {
            Logger.getLogger(VClassFileManager.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Iterable<JavaFileObject> list(Location location,
            String packageName,
            Set<Kind> kinds,
            boolean recurse)
            throws IOException {

        String kindString="Kinds=";
        
        for (Kind k : kinds) {
            kindString+="[K=" + k.name() + ", ext=" + k.extension+"]";
        }

        System.out.println("LIST: " + location + ":" + packageName + ":" + kindString + ":"+ recurse);

        Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);

        ArrayList<JavaFileObject> finals = new ArrayList<JavaFileObject>();

        for (JavaFileObject obj : result) {
            finals.add(obj);
        }


        for (ClassFileObject f : custom) {
            if (location.equals(StandardLocation.CLASS_PATH) && kinds.contains(Kind.CLASS)) {

//                String fPackageName = f.getName();
//                String className = f.getName();
//
//                if (fPackageName.contains("/")) {
//                    int pos = fPackageName.lastIndexOf('/');
//                    fPackageName = fPackageName.substring(0, pos);
//                    fPackageName = fPackageName.replace("/", ".");
//                    className = f.getName().substring(pos+1);
//                } else {
//                    fPackageName = "";
//                }
//
//                if (fPackageName.equals(packageName)) {
//                    f.setName(className);
//                    finals.add(f);
//                }
                
                 if (packageName.equals(f.getPackageName())) {
                    finals.add(f);
                }
            }
        }

        return finals;
    }

    public Collection<String> getPackageNames() {
        Collection<String> result = new ArrayList<String>();
        
        for(ClassFileObject o : custom) {
            result.add(o.getPackageName());
        }
        
        return result;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
            Location location, String name, Kind kind, FileObject sibling)
            throws IOException {

        ClassFileObject co = new ClassFileObject(name, kind);
        loader.add(name, co);

        custom.add(co);

        return co;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return loader;
    }

    public Collection<ClassEntry> getClasses() {
        return loader.getClasses();
    }

    public void removeClass(String className) {
        loader.delete(className);

        ClassEntryClassLoader newLoader =
                new ClassEntryClassLoader(mainCanvas.getClassLoader());

        newLoader.addClasses(loader.getClasses());

        loader = newLoader;
    }
//    public void setClasses(Collection<ClassEntry> classes) {
//        loader.clear();
//        loader.addClasses(classes);
//    }



    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof ClassFileObject) {
            return ((ClassFileObject) file).getName().replace(".class", "");
        } else {
            return super.inferBinaryName(location, file);
        }
    }

    @Override
    public boolean hasLocation(Location location) {
        return 
                location == StandardLocation.CLASS_PATH
                || location == StandardLocation.PLATFORM_CLASS_PATH;
        // we don't suppoert anything else
    }
    
    public void addClassFiles(Collection<ClassFileObject> objects) {
        custom.addAll(objects);
    }

}
