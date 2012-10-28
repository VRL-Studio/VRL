/* 
 * SetTimeStampTask.java
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

package eu.mihosoft.vrl.lang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class SetTimeStampTask extends Task {

    private String file;
    private String location;
    private Collection<FileSet> filesets = new ArrayList<FileSet>();

    public void setFile(String file) {
        this.file = file;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }

    @Override
    public void execute() {

//        System.out.println(">> file: " + file);

//        System.out.println(">> location: " + location);

//        System.out.println(">> fileset: ");

        for (FileSet fs : filesets) {
            String[] files = fs.getDirectoryScanner().getIncludedFiles();

            for (String f : files) {
                if (!f.endsWith("SetTimeStampTask.java")
                        && !f.endsWith("RemoveTimeStampTask.java")) {
                    setTimeStamp(location + "/" + f);
                }
            }
        }
    }

    private void setTimeStamp(String file) {
//        System.out.println(">>> processing file: " + file);

        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            in = new BufferedReader(new FileReader(file));

            ArrayList<String> lines = new ArrayList<String>();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            DateFormat yearFormat = new SimpleDateFormat("yyyy");

            Date date = new Date();

            while (in.ready()) {
                String line = in.readLine();
                line = line.replaceAll(
                        "/\\*<VRL_COMPILE_DATE>\\*/.*/\\*</VRL_COMPILE_DATE>\\*/",
                        "/*<VRL_COMPILE_DATE>*/\""
                        + dateFormat.format(date)
                        + "\"/*</VRL_COMPILE_DATE>*/");

                line = line.replaceAll(
                        "/\\*<VRL_COMPILE_DATE_YEAR>\\*/.*/\\*</VRL_COMPILE_DATE_YEAR>\\*/",
                        "/*<VRL_COMPILE_DATE_YEAR>*/\""
                        + yearFormat.format(date)
                        + "\"/*</VRL_COMPILE_DATE_YEAR>*/");


                lines.add(line);
                //System.out.println("Line: " + line);
            }

            out = new BufferedWriter(new FileWriter(file));

            for (String l : lines) {
                out.write(l);
                out.newLine();
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
            try {
                out.close();
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}
