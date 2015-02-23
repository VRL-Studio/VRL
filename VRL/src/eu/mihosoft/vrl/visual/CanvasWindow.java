/* 
 * CanvasWindow.java
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
package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.AnimationTask;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import eu.mihosoft.vrl.effects.FadeEffect;
import eu.mihosoft.vrl.effects.FlipEffect;
import eu.mihosoft.vrl.effects.SelectionEffect;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Draggable container window with title bar and icon pane.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CanvasWindow extends VComponent
        implements ShadowPainter, IDObject,
        Comparable<CanvasWindow>, Selectable {

    private Graphics canvasGraphics;
    private boolean initialized;
    /**
     * a canvas object is active if the user clicks on the title bar
     */
    private boolean active = false;
    private boolean moved;
//    private int ID;
//    private int connectorID;
    private JLabel title;
    private CanvasWindowTitleBar titleBar;
//    private int titleHeight = 25;
    private JPopupMenu popup;
    private Shape shape;
    private boolean minimized = false;
    private boolean resizing = false;
    private Dimension previousSize;
    private ArrayList<Task> removeTasks = new ArrayList<Task>();
    private WindowContentProvider contentProvider;
    private FadeEffect fadeEffect;
    private boolean selected;
    private SelectionEffect colorizeEffect;
    private CloseIcon closeIcon;
    private ArrayList<CapabilityChangedListener> capabilityChangedListeners
            = new ArrayList<CapabilityChangedListener>();
    private boolean selectable = true;
    private boolean movable = true;
    private boolean menuEnabled = true;
    private boolean resizable = true;
    private boolean activatable = true;
    private boolean closing = false;
    private boolean added = false;
    private boolean askIfClose = false;
    /**
     * Value from 0.0 to 1.0, 0 means completely minimized, 1 means maximized
     */
    private double minimizeValue;
    /**
     * list of action listeners
     */
    private final Collection<CanvasActionListener> actionListeners
            = new ArrayList<CanvasActionListener>();
    /**
     * Defines the identifier for window selected action (caused by clicking on
     * the title bar)
     */
    public static final String ACTIVE_ACTION = "set-active";
    /**
     * Defines the identifier for window selected action (caused by clicking on
     * the title bar)
     */
    public static final String INACTIVE_ACTION = "set-inactive";
    /**
     * Defines the identifier for window minimized action (caused by clicking on
     * the title bar of another window or by clicking on the canvas)
     */
    public static final String MINIMIZE_ACTION = "set-minimize";
    /**
     * Defines the identifier for window minimized action (caused by
     * double-clicking on the title bar)
     */
    public static final String MAXIMIZE_ACTION = "set-maximize";
    /**
     * Defines the identifier for window close action (caused by clicking on the
     * close icon)
     */
    public static final String CLOSE_ACTION = "set-close";
    /**
     * Defines the identifier for window closed action (caused by clicking on
     * the close icon)
     */
    public static final String CLOSED_ACTION = "set-closed";
    /**
     * Defines the identifier for window hide action (caused by invoking
     * hideWindow() or fadeOut() )
     */
    public static final String HIDE_ACTION = "set-hide";
    /**
     * Defines the identifier for window hide action (caused by invoking
     * showWindow() or fadeIn() )
     */
    public static final String SHOW_ACTION = "set-show";
    /**
     * Defines the identifier for window move action (caused by draggin the
     * window around )
     */
    public static final String MOVE_ACTION = "set-move";
    /**
     * Defines the identifier for window add action (caused by adding the window
     * to the canvas )
     */
    public static final String ADDED_ACTION = "set-added";
    /**
     * Defines the identifier for window visible action (caused by calling
     * setVisible() )
     */
    public static final String VISIBLE_ACTION = "set-visible";
    /**
     * Window location. It may contain negative coordinates. However, the real
     * window position is always non negative. The purpose of this variable is
     * to remember relative window position (relevant for moving window groups).
     */
    private Point location;
