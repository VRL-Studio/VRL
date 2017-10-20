/* 
 * LoggingController.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
 * 
 * This file is part of VRL-Studio.
 *
 * VRL-Studio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL-Studio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL-Studio includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL-Studio. Neither the VRL Canvas attribution icon nor any
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
 * Second, keep the links to "About VRL-Studio" and "About VRL". The
 * copyright notice must remain.
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
package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.io.ConfigurationFile;
import eu.mihosoft.vrl.system.Messaging;
import eu.mihosoft.vrl.system.RedirectableStream;
import java.awt.Color;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class LoggingController {

    private static final PrintStream OUT = System.out;
    private static final PrintStream ERR = System.err;
    private final JTextArea view;
    private RedirectableStream out;
    private RedirectableStream err;
    private RedirectableStream msgout;
    public static final String SHOW_OUT_LOG_KEY = "Log:show-out";
    public static final String SHOW_ERR_LOG_KEY = "Log:show-err";
    private final ConfigurationFile config;
    private LogBackground logBackground;

    public LoggingController(JTextArea view, ConfigurationFile config) {
        this.view = view;
        this.config = config;

        initialize();
    }

    private void initialize() {
        out = Messaging.createRedirectableStreamWithView(
                Messaging.STD_OUT, view, OUT, Color.white, false);

        err = Messaging.createRedirectableStreamWithView(
                Messaging.STD_ERR, view, ERR, Color.red, false);

        msgout = Messaging.createRedirectableStreamWithView(
                Messaging.MSG_OUT, view, OUT, Color.white, false);

        msgout.setRedirectToStdOut(true);
        msgout.setRedirectToUi(true);

        out.setRedirectToStdOut(true);
        err.setRedirectToStdOut(true);

        System.setOut(out);
        System.setErr(err);


        // add a custom log handler that redirects to vrl streams
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(new Handler() {
            
            private final Formatter formatter = new SimpleFormatter();

            {
                setLevel(Level.ALL);
            }

            @Override
            public void publish(LogRecord record) {
                // we redirect logs to the out/err stream since we replaced
                // them with custom instances that are visible in the log
                // window
                // reason: all output (logger, sout, serr, etc.) is collected.
                if (record.getLevel() == Level.SEVERE 
                        || record.getLevel() == Level.WARNING) {
                    System.err.println(formatter.format(record));
                } else {
                    System.out.println(formatter.format(record));
                }
            }

            @Override
            public void flush() {
                //
            }

            @Override
            public void close() throws SecurityException {
                //
            }
        });

        view.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (logBackground != null) {
                    logBackground.setText(view.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (logBackground != null) {
                    logBackground.setText(view.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (logBackground != null) {
                    logBackground.setText(view.getText());
                }
            }
        });

        // return if loading is not possible: nothing to initialize
        if (!config.load()) {
            return;
        }

        if (config.containsProperty(SHOW_OUT_LOG_KEY)) {
            boolean enableOut = Boolean.parseBoolean(
                    config.getProperty(SHOW_OUT_LOG_KEY));
            setStdOutEnabled(enableOut);
        }

        if (config.containsProperty(SHOW_ERR_LOG_KEY)) {
            boolean enableErr = Boolean.parseBoolean(
                    config.getProperty(SHOW_ERR_LOG_KEY));
            setStdErrEnabled(enableErr);
        }
    }

    public void setStdOutEnabled(boolean v) {
        out.setRedirectToUi(v);
    }

    public void setStdErrEnabled(boolean v) {
        err.setRedirectToUi(v);
    }

    /**
     * @return the logBackground
     */
    public LogBackground getLogBackground() {
        return logBackground;
    }

    /**
     * @param logBackground the logBackground to set
     */
    public void setLogBackground(LogBackground logBackground) {
        this.logBackground = logBackground;
    }
}
