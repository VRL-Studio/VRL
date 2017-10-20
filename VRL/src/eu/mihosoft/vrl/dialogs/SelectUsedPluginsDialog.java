/* 
 * SelectUsedPluginsDialog.java
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
package eu.mihosoft.vrl.dialogs;

import eu.mihosoft.vrl.io.VProjectController;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.AbstractPluginDependency;
import eu.mihosoft.vrl.system.PluginConfigurator;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.visual.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class SelectUsedPluginsDialog {

    public static void show(final VProjectController projectController) {
        final UsedPluginsDialogPanel componentPanel
                = new UsedPluginsDialogPanel(projectController);

        int answer = VDialog.showConfirmDialog(
                projectController.getCurrentCanvas(),
                "Select Plugins", componentPanel,
                new String[]{"OK", "Cancel"});

        if (answer == 0) {
            if (componentPanel.assignChanges()) {
                VDialog.showMessageDialog(projectController.getCurrentCanvas(),
                        "Warning: deselected plugins!",
                        "<html><div align=left>"
                        + "You have deselected plugins that where previously used.<br><br>"
                        + "If your session depends on these plugins you have to expect<br>"
                        + "loading errors.<br><br>"
                        + "To fix the loading errors select the missing plugins and try<br>"
                        + "again. Do not save any changes until all necessary plugins are<br>"
                        + "selected."
                        + "</div></html>");
            }

            try {
                projectController.open(
                        projectController.getCurrentSession());
            } catch (IOException ex) {
                Logger.getLogger(SelectUsedPluginsDialog.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

    }
}

class UsedPluginsDialogPanel extends VComponent {

    private VProjectController projectController;
    private Collection<PluginView> pluginViews = new ArrayList<PluginView>();

    public UsedPluginsDialogPanel(VProjectController projectController) {
        super(projectController.getCurrentCanvas());
        this.projectController = projectController;

//        setLayout(new GridLayout());
        Box listBox = Box.createVerticalBox();

        listBox.setBorder(new EmptyBorder(5, 15, 5, 15));

        VScrollPane scrollPane = new VScrollPane(listBox);
        scrollPane.setMinimumSize(new Dimension(600, 400));
        add(scrollPane);

        Collection<AbstractPluginDependency> projectDeps
                = projectController.getProject().getProjectInfo().getPluginDependencies();

        for (AbstractPluginDependency pDep : VRL.getAvailablePlugins()) {

            // we do not want to see VRL in the list (cannot be (de)selected)
            if (pDep.getName().equals("VRL")) {
                continue;
            }

            boolean used = false;

            for (AbstractPluginDependency usedPDep : projectDeps) {
                if (pDep.getName().equals(usedPDep.getName())) {
                    used = true;
                    break;
                }
            }

            PluginView view = new PluginView(
                    projectController.getCurrentCanvas(),
                    pDep, used, isAutoSelected(pDep));

            listBox.add(view);

            pluginViews.add(view);
        }
    }

    private boolean isAutoSelected(AbstractPluginDependency pDep) {
        PluginConfigurator pConf
                = VRL.getPluginByDependency(pDep.toPluginDependency());

        if (pConf == null) {
            return false;
        } else {
            return pConf.isAutomaticallySelected();
        }
    }

    public boolean assignChanges() {
        Collection<AbstractPluginDependency> projectDeps
                = new ArrayList<AbstractPluginDependency>();

        Collection<AbstractPluginDependency> oldProjectDeps
                = projectController.getProject().getProjectInfo().
                getPluginDependencies();

        for (AbstractPluginDependency pDep : VRL.getAvailablePlugins()) {

            boolean used = false;

            for (PluginView view : pluginViews) {
                if (pDep.getName().equals(view.getPluginDependency().getName())
                        && view.isSelected()) {
                    used = true;
                    break;
                }
            }

            if (used) {
                projectDeps.add(pDep);
            }
        }

        projectController.getProject().getProjectInfo().
                setPluginDependencies(projectDeps);

        projectController.getProject().saveProjectInfo();
        try {
            projectController.getProject().
                    getProjectFile().commit("Plugins activated/deactivated.");
        } catch (IOException ex) {
            Logger.getLogger(UsedPluginsDialogPanel.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        boolean unselected = false;

        for (AbstractPluginDependency pDep : oldProjectDeps) {
            boolean found = false;
            for (AbstractPluginDependency pDep2 : projectDeps) {
                if (pDep.getName().equals(pDep2.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                unselected = true;
                break;
            }
        }

        return unselected;
    }
}

class PluginView extends VComponent {

    private AbstractPluginDependency pluginDependency;
    public static String ENABLED_ACTION_CMD = "plugin-enabled";
    public static String DISABLED_ACTION_CMD = "plugin-disabled";
    boolean selected;
    boolean autoSelected;
//    private ActionListener actionListener;

    public PluginView(
            VisualCanvas canvas,
            AbstractPluginDependency pluginDependency, boolean selected,
            boolean autoSelected/*
     * ,ActionListener listener
     */) {
        super(canvas);
        this.pluginDependency = pluginDependency;
//        this.actionListener = listener;
        this.selected = selected;
        this.autoSelected = autoSelected;

        setAlignmentX(Component.LEFT_ALIGNMENT);

        initialize();
    }

    private void initialize() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        final JCheckBox checkBox = new JCheckBox();
        checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkBox.setSelected(selected || isAutoSelected());
        CanvasLabel label = new CanvasLabel(this, getPluginDependency().getName());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        checkBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selected = checkBox.isSelected();
            }
        });

        add(checkBox);
        add(label);

        checkBox.setEnabled(!isAutoSelected());
        if (isAutoSelected()) {
            checkBox.setToolTipText("This plugin is always active and cannot be deactivated.");
        }

    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isAutoSelected() {
        return autoSelected;
    }

    /**
     * @return the pluginDependency
     */
    public AbstractPluginDependency getPluginDependency() {
        return pluginDependency;
    }
}
