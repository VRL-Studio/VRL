/* 
 * Canvas.java
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
package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.AnimationManager;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.io.ConfigurationFile;
import eu.mihosoft.vrl.io.vrlx.SessionInitializer;
import eu.mihosoft.vrl.reflection.ComponentManagement;
import eu.mihosoft.vrl.reflection.SessionInitializerGroup;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VRL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Canvas is the toplevel container class. All visual elements use Canvas as
 * drawing device. As it is a Java Bean it can easily be used from within the
 * Swing designer that comes with the Netbeans Plattform.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Canvas extends JPanel
        implements
        //        MouseWheelListener,
        Externalizable,
        ContainerListener,
        ComponentListener {

    private static final long serialVersionUID = -6636791744395958456L;
    /**
     * the currently used Transferable object, i.e. the object that is dragged
     * around to establish a new connection
     */
    private GlobalForegroundPainter actualDraggable;
    private boolean initialized;
    /**
     * a list containing all objects that want to draw on the canvas's
     * foreground
     */
    private ArrayList<GlobalForegroundPainter> globalForegroundPainters =
            new ArrayList<GlobalForegroundPainter>();
    /**
     * a list containing all objects that want to draw on the canvas's
     * background
     */
    private ArrayList<GlobalBackgroundPainter> globalBackgroundPainters =
            new ArrayList<GlobalBackgroundPainter>();
    /**
     * a list containing all connection
     */
    private Connections dataConnections;
    /**
     * a list conaining all instances of CanvasWindow that belong to the canvas
     */
    private CanvasWindows canvasWindows;
    /**
     * defines the style of the canvas
     */
    private Style style;
    /**
     * defines the background grid of the canvas
     */
    private CanvasGrid grid;
    /**
     * defines the updateType of the canvas's connectors
     */
    private DataProcessingMode dataProcessingMethod =
            DataProcessingMode.UPDATE_ON;
    /**
     *
     */
//    transient private RepaintThread repainter;
    /**
     *
     */
    private AnimationManager animationManager;
    /**
     *
     */
    private EffectPane effectPane;
    /**
     *
     */
    private MessageBox messageBox;
    /**
     *
     */
    private Dock dock;
    /**
     *
     */
    transient private BufferedImage buffer;
    /**
     *
     */
    private EventRecorder eventRecorder;
    /**
     *
     */
    private Clipboard clipBoard = new Clipboard();
    /**
     *
     */
    private boolean disableEffects = false;
    /**
     *
     */
    CanvasAWTListener globalEventListener;
    /**
     *
     */
    private BackgroundImage backgroundImage;
    /**
     *
     */
//    private EffectManager effectManager;
    /**
     *
     */
//    private BlurEffect blurEffect;
    /**
     * Defines the canvas mode.
     */
    private CapabilityManager capabilityManager = new CanvasCapabilityManager();
    /**
     * Canvas mouse control.
     */
    private CanvasMouseControl mouseControl;
    /**
     * Canvas style evaluator.
     */
    private CanvasStyleManager styleManager;
    /**
     *
     */
    private boolean fullScreenMode = false;
    /**
     *
     */
    private ArrayList<Boolean> visibilityStatesBeforeFullscreen =
            new ArrayList<Boolean>();
    /**
     *
     */
    private WindowGroups windowGroups;
    /**
     *
     */
    private WindowGroupMenuController windowGroupController;
    /**
     *
     */
    private PreviousWindowGroupApplet previousWindowGroupApplet;
    /**
     *
     */
    private NextWindowGroupApplet nextWindowGroupApplet;
    /**
     *
     */
    private MessageBoxApplet messageBoxApplet;
    /**
     *
     */
    private boolean presentationMode;
    /**
     *
     */
    private MemoryUsageDisplay memoryUsageDisplay;
    /**
     *
     */
    private AttributionDisplay attributionDisplay;
    /**
     *
     */
    private SessionInitializer sessionInitializer;
    /**
     *
     */
    private SessionInitializerGroup sessionInitializers;
    /**
     * indicates whether a session is currently saving or not
     */
    private boolean savingSession;
    /**
     * indicates whether a session is currently loading or not
     */
    private boolean loadingSession = false;
    /**
     *
     */
    private String sessionFileName;
    /**
     *
     */
    private Connections controlFlowConnections;
    /**
     *
     */
    private VMenuController windowMenuController;
    /**
     *
     */
    private VMenuController canvasMenuController;
    /**
     *
     */
    private ActionDelegator actionDelegator;
    /**
     *
     */
    private VMenuController styleMenuController;
    /**
     *
     */
    public static final String BACKGROUND_COLOR_KEY = "Canvas:Background:Color";
    /**
     *
     */
    public static final String TEXT_COLOR_KEY = "Canvas:Text:Color";
    /**
     *
     */
    public static final String CARET_COLOR_KEY = "Canvas:Caret:Color";
    /**
     *
     */
    public static final String TEXT_SELECTION_COLOR_KEY = "Canvas:Text:Selection:Color";
    /**
     *
     */
    public static final String SELECTED_TEXT_COLOR_KEY = "Canvas:Text:Selected:Color";
    /**
     *
     */
    private static HashSet<WeakReference<PaintListener>> paintListeners =
            new HashSet<WeakReference<PaintListener>>();
    private RepaintVisualizer repaintVisualizer = new RepaintVisualizer();
    private MouseEvent lastMouseEvent;
    private boolean useCaptureBuffer;
    private boolean ignoreInputEvents;
    private boolean disposed;
    private boolean active;
    private boolean ignoreMessages = false;
    private static ArrayList<Disposable> disposables =
            new ArrayList<Disposable>();
    
    private boolean autoScrollEnabled = true;
    private int autoScrollSensitiveBorderSize = 50;
    
    
