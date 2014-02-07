/* 
 * Scope.java
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

package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.ICodeRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Scope extends CodeEntity {

    public Scope getParent();

    public ScopeType getType();

    public String getName();

    public Object[] getScopeArgs();

    public Collection<Variable> getVariables();

    public Variable getVariable(String name);

    public Variable createVariable(IType type, String varName);

    public Variable createStaticVariable(IType type);

    public void assignConstant(String varName, Object constant);

    public void assignVariable(String varNameDest, String varNameSrc);

    public ControlFlow getControlFlow();

    public List<Scope> getScopes();

    public Scope getScopeById(String id);

    public Variable createVariable(IType type);

    public DataFlow getDataFlow();

    @Deprecated
    public void generateDataFlow();

    public Scope createScope(String id, ScopeType type, String name, Object[] args);

    public List<Comment> getComments();

    public void createComment(String id, ICodeRange range, String comment);
}

class ScopeImpl implements Scope {

    private String id;
    Scope parent;
    ScopeType type;
    private final String name;
    Object[] scopeArgs;
    Map<String, Variable> variables = new HashMap<>();
    ControlFlow controlFlow;
    DataFlow dataFlow;
    private final List<Scope> scopes = new ArrayList<>();
//    private String code;
    private List<Scope> readOnlyScopes;
    private ICodeRange location;
    private final List<Comment> comments = new ArrayList<>();

    public ScopeImpl(String id, Scope parent, ScopeType type, String name, Object... scopeArgs) {
        this.id = id;
        this.parent = parent;

        this.type = type;
        this.name = name;

        this.scopeArgs = scopeArgs;
        this.controlFlow = new ControlFlowImpl(this);
        this.dataFlow = new DataFlowImpl();

        if (parent != null) {
            if (this.parent instanceof ScopeImpl) {
                ((ScopeImpl) this.parent).addScope(this);
            } else {
                throw new UnsupportedOperationException("Unsupported parent scope specified."
                        + " Only " + ScopeImpl.class + " based implementations are supported!");
            }

            if (parent.getType() != ScopeType.CLASS && parent.getType() != ScopeType.NONE && parent.getType() != ScopeType.COMPILATION_UNIT) {
                parent.getControlFlow().callScope(this);
            }
        }
    }

    @Override
    public ScopeType getType() {
        return type;
    }

    @Override
    public Object[] getScopeArgs() {
        return scopeArgs;
    }

    @Override
    public Collection<Variable> getVariables() {
        return variables.values();
    }

    @Override
    public Variable getVariable(String name) {
        Variable result = variables.get(name);

        if (result == null && getParent() != null) {
            result = getParent().getVariable(name);
        }

        if (result == null) {

            String parentName = "<unknown>";

            if (parent != null) {
                parentName = parent.getName();
            }

//            throw new IllegalArgumentException(
//                    "Variable '"
//                    + name
//                    + "' does not exist in scope '" + parentName + "'!");
        }

        return result;
    }

    @Override
    public Variable createVariable(IType type, String varName) {
        Variable variable = new VariableImpl(this, type, varName, null, false);
        variables.put(varName, variable);
        return variable;
    }

    @Override
    public Variable createVariable(IType type) {
        String varNamePrefix = "vrlInternalVar";

        int counter = 0;
        String varName = varNamePrefix + counter;

        while (getVariable(varName) != null) {
            counter++;
            varName = varNamePrefix + counter;
        }

        return createVariable(type, varName);
    }

    @Override
    public Variable createStaticVariable(IType type) {
        Variable variable = VariableImpl.createStaticVar(parent, type);
        variables.put(variable.getName(), variable);
        return variable;
    }

    @Override
    public void assignConstant(String varName, Object constant) {
        Variable var = getVariable(varName);

        if (var == null) {
            throw new IllegalArgumentException("Variable " + varName + " does not exist!");
        }

        var.setValue(constant);
        var.setConstant(true);

    }

    @Override
    public void assignVariable(String varNameDest, String varNameSrc) {
        Variable varDest = getVariable(varNameDest);
        Variable varSrc = getVariable(varNameSrc);

        if (varDest == null) {
            throw new IllegalArgumentException("Variable " + varNameDest + " does not exist!");
        }

        if (varSrc == null) {
            throw new IllegalArgumentException("Variable " + varNameSrc + " does not exist!");
        }

        System.out.println(">> assignment: " + varNameDest + "=" + varNameSrc);
    }

    @Override
    public ControlFlow getControlFlow() {
        return controlFlow;
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    /**
     * @return the scopes
     */
    @Override
    public List<Scope> getScopes() {

        if (readOnlyScopes == null) {
            readOnlyScopes = Collections.unmodifiableList(scopes);
        }

        return readOnlyScopes;
    }

    @Override
    public String toString() {
        String result = "Scope:" + type;

        result += "\n>> Variables:\n";

        for (Variable v : variables.values()) {
            result += " --> " + v.toString() + "\n";
        }

        result += "\n>> ControlFlow:\n" + controlFlow.toString();

        result += "\n>> SubScopes:\n";

        for (Scope s : scopes) {
            result += s.toString() + "\n";
        }

        return result;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
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

//    /**
//     * @return the code
//     */
//    @Override
//    public String getCode() {
//        return code;
//    }
//
//    /**
//     * @param code the code to set
//     */
//    @Override
//    public void setCode(String code) {
//        this.code = code;
//    }
    @Override
    public DataFlow getDataFlow() {
        return dataFlow;
    }

    @Override
    public void generateDataFlow() {

        System.out.println("DATAFLOW---------------------------------");

        for (Invocation i : controlFlow.getInvocations()) {
//            System.out.println("invocation: " + i);
            for (Variable v : i.getArguments()) {
                System.out.println("--> varname: " + v.getName() + ", " + i);
            }

            if (i instanceof ScopeInvocation) {
                ((ScopeInvocation) i).getScope().generateDataFlow();
            }
        }

        boolean isClassOrScript = getType() == ScopeType.CLASS || getType() == ScopeType.NONE || getType() == ScopeType.COMPILATION_UNIT;

        if (isClassOrScript) {
            for (Scope s : getScopes()) {
                s.generateDataFlow();
            }
        }
    }

    @Override
    public Scope createScope(String id, ScopeType type, String name, Object[] args) {
        Scope scope = new ScopeImpl(id, this, type, name, args);

        return scope;
    }

    @Override
    public Scope getScopeById(String id) {
        for (Scope s : scopes) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    private void addScope(ScopeImpl s) {
        scopes.add(s);
    }

    /**
     * @return the location
     */
    @Override
    public ICodeRange getRange() {
        return location;
    }

    /**
     * @param location the location to set
     */
    @Override
    public void setRange(ICodeRange location) {
        this.location = location;
    }

    @Override
    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public void createComment(String id, ICodeRange range, String comment) {
        this.comments.add(new CommentImpl(id, range, comment));
    }

}
