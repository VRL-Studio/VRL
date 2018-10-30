/* 
 * EventRecorder.java
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

package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.io.vrlx.AbstractEvent;
import eu.mihosoft.vrl.io.vrlx.AbstractMouseEvent;
import eu.mihosoft.vrl.io.vrlx.AbstractEventArray;
import eu.mihosoft.vrl.io.vrlx.AbstractKeyEvent;
import eu.mihosoft.vrl.animation.AnimationGroup;
import eu.mihosoft.vrl.animation.AnimationTask;
import eu.mihosoft.vrl.animation.TaskAnimation;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class EventRecorder implements CanvasChild {

    private Canvas mainCanvas;
    private AbstractEventArray events = new AbstractEventArray();
    private AnimationGroup eventGroup;
    private long firstTime = 0;
    private boolean recording = false;
    private boolean playing = false;
//    private Point canvasPosOffset;
    private Point lastCanvasPos;
    private Dimension lastCanvasSize;
    private Component focusOwner;
    private boolean lastEventChangedFocusOwner;
    private InputEvent lastEvent;
    private InputEvent lastEventThatChangedFocusOwner;

    public EventRecorder(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
        eventGroup = new AnimationGroup(mainCanvas.getAnimationManager());

    }

    public void reset() {
        firstTime = 0;
        recording = false;
        playing = false;
    }

    public void deleteEventArray() {
        eventGroup = new AnimationGroup(mainCanvas.getAnimationManager());
        events = new AbstractEventArray();
    }

    public void startRecord() {
        if (!isPlaying()) {
            reset();
            deleteEventArray();
            setRecording(true);
            lastCanvasPos = mainCanvas.getAbsCanvasPos();
        }
    }

    public void stopRecord() {
        setRecording(false);
    }

    public boolean hasRecord() {
        return events.size() > 0;
    }

    void addEvent(InputEvent event) {

        if (firstTime == 0) {
            firstTime = event.getWhen();
        }

        TaskAnimation animation =
                new TaskAnimation(new EventTask(mainCanvas,
                event));

        double offset = (event.getWhen() - firstTime) / 1e3;

        animation.setOffset(offset);
        animation.setDuration(0);

        eventGroup.add(animation);
    }

    public Component getTopLevelParent(Component c) {
        // find out top level parent of mainCanvas
        Container parent = c.getParent();

        Point cPos = c.getLocation();

        while (parent != null) {
            parent = c.getParent();
            if (parent != null) {
                c = parent;
                cPos.x += c.getX();
                cPos.y += c.getY();
            }
        }

        return c;
    }

    private int getTopLevelYInset() {
        int yOffset = 0;

        Component topParent = getTopLevelParent(getMainCanvas());
        if (topParent instanceof JComponent) {
            JComponent c = (JComponent) topParent;
            yOffset = c.getInsets().top;
        }

        return yOffset;
    }

    public void captureEvent(InputEvent event) {
        if (isRecording()) {
            AbstractEvent aEvent = null;
            if (event instanceof MouseEvent) {
                MouseEvent mEvent = (MouseEvent) event;
//                System.out.println("X: " + mEvent.getX() +
//                " Y: " + mEvent.getY());

                aEvent = new AbstractMouseEvent(mEvent);

                AbstractMouseEvent amEvent = (AbstractMouseEvent) aEvent;

                amEvent.setY(amEvent.getY() - getTopLevelYInset());

            }
            if (event instanceof KeyEvent) {
                aEvent = new AbstractKeyEvent((KeyEvent) event);
            }
            if (aEvent != null) {
                events.add(aEvent);
            }
        }
    }

    public void play() {
        createEventAnimation();

        eventGroup.reset();
        setPlaying(true);

        AnimationTask task = new AnimationTask() {

            public EventRecorder recorder = EventRecorder.this;

            public void firstFrameStarted() {
                //
            }

            public void lastFrameStarted() {
                System.out.println(">> EventRecorder: stopped");
                setPlaying(false);
            }

            public void frameStarted(double time) {
                //
            }
        };

        SetEventRecorderStateAnimation animation =
                new SetEventRecorderStateAnimation(task);

        animation.setOffset(0);
        animation.setDuration(0.0);

        eventGroup.appendUniqueAnimation(animation);
        mainCanvas.getAnimationManager().addAnimation(eventGroup);
    }

    /**
     * @return the mainCanvas
     */
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    /**
     * @param mainCanvas the mainCanvas to set
     */
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    /**
     * @return the recording
     */
    public boolean isRecording() {
        return recording;
    }

    /**
     * @param recording the recording to set
     */
    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    /**
     * @return the playing
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * @param playing the playing to set
     */
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void load(XMLDecoder d) {
        reset();
        deleteEventArray();


        events = (AbstractEventArray) d.readObject();

        createEventAnimation();
    }

    public void createEventAnimation() {
        eventGroup = new AnimationGroup(mainCanvas.getAnimationManager());

        Component dummy = new JPanel();

        for (AbstractEvent e : events) {
            if (e instanceof AbstractMouseEvent) {
                AbstractMouseEvent mEvent = (AbstractMouseEvent) e;
                mEvent.setY(mEvent.getY() + getTopLevelYInset());
            }
            // creating events is only possible if a source exists
            // therefore we use a dummy component
            // the real source of the event will be determined at
            // playback
            addEvent(e.getEvent(dummy));
        }
    }

    public void save(XMLEncoder e) {
        e.writeObject(events);
    }

    /**
     * @return the focusOwner
     */
    public Component getFocusOwner() {
        return focusOwner;
    }

    /**
     * @param focusOwner the focusOwner to set
     */
    void setFocusOwner(Component focusOwner) {
        this.focusOwner = focusOwner;
    }

    /**
     * @return the lastEventChangedFocusOwner
     */
    public boolean isLastEventChangedFocusOwner() {
        return lastEventChangedFocusOwner;
    }

    /**
     * @param lastEventChangedFocusOwner the lastEventChangedFocusOwner to set
     */
    public void setLastEventChangedFocusOwner(
            boolean lastEventChangedFocusOwner) {
        this.lastEventChangedFocusOwner = lastEventChangedFocusOwner;
    }

    /**
     * @return the lastEvent
     */
    public InputEvent getLastEvent() {
        return lastEvent;
    }

    /**
     * @param lastEvent the lastEvent to set
     */
    public void setLastEvent(InputEvent lastEvent) {
        this.lastEvent = lastEvent;
    }

    /**
     * @return the lastEventThatChangedFocusOwner
     */
    public InputEvent getLastEventThatChangedFocusOwner() {
        return lastEventThatChangedFocusOwner;
    }

    /**
     * @param lastEventThatChangedFocusOwner the lastEventThatChangedFocusOwner to set
     */
    public void setLastEventThatChangedFocusOwner(
            InputEvent lastEventThatChangedFocusOwner) {
        this.lastEventThatChangedFocusOwner = lastEventThatChangedFocusOwner;
    }
}

class SetEventRecorderStateAnimation extends TaskAnimation {

    public SetEventRecorderStateAnimation(AnimationTask task) {
        super(task);
    }
}
