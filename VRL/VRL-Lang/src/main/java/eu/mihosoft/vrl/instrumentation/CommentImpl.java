/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.ICodeRange;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CommentImpl implements Comment {

    private String id;
    private ICodeRange codeRange;
    private String comment;
    private CommentType type = CommentType.UNDEFINED;

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

}
