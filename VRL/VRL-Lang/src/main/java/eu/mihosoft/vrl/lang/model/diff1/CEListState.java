/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff1;

import eu.mihosoft.ai.astar.Action;
import eu.mihosoft.ai.astar.State;
import eu.mihosoft.vrl.lang.model.diff1.actions.DecreaseIndexAction;
import eu.mihosoft.vrl.lang.model.diff1.actions.IncreaseIndexAction;
import java.util.Objects;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class CEListState implements State<CEList> {

    private String actionName;
    private Action<CEList> action;

    private CEList entities;

    public CEListState() {
        this.entities = new CEList();
    }

    /**
     *
     * @param entities CodeEntitieList with list of CodeEtities and index
     */
    public CEListState(CEList entities) {
        this.entities = new CEList(entities, true);
    }

    public CEListState(CEListState other) {

        if (this.action instanceof IncreaseIndexAction || this.action instanceof DecreaseIndexAction) {
            this.entities = new CEList(other.entities, false);
        } else {
            this.entities = new CEList(other.entities, true);
        }
        this.actionName = other.actionName;
        this.action = other.action;
    }

    @Override
    public Action<CEList> getAction() {
        return this.action;
    }

    @Override
    public void setAction(Action<CEList> action) {
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
    public State<CEList> clone() {

        CEListState result = new CEListState(this);
        return result;
    }

    @Override
    public CEList set(int i, CEList value) {

        if (i != 0) {
            throw new IndexOutOfBoundsException("Index " + i + " != 0.");
        }
        entities = value;

        return entities;
    }

    @Override
    public CEList get(int i) {
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
    public State<CEList> newInstance(int i) {
        if (i != 1) {
            throw new IndexOutOfBoundsException("Size " + i + " != 1.");
        }

        return new CEListState();
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
        final CEListState other = (CEListState) obj;

        if (this.entities.getIndex() != other.entities.getIndex()) {
            return false;
        }
        return this.entities.getListWithCodeEntities().equals(other.entities.getListWithCodeEntities());

    }

    @Override
    public String toString() {
        return "CodeEntityListState{" + "actionName=" + actionName + ", action=" + action + ", entities=" + entities.getCodeEntitieNames() + ", index=" + entities.getIndex() + '}';
    }

}