//    private JTextArea backgroundLogView;
//    private LoggingController loggingController;

    /**
     * Constructor.
     */
    public Canvas() {
        init(false);
    }

    /**
     * Creates an new instance of Canvas.
     */
    public Canvas(boolean nestedCanvas) {
        init(nestedCanvas);
    }

    public void addDisposable(Disposable d) {
        disposables.add(d);
    }

    private void disposeDisposables() {
        for (Disposable d : disposables) {
            try {
                d.dispose();
            } catch (Throwable tr) {
                tr.printStackTrace(System.err);
            }
        }

        disposables.clear();
    }

    public void addPaintListener(PaintListener l) {
        paintListeners.add(new WeakReference<PaintListener>(l));

        cleanupPaintListenersList();
    }

    public void removePaintListener(PaintListener l) {
        ArrayList<WeakReference<PaintListener>> delList =
                new ArrayList<WeakReference<PaintListener>>();
        for (WeakReference<PaintListener> wR : paintListeners) {
            PaintListener p = wR.get();
            if (p != null && p.equals(l)) {
                delList.add(wR);
            }
        }
        for (WeakReference<PaintListener> wR : delList) {
            paintListeners.remove(wR);
        }
    }

    /**
     * Cleans up the weak ref canvas list.
     */
    private static void cleanupPaintListenersList() {
        ArrayList<WeakReference<PaintListener>> delList =
                new ArrayList<WeakReference<PaintListener>>();

        for (WeakReference<PaintListener> wR : paintListeners) {
            if (wR.get() == null) {
                delList.add(wR);
            }
        }

        for (WeakReference<PaintListener> wR : delList) {
            paintListeners.remove(wR);
        }
    }

    private void notifyPaintListeners(Graphics g) {
        Rectangle r = g.getClipBounds();
        for (WeakReference<PaintListener> wR : paintListeners) {
            PaintListener p = wR.get();

            if (p != null) {
                p.paintEvent(r, getLastMouseEvent());
            }
        }
    }

    private void notifyPaintListeners(Point pos) {
        for (WeakReference<PaintListener> wR : paintListeners) {
            PaintListener p = wR.get();

            if (p != null) {
                p.paintEvent(new Rectangle(pos.x - 10, pos.y - 10, 20, 20), getLastMouseEvent());
            }
        }
    }

    private void init(boolean nestedCanvas) {

//        this.addMouseWheelListener(this);
        this.setMouseControl(new CanvasMouseControl(this));
        this.addContainerListener(this);
        this.setSize(320, 200);
        this.setVisible(true);

//        AnimatedLayout aL = new AnimatedLayout(new CanvasLayout());
//        aL.setDuration(0.5);
//        setLayout(aL);
//        aL.setAnimated(true);

        setLayout(new VLayout(new CanvasLayout()));

        // backgroundImage has to be initialized before style
        backgroundImage = new BackgroundImage(this);

        setStyleManager(new CanvasStyleManager(this));

        setStyle(new Style("Default"), false);

        animationManager = new AnimationManager();

        messageBox = new MessageBox(this);

        dock = new Dock(this);

        nextWindowGroupApplet = new NextWindowGroupApplet(this);
        nextWindowGroupApplet.setVisible(false);
        previousWindowGroupApplet = new PreviousWindowGroupApplet(this);
        previousWindowGroupApplet.setVisible(false);

        getDock().addDockApplet(previousWindowGroupApplet);

        messageBoxApplet = new MessageBoxApplet(messageBox);

        dock.addDockApplet(messageBoxApplet);

        messageBoxApplet.showApplet();

        effectPane = new EffectPane(this);
        this.add(effectPane);

//        effectPane.enablePulseEffect(false);

        grid = new CanvasGrid(this, 50, 50);
        grid.setSize(this.getWidth(), this.getHeight());

        getStyleManager().addStyleChangedListener(grid);

        this.add((GlobalBackgroundPainter) grid);

        this.add((GlobalBackgroundPainter) backgroundImage);

//        this.add((GlobalBackgroundPainter) new SpotPainter(this));

        dataConnections = new Connections(this);
        this.add((GlobalBackgroundPainter) dataConnections);

        controlFlowConnections = new Connections(this);
        add((GlobalBackgroundPainter) controlFlowConnections);

        canvasWindows = new CanvasWindows(this);
        
        
        
        // add logview
//        backgroundLogView = new FullScreenLogView();
//        add(backgroundLogView);
        
        
        // we set opaque true, even if the object is transparent
        // this is because of the custom repaint manager which
        // currently redraws everything, no matter if transparent or not
        // WARNING: this may change in the future
        this.setOpaque(false);

        // set custom repaint manager

        if (!nestedCanvas) {
            RepaintManager.setCurrentManager(new CanvasRepaintManager(this));
        }

//        repainter = new RepaintThread();

        // event recorder
        eventRecorder = new EventRecorder(this);

        // global event listener that does currently control selection shortcuts
        registerGlobalEventListener();


//        effectManager = new EffectManager(this);
//        blurEffect = new BlurEffect(this);
//        colorizeEffect = new SelectionEffect(this);
//        effectManager.getEffects().add(blurEffect);
//
//        blurEffect.setBlurValue(0.1f);
//        colorizeEffect.setColor(new Color(0,0,0,100));

        addComponentListener(this);

        capabilityManager.enableCapabilities(
                CanvasCapabilities.DEFAULT_CAPABILITIES);

        windowGroups = new WindowGroups();
        windowGroupController = new WindowGroupMenuController();
        windowGroupController.setMainCanvas(this);

        getDock().addDockApplet(nextWindowGroupApplet);

        memoryUsageDisplay = new MemoryUsageDisplay(this);

        attributionDisplay = new AttributionDisplay(this);
        add(attributionDisplay);

        // selection rectangle
        SelectionRectangle rect = new SelectionRectangle(this);

        SelectionRectangleController selectionRectangleController =
                new SelectionRectangleController(this, rect);

        add(rect);

        addMouseListener(selectionRectangleController.getMouseListener());
        addMouseMotionListener(
                selectionRectangleController.getMouseMotionListener());

        sessionInitializers = new SessionInitializerGroup();

        windowMenuController = new VMenuController();
        canvasMenuController = new VMenuController();

        actionDelegator = new CanvasActionDelegator(this);

        styleMenuController = new VMenuController();

        setActive(false);

    }