//    /**
//     * Defines whether to hide the titlebar if mouse pointer leaves this window.
//     */
//    private boolean hideTitleBarOnExit = false;
    /**
     * Key for fade-in duration property.
     */
    public static final String FADE_IN_DURATION_KEY
            = "CanvasWindow:fadeInDuration";
    /**
     * Key for fade-out duration property.
     */
    public static final String FADE_OUT_DURATION_KEY
            = "CanvasWindow:fadeOutDuration";
    /**
     * Key for flip duration style property.
     */
    public static final String FLIP_DURATION_KEY = "CanvasWindow:flipDuration";
    /**
     * Key for shadow width style property.
     */
    public static final String SHADOW_WIDTH_KEY = "CanvasWindow:shadowWidth";
    /**
     * Key for shadow width style property.
     */
    public static final String BORDER_THICKNESS_KEY
            = "CanvasWindow:borderThickness";
    /**
     * Key for shadow width style property.
     */
    public static final String BORDER_COLOR_KEY
            = "CanvasWindow:borderColor";
    /**
     * Key for shadow width style property.
     */
    public static final String TRANSPARENCY_KEY
            = "CanvasWindow:transparency";
    /**
     * Key for shadow width style property.
     */
    public static final String UPPER_BACKGROUND_COLOR_KEY
            = "CanvasWindow:upperBackgroundColor";
    /**
     * Key for shadow width style property.
     */
    public static final String LOWER_BACKGROUND_COLOR_KEY
            = "CanvasWindow:lowerBackgroundColor";
    /**
     * Key for shadow width style property.
     */
    public static final String ACTIVE_ICON_COLOR_KEY
            = "CanvasWindow:Icon[active]:Color";
    /**
     * Key for shadow width style property.
     */
    public static final String ICON_COLOR_KEY
            = "CanvasWindow:Icon[inactive]:Color";
    public static final String TITLE_TRANSPARENCY_KEY = "CanvasWindow:Title:transparency";
    public static final String UPPER_TITLE_COLOR_KEY = "CanvasWindow:Title:upperColor";
    public static final String LOWER_TITLE_COLOR_KEY = "CanvasWindow:Title:lowerColor";
    public static final String UPPER_ACTIVE_TITLE_COLOR_KEY = "CanvasWindow:Title[active]:upperColor";
    public static final String LOWER_ACTIVE_TITLE_COLOR_KEY = "CanvasWindow:Title[active]:lowerColor";
    public static final String BACKGROUND_PAINTER_KEY = "CanvasWindow:backgroundPainter";
    public static final String TITLEBAR_PAINTER_KEY = "CanvasWindow:titleBarPainter";
    private boolean groupDraggingEnabled;
    private double groupDraggingScale = 1.f;
    private Point2D.Double groupDraggingOffset = new Point2D.Double();
    private AffineTransform groupDraggingTransform = new AffineTransform();
    /**
     * Defines maximum window width, see {@link #setMaxWindowSize(int, int) }.
     */
    private int maxWidth = 3000;
    /**
     * Defines maximum window height, see {@link #setMaxWindowSize(int, int) }.
     */
    private int maxHeight = 3000;

    /**
     * Constructor.
     *
     * @param title the window title
     * @param mainCanvas the main canvas object
     */
    public CanvasWindow(String title, Canvas mainCanvas) {
        initialize(title, mainCanvas);
    }

    /**
     * Initializes the window.
     *
     * @param titleText the window title
     * @param mainCanvas the main canvas object
     */
    private void initialize(String titleText, final Canvas mainCanvas) {
        setMainCanvas(mainCanvas);

        if (!initialized) {
            // INIT
            initialized = true;

            titleBar = new CanvasWindowTitleBar(this);
            title = getTitleBar().getTitleLabel();
            titleBar.setTitle(titleText);

            try {
                addCloseIcon();
            } catch (Exception ex) {
                // just ignore exception
            }

            CapabilityChangedListener capabilityListener
                    = new CapabilityChangedListener() {

                        @Override
                        public void capabilityChanged(
                                CapabilityManager manager, Integer bit) {
                                    defineCapabilities();
                                }
                    };

            addCapabilityListener(capabilityListener);

            VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
            this.setLayout(layout);

//            this.setLayout(new BorderLayout() );
            this.add(getTitleBar());

            this.setBorder(new ShadowBorder(this));

            // we set opaque true, even if the object is transparent
            // this is because of the custom repaint manager which
            // currently redraws everything, no matter if transparent or not
            // WARNING: this may change in the future
            this.setOpaque(false);

            // TODO: this is only an EXPERIMENT
//            this.setOpaque(false);
            Style style = getStyle();

            setBackground(style.getBaseValues().getColor(UPPER_BACKGROUND_COLOR_KEY));

//            effectManager.getEffects().add(new FadeInEffect(this));
//            effectManager.getEffects().add(new FadeOutEffect(this));
//            effectManager.getEffects().add(new BlurEffect(this));
            fadeEffect = new FadeEffect(this);
            getEffectManager().getEffects().add(fadeEffect);

            fadeEffect.getAnimation().addFrameListener(new AnimationTask() {

                @Override
                public void firstFrameStarted() {
                    if (fadeEffect.isFadeIn()) {
                        CanvasWindow.this.setVisible(true);
                    }
                }

                @Override
                public void frameStarted(double time) {
                    //
                    if (isSelected()) {
                        colorizeEffect.setTransparency(
                                fadeEffect.getTransparency());
                    }
                }

                @Override
                public void lastFrameStarted() {
                    if (!fadeEffect.isFadeIn()) {
                        CanvasWindow.this.setVisible(false);
                    }
                }
            });

            colorizeEffect = new SelectionEffect(this);
            colorizeEffect.setColor(new Color(0.f, 0.f, 1.f, 0.3f));
            getEffectManager().getEffects().add(colorizeEffect);

            getEffectManager().getEffects().add(new FlipEffect(this));

            getRemoveTasks().add(new Task() {

                @Override
                public void run() {
                    mainCanvas.getWindowGroupController().
                            removeWindow(CanvasWindow.this);
                }
            });

            addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(ComponentEvent ce) {
                    //
                }

                @Override
                public void componentMoved(ComponentEvent ce) {
                    try {
                        java.util.List<Connection> connections
                                = CanvasWindow.this.getMainCanvas().
                                getDataConnections().
                                getAllWith(CanvasWindow.this);
                        //
                        // new redraw request for each connection to prevent
                        // connection redraw bug where the intersection of
                        // the connection and the window shawdow border was 
                        // repainted before the other parts.
                        //
                        // this resulted in:
                        //    |       (this is where the bug occurs)
                        //  o-|-------\    |
                        // ___|        \   ↓  ______
                        //              ---  |______|
                        //                 --|-o    |
                        //                   |______|
                        //
                        for (Connection connection : connections) {
                            connection.contentChanged();
                        }
                    } catch (Exception ex) {
                    }

                }
            });

            final JScrollPane scrollPane;

            ArrayList<Container> parentContainersOfCanvas
                    = VSwingUtil.
                    getAllParents(mainCanvas, JScrollPane.class);

            if (!parentContainersOfCanvas.isEmpty()) {
                scrollPane
                        = (JScrollPane) parentContainersOfCanvas.get(0);
            } else {
                scrollPane = null;
            }

            addActionListener(new CanvasActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals(MOVE_ACTION)
                            && mainCanvas.isAutoScrollEnabled()) {
                        if (scrollPane == null) {
                            return;
                        }

                        if (getY() + mainCanvas.getAutoScrollSensitiveBorderSize()
                                > mainCanvas.getVisibleRect().y
                                + mainCanvas.getVisibleRect().height) {
                            scrollPane.getVerticalScrollBar().setValue(
                                    scrollPane.getVerticalScrollBar().getValue() + 30);
                        }

                        if (getY() - mainCanvas.getAutoScrollSensitiveBorderSize()
                                < mainCanvas.getVisibleRect().y) {
                            scrollPane.getVerticalScrollBar().setValue(
                                    scrollPane.getVerticalScrollBar().getValue() - 30);
                        }
                        
                        if (getX() + mainCanvas.getAutoScrollSensitiveBorderSize()
                                > mainCanvas.getVisibleRect().x
                                + mainCanvas.getVisibleRect().width) {
                            scrollPane.getHorizontalScrollBar().setValue(
                                    scrollPane.getHorizontalScrollBar().getValue() + 30);
                        }

                        if (getX() - mainCanvas.getAutoScrollSensitiveBorderSize()
                                < mainCanvas.getVisibleRect().x) {
                            scrollPane.getHorizontalScrollBar().setValue(
                                    scrollPane.getHorizontalScrollBar().getValue() - 30);
                        }
                    }
                }
            });

            initMenu();

        } else {
            System.out.println(">> CanvasObject: already initialized!");
        }

//        setPainter(new BackgroundPainter(this));
//        class CustomPainter implements Painter {
//
//            CanvasWindow window;
//
//            public CustomPainter(CanvasWindow c) {
//                this.window = c;
//            }
//
//            @Override
//            public void paint(Graphics g, Style s, Dimension d) {
//                Graphics2D g2 = (Graphics2D) g;
//                g2.setColor(Color.BLACK);
//                g2.setStroke(new BasicStroke(1));
//                g2.drawRect(0, 0, d.width - 1, d.height - 1);
//                g2.setColor(new Color(80, 80, 80, 120));
//                g2.fillRect(0, 0, d.width, d.height);
//            }
//        }
//
//        setPainter(new CustomPainter(this));
        setPainterKey(CanvasWindow.BACKGROUND_PAINTER_KEY);
    }

    void added() {
        fireAction(new ActionEvent(this, 0, ADDED_ACTION));
        added = true;
    }

