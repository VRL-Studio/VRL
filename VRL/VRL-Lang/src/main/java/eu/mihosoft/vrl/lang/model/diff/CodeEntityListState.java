/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.ai.astar.Action;
import eu.mihosoft.ai.astar.State;
import eu.mihosoft.vrl.lang.model.diff.actions.DecreaseIndexAction;
import eu.mihosoft.vrl.lang.model.diff.actions.IncreaseIndexAction;
import java.util.Objects;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class CodeEntityListState implements State<CodeEntityList> {

    private String actionName;
    private Action<CodeEntityList> action;

    private CodeEntityList entities;

    public CodeEntityListState() {
        this.entities = new CodeEntityList();
    }

    /**
     *
     * @param entities CodeEntitieList with list of CodeEtities and index
     */
    public CodeEntityListState(CodeEntityList entities) {
        this.entities = new CodeEntityList(entities, true);
    }

    public CodeEntityListState(CodeEntityListState other) {

        if (this.action instanceof IncreaseIndexAction || this.action instanceof DecreaseIndexAction) {
            this.entities = new CodeEntityList(other.entities, false);
        } else {
            this.entities = new CodeEntityList(other.entities, true);
        }

        this.actionName = other.actionName;
        this.action = other.action;
    }

    @Override
    public Action<CodeEntityList> getAction() {
        return this.action;
    }

    @Override
    public void setAction(Action<CodeEntityList> action) {
        this.action = action;
    }

    @Override
    public String getActionName() {
        return this.actionName;
    }

    @Override
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public State<CodeEntityList> clone() {
        
        CodeEntityListState result = new CodeEntityListState(this);
        return result;
    }

    @Override
    public CodeEntityList set(int i, CodeEntityList value) {

        if (i != 0) {
            throw new IndexOutOfBoundsException("Index " + i + " != 0.");
        }
        entities = value;

        return entities;
    }

    @Override
    public CodeEntityList get(int i) {
        if (i != 0) {
            throw new IndexOutOfBoundsException("Index " + i + " != 0.");
        }
        return entities;
    }

    @Override
    public int size() {
        return 1;//entities.size();
    }

    @Override
    public State<CodeEntityList> newInstance(int i) {
        if (i != 1) {
            throw new IndexOutOfBoundsException("Size " + i + " != 1.");
        }

        return new CodeEntityListState();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.entities);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CodeEntityListState other = (CodeEntityListState) obj;

        if (this.entities.getIndex() != other.entities.getIndex()) {
            return false;
        }
        return this.entities.equals(other.entities);

    }

    @Override
    public String toString() {
        return "CodeEntityListState{" + "actionName=" + actionName + ", action=" + action + ", entities=" + entities + ", index=" + entities.getIndex() + '}';
    }

}