//    public void initBackgroundLog(ConfigurationFile config) {
//        backgroundLogView.setBackground(Color.BLACK);
//        backgroundLogView.setForeground(Color.white);
//        backgroundLogView.setEditable(false);
//        loggingController = new LoggingController(backgroundLogView, config);
//    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean v) {
        setActive(v, true);
    }

    public void setActive(boolean v, boolean visualize) {
        active = v;

        if (visualize) {
            if (v) {
                getEffectPane().stopSpot();
            } else {
                getEffectPane().startSpot();
            }
            repaint();
        }
        setIgnoreInput(!v);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        if (effectPane != null) {
            effectPane.componentResized(null);
        }
    }

    /**
     * Adds a window to this canvas.
     *
     * @param w the window to add
     */
    public void addWindow(final CanvasWindow w) {
//        getWindows().add(w);

        VSwingUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                if (!getWindows().contains(w)) {
                    getWindows().add(w);
                }
            }
        });

//        VSwingUtil.newWaitController().requestWait(new ProceedRequest() {
//
//            @Override
//            public boolean proceed() {
//                return w.isAdded();
//            }
//        });

    }

//    public BlurEffect getBlurEffect(){
//        return blurEffect;
//    }
    /**
     * Adds a new GlobalForegroundPainter object to the canvas.
     *
     * @param p the object that is to be added to the Canvas object
     */
    public void add(GlobalForegroundPainter p) {
        globalForegroundPainters.add(p);
    }

    /**
     * Adds a new GlobalForegroundPainter object to the canvas.
     *
     * @param index the index
     * @param p the object that is to be added to the Canvas object
     */
    public void add(int index, GlobalForegroundPainter p) {
        globalForegroundPainters.add(index, p);
    }

    /**
     * Removes a GlobalForegroundPainter object from the canvas.
     *
     * @param p the object that is to be deleted
     */
    public void remove(GlobalForegroundPainter p) {

        globalForegroundPainters.remove(p);
    }

    /**
     * Adds a new GlobalBackgroundPainter object to the canvas.
     *
     * @param p the object that is to be added to the Canvas object
     */
    public final void add(GlobalBackgroundPainter p) {
        globalBackgroundPainters.add(p);
    }

    /**
     * Adds a new GlobalBackgroundPainter object to the canvas.
     *
     * @param index the index
     * @param p the object that is to be added to the Canvas object
     */
    public void add(int index, GlobalBackgroundPainter p) {

        if (index <= 0) {
            index = 2;
        } else {
            index += 2;
        }

        index = Math.min(globalBackgroundPainters.size(), index);

        globalBackgroundPainters.add(index, p);
    }

    /**
     * Removes a GlobalBackgroundPainter object from the canvas.
     *
     * @param p the object that is to be deleted
     */
    public void remove(GlobalBackgroundPainter p) {
        globalBackgroundPainters.remove(p);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fill(new Rectangle.Double(0, 0, getWidth(), getHeight()));

        grid.setSize(getWidth(), getHeight());

//        getBackgroundImage().generateScaledImage();

        getBackgroundImage().sizeChanged();



        if (!isFullScreenMode()) {
            // perform global background painting
            for (GlobalBackgroundPainter p : this.globalBackgroundPainters) {
                p.paintGlobal(g2);
            }
        }

    }

