/* 
 * CommentImpl.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
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
 * Computing and Visualization in Science, in press.
 */
package eu.mihosoft.vrl.lang.model;

import eu.mihosoft.vrl.lang.model.CommentType;
import eu.mihosoft.vrl.lang.model.Comment;
import eu.mihosoft.vrl.lang.model.ICodeRange;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.workflow.VNode;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CommentImpl implements Comment {

    private String id;
    private ICodeRange codeRange;
    private String comment;
    private CommentType type = CommentType.UNDEFINED;
    private Scope parent;
    private VNode node;
    private ObservableCodeImpl observableCode;

    public CommentImpl(String id, ICodeRange codeRange, String comment) {
        this.id = id;
        this.codeRange = codeRange;
        this.comment = comment;
    }

    public CommentImpl(String id, ICodeRange codeRange, String comment, CommentType type) {
        this.id = id;
        this.codeRange = codeRange;
        this.comment = comment;
        this.type = type;
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the codeRange
     */
    @Override
    public ICodeRange getRange() {
        return codeRange;
    }

    /**
     * @param codeRange the codeRange to set
     */
    @Override
    public void setRange(ICodeRange codeRange) {
        this.codeRange = codeRange;
    }

    /**
     * @return the comment
     */
    @Override
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the type
     */
    @Override
    public CommentType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    @Override
    public void setType(CommentType type) {
        this.type = type;
    }

    /**
     * @return the parent
     */
    @Override
    public Scope getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Scope parent) {
        this.parent = parent;

        if (parent != null) {
            // TODO: how shall we integrate comments? 06.06.2014
//            this.node = parent.getFlow().newNode();
//            this.node.getValueObject().setValue(this);
//            this.node.setTitle("//");
        }
    }

    @Override
    public VNode getNode() {
        return this.node;
    }

    private ObservableCodeImpl getObservable() {
        if (observableCode == null) {
            observableCode = new ObservableCodeImpl();
        }

        return observableCode;
    }

    @Override
    public void addEventHandler(ICodeEventType type, CodeEventHandler eventHandler) {
        getObservable().addEventHandler(type, eventHandler);
    }

    @Override
    public void removeEventHandler(ICodeEventType type, CodeEventHandler eventHandler) {
        getObservable().removeEventHandler(type, eventHandler);
    }

    @Override
    public void fireEvent(CodeEvent evt) {
        getObservable().fireEvent(evt);

        if (!evt.isCaptured() && getParent() != null) {
            getParent().fireEvent(evt);
        }
    }

}
