/* 
 * G4J.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
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

package eu.mihosoft.g4j.lang;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@ComponentInfo(name="G4j", category="VRL/Language",
        description = "Experimental Template Generator")
public class G4J implements StringProcessor, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private static final String id = "Processor:g4j";

    @Override
    @MethodInfo(valueName="Java Code", valueStyle="silent", hide=false)
    public String process(
            @ParamInfo(name="G4J Code", style="silent")String code) {

        ArrayList<TemplateClass> templateClasses = new ArrayList<TemplateClass>();
        ArrayList<TemplateClass> templateInstances = new ArrayList<TemplateClass>();


        TemplateClassProcessor tP =
                new TemplateClassProcessor(templateClasses, templateInstances);

        tP.process(code);

        String finalCode = "// processed code\n\n";

        int oldLength = 0;
        int counter = 0;

        while (finalCode.length() > oldLength) {

            System.out.println("\n >> --- G4J Pass " + counter + " ---\n");
            oldLength = finalCode.length();

            finalCode = "// processed code\n" + "// --> passes: " + counter + "\n\n";

            counter++;

            System.out.println("Template Classes:");

            for (TemplateClass tC : tP.getTemplateClasses()) {
                System.out.println(tC);
            }

            System.out.println("Template Instances:");

            for (TemplateClass tC : tP.getTemplateInstances()) {
                System.out.println(tC);
            }

            for (TemplateClass tC : tP.getTemplateClasses()) {

//                System.out.println(">> tC : " + tC);

                Collection<TemplateClass> instances =
                        new ArrayList<TemplateClass>();

                for (TemplateClass t : tP.getTemplateInstances()) {
//                    System.out.println(" --> search: " + t);
                    if (tC.getName().equals(t.getName())) {
                        instances.add(t);
                    }
                }

                ClassCodeExtractor cE = new ClassCodeExtractor(tC);
                String templateClassCode = cE.process(code);

                for (TemplateClass tI : instances) {

//                    System.out.println(" --> tI: " + tI);

//                    System.out.println("Code: " + tI);

                    TemplateInstanceCodeCreator tIC =
                            new TemplateInstanceCodeCreator(tC, tI,
                            templateClasses, templateInstances);

//                    System.out.println(tIC.process(templateClassCode));

                    finalCode += tIC.process(templateClassCode);
                }
            }

            tP.process(finalCode);
        }


        // finally convert <<T>,V> to T_V notation

        Matcher m = Patterns.TEMPLATE_ARGUMENT.matcher(finalCode);

        while (m.find()) {

            String templateArgs = m.group();

            int start = m.start();
            int end = m.end();
            
            templateArgs = templateArgs.
                    replaceFirst("<<", "").
                    replaceFirst(">>", "").trim();

            String codeBefore = finalCode.substring(0, start).trim();
            String replacement = TemplateClassProcessor.
                    replaceTemplateArguments(templateArgs);
            String codeAfter = finalCode.substring(end);

            finalCode = codeBefore + replacement + codeAfter;

            m = Patterns.TEMPLATE_ARGUMENT.matcher(finalCode);
        }


        return finalCode;
    }

    public String getID() {
        return id;
    }
}