//    @Override
//    public void paint(Graphics g) {
//        effectManager.paint(this, g);
//    }
//
//    @Override
//    public EffectManager getEffectManager() {
//        return effectManager;
//    }
    @Override
    public void paint(Graphics g) {
        
        if (effectPane != null) {
            effectPane.componentResized(null);
        }

        Graphics2D g2 = null;

        if (isUseCaptureBuffer()) {
            if (buffer == null || buffer.getWidth() != getWidth()
                    || buffer.getHeight() != getWidth()) {
                buffer = ImageUtils.createCompatibleImage(getWidth(), getHeight());
            }

            g2 = buffer.createGraphics();
            g2.setClip(g.getClip());
        } else {
            g2 = (Graphics2D) g;
        }

        super.paint(g2);

        if (!isFullScreenMode()) {
            // perform global foreground painting
            for (GlobalForegroundPainter p : this.globalForegroundPainters) {
                p.paintGlobal(g2);
            }
        }

        Rectangle clip = g.getClipBounds();

        if (isUseCaptureBuffer()) {
            g2.dispose();

            BufferedImage img = buffer.getSubimage(
                    clip.x, clip.y,
                    clip.width, clip.height);

            g.drawImage(img, clip.x, clip.y,
                    clip.width, clip.height, null);
        }

        notifyPaintListeners(g);
    }

    /**
     * Returns the buffer of the current canvas session.
     *
     * @return the snap shot
     */
    public synchronized BufferedImage getBuffer() {
        if (!isUseCaptureBuffer()) {
            throw new IllegalStateException("Capture Buffer disabled!");
        }
        return buffer;
    }

    /**
     * Returns a snap shot of the current canvas session.
     *
     * @return the snap shot
     */
    public synchronized BufferedImage snapShot() {
        return snapShot(getVisibleRect());
    }

    /**
     * Returns a snap shot of a specific region of the canvas session.
     *
     * @param region the region
     * @return the snap shot
     */
    public synchronized BufferedImage snapShot(Rectangle region) {
        if (!isUseCaptureBuffer()) {
            throw new IllegalStateException("Capture Buffer disabled!");
        }
        if (buffer == null) {
            repaint();
        }

        if (region == null) {
            return buffer;
        }

        if (buffer == null) {
            return null;
        }

        BufferedImage img = buffer;

        try {
            img = buffer.getSubimage(
                    region.x, region.y, region.width, region.height);
        } catch (Exception ex) {
            //
        }

        return img;
    }

    /**
     * Returns a screen shot of a specific region of the canvas session. This
     * method behaves like the {@link #snapShot()} methods but is less
     * efficient. It cannot be used for recording video in realtime. On the
     * other hand it does not rely on capture buffer.
     *
     * @param region the region
     * @return the snap shot
     */
    public synchronized BufferedImage screenshot(Rectangle region) {

        BufferedImage img = ImageUtils.createCompatibleImage(region.width, region.height);

        Graphics g2 = img.createGraphics();
        g2.setClip(region);

        paint(g2);

        g2.dispose();

        return img;
    }

    /**
     * Returns a screen shot of a specific region of the canvas session. This
     * method behaves like the {@link #snapShot()} methods but is less
     * efficient. It cannot be used for recording video in realtime. On the
     * other hand it does not rely on capture buffer.
     *
     * @return the snap shot
     */
    public synchronized BufferedImage screenshot() {

        return screenshot(getVisibleRect());
    }

    /**
     * Refreshes this canvas. This method should only be called when loading a
     * file or changing style. It involves the animation manager. Therefore, do
     * not call it frequently! The revalidation will start with a delay of one
     * second and will be repeated three seconds.
     *
     * @see JComponent#revalidate()
     */
    protected void refresh() {
        if (getAnimationManager() != null) {

            class UpdateAnimation extends Animation {
                //
            }

            Animation updateLayoutAnimation = new UpdateAnimation();
            updateLayoutAnimation.setOffset(1);
            updateLayoutAnimation.setDuration(3);
            updateLayoutAnimation.addFrameListener(new FrameListener() {
                @Override
                public void frameStarted(double time) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            revalidate();
                        }
                    });
                }
            });

            getAnimationManager().addUniqueAnimation(updateLayoutAnimation);
        }
    }

    /**
     * Registers the event recorder.
     */
    public void registerEventRecorder() {

        EventQueue queue = new EventQueue() {
            @Override
            protected void dispatchEvent(AWTEvent event) {
                boolean eventIsNotFromEventRecorder =
                        (event instanceof InputEvent)
                        && (!(event instanceof VKeyEvent)
                        || !(event instanceof VMouseEvent));

                if (eventIsNotFromEventRecorder) {
                    eventRecorder.captureEvent((InputEvent) event);
                }

                boolean eventIsFromEventRecorder =
                        (event instanceof VKeyEvent)
                        || (event instanceof VMouseEvent);

                boolean eventIsNoInputEvent =
                        !(event instanceof InputEvent);
//                boolean dockAppletIsParent = eventIsMouseEventOnDock(event);

                if (eventIsFromEventRecorder
                        || !eventRecorder.isPlaying()
                        || eventIsNoInputEvent) {
                    super.dispatchEvent(event);
                }
            }
        };

        Toolkit.getDefaultToolkit().getSystemEventQueue().push(queue);

    }

    /**
     * Registers the global event listener.
     */
    public final void registerGlobalEventListener() {

        globalEventListener = new CanvasAWTListener(this);

        Toolkit.getDefaultToolkit().addAWTEventListener(globalEventListener,
                AWTEvent.MOUSE_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(globalEventListener,
                AWTEvent.MOUSE_MOTION_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(globalEventListener,
                AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(globalEventListener,
                AWTEvent.KEY_EVENT_MASK);

    }

    /**
     * Tries to find a selectable component at specific location.
     *
     * @param c the container to check
     * @param p the location relative to container position
     * @return the selectable component if such a component exists at the
     * specified location; <code>null</code> otherwise
     */
    public Component findSelectableComponentAt(Container c, Point p) {
        Component result = c.findComponentAt(p);

//        System.out.println("S: " + result);

        if (!(result instanceof Selectable)) {
            result = findSelectableParentComponent(result);
        }

        // We check whether result is a typerepresentation that is a child
        // of another type representation, e.g., an element inside
        // ArrayInputType.
        //
        // In this case we return the parent typerepresentation.
        if (result instanceof TypeRepresentationBase) {
            Component newC = findSelectableParentComponent(result);
            if (newC instanceof TypeRepresentationBase) {
                result = newC;
            }
        }

        return result;
    }

    /**
     * Tries to find a selectable parent component.
     *
     * @param c the component to check
     * @return the selectable parent container if such a component exists at the
     * specified location; <code>null</code> otherwise
     */
    public Component findSelectableParentComponent(Component c) {
        Component result = null;
        Container parent = c.getParent();

//        System.out.println("**********BEGIN**********");
        while (parent != null) {
            parent = c.getParent();
            if (parent != null) {
                c = parent;
            }

//            System.out.println(c);
            if (c instanceof Selectable) {
                result = c;

                // We check whether c is a typerepresentation that is a child
                // of another type representation, e.g., an element inside
                // ArrayInputType.
                //
                // In this case we return the parent typerepresentation.
                if (c instanceof TypeRepresentationBase) {
                    Component newC = findSelectableParentComponent(c);
                    if (newC instanceof TypeRepresentationBase) {
                        result = newC;
                    }
                }

                break;
            }
        }

//        System.out.println("**********END**********");

        return result;
    }

    /**
     * Tries to find a selectable child component at specific location.
     *
     * @param c the component to check
     * @param p the location to search
     * @return the selectable child component if such a component exists at the
     * specified location; <code>null</code> otherwise
     */
    public Component findSelectableChildComponentAt(Container c, Point p) {
        // We can't use getComponentAt as this would return the effectPane
        // thats why we iterate through the objects and return the lowest
        // selected child under the effectPane.
        Selectable result = null;
//        Container c = this;
        Container cOld = null;
        int x = p.x;
        int y = p.y;

        // If c does not change inside of the for loop we break, as we won't
        // another result. Thus, we have reached the lowest level
        while (c != cOld) {

            cOld = c;
//            System.out.println("**************");

            for (Component i : c.getComponents()) {

                if (!(i instanceof EffectPane)) {

                    // Compute new coordinates: each component has it's own
                    // coordinate system. Therefore, we have to substract the
                    // position of the current container.
//                    int xTmp = x - i.getX();
//                    int yTmp = y - i.getY();

                    // We check if c has child components that contain point p.
                    // If c is selectable we store a reference to that component
                    // in s.
                    boolean iIsNotEffectPane = !(i instanceof EffectPane);
                    if (i instanceof Container && iIsNotEffectPane) {
                        if (i.contains(x, y)) {
                            c = (Container) i;
                            x -= c.getX();
                            y -= c.getY();
                            if (i instanceof Selectable) {
//                                Selectable sTmp = (Selectable) i;
                                result = (Selectable) i;
                            }

                            break;
                        }
                    }
                }
            }
        }

        return (Component) result;
    }

    /**
     * Determines if a mouse event affects the dock. The purpose is to filter
     * mouse events on the dock from others. This is necessary because if event
     * recorder plays back events it must be possible to interrupt.
     *
     * @param e the event
     * @return <code>true</code> if the event occured on the dock;
     * <code>false</code> otherwise
     */
    public boolean eventIsMouseEventOnDock(AWTEvent e) {
        boolean result = false;

        if (e instanceof MouseEvent) {

            MouseEvent m = (MouseEvent) e;

            if (getDock() != null) {

                Point offset = getDock().getLocation();

                result = getDock().contains(
                        new Point(m.getX() - offset.x, m.getY() - offset.y));
            }
        }

        return result;
    }

//    /**
//     * Mac OS X Hack. Turning wheel during drag operation caused unwanted
//     * dropping under OS X.
//     * @param mouseWheelEvent the mouse wheel event
//     */
//    @Override
//    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
////        System.out.println(">> Wheel gesture: reset actualDraggable!");
//
//        if ((getActualDraggable() != null) && (getActualDraggable() instanceof Transferable)) {
//
//            Transferable t = (Transferable) getActualDraggable();
//
//            t.setLocation(0, 0);
//            t.setDragged(false);
//        }
//    }
    /**
     * Returns the Transferable object that is currently in use.
     *
     * @return the Transferable object that is currently in use
     * @see Transferable
     */
    public GlobalForegroundPainter getActualDraggable() {
        return actualDraggable;
    }

    /**
     * Defines the Transferable object that is currently in use.
     *
     * @param actualDraggable the object that is to be set as currently used
     * Transferable object
     * @see Transferable
     */
    public void setActualDraggable(GlobalForegroundPainter actualDraggable) {
        globalForegroundPainters.remove(this.actualDraggable);
        this.actualDraggable = actualDraggable;

        if (actualDraggable != null) {
            globalForegroundPainters.add(actualDraggable);
        }
    }

    /**
     * This method is called whenever a component has been added.
     *
     * @param e the container event
     */
    @Override
    public void componentAdded(ContainerEvent e) {
//        System.out.println(">> Canvas: ChildObject added!");
//        repaint();
    }

    /**
     * This method is called whenever a component has been removed.
     *
     * @param e the container event
     */
    @Override
    public void componentRemoved(ContainerEvent e) {
//        System.out.println(">> Canvas: ChildObject removed!");
//        repaint();
    }

    /**
     * Returns canvas's Connections object.
     *
     * @return canvas's Connections object
     */
    public Connections getDataConnections() {
        return dataConnections;
    }

    /**
     * Removes all objects and connections from the canvas.
     */
    public void clearCanvas() {
        canvasWindows.removeAll();
    }

    /**
     * Returns the windows of this canvas.
     *
     * @return the windows of this canvas
     */
    public CanvasWindows getWindows() {
        return canvasWindows;
    }

    /**
     * Defines the windows of this canvas.
     *
     * @param windows the windows to set
     */
    protected void setWindows(CanvasWindows windows) {
        this.canvasWindows = windows;
    }

    /**
     * Returns canvas's style object.
     *
     * @return canvas's style object
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Defines the style object.
     *
     * @param style the style object
     * @param threadsafe defines whether to run in threadsafe mode using
     * <code>SwingUtilities.invokeLater()</code>
     */
    public final void setStyle(final Style style, boolean threadsafe) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Style finalStyle = style;

                if (finalStyle == null) {
                    finalStyle = new Style("Default");
                }

                getStyleManager().evaluateStyle(finalStyle);
                Canvas.this.style = finalStyle;

                repaint();

                ArrayList<Component> vComponents =
                        VSwingUtil.getAllChildren(
                        Canvas.this, VComponent.class);

                for (Component c : vComponents) {
                    ((VComponent) c).contentChanged();
                }

                refresh();
            }
        };
        if (threadsafe) {
            SwingUtilities.invokeLater(runnable);
        } else {
            runnable.run();
        }

    }

    /**
     * Defines the style object. This method is threadsafe.
     *
     * @param style the style object
     * @see #setStyle(eu.mihosoft.vrl.visual.CanvasStyle, boolean)
     */
    public void setStyle(final Style style) {
        setStyle(style, true);
    }

    /**
     * Returns the data processiong mode of this canvas.
     *
     * @return the data processiong mode of this canvas
     */
    public DataProcessingMode getDataProcessingMode() {
        return dataProcessingMethod;
    }

    /**
     * Defines the data processing mode of this canvas.
     *
     * @param dataProcessingMethod the data processing mode to set
     */
    public void setDataProcessingMode(
            DataProcessingMode dataProcessingMethod) {
        this.dataProcessingMethod = dataProcessingMethod;
    }

    /**
     * Returns the effect pane of this canvas.
     *
     * @return the effect pane of this canvas
     */
    public EffectPane getEffectPane() {
        return this.effectPane;
    }

    /**
     * Defines the object z order.
     *
     * @param c the component to chnge
     * @param index the new index of the component
     */
    public void setObjectZOrder(Component c, int index) {
        super.setComponentZOrder(c, index);
        super.setComponentZOrder(effectPane, 0);
    }

