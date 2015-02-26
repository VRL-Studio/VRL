/* 
 * ForDeclaration_Impl.java
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

import javafx.collections.ListChangeListener;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class SimpleForDeclaration_Impl extends ScopeImpl implements SimpleForDeclaration {

    private final ForDeclarationMetaData metadata;

    public SimpleForDeclaration_Impl(String id, Scope parent, String varName, int from, int to, int inc) {
        super(id, parent, ScopeType.FOR, ScopeType.FOR.name(), new ForDeclarationMetaData(varName, from, to, inc));

        boolean forceIncrement = from < to;
        boolean equal = from == to;

        if (forceIncrement && !equal && inc <= 0) {
            throw new IllegalArgumentException("For loop cannot have negative or zero increment!");
        } else if (!forceIncrement && !equal && inc >= 0) {
            throw new IllegalArgumentException("For loop cannot have positive or zero increment!");
        }

        metadata = (ForDeclarationMetaData) getScopeArgs()[0];

        setVarName(varName, null);
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

//    /**
//     * @param varName the varName to set (creates var if not null or empty)
//     */
//    public final void setVarName(String varName) {
//        metadata.setVarName(varName);
//        if (varName != null && !varName.isEmpty()) {
////            Variable v = _createVariable(Type.INT, varName);
//            createParamVariable(Type.INT, varName);
//        }
//    }
    @Override
    public void defineParameters(Invocation i) {
        i.getArguments().clear();

        i.getArguments().addAll(Argument.constArg(Type.INT, 0),Argument.constArg(Type.INT, 0),Argument.constArg(Type.INT, 0));

        i.getArguments().addListener((ListChangeListener.Change<? extends IArgument> c) -> {
            System.out.println("change: " + c);
            i.getArguments().get(0).getConstant().ifPresent(constVal->metadata.setFrom((Integer)constVal));
            i.getArguments().get(1).getConstant().ifPresent(constVal->metadata.setTo((Integer)constVal));
            i.getArguments().get(2).getConstant().ifPresent(constVal->metadata.setInc((Integer)constVal));
        });
    }

    public final void setVarName(String varName, ICodeRange codeRange) {
        metadata.setVarName(varName);
        if (varName != null && !varName.isEmpty()) {
            DeclarationInvocationImpl inv
                    = (DeclarationInvocationImpl) getControlFlow().
                    declareVariable(getId(), Type.INT, varName);
            inv.setTextRenderingEnabled(false);
            inv.setRange(codeRange);
        }
    }

    /**
     * @param from the from to set
     */
    public void setFrom(int from) {
        metadata.setFrom(from);
        getInvocation().ifPresent(i -> i.getArguments().
                set(0, Argument.constArg(Type.INT, getFrom())));
    }

    /**
     * @param to the to to set
     */
    public void setTo(int to) {
        metadata.setTo(to);
        getInvocation().ifPresent(i -> i.getArguments().
                set(1, Argument.constArg(Type.INT, getTo())));
    }

    /**
     * @param inc the inc to set
     */
    public void setInc(int inc) {
        metadata.setInc(inc);
        getInvocation().ifPresent(i -> i.getArguments().
                set(2, Argument.constArg(Type.INT, getInc())));
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