//    @Override
//    public void processMouseEvent(MouseEvent event) {
//        // we need to redispatch mouse events because this window has a
//        // mouse listener that consumes the events
//        super.processMouseEvent(event);
//        
//        if (getParent() != null && event.getSource() == this) {
////            MouseEvent newEvent = SwingUtilities.convertMouseEvent(
////                    this, event, getParent());
////            getParent().dispatchEvent(newEvent);
////            System.out.println("me: " + event);
//            
//            
//
//            MouseEvent newEvent = SwingUtilities.convertMouseEvent(
//                    this, event, getMainCanvas());
//
//            for (Component c : getMainCanvas().getComponents()) {
//                if (c != this && c != getMainCanvas().getEffectPane()
//                        && c.getBounds().contains(newEvent.getPoint())) {
//                    
//                    System.out.println("C-Begin: " + c);
//                    
//                    Component receiver = VSwingUtil.getDeepestReceivingChildAt(
//                            (Container)c, SwingUtilities.convertPoint(
//                            getMainCanvas(), newEvent.getPoint(), c)); 
////
//                    newEvent = SwingUtilities.convertMouseEvent(
//                            getMainCanvas(), newEvent, receiver);
//                    
//                    receiver.dispatchEvent(newEvent);
//                    break;
//                }
//            }
//        }
//    }
//    
//    public static boolean contains(Container parent, Component child, Point location) {
//        for (Component c : parent.getComponents()) {
//            Rectangle r = c.getBounds();
//            if r.contains(location);
//        }
//    }
    @Override
    public void showBack() {
        super.showBack();
        getTitleBar().setVisible(true);
        Dimension size = getBackSide().getPreferredSize();
        size.setSize(size.getWidth(),
                size.getHeight() - getTitleBar().getHeight());
        getBackSide().setSize(size);
        getBackSide().setPreferredSize(size);
    }

    @Override
    public void flip() {
        if (getEffectManager() == null || getEffectManager().isDisabled()) {
            super.flip();
        } else {
            double duration = (Double) getStyle().
                    getBaseValues().get(FLIP_DURATION_KEY);

            getEffectManager().startEffect("FlipEffect", duration);
        }
    }

    /**
     * Initializes the window menus.
     */
    private void initMenu() {
        JMenu viewMenu = new JMenu("View");

        JMenuItem item = new JMenuItem("Change Window Title");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String title = (String) JOptionPane.showInputDialog(
                        CanvasWindow.this,
                        "New Title:",
                        "Change Window Title:",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        getTitle());

                if (title != null) {

                    ArrayList<CanvasWindow> windows
                            = new ArrayList<CanvasWindow>();

                    if (!isSelected()) {
                        windows.add(CanvasWindow.this);
                    } else {
                        for (Selectable s : CanvasWindow.this.getMainCanvas().getClipBoard()) {
                            if (s instanceof CanvasWindow) {
                                windows.add((CanvasWindow) s);
                            }
                        }
                    }

                    for (CanvasWindow w : windows) {
                        w.setTitle(title);
                    }
                }
            }
        });

        viewMenu.add(item);

        item = new JMenuItem("Select/Unselect active Windows");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<CanvasWindow> windows = new ArrayList<CanvasWindow>();

                for (CanvasWindow w : CanvasWindow.this.getMainCanvas().getWindows()) {
                    if (w.isActive()) {
                        windows.add(w);
                    }
                }

                for (CanvasWindow w : windows) {
                    if (w.isSelected()) {
                        getMainCanvas().getClipBoard().unselect(w);
                    } else {
                        getMainCanvas().getClipBoard().select(w);
                    }
                }
            }
        });

        viewMenu.add(item);

        getPopup().add(viewMenu);

        getMainCanvas().getWindowMenuController().
                buildMenu(new MenuAdapter(getPopup()), this);
    }

    /**
     * Adds an icon component to this canvas window.
     *
     * @param c the icon to add
     */
    public void addIcon(Component c) {
        titleBar.addIcon(c);
    }

    /**
     * Removes an icon from this canvas window.
     *
     * @param c the icon to remove
     */
    public void removeIcon(Component c) {
        titleBar.removeIcon(c);
    }

    /**
     * Defines the capabilities of this window.
     */
    protected void defineCapabilities() {
        if (getMainCanvas().getCapabilityManager().
                isCapable(CanvasCapabilities.ALLOW_INSTANCIATION)) {
            addCloseIcon();
        } else {
            removeCloseIcon();
        }
    }

    /**
     * Removes the close icon.
     */
    public void removeCloseIcon() {
        if (closeIcon != null) {
            removeIcon(closeIcon);
            closeIcon = null;
        }
    }

    /**
     * Adds a close icon to this window.
     */
    public void addCloseIcon() {
        if (getMainCanvas().getCapabilityManager().
                isCapable(CanvasCapabilities.ALLOW_INSTANCIATION)) {

            if (closeIcon == null) {
                closeIcon = new CloseIcon(getMainCanvas());
                closeIcon.setActionListener(new CanvasActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        boolean close = true;

                        if (isAskIfClose()) {
                            close = VDialog.showConfirmDialog(getMainCanvas(),
                                    "Close Window?",
                                    "<html><div align=center>"
                                    + "Shall the window be closed?<br><br>"
                                    + "<b>"
                                    + "All unsaved user input of this window will be lost!"
                                    + "<b>"
                                    + "</div></html>", VDialog.DialogType.YES_NO)
                                    == VDialog.YES;
                        }

                        closeIcon.deactivate();

                        if (close) {
                            CanvasWindow.this.close();
                        }
                    }
                });

                closeIcon.setMinimumSize(
                        new Dimension(titleBar.getIconSize(), titleBar.getIconSize()));
                closeIcon.setPreferredSize(
                        new Dimension(titleBar.getIconSize(), titleBar.getIconSize()));
                closeIcon.setMaximumSize(
                        new Dimension(titleBar.getIconSize(), titleBar.getIconSize()));

                addIcon(closeIcon);
            }
        } else {
            throw new NotCapableOfException(
                    "Not capable of instantiate or remove objects"
                    + " from canvas! Thus, no close icon can be added to this "
                    + "window.");
        }
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {

        super.setBounds(x, y, Math.min(w, maxWidth), Math.min(h, maxHeight));
    }

    @Override
    public Dimension getPreferredSize() {

        int w = super.getPreferredSize().width;
        int h = super.getPreferredSize().height;

        if (w < maxWidth && h < maxHeight) {
            return new Dimension(w, h);
        } else {
            return new Dimension(Math.min(maxWidth, w), Math.min(maxHeight, h));
        }
    }

    /**
     * <p>
     * Defines the maximum window size. No layout manager can override this
     * definition. The purpose of this method is to prevent heap overflows
     * caused by insanely large windows. As windows use buffers to increase
     * drawing performance these values are highly important. Setting these
     * values to {@link Short#MAX_VALUE} or even worse to
     * {@link Integer#MAX_VALUE} will definitely cause heap overflows on
     * virtually any computer if windows show large content far beyond screen
     * resolution. </p>
     * <p>
     * <b>Note:</b> Buffers internally use Integer arrays (RGBA, 32 bit per
     * pixel). How to compute memory consumption of internal buffers:
     * <code>memInMB = (width*height*32)/1024/1024</code>. </p>
     */
    public void setMaxWindowSize(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

//    @Override
//    public void doLayout() {
//        System.out.println("CanvasWindow.doLayout()");
//        super.doLayout();
//        System.out.println(">> Size: W: " + getWidth() + " H: " + getHeight());
//    }
    @Override
    protected void paintComponent(Graphics g) {
//        int top = getInsets().top;
//        int bottom = getInsets().bottom;
//        int left = getInsets().left;
//        int right = getInsets().right;
//
//        boolean shadowPainter = getPainter() instanceof ShadowPainter;
//
//        if (shadowPainter) {
//            shape = ((ShadowPainter) getPainter()).getShape();
//        }
//
//        if (shape == null || !shadowPainter) {
//            // TODO why not -1 at (*)?
//            // because shape for shadow needs to be
//            // +1 in width. Otherwise window border is not shown on the right
//            // side.
//            shape = new RoundRectangle2D.Double(left + 0, top + 0,
//                    getWidth() - right - left,//(*)
//                    getHeight() - bottom - top - 1, 20, 20);
//        }

        if (isDraggingEnabled()) {
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform at = g2.getTransform();
            at.scale(groupDraggingScale, groupDraggingScale);

            Point2D.Double offset = new Point2D.Double();

            // transform the original offset to find out the offset that
            // originates from translation
            at.transform(getGroupDraggingOffset(), offset);

            // compute this offset
            Point2D.Double realOffset = new Point2D.Double(
                    offset.x - getGroupDraggingOffset().x,
                    offset.y - getGroupDraggingOffset().y);

            // apply invers transform because translating the affine transform
            // is done in scaled coordinates, whereas the offset we have to
            // translate is measured in pixel scale (1.0f).
            //
            // this is necessary to prevent that the zoomed window moves
            // away from the mouse pointer
            try {
                at.inverseTransform(realOffset, realOffset);
            } catch (NoninvertibleTransformException ex) {
                Logger.getLogger(CanvasWindow.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            // finally perform translation
            at.translate(realOffset.x, realOffset.y);

            groupDraggingTransform = at;

            // apply transform object to graphics device
            g2.setTransform(at);
        }

        super.paintComponent(g);
    }

//    @Override
//    public void paint(Graphics g) {
//        
//
//        super.paint(g);
//    }
    /**
     * Indicates whether this canvas window has currently been moved (dragged).
     *
     * @return <code>true</code> if the canvas window has currently been moved;
     * <code>false</code> otherwise
     */
    public boolean isMoved() {
        return moved;
    }

    /**
     * Defines whether this window is currently moved (dragged).
     *
     * @param moved the value to set
     */
    public void setMoved(boolean moved) {
        this.moved = moved;

        if (moved) {
            fireAction(new ActionEvent(this, 0, MOVE_ACTION));
        }
    }

    /**
     * Returns the graphics context of the parent canvas.
     *
     * @return the graphics context of the parent canvas
     */
    public Graphics getCanvasGraphics() {
        return canvasGraphics;
    }

    /**
     * Defines the graphics context of the parent canvas.
     *
     * @param canvasGraphics the graphics context to set
     */
    public void setCanvasGraphics(Graphics canvasGraphics) {
        this.canvasGraphics = canvasGraphics;
    }

//    @Override
//    public int getID() {
//        return ID;
//    }
//
//    @Override
//    public void setID(int ID) {
//        this.ID = ID;
//    }
//    public int getConnectorID() {
//        return connectorID;
//    }
//
//    public void setConnectorID(int connectorID) {
//        this.connectorID = connectorID;
//    }
    /**
     * Returns the window title.
     *
     * @return the window title
     */
    public String getTitle() {
        return titleBar.getTitleLabel().getText();
    }

    /**
     * Defines the title of the window.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
//        this.title.setText(title);
        titleBar.setTitle(title);
//        getTitleBar().setPreferredSize(
//                new Dimension(this.title.getPreferredSize().width,
//                titleHeight));
    }

//    @Override
//    public boolean equals(Object o) {
//        boolean result = false;
//        if (o instanceof CanvasWindow) {
//            CanvasWindow obj = (CanvasWindow) o;
//            result = this.getID() == obj.getID();
//        }
//        return result;
//    }
    @Override
    public int compareTo(CanvasWindow o) {
        int result = 0;

        if (o == null) {
            throw new IllegalArgumentException();
        }

        result = getID() - o.getID();

        return result;
    }

//    public ArrayList<Connector> getConnectors() {
//        //TODO find a more sophisticated way to do that
//        return new ArrayList<Connector>();
//    }
    @Override
    public Shape getShape() {

        boolean validShadowPainter
                = getPainter() != null && getPainter() instanceof ShadowPainter;

        if (validShadowPainter) {
            shape = ((ShadowPainter) getPainter()).getShape();
        }

        if (shape == null || !validShadowPainter) {
            int top = getInsets().top;
            int bottom = getInsets().bottom;
            int left = getInsets().left;
            int right = getInsets().right;

            shape = new RoundRectangle2D.Double(left + 0, top + 0,
                    getWidth() - right - left,//(*)
                    getHeight() - bottom - top - 1, 3, 3);
        }

        return shape;
    }

    /**
     * Indicates whether this window is currently active.
     *
     * @return <code>true</code> if the window is currently active;
     * <code>false</code> otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Defines whether the window is to be active.
     *
     * @param active the state to set
     */
    public void setActive(boolean active) {
        if (isActivatable()) {
            if (this.active != active) {
                this.active = active;
                this.contentChanged();
                this.getTitleBar().contentChanged();

                VSwingUtil.repaintRequest(this);

                if (active) {
                    fireAction(new ActionEvent(this, 0, ACTIVE_ACTION));
                } else {
                    fireAction(new ActionEvent(this, 0, INACTIVE_ACTION));
                }
            }
        }
    }

    /**
     * Fires an action.
     *
     * @param event the event
     */
    protected void fireAction(ActionEvent event) {
        for (CanvasActionListener l : actionListeners) {
            l.actionPerformed(event);
        }
    }

    /**
     * Returns the title bar of the window.
     *
     * @return the title bar of the window
     */
    public CanvasWindowTitleBar getTitleBar() {
        return titleBar;
    }

    /**
     * Indicates whether this window is minimized.
     *
     * @return <code>true</code> if this window is minimized; <code>false</code>
     * otherwise
     */
    public boolean isMinimized() {
        return minimized;
    }

    /**
     * Defines whether this window is to be minimized.
     *
     * @param minimized the state to set
     */
    void setMinimized(boolean minimized) {
        this.minimized = minimized;
    }

    /**
     * Indicates whether this window is currently resizing.
     *
     * @return <code>true</code> if this window is currently resizing;
     * <code>false</code> otherwise
     */
    public boolean isResizing() {
        return resizing;
    }

    /**
     * Defines whether this window is currently resizing.
     *
     * @param resizing the state to set
     */
    void setResizing(boolean resizing) {
        this.resizing = resizing;
    }

    /**
     * Returns the previous size of this window, i.e., the window size before
     * the last minimization.
     *
     * @return the previous size of this window
     */
    public Dimension getPreviousSize() {
        return previousSize;
    }

    /**
     * Defines the previous size of this window.
     *
     * @param previousSize the size to set
     */
    void setPreviousSize(Dimension previousSize) {
        this.previousSize = previousSize;
    }

    /**
     * Returns the minimize value of this window.
     *
     * @return the minimization value; <code>range: [0,1]</code>
     */
    double getMinimizeValue() {
        return minimizeValue;
    }

    /**
     * Defines the minimization value.
     *
     * @param minimizeValue the value to set (; <code>valid range: [0,1]</code>)
     */
    void setMinimizationValue(double minimizeValue) {
        this.minimizeValue = minimizeValue;
    }

    /**
     * Returns the remove tasks of this window.
     *
     * @return the remove tasks of this window
     */
    public ArrayList<Task> getRemoveTasks() {
        return removeTasks;
    }

    /**
     * Defines the content provider of this window.
     *
     * @param contentProvider the content provider to set
     */
    public void setContentProvider(WindowContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {

        if (isSelectable()) {
            this.selected = selected;

            if (selected) {
                colorizeEffect.setUnselect(false);
                getEffectManager().startEffect(colorizeEffect, 0.3);
            } else {
                colorizeEffect.setUnselect(true);
                getEffectManager().startEffect(colorizeEffect, 0.3);
            }
        }
    }

    /**
     * @return the changeListeners
     */
    public Collection<CanvasActionListener> getChangeListeners() {
        return actionListeners;
    }

    /**
     * The capability changed listeners of this window. To add listeners use the {@link CanvasWindow#addCapabilityListener(
     * eu.mihosoft.vrl.visual.CapabilityChangedListener)} instead.
     *
     * @return the capabilityChangedListeners
     */
    public ArrayList<CapabilityChangedListener> getCapabilityChangedListeners() {
        return capabilityChangedListeners;
    }

    /**
     * Adds a capability changed listener to the capability manager. The
     * listener will be removed if the <code>dispose()</code> method of this
     * window is called.
     *
     * @param listener the listener to add
     */
    public void addCapabilityListener(CapabilityChangedListener listener) {
        getMainCanvas().getCapabilityManager().
                addCapabilityChangedListener(listener);
        getCapabilityChangedListeners().add(listener);
    }

    /**
     * @return the popup
     */
    public JPopupMenu getPopup() {
        return popup;
    }

    /**
     * @param popup the popup to set
     */
    public void setPopup(JPopupMenu popup) {
        this.popup = popup;
    }

//    /**
//     * Popup menu listener
//     */
//    private class ObjectMenuListener implements ActionListener {
//
//        private CanvasWindow parent;
//
//        public ObjectMenuListener(CanvasWindow parent) {
//            this.parent = parent;
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            if (e.getActionCommand().equals("Delete")) {
//                mainCanvas.getWindows().removeObject(ID);
//            }
//        }
//    }
    /**
     * Defines the size of the title bar.
     *
     * @param d the size of the title bar
     */
    public void setTitleBarSize(Dimension d) {
        titleBar.setSize(d);
    }

    /**
     * Returns the size of the title bar.
     *
     * @return the size of the title bar
     */
    public Dimension getTitleBarSize() {
        return titleBar.getSize();
    }

    /**
     * This method is called if the window will be removed.
     */
    void windowRemoved() {
        for (Task t : removeTasks) {
            t.run();
        }
//        mainCanvas.getWindows().removeObject(ID);
        dispose();
    }

//    @Override
//    public Painter getPainter() {
//
//        setPainter(getStyle().getBaseValues().
//                getPainter(CanvasWindow.BACKGROUND_PAINTER_KEY));
//
//        return super.getPainter();
//    }
    @Override
    public void contentChanged() {

        if (getPopup() != null) {
            SwingUtilities.updateComponentTreeUI(getPopup());
        }

        super.contentChanged();
    }

//    public ArrayList<BufferedPainter> getAllChildBufferedPainters() {
//        ArrayList<BufferedPainter> result = new ArrayList<BufferedPainter>();
//
//        ArrayList<Container> containersCurrent = new ArrayList<Container>();
//        ArrayList<Container> containersNext = new ArrayList<Container>();
//
//
//        containersCurrent.add(this);
//
//        while (!containersCurrent.isEmpty()) {
//
//            for (Container p : containersCurrent) {
//                for (Component c : p.getComponents()) {
//                    if (c instanceof BufferedPainter) {
//                        BufferedPainter b = (BufferedPainter) c;
//                        result.add(b);
//                    }
//                    if (c instanceof Container) {
//                        containersNext.add((Container) c);
//                    }
//                }
//            }
//
//            containersCurrent = containersNext;
//            containersNext = new ArrayList<Container>();
//        }
//
//        return result;
//    }
    /**
     * Returns the content provider of that is associated with the window.
     *
     * @return the content provider of that is associated with the window
     */
    public WindowContentProvider getContentProvider() {
        return contentProvider;
    }

    /**
     * Defines the height of the window. This method is mainly used for animated
     * resize effect.
     *
     * @param height the height that is to be set
     */
    void resizeHeight(int height) {
        getTitleBar().setMaximumSize(new Dimension(Integer.MAX_VALUE,
                getTitleBar().getSize().height));

        setPreferredSize(
                new Dimension(getWidth(), height));

        revalidate();
    }

    /**
     * Minimizes this window.
     */
    public void minimize() {
        int minHeight = titleBar.getHeight()
                + getInsets().top + getInsets().bottom;

        setPreviousSize(getPreferredSize());

        Animation resizeAnimation
                = new MinimizeAnimation(this, getHeight(),
                        minHeight);
        resizeAnimation.setDuration(0.05 / 100.0 * this.getHeight());
        this.getMainCanvas().getAnimationManager().
                addAnimation(resizeAnimation);

        fireAction(new ActionEvent(this, 0, MINIMIZE_ACTION));
    }

    /**
     * Maximizes this window.
     */
    public void maximize() {
        maximize(null);
    }

    /**
     * Maximizes this window and adds an animation task to the maximize
     * animation.
     *
     * @param t the task to add
     */
    public void maximize(AnimationTask t) {
        if (isMinimized()) {
            Animation resizeAnimation = new MaximizeAnimation(this, getHeight(),
                    getPreviousSize().height);

            if (t != null) {
                resizeAnimation.addFrameListener(t);
            }

            resizeAnimation.addFrameListener(new AnimationTask() {

                @Override
                public void firstFrameStarted() {
                    //
                }

                @Override
                public void frameStarted(double time) {
                    //
                }

                @Override
                public void lastFrameStarted() {
                    fireAction(new ActionEvent(this, 0, MAXIMIZE_ACTION));
                }
            });

            resizeAnimation.setDuration(0.05 / 100.0 * getPreviousSize().height);
            getMainCanvas().getAnimationManager().addAnimation(resizeAnimation);

            //        setPreferredSize(new Dimension(
//                getPreviousSize().width, this.getHeight()));
//        setPreferredSize(new Dimension(
//                getSize().width, this.getHeight()));
            revalidate();
        } else {
            if (t != null) {
                t.lastFrameStarted();
            }
        }
    }

    /**
     * Fades out this window.
     *
     * @param duration the duration of the fade out effect
     */
    public void fadeOut(double duration) {
        fireAction(new ActionEvent(this, 0, HIDE_ACTION));
        fadeEffect.setFadeIn(false);
        getEffectManager().startEffect(fadeEffect.getName(), duration);
    }

    @Override
    public void setVisible(boolean value) {
        super.setVisible(value);
        this.contentChanged();

        // 25.07.2011
        // must be fired from different thread.
        // otherwise dialog / foxtrott related dialog freezes occur.
        // must be the code that waits for input
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                fireAction(new ActionEvent(this, 0, VISIBLE_ACTION));
            }
        });

        t.start();
    }

    /**
     * Hides the window using the fade out effect.
     */
    public void hideWindow() {
        fadeOut(getStyle().getBaseValues().getDouble(FADE_OUT_DURATION_KEY));

        if (getMainCanvas().isDisableEffects()) {
            fadeEffect.setTransparency(0.f);
            setVisible(false);
            moveToBack();
        }
    }

    /**
     * Moves this window to the back.
     */
    public void moveToBack() {
        getMainCanvas().setObjectZOrder(this, 0);
    }

    /**
     * Moves this window to the front.
     */
    public void moveToFront() {
        getMainCanvas().setObjectZOrder(this,
                getMainCanvas().getComponentCount() - 1);
    }

    /**
     * Fades in this window.
     *
     * @param duration the duration of the fade in effect
     */
    public void fadeIn(double duration) {
        fireAction(new ActionEvent(this, 0, SHOW_ACTION));
        fadeEffect.setFadeIn(true);
        getEffectManager().startEffect(
                fadeEffect.getName(), duration);
    }

    /**
     * Shows this window using the fade in effect.
     */
    public void showWindow() {
        getMainCanvas().getWindows().setActive(this);
        setActive(false);
        fadeIn(getStyle().getBaseValues().getDouble(FADE_IN_DURATION_KEY));
    }

    /**
     * Returns the transparency of this window.
     *
     * @return the transparency of this window
     */
    public double getTransparency() {
        return fadeEffect.getTransparency();
    }

    /**
     * Dispose additional resources, e.g., Java 3D render threads.
     */
    @Override
    public void dispose() {
        try {
//            System.out.println(">> Dispose: \"" + getTitle() + "\"");

            if (isSelected()) {
                getMainCanvas().getClipBoard().remove(this);
            }

            for (CapabilityChangedListener l : capabilityChangedListeners) {
                if (l != null) {
                    getMainCanvas().getCapabilityManager().
                            removeCapabilityChangedListener(l);
                }
            }

            if (getPopup() != null) {
                getPopup().setVisible(false);
                popup = null;
            }
        } catch (Exception ex) {
            //
        } finally {
            super.dispose();
        }
    }

    /**
     * Closes this window.
     */
    public void close() {
        VSwingUtil.invokeLater(new Runnable() {

            @Override
            public void run() {
                closing = true;
                added = false;

                fireAction(new ActionEvent(this, 0, CanvasWindow.CLOSE_ACTION));
                getMainCanvas().getWindows().removeObject(getID());
            }
        });
    }

//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
    /**
     * Adds a change listener to this window.
     *
     * @param l the listener to add
     * @return <code>true</code> (as specified by {@link Collection#add})
     */
    public boolean addActionListener(CanvasActionListener l) {
        return actionListeners.add(l);
    }

    /**
     * Removes a change listener from this window.
     *
     * @param l the listener to remove
     * @return <code>true</code> (as specified by {@link Collection#remove})
     */
    public boolean removeActionListener(CanvasActionListener l) {
        return actionListeners.remove(l);
    }

    /**
     * Returns all connectors of this window.
     *
     * @return a list containing all connectors of this window
     */
    public Collection<Connector> getConnectors() {
        ArrayList<Connector> result = new ArrayList<Connector>();

        for (Component c : VSwingUtil.getAllChildren(this, Connector.class)) {
            result.add((Connector) c);
        }

        return result;
    }

    /**
     * Defines the location of this window. This method works like
     * {@link javax.swing.JComponent#setLocation(int, int) }. Additionally, it
     * ensures that this window is always inside the canvas (it either requests
     * the canvas to resize or truncate the coordinates). This method converts
     * window coordinates to the correct coordinates of the Swing component.
     * That is, insets will be handled automatically.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setWindowLocation(int x, int y) {

        if (location == null) {
            location = new Point(x, y);
        } else {
            location.x = x;
            location.y = y;
        }

        // ensure that component is always completely inside of the
        // canvas
        x = Math.max(0, x);
        y = Math.max(0, y);

        if (x + getWidth() > getMainCanvas().getWidth()) {
            getMainCanvas().doLayout();
        }

        if (y + getHeight() > getMainCanvas().getHeight()) {
            getMainCanvas().doLayout();
        }

        super.setLocation(x - getInsets().left, y - getInsets().top);

        setMoved(true);
    }

    /**
     * Returns the canvas position of this window. This method works like
     * {@link javax.swing.JComponent#getLocation() }. However, it uses the
     * coordinates defined by {@link CanvasWindow#setWindowLocation(int, int) }
     * even if these coordinates are outside the canvas. This makes it easier to
     * remember window position relative to other windows (usefull for moving
     * window groups). This method converts window coordinates to the correct
     * coordinates of the Swing component. That is, insets will be handled
     * automatically.
     *
     * @return the canvas position of this window
     */
    public Point getWindowLocation() {

        Point result = null;

        if (location != null) {
            result = location;
        } else {

            result = getLocation();

            result.x += getInsets().left;
            result.y += getInsets().top;
        }

        return result;
    }

    /**
     * Resets the window location defined by
     * {@link CanvasWindow#setWindowLocation(int, int) }.
     */
    public void resetWindowLocation() {
        location = null;
    }

    /**
     * Returns the bounding rectangle of this window. This method converts
     * window coordinates to the correct coordinates of the Swing component.
     * That is, insets will be handled automatically.
     *
     * @return the bounding rectangle of this window
     */
    public Rectangle getWindowBounds() {
        Rectangle result = getBounds();

        result.x += getInsets().left;
        result.y += getInsets().top;

        result.width -= (getInsets().right + getInsets().left);
        result.height -= (getInsets().bottom + getInsets().top);

        return result;
    }

//    /**
//     * Indicates whether to hide the title bar if the mouse cursor leaves
//     * this window.
//     * @return the state
//     */
//    public boolean isHideTitleBarOnExit() {
//        return hideTitleBarOnExit;
//    }
//
//    /**
//     * Defines whether to hide the title bar if the mouse cursor leaves
//     * this window.
//     * @param hideTitleBarOnExit the state to set
//     */
//    public void setHideTitleBarOnExit(boolean hideTitleBarOnExit) {
//        this.hideTitleBarOnExit = hideTitleBarOnExit;
//    }
    /**
     * @return the selectable
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * @param selectable the selectable to set
     */
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * @return the movable
     */
    public boolean isMovable() {
        return movable;
    }

    /**
     * @param movable the movable to set
     */
    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    /**
     * @return the menuEnabled
     */
    public boolean isMenuEnabled() {
        return menuEnabled;
    }

    /**
     * @param menuEnabled the menuEnabled to set
     */
    public void setMenuEnabled(boolean menuEnabled) {
        this.menuEnabled = menuEnabled;
    }

    /**
     * @return the resizable
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * @param resizable the resizable to set
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    /**
     * @return the activatable
     */
    public boolean isActivatable() {
        return activatable;
    }

    /**
     * @param activatable the activatable to set
     */
    public void setActivatable(boolean activatable) {
        this.activatable = activatable;
    }

    /**
     * @return the closing
     */
    public boolean isClosing() {
        return closing;
    }

    /**
     * @return the added
     */
    public boolean isAdded() {
        return added;
    }

    /**
     * @return the draggingEnabled
     */
    public boolean isDraggingEnabled() {
        return groupDraggingEnabled;
    }

    /**
     * @param draggingEnabled the draggingEnabled to set
     */
    public void setDraggingEnabled(boolean draggingEnabled) {
        this.groupDraggingEnabled = draggingEnabled;
    }

    /**
     * @return the groupDraggingScale
     */
    public double getGroupDraggingScale() {
        return groupDraggingScale;
    }

    /**
     * @param groupDraggingScale the groupDraggingScale to set
     */
    public void setGroupDraggingScale(double groupDraggingScale) {
        this.groupDraggingScale = groupDraggingScale;
    }

    /**
     * @param groupDraggingScale the groupDraggingScale to set
     */
    public void setGroupDraggingOffset(double xOffset, double yOffset) {
        this.groupDraggingOffset = new Point2D.Double(xOffset, yOffset);
    }

    /**
     * @return the groupDraggingOffset
     */
    public Point2D.Double getGroupDraggingOffset() {
        return groupDraggingOffset;
    }

    /**
     * @return the groupDraggingTransform
     */
    public AffineTransform getGroupDraggingTransform() {

        System.out.println("TRANSFORM: " + groupDraggingTransform.getScaleX());

        return groupDraggingTransform;
    }

    /**
     * @param askIfClose the askIfClose to set
     */
    public void setAskIfClose(boolean askIfClose) {
        this.askIfClose = askIfClose;
    }

    /**
     * @return the askIfClose
     */
    protected boolean isAskIfClose() {
        return askIfClose;
    }
}

/**
 * Minimize animation.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class MinimizeAnimation extends Animation implements FrameListener {

    private static final long serialVersionUID = -4870347782064401539L;
    private CanvasWindow component;
    private LinearInterpolation resizeY;

    /**
     * Constructor
     *
     * @param component the window to animate
     * @param start the start balue
     * @param stop the stop value
     */
    public MinimizeAnimation(CanvasWindow component, int start, int stop) {
        this.component = component;
        addFrameListener(this);

        resizeY = new LinearInterpolation(start, stop);

        getInterpolators().add(resizeY);

    }

    @Override
    public void frameStarted(double time) {

        if (getTime() == 0) {
            for (Component c : component.getComponents()) {
                if (!(c instanceof CanvasWindowTitleBar)
                        && c != component.getBackSide()) {
                    c.setVisible(false);
                }
            }
        }

        int y = (int) resizeY.getValue();
        component.resizeHeight(y);
        component.setResizing(true);
        component.setMinimizationValue(getTime());

        if (getTime() == 1.0) {
            component.setMinimized(true);
            component.setResizing(false);
        }
    }
}