//    /**
//     * Returns the repaint thread of this canvas.
//     * @return the repaint thread of this canvas
//     */
//    public RepaintThread getRepainter() {
//        return repainter;
//    }
//
//    /**
//     * Defines the repaint thread of this canvas.
//     * @param repainter the repaint thread of this canvas
//     */
//    public void setRepainter(RepaintThread repainter) {
//        this.repainter = repainter;
//    }
    /**
     * Returns the animation manager of this canvas.
     *
     * @return the animation manager of this canvas
     */
    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    /**
     * Returns the message box of this canvas.
     *
     * @return the message box of this canvas
     */
    public MessageBox getMessageBox() {
        return messageBox;
    }

    /**
     * Returns position relative to main canvas's upper left corner.
     *
     * @param c the canvas child
     * @param considerConnectorPos defines whether to treat connectors
     * differently (using connectors getAbsPos() method)
     * @return position relative to main canvas's upper left corner
     */
    public Point getAbsPos(JComponent c, boolean considerConnectorPos) {

        if (c instanceof Connector && considerConnectorPos) {
            Connector connector = (Connector) c;
            return connector.getAbsPos();
        }

        Component p = null;

        if (c instanceof Component) {
            p = (Component) c;
        } else {
            return null;
        }

        Point location = p.getLocation();

        p = p.getParent();

        while (p != null && !(p instanceof Canvas)) {
            location.x += p.getLocation().x;
            location.y += p.getLocation().y;

            p = p.getParent();

            if (p == null) {
                return null;
            }
        }

        return location;
    }

    /**
     * Returns the canvas location relative to the top level parent.
     *
     * @return the canvas location relative to the top level parent
     */
    public Point getAbsCanvasPos() {
        // find out location relative to top level parent of mainCanvas
        Component c = this;
        Component parent = this.getParent();

        Point cPos = c.getLocation();

        while (parent != null) {
            parent = c.getParent();
            if (parent != null) {
                c = parent;
                cPos.x += c.getX();
                cPos.y += c.getY();
            }
        }

        return cPos;
    }

    /**
     * Returns the top level parent of this canvas. Usually this is a JFrame
     * window.
     *
     * @return the top level parent of this canvas
     */
    public Container getTopLevelParent() {
        // find out top level parent of mainCanvas
        Container c = this;
        Container parent = c.getParent();

        while (parent != null) {
            parent = c.getParent();
            if (parent != null) {
                c = parent;
            }
        }

        return c;
    }

    /**
     * Returns the dock of this canvas.
     *
     * @return the dock of this canvas
     */
    public final Dock getDock() {
        return dock;
    }

    /**
     * Canvas is not serializable. Thus this method throws
     * <code>NotSerializableException</code>.
     *
     * @param out
     * @throws java.io.IOException
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        throw new NotSerializableException("Binary session serialization is "
                + "evil! Canvas is not serializable!");
    }

    /**
     * Canvas is not serializable. Thus this method throws
     * <code>NotSerializableException</code>.
     *
     * @param arg0
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    @Override
    public void readExternal(ObjectInput arg0) throws IOException,
            ClassNotFoundException {
        throw new NotSerializableException("Binary session serialization is "
                + "evil! Canvas is not serializable!");
    }

    /**
     * Returns the event recorder of this canvas.
     *
     * @return the event recorder of this canvas
     */
    public EventRecorder getEventRecorder() {
        return eventRecorder;
    }

    /**
     * Defines the event recorder of this canvas.
     *
     * @param eventRecorder the event recorder to set
     */
    void setEventRecorder(EventRecorder eventRecorder) {
        this.eventRecorder = eventRecorder;
    }

    /**
     * Indicates whether effects are disabled.
     *
     * @return <code>true</code> if effects are disabled; <code>false</code>
     * otherwise
     */
    public boolean isDisableEffects() {
        return disableEffects;
    }

    /**
     * Defines whether effects are to be disabled.
     *
     * @param disableEffects the state to set
     */
    public void setDisableEffects(boolean disableEffects) {
        this.disableEffects = disableEffects;
    }

    /**
     * Returns the clip board of this canvas.
     *
     * @return the clip board of this canvas
     */
    public Clipboard getClipBoard() {
        return clipBoard;
    }

    /**
     * Returns the background image of this canvas.
     *
     * @return the backgroundImage of this canvas
     */
    public BackgroundImage getBackgroundImage() {
        return backgroundImage;
    }

    /**
     * Defines the background image of this canvas.
     *
     * @param backgroundImage the background image to set
     */
    public void setBackgroundImage(BackgroundImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    public void componentResized(ComponentEvent e) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        effectPane.componentResized(e);
    }

    @Override
    public void componentShown(ComponentEvent e) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void componentHidden(ComponentEvent e) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Disposes additional resources, e.g., Java 3D render threads.
     */
    public void dispose() {

        disposeDisposables();

        // dispose can only be performed once
        if (disposed) {
            return;
        }

        try {
            setIgnoreInput(false);

            canvasWindows.dispose();
            styleManager.dispose();

            for (GlobalForegroundPainter gF : globalForegroundPainters) {
                if (gF instanceof Disposable) {
                    ((Disposable) gF).dispose();
                }
            }

            for (GlobalBackgroundPainter gB : globalBackgroundPainters) {
                if (gB instanceof Disposable) {
                    ((Disposable) gB).dispose();
                }
            }

            if (globalEventListener != null) {
                Toolkit.getDefaultToolkit().removeAWTEventListener(
                        globalEventListener);
            }
        } finally {
            disposed = true;
        }

    }

    @Override
    protected void finalize() throws Throwable {

        System.err.println(getClass().getName() + ": Finalize!!!");

        super.finalize();
    }

    /**
     * @return the capabilityManager
     */
    public CapabilityManager getCapabilityManager() {
        return capabilityManager;
    }

    /**
     * Returns the mouse control of this canvas.
     *
     * @return the mouse control
     */
    public CanvasMouseControl getMouseControl() {
        return mouseControl;
    }

    /**
     * Defines and registers the mousecontrol of this canvas.
     *
     * @param mouseControl the mouse control to set
     */
    public final void setMouseControl(CanvasMouseControl mouseControl) {

        // removes old listenr
        if (this.mouseControl != null) {
            this.removeMouseListener(this.mouseControl);
            this.removeMouseMotionListener(this.mouseControl);
        }

        // registers new listener
        if (mouseControl != null) {
            addMouseListener(mouseControl);
            addMouseMotionListener(mouseControl);
        }

        // assigns new listener
        this.mouseControl = mouseControl;
    }

    /**
     * Returns the style manager of this canvas.
     *
     * @return the style manager of this canvas
     */
    public final CanvasStyleManager getStyleManager() {
        return styleManager;
    }

    /**
     * Defines the style manager of this canvas.
     *
     * @param styleManager the style manager to set
     */
    public final void setStyleManager(CanvasStyleManager styleManager) {
        this.styleManager = styleManager;
    }

    /**
     * @return the fullScreenMode
     */
    public boolean isFullScreenMode() {
        return fullScreenMode;
    }

    /**
     * @param fullScreenMode the fullScreenMode to set
     */
    public void setFullScreenMode(boolean fullScreenMode) {
        this.fullScreenMode = fullScreenMode;

        if (isFullScreenMode()) {
            for (CanvasWindow w : getWindows()) {
                visibilityStatesBeforeFullscreen.add(w.isVisible());
                w.setVisible(false);
            }
        } else {
            int i = 0;
            for (Boolean b : visibilityStatesBeforeFullscreen) {
                getWindows().get(i).setVisible(b);
                i++;
            }
            visibilityStatesBeforeFullscreen = new ArrayList<Boolean>();
        }
    }

    /**
     * Returns the window groups.
     *
     * @return the windowGroups
     */
    public WindowGroups getWindowGroups() {
        return windowGroups;
    }

    /**
     * Returns the window group controller.
     *
     * @return the windowGroupController
     */
    public WindowGroupMenuController getWindowGroupController() {
        return windowGroupController;
    }

    /**
     * @param windowGroups the windowGroups to set
     */
    public void setWindowGroups(WindowGroups windowGroups) {
        this.windowGroups = windowGroups;
    }

    /**
     * Defines whether this canvas is to be in presentation mode. If the canvas
     * is in presentation mode the dock will show applets to navigate through
     * the window groups of the current session.
     *
     * @see NextWindowGroupApplet
     * @see PreviousWindowGroupApplet
     * @param state the state to set
     */
    public void setPresentationMode(boolean state) {
        if (state) {
            getNextWindowGroupApplet().showApplet();
            getPreviousWindowGroupApplet().showApplet();
        } else {
            getNextWindowGroupApplet().hideApplet();
            getPreviousWindowGroupApplet().hideApplet();
        }
        presentationMode = state;
    }

    /**
     * Indicates whether this canvas is in presentation mode, i.e., if at least
     * one window group has been defined. If the canvas is in presentation mode
     * the dock will show applets to navigate through the window groups of the
     * current session.
     *
     * @see NextWindowGroupApplet
     * @see PreviousWindowGroupApplet
     * @return <code>true</code> if this canvas is in presentation mode;
     * <code>false</code> otherwise
     */
    public boolean isPresentationMode() {
        return presentationMode;
    }

    /**
     * @return the messageBoxApplet
     */
    public MessageBoxApplet getMessageBoxApplet() {
        return messageBoxApplet;
    }

    /**
     * @return the previousWindowGroupApplet
     */
    public PreviousWindowGroupApplet getPreviousWindowGroupApplet() {
        return previousWindowGroupApplet;
    }

    /**
     * @return the nextWindowGroupApplet
     */
    public NextWindowGroupApplet getNextWindowGroupApplet() {
        return nextWindowGroupApplet;
    }

    /**
     * Defines whether to show memory usage.
     *
     * @param value the value to set
     */
    public void showMemoryDisplay(boolean value) {
        if (value) {
            add(0, memoryUsageDisplay);
            memoryUsageDisplay.run(1);
        } else {
            remove(memoryUsageDisplay);
            memoryUsageDisplay.stop();
        }
    }

    /**
     * Defines whether to show repaint areas.
     *
     * @param value the value to set
     */
    public void showRepaintAreas(boolean value) {
        if (value) {
            add(repaintVisualizer);
        } else {
            remove(repaintVisualizer);
        }
    }

    /**
     * @return the sessionInitializer
     */
    public SessionInitializer getSessionInitializer() {
        return sessionInitializer;
    }

    /**
     * @param sessionInitializer the sessionInitializer to set
     */
    public void setSessionInitializer(SessionInitializer sessionInitializer) {
        this.sessionInitializer = sessionInitializer;

        getSessionInitializers().addSessionInitializer(getSessionInitializer());
    }

    /**
     * Indicates whether a session is currently saving.
     *
     * @return <code>true</code> if a session is currently saving;
     * <code>false</code> otherwise
     */
    public boolean isSavingSession() {
        return savingSession;
    }

    /**
     * Defines whether a session is currently saving.
     *
     * @param savingSession the savingSession session state to set
     */
    public void setSavingSession(boolean savingSession) {
        this.savingSession = savingSession;
    }

    /**
     * @return the sessionFileName
     */
    public String getSessionFileName() {
        return sessionFileName;
    }

    /**
     * @param sessionFileName the session file name to set
     */
    public void setSessionFileName(String sessionFileName) {
        this.sessionFileName = sessionFileName;
    }

    /**
     * Indicates whether a session is currently loading.
     *
     * @return <code>true</code> if a session is currently loading;
     * <code>false</code> otherwise
     */
    public boolean isLoadingSession() {
        return loadingSession;
    }

    /**
     * Defines whether a session is currently loading.
     *
     * @param loadingSession the loading session state to set
     */
    public void setLoadingSession(boolean loadingSession) {
        this.loadingSession = loadingSession;
        getWindows().setAllInactive();
    }

    /**
     * Returns all connections of this canvas.
     *
     * @return all connections of this canvas
     */
    public Collection<Connection> getAllConnections() {
        return dataConnections;
    }

    /**
     * @return the controlFlowConnections
     */
    public Connections getControlFlowConnections() {
        return controlFlowConnections;
    }

    /**
     * @return the sessionInitializers
     */
    public SessionInitializerGroup getSessionInitializers() {
        return sessionInitializers;
    }

    public ActionDelegator getActionDelegator() {
        return actionDelegator;
    }

    /**
     * @return the windowMenuController
     */
    MenuController getWindowMenuController() {
        return windowMenuController;
    }

    /**
     * @return the canvasMenuController
     */
    public MenuController getCanvasMenuController() {
        return canvasMenuController;
    }

    /**
     * @return the styleMenuController
     */
    public VMenuController getStyleMenuController() {
        return styleMenuController;
    }

    /**
     * @param lastMouseEvent the lastMouseEvent to set
     */
    void setLastMouseEvent(MouseEvent lastMouseEvent) {
        this.lastMouseEvent = lastMouseEvent;
        notifyPaintListeners(lastMouseEvent.getPoint());
    }

    /**
     * @return the lastMouseEvent
     */
    MouseEvent getLastMouseEvent() {
        return lastMouseEvent;
    }

    /**
     * @return the useCaptureBuffer
     */
    public boolean isUseCaptureBuffer() {
        return useCaptureBuffer;
    }

    /**
     * @param useCaptureBuffer the useCaptureBuffer to set
     */
    public void enableCaptureBuffer(boolean useCaptureBuffer) {
        this.useCaptureBuffer = useCaptureBuffer;
    }

    public boolean isIgnoreInput() {
        return ignoreInputEvents;
    }

    /**
     * @param ignoreInputEvents the ignoreInputEvents to set
     */
    public void setIgnoreInput(boolean ignoreInputEvents) {
        this.ignoreInputEvents = ignoreInputEvents;
    }

    /**
     * @return the disposed
     */
    public boolean isDisposed() {
        return disposed;
    }

    public boolean isIgnoreMessages() {
        return ignoreMessages;
    }

    public void setIgnoreMessages(boolean v) {
        ignoreMessages = v;
    }

    /**
     * @return the autoScrollEnabled
     */
    public boolean isAutoScrollEnabled() {
        return autoScrollEnabled;
    }

    /**
     * @param autoScrollEnabled the autoScrollEnabled to set
     */
    public void setAutoScrollEnabled(boolean autoScrollEnabled) {
        this.autoScrollEnabled = autoScrollEnabled;
    }

    /**
     * @return the autoScrollSensitiveBorderSize
     */
    public int getAutoScrollSensitiveBorderSize() {
        return autoScrollSensitiveBorderSize;
    }

    /**
     * @param autoScrollSensitiveBorderSize the autoScrollSensitiveBorderSize to set
     */
    public void setAutoScrollSensitiveBorderSize(int autoScrollSensitiveBorderSize) {
        this.autoScrollSensitiveBorderSize = autoScrollSensitiveBorderSize;
    }

}

