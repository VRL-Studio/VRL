/* 
 * Main.java
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
package eu.mihosoft.vrl.lang.model;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.codehaus.groovy.ast.expr.MethodCall;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Main {

    public static void main(String[] args) {
        
            //        VLanguageModel model = VLanguageFactory.newModel();
//
//        VClass clazz = model.newClass();
//        clazz.setName("MyClass");
//        
//        VMethod m1 = model.newMethod();
//        
//        m1.setName("m1");
//        m1.setModifiers(new Modifiers(Modifier.PUBLIC));
//        m1.setReturnType(new Type("my.package.MyType"));
//        m1.setParameters(new Parameter(new Type("java.lang.Integer"),"param1"),new Parameter(new Type("java.lang.Integer"),"param2"));
//
//        
//        clazz.addMethod()
        
//        try {    
//            System.in.read();
//            
//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        System.out.println("starting...");

        VisualCodeBuilder vCodeBuilder = new VisualCodeBuilder_Impl();

        List<CompilationUnitDeclaration> cuDeclarations = Collections.synchronizedList(new ArrayList<>());
        
        for (int i = 0; i < 100; i++) {
            System.out.println("i: " + i);
            CompilationUnitDeclaration cuDev = vCodeBuilder.declareCompilationUnit("File" + i + ".java", "myPackage");
            ClassDeclaration cDec = deClareClass(vCodeBuilder, cuDev, "File" + i, i, 1000);

            cuDeclarations.add(cuDev);
        }

        System.out.println("writing...");

        File srcDir = new File("test-src/myPackage");
        srcDir.mkdirs();

        for (CompilationUnitDeclaration cuDec : cuDeclarations) {
            File f = new File(srcDir, cuDec.getFileName());

//            System.out.println("f: -> " + f.getAbsolutePath());
            String code = Scope2Code.getCode(cuDec);

            try {
                Files.write(code, f, Charset.forName("UTF-8"));
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static ClassDeclaration deClareClass(VisualCodeBuilder vCodeBuilder, CompilationUnitDeclaration cuDec, String name, int index, int numMethods) {

        Extends extendz = new Extends();

        if (index > 0) {
            extendz = new Extends(new Type(cuDec.getPackageName(), "File" + (index - 1)));
        }

        ClassDeclaration cDec = vCodeBuilder.declareClass(cuDec, new Type(cuDec.getPackageName(), name), new Modifiers(Modifier.PUBLIC), extendz, new Extends());

        MethodDeclaration prevMDec = null;

        for (int i = 1; i <= numMethods; i++) {
            MethodDeclaration mDec = vCodeBuilder.declareMethod(cDec, new Modifiers(Modifier.PUBLIC), Type.VOID, name + "M" + i, new Parameters(new Parameter(Type.INT, "v" + i)));

            
            ForDeclaration forD = vCodeBuilder.declareFor(mDec, "i", 1, 10, i);

            if (prevMDec != null) {
                vCodeBuilder.invokeMethod(forD, "this", prevMDec, Argument.varArg(forD.getVariable(forD.getVarName())));
                
                vCodeBuilder.declareVariable(forD, Type.INT, "j");
            }            
            
            prevMDec = mDec;
        }

        return cDec;
    }
}
