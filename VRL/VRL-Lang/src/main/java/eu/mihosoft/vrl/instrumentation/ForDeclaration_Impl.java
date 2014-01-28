/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ForDeclaration_Impl extends ScopeImpl implements ForDeclaration {

    private final ForDeclarationMetaData metadata;

    public ForDeclaration_Impl(String id, Scope parent, String varName, int from, int to, int inc) {
        super(id, parent, ScopeType.FOR, ScopeType.FOR.name(), new ForDeclarationMetaData(varName, from, to, inc));

        boolean forceIncrement = from < to;
        boolean equal = from == to;
        
        if (forceIncrement && !equal && inc <= 0) {
            throw new IllegalArgumentException("For loop cannot have negative or zero increment!");
        } else if (!forceIncrement && !equal && inc >= 0) {
            throw new IllegalArgumentException("For loop cannot have positive or zero increment!");
        }

        metadata = (ForDeclarationMetaData) getScopeArgs()[0];

        createVariable(new Type("int"), varName);
    }

    @Override
    public String getVarName() {
        return metadata.getVarName();
    }

    @Override
    public int getFrom() {
        return metadata.getFrom();
    }

    @Override
    public int getTo() {
        return metadata.getTo();
    }

    @Override
    public int getInc() {
        return metadata.getInc();
    }
    
     /**
     * @param varName the varName to set
     */
    public void setVarName(String varName) {
        metadata.setVarName(varName);
    }

    /**
     * @param from the from to set
     */
    public void setFrom(int from) {
        metadata.setFrom(from);
    }

    /**
     * @param to the to to set
     */
    public void setTo(int to) {
        metadata.setTo(to);
    }

    /**
     * @param inc the inc to set
     */
    public void setInc(int inc) {
        metadata.setInc(inc);
    }

}

class ForDeclarationMetaData {

    private String varName;
    private int from;
    private int to;
    private int inc;

    public ForDeclarationMetaData(String varName, int from, int to, int inc) {
        this.varName = varName;
        this.from = from;
        this.to = to;
        this.inc = inc;
    }

    /**
     * @return the varName
     */
    public String getVarName() {
        return varName;
    }

    /**
     * @return the from
     */
    public int getFrom() {
        return from;
    }

    /**
     * @return the to
     */
    public int getTo() {
        return to;
    }

    /**
     * @return the inc
     */
    public int getInc() {
        return inc;
    }

    /**
     * @param varName the varName to set
     */
    public void setVarName(String varName) {
        this.varName = varName;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * @param to the to to set
     */
    public void setTo(int to) {
        this.to = to;
    }

    /**
     * @param inc the inc to set
     */
    public void setInc(int inc) {
        this.inc = inc;
    }

}
