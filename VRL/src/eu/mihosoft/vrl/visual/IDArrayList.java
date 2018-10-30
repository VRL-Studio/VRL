/* 
 * IDArrayList.java
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

import eu.mihosoft.vrl.system.Constants;
import eu.mihosoft.vrl.system.VMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is an extended version of <code>ArrayList</code>. Its purpose is to give
 * each element in the list a unique ID value which is independant of the
 * position in the list. For a big number of elements HashMap is probably more
 * efficient in many cases.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 * @param <E>
 */
public class IDArrayList<E extends IDObject> extends ArrayList<E> {

    private static final long serialVersionUID = 4312393627675210967L;

    /**
     * @return the DEBUG
     */
    public static boolean isDebug() {
        return DEBUG;
    }

    /**
     * @param state the DEBUG state to set
     */
    public static void setDebug(boolean state) {
        DEBUG = state;
    }
    private IDTable idTable = new IDTable();
    private final IDTable originalIdTable = new IDTable();
    private static boolean DEBUG;
    private final List<Thread> threads = new ArrayList<Thread>();

    private final Object lock = new Object();

    @Override
    public boolean add(E e) {

        synchronized (lock) {

            int id = getNewId();
            e.setID(id);

            if (getById(id) != null) {
                System.err.println(
                        "ID already exists : "
                        + getById(id).getID() + " : " + getById(id));
                VMessage.criticalErrorDetected();
            }

//        System.out.println(">> IDArrayList: entry added.");
            boolean result = super.add(e);

            updateToolTips();
            updateIdTable();

            return result;
        }
    }

    /**
     * Method for debugging multithreading. NOTE: This method must not be
     * accessible via the public API.
     */
    private void checkThatAccessIsSingleThreaded() {
        if (!threads.contains(Thread.currentThread())) {
            System.out.println(">> IDArrayList: adding thread "
                    + Thread.currentThread());
            threads.add(Thread.currentThread());
        }

        if (threads.size() > 1) {
            System.err.println(">> IDArrayList: accessing this list from more"
                    + " than one thread is not supported! Number of threads = " + threads.size());

            VMessage.criticalErrorDetected();
        }
    }

    /**
     * Just used for debugging
     */
    public void updateToolTips() {

        synchronized (lock) {

            if (isDebug()) {
                int i = 0;
                for (IDObject iDObject : this) {
                    if (iDObject instanceof Connector) {
                        Connector c = (Connector) iDObject;

                        c.getTransferable().setToolTipText(
                                "Index: " + i + ", Id: " + c.getID());
                    }
                    i++;
                }
            }
        }
    }

    @Override
    public void add(int index, E e) {

        checkThatAccessIsSingleThreaded();

        synchronized (lock) {

            int id = getNewId();

            e.setID(id);

            super.add(index, e);

            updateToolTips();
            updateIdTable();
        }
    }

    /**
     * Adds an element with specific id.
     *
     * @param e the element to add
     * @param ID the id to set
     * @return <code>true</code> (as specified by {@link Collection#add})
     */
    public boolean addWithID(E e, int ID) {

        synchronized (lock) {

            boolean result = false;

            if (getById(ID) != null) {
                System.out.println(">> IDArrayList: element with equal id already"
                        + " exists!");
                VMessage.criticalErrorDetected();
            } else {
                e.setID(ID);

                result = super.add(e);
            }

            updateToolTips();
            updateIdTable();

            return result;
        }
    }

    /**
     * Returns an element by id.
     *
     * @param ID the id of the element that is to be returned
     * @return the element with specified id if such an element exists;
     * <code>null</code> otherwise
     */
    public E getById(int ID) {

        synchronized (lock) {

            E element = null;
            for (E e : this) {
                if (e.getID() == ID) {
                    element = e;
                }
            }
            return element;

        }
    }