/**
 * Maximize animation.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class MaximizeAnimation extends Animation implements FrameListener {

    private static final long serialVersionUID = -525293624807445622L;
    private CanvasWindow component;
    private LinearInterpolation resizeY;

    /**
     * Constructor
     *
     * @param component the window to animate
     * @param start the start balue
     * @param stop the stop value
     */
    public MaximizeAnimation(CanvasWindow component, int start, int stop) {
        this.component = component;
        addFrameListener(this);

        resizeY = new LinearInterpolation(start, stop);

        getInterpolators().add(resizeY);

    }

    @Override
    public void frameStarted(double time) {

        int y = (int) resizeY.getValue();
        component.resizeHeight(y);

        component.setResizing(true);
        component.setMinimizationValue(getTime());

        if (getTime() == 1.0) {

            if (component.isFrontVisible()) {
                for (Component c : component.getComponents()) {
                    if (!(c instanceof CanvasWindowTitleBar)) {
                        c.setVisible(true);
                    }
                }
            }

            component.setResizing(false);
            component.setMinimized(false);

            // necessary to make windows resizable again after animation
            component.setPreferredSize(null);
            component.setMinimumSize(null);
            component.setMaximumSize(null);
            component.revalidate();
        }
    }
}

/**
 * Contains mouse gesture specific methods
 */
