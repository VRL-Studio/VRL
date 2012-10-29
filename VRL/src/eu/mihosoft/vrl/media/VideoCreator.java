/* 
 * VideoCreator.java
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

package eu.mihosoft.vrl.media;


import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.ext.com.bric.qt.*;
import eu.mihosoft.vrl.io.*;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VParamUtil;
import eu.mihosoft.vrl.types.CanvasRequest;
import eu.mihosoft.vrl.visual.ImageUtils;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

dkn

/**
 * Creates uncompressed video files (mov) from image files.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@ComponentInfo(name = "Video Creator", category = "VRL/Media",
description = "Video Creator (converts images (jpg, png) to mov-video)")
@ObjectInfo(name = "Video Creator")
public class VideoCreator implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private boolean stop;

    @MethodInfo(noGUI = true)
    public void convert(File input, File output, float spf) throws IOException {
        convert(null, input, output, spf);
    }

    @MethodInfo(noGUI = true)
    public void convert(String input, String output, float spf) throws IOException {
        convert(null, new File(input), new File(output), spf);
    }

    /**
     * Converts a set of image files to a video file (mov).
     * @param cReq canvas request (necessary for ui interaction)
     * @param input input folder
     * @param output output file
     * @param spf seconds per frame
     * @param from specifies the start index of the image set that shall be used for image creation
     * @param to specifies the stop index of the image set that shall be used for image creation
     * @throws IOException 
     */
    @MethodInfo(hide = true)
    public void convert(CanvasRequest cReq,
            @ParamInfo(name = "Input Folder",
            style = "load-folder-dialog", options = "") File input,
            @ParamInfo(name = "Output File", style = "save-dialog",
            options = "extensions=[\"mov\"];description=\".mov -files\"") File output,
            @ParamInfo(name = "Seconds per Frame",
            style = "default", options = "0.1D") float spf,
            @ParamInfo(name = "From:", options = "min=1") int from,
            @ParamInfo(name = "To:", options = "min=1") int to)
            throws IOException {
        stop = false;

        VParamUtil.throwIfNotValid(
                VParamUtil.VALIDATOR_EXISTING_FOLDER, input);

        MessageBox mBox = null;

        if (cReq != null) {
            VisualCanvas c = cReq.getCanvas();
            mBox = c.getMessageBox();
        }

        String msg = ">> Creating Movie file: " + output;
        System.out.println(msg);

        if (mBox != null) {
            mBox.addUniqueMessage("Creating Movie File:",
                    msg, null, MessageType.INFO);
        }

        if (!output.getName().toLowerCase().endsWith(".mov")) {
            output = new File(output.getAbsoluteFile().getParent(),
                    output.getName() + ".mov");
        }

        JPEGMovieAnimation anim = null;
        try {
            anim = new JPEGMovieAnimation(output);
        } catch (IOException ex) {
            Logger.getLogger(VideoCreator.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        ArrayList<File> images =
                IOUtil.listFiles(input, new String[]{".png", ".jpg"});

        if (images.isEmpty()) {
            msg = "--> Error: no images found in " + output;
            System.out.print(msg);
            mBox.addUniqueMessage("Cannot find images files:",
                    msg, null, MessageType.ERROR);
            return;
        }

        if (from > images.size()) {
            msg = "--> Error: only " + images.size()
                    + " images. Start index " + from + " is invalid!";
            System.out.print(msg);
            mBox.addUniqueMessage("Illegal start index:",
                    msg, null, MessageType.ERROR);
            return;
        }

        if (to > images.size()) {
            msg = "--> Error: only " + images.size()
                    + " images. Stop index " + from + " is invalid!";
            System.out.print(msg);
            mBox.addUniqueMessage("Illegal start index:",
                    msg, null, MessageType.ERROR);
            return;
        }

        if (from > to) {
            msg = "--> Error: only " + images.size()
                    + " images. Stop index > start index is invalid!";
            System.out.print(msg);
            mBox.addUniqueMessage("Illegal start and stop index:",
                    msg, null, MessageType.ERROR);
            return;
        }

        Collections.sort(images);

        for (int i = from - 1; i < to; i++) {

            msg = "--> processing image: " + (i + 1) + "/" + to;
            System.out.print(msg);

            if (mBox != null) {
                mBox.addUniqueMessage("Creating Movie File:",
                        msg, null, MessageType.INFO);
            }

            BufferedImage img = ImageIO.read(images.get(i));

            img = ImageUtils.convertToBufferedImage(
                    img, BufferedImage.TYPE_INT_RGB);

            anim.addFrame(spf, img, 0.9f);

            System.out.println("\t[Done]");

            if (stop) {
                msg = "--> processing stopped.";
                System.out.println(msg);
                if (mBox != null) {
                    mBox.addUniqueMessage("Creating Movie File:",
                            msg, null, MessageType.INFO);
                    break;
                }
            }
        }

        anim.close();

        if (!stop) {

            msg = ">> finished.";

            if (mBox != null) {
                mBox.addUniqueMessage(
                        "Creating Movie File:", msg, null, MessageType.INFO);
            }

            System.out.println(msg);
        }
    }

    /**
     * Converts a set of image files to a video file (mov).
     * @param cReq canvas request (necessary for ui interaction)
     * @param input input folder
     * @param output output file
     * @param spf seconds per frame
     * @throws IOException 
     */
    @MethodInfo(hide = false)
    public void convert(CanvasRequest cReq,
            @ParamInfo(name = "Input Folder",
            style = "load-folder-dialog", options = "") File input,
            @ParamInfo(name = "Output File", style = "save-dialog",
            options = "extensions=[\"mov\"];description=\".mov -files\"") File output,
            @ParamInfo(name = "Seconds per Frame",
            style = "default", options = "value=0.1D") float spf)
            throws IOException {

        stop = false;

        VParamUtil.throwIfNotValid(
                VParamUtil.VALIDATOR_EXISTING_FOLDER, input);

        MessageBox mBox = null;

        if (cReq != null) {
            VisualCanvas c = cReq.getCanvas();
            mBox = c.getMessageBox();
        }

        String msg = ">> Creating Movie file: " + output;
        System.out.println(msg);

        if (mBox != null) {
            mBox.addUniqueMessage("Creating Movie File:",
                    msg, null, MessageType.INFO);
        }

        if (!output.getName().toLowerCase().endsWith(".mov")) {
            output = new File(output.getAbsoluteFile().getParent(),
                    output.getName() + ".mov");
        }

        JPEGMovieAnimation anim = null;
        try {
            anim = new JPEGMovieAnimation(output);
        } catch (IOException ex) {
            Logger.getLogger(VideoCreator.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        ArrayList<File> images =
                IOUtil.listFiles(input, new String[]{".png", ".jpg"});

        if (images.isEmpty()) {
            msg = "--> Error: no images found in " + output;
            mBox.addUniqueMessage("Cannot find images files:",
                    msg, null, MessageType.ERROR);
            return;
        }

        Collections.sort(images);

        for (int i = 0; i < images.size(); i++) {

            msg = "--> processing image: " + (i + 1) + "/" + images.size();
            System.out.print(msg);

            if (mBox != null) {
                mBox.addUniqueMessage("Creating Movie File:",
                        msg, null, MessageType.INFO);
            }

            BufferedImage img = ImageIO.read(images.get(i));

            img = ImageUtils.convertToBufferedImage(
                    img, BufferedImage.TYPE_INT_RGB);

            anim.addFrame(spf, img, 0.9f);

            System.out.println("\t[Done]");

            if (stop) {
                msg = "--> processing stopped.";
                System.out.println(msg);
                if (mBox != null) {
                    mBox.addUniqueMessage("Creating Movie File:",
                            msg, null, MessageType.INFO);
                    break;
                }
            }
        }

        anim.close();

        if (!stop) {

            msg = ">> finished.";

            if (mBox != null) {
                mBox.addUniqueMessage(
                        "Creating Movie File:", msg, null, MessageType.INFO);
            }

            System.out.println(msg);
        }
    }

    @MethodInfo(hide = false)
    public void stopConvert() {
        stop = true;
    }
}
