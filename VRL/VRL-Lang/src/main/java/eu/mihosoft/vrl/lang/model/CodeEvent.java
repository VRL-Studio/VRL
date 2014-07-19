/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CodeEvent implements ICodeEvent {

    private final CodeEntity source;
    private final ICodeEventType type;

    private List<CodeEntity> added;
    private List<CodeEntity> removed;
    private List<CodeEntity> modified;
    
    private boolean captured = false;

    public CodeEvent(ICodeEventType type, CodeEntity source) {
        this.type = type;
        this.source = source;
    }

    @Override
    public final List<CodeEntity> getAdded() {

        if (added == null) {
            added = new ArrayList<>();
        }

        return added;
    }

    @Override
    public final List<CodeEntity> getModified() {

        if (modified == null) {
            modified = new ArrayList<>();
        }

        return modified;
    }

    @Override
    public final List<CodeEntity> getRemoved() {

        if (removed == null) {
            removed = new ArrayList<>();
        }

        return removed;
    }

    @Override
    public final CodeEntity getSource() {
        return source;
    }

    @Override
    public final ICodeEventType getType() {
        return type;
    }

    @Override
    public void capture() {
        this.captured = true;
    }

    @Override
    public boolean isCaptured() {
        return this.captured;
    }

}