class MouseControl implements MouseListener, MouseMotionListener,
        Serializable {

    private CanvasWindow parent;
    private Point initMousePos;

    public MouseControl(CanvasWindow parent) {
        this.parent = parent;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1
                && e.getClickCount() == 2
                && parent.isResizable()) {

            ArrayList<CanvasWindow> windows = new ArrayList<CanvasWindow>();

            if (!parent.isSelected()) {
                windows.add(parent);
            } else {
                for (Selectable s : parent.getMainCanvas().getClipBoard()) {
                    if (s instanceof CanvasWindow) {
                        windows.add((CanvasWindow) s);
                    }
                }
            }

            for (CanvasWindow w : windows) {
                if (parent.isMinimized()) {
                    w.maximize();
                } else {
                    w.minimize();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        initMousePos = mouseEvent.getPoint();

        if (parent instanceof CanvasWindow && parent.isActivatable()) {
            CanvasWindow c = parent;
            parent.getMainCanvas().getWindows().setActive(c);

//            parent.getMainCanvas().repaint();
//            System.out.println("Repainting Canvas!");
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

        ArrayList<CanvasWindow> windows = new ArrayList<CanvasWindow>();

        if (!parent.isSelected()) {
            windows.add(parent);
        } else {
            for (Selectable s : parent.getMainCanvas().getClipBoard()) {
                if (s instanceof CanvasWindow) {
                    windows.add((CanvasWindow) s);
                }
            }
        }

        for (CanvasWindow w : windows) {
            w.setMoved(false);
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        // sometimes swing has bugs with mouseover events
        // to prevent the case of having more than one selected
        // connector, we first unselect all
//            parentCanvasChild.unselectConnectors();
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        // nothing to do
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

        if (parent.getMainCanvas().getCapabilityManager().
                isCapable(CanvasCapabilities.ALLOW_DRAG) && parent.isMovable()) {

            ArrayList<CanvasWindow> windows = new ArrayList<CanvasWindow>();

            if (!parent.isSelected()) {
                windows.add(parent);
            } else {
                for (Selectable s : parent.getMainCanvas().getClipBoard()) {
                    if (s instanceof CanvasWindow) {
                        windows.add((CanvasWindow) s);
                    }
                }
            }

            int moveX = (int) (mouseEvent.getX() - initMousePos.getX());
            int moveY = (int) (mouseEvent.getY() - initMousePos.getY());

            if (parent.getWindowLocation().x + moveX < 0) {
                moveX = -parent.getWindowLocation().x;
            }

            if (parent.getWindowLocation().y + moveY < 0) {
                moveY = -parent.getWindowLocation().y;
            }

            for (CanvasWindow w : windows) {

                // compute new component location
                int x = w.getWindowLocation().x + moveX;
                int y = w.getWindowLocation().y + moveY;

                // finally set the location
                w.setWindowLocation(x, y);
            }

            Component child = null;

//            for(CanvasWindow w : parent.getMainCanvas().getWindows()) {
//                child = 
//                        parent.getMainCanvas().getComponentAt(mouseEvent.getPoint());
//                
//                if (child!=null) {
//                    break;
//                }
//            }
            // convert to canvas coordinates
            Point locationOnCanvas = SwingUtilities.convertPoint(
                    mouseEvent.getComponent(),
                    mouseEvent.getPoint(), parent.getMainCanvas());

            Point locationOnWindow = SwingUtilities.convertPoint(
                    mouseEvent.getComponent(),
                    mouseEvent.getPoint(), parent);

            //System.out.println("POINT: " + locationOnCanvas);
//            child =
//                    parent.getMainCanvas().
//                    getComponentAt(locationOnCanvas);
            Area parentArea = new Area(parent.getBounds());
//            parentArea.transform(parent.getGroupDraggingTransform());

            Rectangle parentBounds = parentArea.getBounds();

            for (CanvasWindow w : parent.getMainCanvas().getWindows()) {

                if (w.getBounds().intersects(parentBounds)
                        && w instanceof VWindowGroup && parent != w) {
                    child = w;
                    break;
                }
            }

            if (child instanceof VWindowGroup) {

                double offsetX = -locationOnWindow.getX();
                double offsetY = -locationOnWindow.getY();

                parent.setGroupDraggingOffset(offsetX, offsetY);

                System.out.println("DRAGGING TO GROUP");

                Area a1 = new Area(parent.getBounds());
                Area a2 = new Area(child.getBounds());

//                a1.transform(parent.getGroupDraggingTransform());
                // TODO use scaled and translated instance of a1 for intersection
                a1.intersect(a2);

//                AffineTransform at;
//                try {
//                    at = parent.getGroupDraggingTransform().createInverse();
//                    a1.transform(at);
//                } catch (NoninvertibleTransformException ex) {
//                    Logger.getLogger(MouseControl.class.getName()).log(Level.SEVERE, null, ex);
//                }
                double distX = a1.getBounds().getWidth();
                double distY = a1.getBounds().getHeight();

//                double dist = Math.sqrt(distX*distX+distY*distY);
//                int dist = Math.min(distX,distY);
                boolean widthIsSmaller = distX < distY;

                double scale = 1.f;

                if (widthIsSmaller) {
                    System.out.println(" --> X");
                    double width = Math.min(parent.getBounds().getWidth(),
                            child.getBounds().getWidth());

                    scale = 1f - distX / width;  //Math.abs(width-distX);

                } else {
                    System.out.println(" --> Y");
                    double height = Math.min(parent.getBounds().getHeight(),
                            child.getBounds().getHeight());

                    scale = 1f - distY / height;//Math.abs(height-distY);
                }

                System.out.println(" --> scale: " + scale);

                double minScale = 0.5;

                if (parent.getMainCanvas().getWidth() > parent.getMainCanvas().getHeight()) {
                    minScale = child.getBounds().getWidth()
                            / parent.getMainCanvas().getBounds().getWidth();
                } else {
                    minScale = child.getBounds().getHeight()
                            / parent.getMainCanvas().getBounds().getHeight();
                }

                scale = Math.min(1.0, scale);
                scale = Math.max(scale, minScale);

                parent.setDraggingEnabled(true);
                parent.setGroupDraggingScale(scale);
            } else {
                parent.setDraggingEnabled(false);
                parent.setGroupDraggingScale(1.f);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        // nothing to do
    }
}

class WindowBackgroundPainter implements Painter, ShadowPainter, BufferedPainter {

    transient private BufferedImage buffer;
    private VComponent parent;

    @Override
    public void paint(Graphics g, Style s, Dimension d) {
        //        System.out.println("PAINT: " + getTitle());
        if (buffer == null || buffer.getWidth() != d.width
                || buffer.getHeight() != d.height) {

//                int top = getInsets().top;
//                int bottom = getInsets().bottom;
//                int left = getInsets().left;
//                int right = getInsets().right;
            int top = 0;
            int bottom = 0;
            int left = 0;
            int right = 0;

            buffer
                    = ImageUtils.createCompatibleImage(d.width, d.height);

            Graphics2D g2 = buffer.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Composite original = g2.getComposite();

            AlphaComposite ac1
                    = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            s.getBaseValues().getFloat(
                                    CanvasWindow.TRANSPARENCY_KEY));
            g2.setComposite(ac1);

            GradientPaint paint = new GradientPaint(0, 0,
                    s.getBaseValues().getColor(
                            CanvasWindow.UPPER_BACKGROUND_COLOR_KEY),
                    0, d.height,
                    s.getBaseValues().getColor(
                            CanvasWindow.LOWER_BACKGROUND_COLOR_KEY),
                    false); // true means to repeat pattern
            g2.setPaint(paint);

            //g2.setColor(getBackground());
            Shape sh = new RoundRectangle2D.Double(left + 0, top + 0,
                    d.width - right - left - 1,
                    d.height - bottom - top - 1, 20, 20);

            g2.fill(sh);

            Stroke oldStroke = g2.getStroke();

            BasicStroke stroke
                    = new BasicStroke(s.getBaseValues().
                            getFloat(CanvasWindow.BORDER_THICKNESS_KEY));

            g2.setStroke(stroke);
            g2.setColor(s.getBaseValues().getColor(
                    CanvasWindow.BORDER_COLOR_KEY));

            sh = new RoundRectangle2D.Double(left + 0, top + 0,
                    d.width - right - left - 1,
                    d.height - bottom - top - 1, 19.5, 19.5);

            g2.draw(sh);

            g2.dispose();

            buffer.flush();
        }
        g.drawImage(buffer, 0, 0, null);
    }

    @Override
    public Shape getShape() {

        int top = parent.getInsets().top;
        int bottom = parent.getInsets().bottom;
        int left = parent.getInsets().left;
        int right = parent.getInsets().right;

        return new RoundRectangle2D.Double(left + 0, top + 0,
                parent.getWidth() - right - left,//(*)
                parent.getHeight() - bottom - top - 1, 20, 20);
    }

    @Override
    public void contentChanged() {
        buffer = null;
    }

    private void setParent(VComponent parent) {
        if (parent == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" is not supported!");
        }

        if (!CanvasWindow.class.isAssignableFrom(parent.getClass())) {
            throw new IllegalArgumentException(
                    "Only \"CanvasWindow\" is supported as parent!");
        }
        this.parent = (CanvasWindow) parent;
    }

    @Override
    public Painter newInstance(VComponent parent) {
        WindowBackgroundPainter result = new WindowBackgroundPainter();

        result.setParent(parent);

        return result;
    }
}