    /**
     * Returns an element index (the position in the array) by id.
     *
     * @param ID the id of the element
     * @return the element index with specified id if such an element exists;
     * <code>null</code> otherwise
     */
    public Integer getIndexById(int ID) {

        synchronized (lock) {

            Integer result = null;
            for (int i = 0; i < size(); i++) {

                if (get(i).getID() == ID) {
                    result = i;
                }
            }
            return result;
        }
    }

    /**
     * Returns an element id (the position in the array) by index.
     *
     * @param index the index of the element
     * @return the element id with specified index if such an element exists;
     * <code>null</code> otherwise
     */
    public Integer getIdByIndex(int index) {

        synchronized (lock) {

            return get(index).getID();

        }
    }

    /**
     * Returns the shortest available (unused) id value (id > 0). This method
     * prevents fragmentation and ensures that all values < maxId are really
     * used. <p>
     * If the id table contains an entry at the current index (
     * <code>index=this.size()</code>) this entry will be used as id. </p>
     *
     * @return the shortest available id value (id > 0) or an id value defined
     * by the id table
     * @see IDTable
     */
    public int getNewId() {

        synchronized (lock) {

//        System.out.println(">> *** ID-Search ***");
            int result = 0;

            if (originalIdTable.size() > size()) {
                result = originalIdTable.get(size());
//            System.out.println("ID FROM TABLE: " + result);
            } else {
                int maxId = getMaxId();
                boolean foundUnusedId = true;

                // we want to find the smallest unused id value
                for (int i = 0; i <= maxId; i++) {
                    foundUnusedId = true;
                    for (IDObject o : this) {
//                System.out.println(">> i = " + i + ", id = " + o.getID());
                        if (o.getID() == i) {
                            foundUnusedId = false;
                        }
                    }
                    if (foundUnusedId) {
                        result = i;
//                 System.out.println(">> found id = " + id);
                        break;
                    }
                }

                // if we didn't find an unused id smaller than maxId then
                // we use id=maxId+1, which isn't in use
                if (!foundUnusedId) {
                    result = maxId + 1;
                }

//            System.out.println("NEW ID: " + result);
            }

//        System.out.println(">> id = " + id);
            return result;

        }
    }

    /**
     * Returns the maximum id value that has been assigned to an IDObject entry.
     * <p>
     * <b>Warning:</b> it returns 0, even 0 is not in use. </p>
     *
     * @return the maximum id value, valid range: [0,MAX_INT]
     */
    private int getMaxId() {
        int max = 0;
        for (E e : this) {
            max = Math.max(e.getID(), max);
        }
//        System.out.println(">> max = " + max);
        return max;
    }

    private void updateIdTable() {
        idTable.clear();
        for (IDObject idObj : this) {
            idTable.add(idObj.getID());
        }
    }

    /**
     * Defines an id table for this list.
     *
     * @param idTable the idTable to set
     */
    public void setIdTable(IDTable idTable) {

        synchronized (lock) {

            this.idTable = idTable;

            // now set the id of every object entry to the value defined in the
            // id table
            int size = Math.min(size(), idTable.size());

            for (int i = 0; i < size(); i++) {
                if (i < size) {
                    get(i).setID(idTable.get(i));
                } else if (i < size()) {
                    get(i).setID(getNewId());
                }
            }

            for (Integer i : idTable) {
                originalIdTable.add(i);
            }

            updateToolTips();
            updateIdTable();

        }
    }

    /**
     * Returns the id table of this list.
     *
     * @return the id table of this list
     */
    public IDTable getIdTable() {

        synchronized (lock) {

            return idTable;

        }
    }

    @Override
    public E remove(int index) {

        synchronized (lock) {

            E result = super.remove(index);

            updateToolTips();
            updateIdTable();

            return result;

        }
    }

    /**
     * Removes an entry defined by its id.
     *
     * @param id the id of the entry to remove
     * @return <code>true</code> (as specified by {@link Collection#remove})
     */
    public boolean removeByID(int id) {

        return remove(getById(id));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {

        synchronized (lock) {

            boolean result = false;

            if (o instanceof IDObject) {

                result = super.remove((E) o);
            }

            updateToolTips();
            updateIdTable();

            return result;

        }
    }
}
