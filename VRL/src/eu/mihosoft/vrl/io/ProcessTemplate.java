/* 
 * ProcessTemplate.java
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

package eu.mihosoft.vrl.io;

import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.MessageBoxViewer;
import eu.mihosoft.vrl.visual.MessageList;
import eu.mihosoft.vrl.visual.MessageViewer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Template class for starting external processes.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ProcessTemplate implements Serializable {

    private static final long serialVersionUID = -2760887758460890720L;
    private String path;// = "/Users/miho/installs/UGToolChain/IsoSurface/";
    private ArrayList<String> command;
    private ArrayList<String> input;
    private ArrayList<String> output;
    private transient Canvas mainCanvas;
    private MessageViewer viewer = new MessageBoxViewer();
    private Integer numberOfMessageLines = 10;
    private String processTitle = "Process";
    private Map<String, String> environment = new HashMap<String, String>();

    /**
     * Constructor.
     */
    public ProcessTemplate() {
        //
    }

    /**
     * This method executes the specified command.
     */
    public void run() {

        MessageList messages = new MessageList();
        messages.setMessageListSize(getNumberOfMessageLines());

        for (int i = 0; i < getNumberOfMessageLines(); i++) {
            messages.addMessage("");
        }

        String mString = getProcessTitle()
                + ": <br><br>" + messages.getMessages();

        getViewer().update(mString);

        ProcessBuilder builder = new ProcessBuilder();
        Map<String, String> env = builder.environment();

        if (environment != null) {
            env.putAll(environment);
        }

        builder.command(getCommand());
        builder.directory(new File(getPath()));
        builder.redirectErrorStream(true);

        final Process p;

        try {
            p = builder.start();

            if (getInput() != null) {
                BufferedWriter writer =
                        new BufferedWriter(
                        new OutputStreamWriter(p.getOutputStream()));

                for (String s : getInput()) {
                    writer.write(s);
                    writer.newLine();
                }

                writer.flush();
            }

            final BufferedReader read =
                    new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            output = new ArrayList<String>();

            long timeStart = 0;
            long timeStop = 0;

            boolean notFinished = true;
            while (notFinished) {
                try {
                    while (read.ready()) {
                        String line = read.readLine();
                        output.add(line);
                        messages.addMessage(line);

                        // don't update the message buffer more than
                        // ten times per second
                        if (timeStop - timeStart > 100
                                || (timeStart == 0 && timeStop == 0)) {
                            timeStart = System.currentTimeMillis();
                            if (getViewer() != null) {
                                String message = getProcessTitle()
                                        + ": <br><br>" + messages.getMessages();
                                getViewer().update(message);
                            }
                        }
                        timeStop = System.currentTimeMillis();
                    }
                    p.exitValue();
                    notFinished = false;
                } catch (IOException ex) {
                    Logger.getLogger(ProcessTemplate.class.getName()).
                            log(Level.SEVERE, null, ex);
                } catch (IllegalThreadStateException ex) {
                    notFinished = true;
                }
            }

            String message = getProcessTitle()
                    + ": <br><br>" + messages.getMessages();
            getViewer().update(message);


        } catch (IOException ex) {
            Logger.getLogger(ProcessTemplate.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the executable path of the process.
     * @return the path
     */
    @MethodInfo(hide = true)
    public String getPath() {
        return path;
    }

    /**
     * Defines the executable path of the process.
     * @param path the path to set
     */
    @MethodInfo(hide = true)
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @param mainCanvas the canvas to set
     */
    @MethodInfo(hide = true, callOptions = "assign-to-canvas")
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;

        if (getViewer() instanceof MessageBoxViewer) {
            MessageBoxViewer mBoxViewer = (MessageBoxViewer) getViewer();
            mBoxViewer.setMainCanvas(mainCanvas);
        }
    }

    /**
     * Returns the command string and all of its options.
     * @return a list, containing the command and its options
     */
    public ArrayList<String> getCommand() {
        return command;
    }

    /**
     * DEfines the command and its options.
     * @param command the command list to set
     */
    public void setCommand(ArrayList<String> command) {
        this.command = command;
    }

    /**
     * Returns the message viewer.
     * @return the viewer
     */
    public MessageViewer getViewer() {
        return viewer;
    }

    /**
     * Defines the message box viewer.
     * @param viewer the viewer to set
     */
    public void setViewer(MessageViewer viewer) {
        this.viewer = viewer;
    }

    /**
     * Returns the main canvas object.
     * @return the main canvas
     */
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    /**
     * @return the numberOfMessageLines
     */
    public Integer getNumberOfMessageLines() {
        return numberOfMessageLines;
    }

    /**
     * Defines the number of lines of process messages.
     * @param numberOfMessageLines the number of lines
     */
    public void setNumberOfMessageLines(Integer numberOfMessageLines) {
        this.numberOfMessageLines = numberOfMessageLines;
    }

    /**
     * Returns the process title
     * @return the process title
     */
    public String getProcessTitle() {
        return processTitle;
    }

    /**
     * Defines the process title.
     * @param processTitle the process title to set
     */
    public void setProcessTitle(String processTitle) {
        this.processTitle = processTitle;
    }

    /**
     * Returns a list containing the process input.
     * @return the input
     */
    public ArrayList<String> getInput() {
        return input;
    }

    /**
     * Defines the list containing the process input.
     * @param input the input to set
     */
    public void setInput(ArrayList<String> input) {
        this.input = input;
    }

    /**
     * Returns the process output.
     * @return the output
     */
    public ArrayList<String> getOutput() {
        return output;
    }

    /**
     * Returns the process environment.
     * @return the process environment
     */
    public Map<String, String> getEnvironment() {
        return environment;
    }
}
