///*
// * TutorialSession.java
// *
// * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
// *
// * Copyright (C) 2009 Michael Hoffer <info@michaelhoffer.de>
// *
// * Supported by the Goethe Center for Scientific Computing of Prof. Wittum
// * (http://gcsc.uni-frankfurt.de)
// *
// * This file is part of Visual Reflection Library (VRL).
// *
// * VRL is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License version 3
// * as published by the Free Software Foundation.
// *
// * VRL is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// *
// * Linking this library statically or dynamically with other modules is
// * making a combined work based on this library.  Thus, the terms and
// * conditions of the GNU General Public License cover the whole
// * combination.
// *
// * As a special exception, the copyright holders of this library give you
// * permission to link this library with independent modules to produce an
// * executable, regardless of the license terms of these independent
// * modules, and to copy and distribute the resulting executable under
// * terms of your choice, provided that you also meet, for each linked
// * independent module, the terms and conditions of the license of that
// * module.  An independent module is a module which is not derived from
// * or based on this library.  If you modify this library, you may extend
// * this exception to your version of the library, but you are not
// * obligated to do so.  If you do not wish to do so, delete this
// * exception statement from your version.
// */
//
//package eu.mihosoft.vrl.reflection;
//
//import java.awt.Dimension;
//import java.awt.GridLayout;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;
//import java.beans.XMLDecoder;
//import java.beans.XMLEncoder;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import javax.swing.JFrame;
//
///**
// * <p>
// * A tutorial session is a visual canvas that can be used to record input events
// * such as mouse events, keyboard events etc.
// * </p>
// * <p>
// * <b>Warning:</b> this class is under costruction and will probably not work
// * correctly!
// * </p>
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class TutorialSession extends VisualCanvas {
//    private static final long serialVersionUID = -5529430444096142747L;
//
//    private String initialSession;
//    private TutorialWindow sessionFrame;
//
//    public TutorialSession() {
//        registerEventRecorder();
//        getDock().addDockApplet(new RecordSessionApplet(this));
//        getDock().addDockApplet(new PlaySessionApplet(this));
//
//        initialSession = saveSessionToString();
//    }
//
//    @Override
//    public boolean saveSession(OutputStream out) {
//        boolean result = true;
//
//        try{
//        XMLEncoder e = new XMLEncoder(out);
//        e.writeObject(getSize());
//        getEventRecorder().save(e);
//        if (!getEventRecorder().hasRecord()) {
//            initialSession = saveSessionToString();
//        }
//        e.writeObject(initialSession);
//        e.close();
//        }
//        catch(Exception ex){
//            result = false;
//        }
//        return false;
//    }
//
//    @Override
//    public void loadSession(InputStream in) {
//        XMLDecoder d = new XMLDecoder(in);
//        setSize((Dimension) d.readObject());
//        getEventRecorder().load(d);
//        initialSession = (String) d.readObject();
//        loadSessionFromString(initialSession);
//        d.close();
//    }
//
//    private void loadSessionFromString(String s) {
//        ByteArrayInputStream sessionIn =
//                new ByteArrayInputStream(s.getBytes());
//        super.loadSession(sessionIn);
//    }
//
//    private String saveSessionToString() {
//        ByteArrayOutputStream sessionOut = new ByteArrayOutputStream();
//        super.saveSession(sessionOut);
//        return sessionOut.toString();
//    }
//
//    public void initFromExistingSession(VisualCanvas canvas) {
//        setDisableEffects(true);
//        if (this.getParent() == null) {
//            ByteArrayOutputStream sessionOut = new ByteArrayOutputStream();
//            canvas.saveSession(sessionOut);
//            String sessionData = sessionOut.toString();
//            loadSessionFromString(sessionData);
//            initialSession = sessionData;
//            sessionFrame = new TutorialWindow("Tutorial Session",this);
//            sessionFrame.setSize(canvas.getSize());
//            sessionFrame.setLayout(new GridLayout());
//            sessionFrame.add(this);
//            sessionFrame.setVisible(true);
//            sessionFrame.toFront();
//
////            sessionFrame.setFocusableWindowState(true);
////            sessionFrame.setVisible(true);
////            sessionFrame.toFront();
//        }
//        setDisableEffects(false);
//    }
//
//    public void startRecording() {
//        initialSession = saveSessionToString();
//        getEventRecorder().startRecord();
//    }
//
//    public void stopRecording() {
//        getEventRecorder().stopRecord();
//    }
//
//    public boolean isRecording() {
//        return getEventRecorder().isRecording();
//    }
//
//    public boolean isPlaying() {
//        return getEventRecorder().isPlaying();
//    }
//
//    public void startPlayback() {
//        setDisableEffects(true);
//        clearCanvasAndInspector();
//        setDisableEffects(false);
//        loadSessionFromString(initialSession);
//        getEventRecorder().play();
//    }
//
//    @Override
//    public void init() {
//        init(false);
//    }
//
//    public void init(boolean resizable) {
//        if (this.getParent() == null) {
//            sessionFrame = new TutorialWindow("Tutorial Session",this);
//            sessionFrame.setSize(getSize());
//            sessionFrame.setLayout(new GridLayout());
//            sessionFrame.add(this);
//            sessionFrame.setVisible(true);
//            sessionFrame.setResizable(resizable);
////            sessionFrame.requestFocusInWindow();
//            sessionFrame.toFront();
////            sessionFrame.setFocusableWindowState(false);
//
//        }
//    }
//
//    /**
//     * @return the sessionFrame
//     */
//    public TutorialWindow getSessionFrame() {
//        return sessionFrame;
//    }
//}