/**
 * An awt event listener responsible for controlling selection gesture for
 * grouping type representations.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class CanvasAWTListener implements AWTEventListener {

    private Canvas mainCanvas;
    public static CanvasAWTListener previousInstance;

    /**
     * Constructor.
     *
     * @param mainCanvas
     */
    public CanvasAWTListener(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;


        // very important: always remove previous instance because there will
        // be multiple instances in the event queue
        Toolkit.getDefaultToolkit().removeAWTEventListener(previousInstance);

        // define this instance as the one that will be removed from the
        // event queue if another instance is created
        previousInstance = this;
    }

    @Override
    public void eventDispatched(AWTEvent event) {

        InputEvent inputEvent = (InputEvent) event;

        if (mainCanvas.isIgnoreInput()) {

            // we allow shortcut events even in ignore mode
            // otherwise loading file/new file actions are only accessible
            // via mouse
            boolean isShortCutEvent = (event instanceof KeyEvent)
                    && (((KeyEvent) event).isAltDown()
                    || ((KeyEvent) event).isAltGraphDown()
                    || ((KeyEvent) event).isControlDown()
                    || ((KeyEvent) event).isShiftDown()
                    || ((KeyEvent) event).isMetaDown());

            boolean canvasIsSource = inputEvent.getSource() == mainCanvas
                    || VSwingUtil.isChildOf(
                    (Component) inputEvent.getSource(), mainCanvas);

            boolean dockIsSource = inputEvent.getSource()
                    == mainCanvas.getDock()
                    || VSwingUtil.isChildOf(
                    (Component) inputEvent.getSource(), mainCanvas.getDock());

            boolean msgBoxIsSource = inputEvent.getSource()
                    == mainCanvas.getMessageBox()
                    || VSwingUtil.isChildOf(
                    (Component) inputEvent.getSource(),
                    mainCanvas.getMessageBox());

            boolean dialogIsSource = VDialogWindow.class.isAssignableFrom(
                    inputEvent.getSource().getClass())
                    || VSwingUtil.getParent((Component) inputEvent.getSource(),
                    VDialogWindow.class) != null;

            if (!isShortCutEvent
                    && canvasIsSource
                    && !(dockIsSource || msgBoxIsSource || dialogIsSource)) {
                inputEvent.consume();
            }
        }

        mainCanvas.getEffectPane().processLocationInputEvent(inputEvent);

        if (event instanceof MouseEvent) {

            MouseEvent m = (MouseEvent) event;

            if (m.getSource() == mainCanvas) {
                mainCanvas.setLastMouseEvent(m);
            }

            if (m.getID() == MouseEvent.MOUSE_CLICKED && m.getModifiersEx()
                    == MouseEvent.CTRL_DOWN_MASK && m.getButton()
                    == MouseEvent.BUTTON1) {
                if (m.getSource() instanceof JComponent) {
                    JComponent c = (JComponent) m.getSource();

                    Component newC =
                            mainCanvas.findSelectableComponentAt(
                            c, m.getPoint());
                    if (newC != null) {

                        if (newC instanceof Selectable) {
                            Selectable s = (Selectable) newC;
                            if (s.isSelected()) {
                                mainCanvas.getClipBoard().unselect(s);
                            } else {
                                mainCanvas.getClipBoard().select(s);
                            }
                        }
                    }
                }
            }
        }
    } // end if mouse envent
}

class SpotPainter implements GlobalBackgroundPainter {

    private Canvas canvas;

    public SpotPainter(eu.mihosoft.vrl.visual.Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void paintGlobal(Graphics g) {
        // paint spot

        Graphics2D g2 = (Graphics2D) g;

        int x = canvas.getVisibleRect().x;
        int y = canvas.getVisibleRect().y;

        int w = canvas.getVisibleRect().width;
        int h = canvas.getVisibleRect().height;

        int radius = Math.max(w, h) / 2;

        float[] dist = {0.0f, 1.0f};
        Color[] colors = {new Color(.80f, .80f, 1.0f, .2f),
            new Color(0.0f, 0.0f, 0.1f, 0.2f)};
        RadialGradientPaint paint =
                new RadialGradientPaint(
                new Point2D.Double(x + w / 2, y + h / 2),
                radius, dist, colors);

        g2.setPaint(paint);

        g2.fillRect(x, y, w, h);
    }
}
