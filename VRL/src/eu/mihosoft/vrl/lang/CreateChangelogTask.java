/* 
 * CreateChangelogTask.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-2012 Steinbeis Forschungszentrum (STZ Olbronn),
 * Copyright (c) 2006-2012 by Michael Hoffer
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.Task;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CreateChangelogTask extends Task {

//    private String file;
    private String location;
    private String destination = "eu/mihosoft/vrl/resources/changelog/changelog.txt";
    private File locationFile;
    private boolean debug;
    private boolean enabled;
    private boolean providesPreGithub = false;

    public void setLocation(String location) {
        this.location = location;
        this.locationFile = new File(location);
    }

    public void setDebug(String debug) {
        this.debug = new Boolean(debug);
    }

    public void setEnabled(String enabled) {
        this.enabled = new Boolean(enabled);
    }

    public void setProvidesPreGithub(String providesPreGithub) {
        this.providesPreGithub = new Boolean(providesPreGithub);
    }

    @Override
    public void execute() {

        if (!isEnabled()) {
            System.out.println(">> skipping changelog creation (disabled)");
            return;
        }

        System.out.println(">> creating changelog");
        System.out.println(" --> location:    " + location);
        System.out.println(" --> destination: " + destination);

        String changelog = "";

        String[] tags = readTags();

        for (int i = tags.length - 1; i >= 0; i--) {
            String version = tags[i];
            String tagDate = getDate(tags[i]);
            String msg = getMsg(tags[i]);

            changelog += "-----------------------------------------------\n";
            changelog += "Version:\t" + version + "\n";
            changelog += "Date:\t\t" + tagDate;
            changelog += "-----------------------------------------------\n\n";
            changelog += msg + "\n";
        }

        if (providesPreGithub) {
            String preGitHubChangelog = "";

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(new File(locationFile,
                        "eu/mihosoft/vrl/resources/changelog/changelog-pre-github.txt")));

                while (reader.ready()) {
                    preGitHubChangelog += reader.readLine() + "\n";
                }

            } catch (IOException ex) {
                Logger.getLogger(CreateChangelogTask.class.getName()).
                        log(Level.SEVERE, null, ex);
                System.err.println(" --> ERROR: cannot read pre-github changelog file");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        Logger.getLogger(CreateChangelogTask.class.getName()).
                                log(Level.SEVERE, null, ex);
                        System.err.println(" --> ERROR: cannot close pre-github changelog file");
                    }
                }
            }

            changelog = changelog + "\n--- [PRE GITHUB] ---\n\n" + preGitHubChangelog;

        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(
                    new FileWriter(
                    new File(locationFile,
                    destination)));

            writer.append(changelog);
        } catch (IOException ex) {
            Logger.getLogger(CreateChangelogTask.class.getName()).
                    log(Level.SEVERE, null, ex);
            System.err.println(" --> ERROR: cannot write destination changelog file");
            ex.printStackTrace(System.err);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(CreateChangelogTask.class.getName()).
                            log(Level.SEVERE, null, ex);
                    System.err.println(" --> ERROR: cannot close destination changelog file");
                }
            }
        }



    }

    public String[] readTags() {

        Runtime rt = Runtime.getRuntime();

        String msg = "";

        if (isDebug()) {
            System.out.println(">> reading tags");
        }

        try {
            Process pr = rt.exec("git for-each-ref refs/tags/ --format=%(refname)", null, locationFile);

            pr.waitFor();

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line = null;

            while ((line = input.readLine()) != null) {
                msg += line + "\n";
            }

            int exitVal = pr.waitFor();
            if (exitVal != 0) {
                System.out.println(" --> exited with error code " + exitVal);
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(CreateChangelogTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateChangelogTask.class.getName()).log(Level.SEVERE, null, ex);
        }


        String[] tags = msg.split("\n");

        for (int i = 0; i < tags.length; i++) {
            tags[i] = tags[i].replace("refs/tags/", "");
        }

        return tags;
    }

    public String getMsg(String tag) {

        Runtime rt = Runtime.getRuntime();

        String msg = "";

        if (isDebug()) {
            System.out.println(">> reading msg for " + tag);
        }

        try {
            Process pr = rt.exec("git log -2 --pretty=%B " + tag, null, locationFile);

            pr.waitFor();

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line = null;


            while ((line = input.readLine()) != null) {
                msg += line + "\n";
            }

            int exitVal = pr.waitFor();
            if (exitVal != 0) {
                System.out.println(" --> exited with error code " + exitVal);
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(CreateChangelogTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateChangelogTask.class.getName()).log(Level.SEVERE, null, ex);
        }


        return msg;
    }

    public String getDate(String tag) {

        Runtime rt = Runtime.getRuntime();

        String msg = "";

        if (isDebug()) {
            System.out.println(">> reading date for " + tag);
        }

        try {
            Process pr = rt.exec("git log -1 --pretty=%cD " + tag, null, locationFile);

            pr.waitFor();

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(pr.getInputStream()));

            String line = null;


            while ((line = input.readLine()) != null) {
                msg += line + "\n";
            }

            int exitVal = pr.waitFor();
            if (exitVal != 0) {
                System.out.println(" --> exited with error code " + exitVal);
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(CreateChangelogTask.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateChangelogTask.class.getName()).
                    log(Level.SEVERE, null, ex);
        }


        return msg;
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        if (destination != null && !destination.trim().isEmpty()) {
            this.destination = destination;
        }
    }
}
