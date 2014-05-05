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
import eu.mihosoft.vrl.base.IOUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Main {

    public static void main(String[] args) {
        
        IOUtil.deleteDirectory(new File("java.txt"));
        IOUtil.deleteDirectory(new File("groovy.txt"));

//        for (int i = 0; i < 200; i += 50) {
            for (int j = 1; j < 50; j += 10) {
                compile(j, 200, j);
            }
//        }

    }

    static void compile(int i, int numClasses, int numMethods) {

        IOUtil.deleteContainedFilesAndDirs(new File("test-src/myPackage"));

        createCode("java", numClasses, numMethods);

        System.out.println(" -> compiling...");

        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cd test-src;javac myPackage/*.java");
//        builder.directory(new File("test-src"));
        builder.redirectErrorStream(true);
        Process p = null;
        long timeStamp = System.nanoTime();
        try {
            p = builder.start();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        readProcessOutput(p);

        double duration = (System.nanoTime() - timeStamp) * 1e-9;

        System.out.println(" -> duration: " + duration);

        appendTime(i, duration, new File("java.txt"));

        IOUtil.deleteContainedFilesAndDirs(new File("test-src/myPackage"));

        createCode("groovy", numClasses, numMethods);

        System.out.println(" -> compiling...");

//        builder = new ProcessBuilder("/bin/bash", "-c", "cd test-src;/home/miho/software/groovy/groovy-2.2.2/bin/groovyc myPackage/*.groovy");
        builder = new ProcessBuilder("/bin/bash", "-c", "cd test-src;/home/miho/Downloads/groovy-1.8.9/bin/groovyc myPackage/*.groovy");
        builder.directory(new File("test-src"));
        p = null;
        timeStamp = System.nanoTime();
        try {
            p = builder.start();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        readProcessOutput(p);

        duration = (System.nanoTime() - timeStamp) * 1e-9;

        System.out.println(" -> duration: " + duration);

        appendTime(i, duration, new File("groovy.txt"));
    }

    private static void appendTime(int i, double time, File f) {
        try {
            Files.append("" + i + " " + time + "\n", f, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void readProcessOutput(Process p) {
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void createCode(String ending, int numClasses, int numMethods) {

        System.out.println(">> creating code for " + ending + ", #cls: " + numClasses + " #methods: " + numMethods +"...");

        VisualCodeBuilder vCodeBuilder = new VisualCodeBuilder_Impl();

        List<CompilationUnitDeclaration> cuDeclarations = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < numClasses; i++) {
//            System.out.println("i: " + i);
            CompilationUnitDeclaration cuDev = vCodeBuilder.declareCompilationUnit("File" + i + "." + ending, "myPackage");
            ClassDeclaration cDec = deClareClass(vCodeBuilder, cuDev, "File" + i, i, numMethods);

            cuDeclarations.add(cuDev);
        }

        System.out.println(" -> writing code for " + ending + "...");

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
