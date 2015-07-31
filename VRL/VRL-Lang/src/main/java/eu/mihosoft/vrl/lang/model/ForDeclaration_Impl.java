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

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ForDeclaration_Impl extends ScopeImpl implements ForDeclaration {

    private final ForDeclarationMetaData metadata;

    public ForDeclaration_Impl(String id, Scope parent, Invocation from, Invocation to, Invocation inc) {
        super(id, parent, ScopeType.FOR, ScopeType.FOR.name(), new ForDeclarationMetaData(from, to, inc));

        metadata = (ForDeclarationMetaData) getScopeArgs()[0];
    }


    @Override
    public Invocation getFrom() {
        return metadata.getFrom();
    }

    @Override
    public Invocation getTo() {
        return metadata.getTo();
    }

    @Override
    public Invocation getInc() {
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

        i.getArguments().addAll(Argument.invArg(getFrom()),Argument.invArg(getTo()),Argument.invArg(getInc()));

//        i.getArguments().addListener((ListChangeListener.Change<? extends IArgument> c) -> {
//            System.out.println("change: " + c);
//            i.getArguments().get(0).getConstant().ifPresent(constVal->metadata.setFrom(constVal));
//            i.getArguments().get(1).getConstant().ifPresent(constVal->metadata.setTo(constVal));
//            i.getArguments().get(2).getConstant().ifPresent(constVal->metadata.setInc(constVal));
//        });
    }



    /**
     * @param from the from to set
     */
    public void setFrom(Invocation from) {
        metadata.setFrom(from);
        getInvocation().ifPresent(i -> i.getArguments().
                set(0, Argument.invArg(getFrom())));
    }

    /**
     * @param to the to to set
     */
    public void setTo(Invocation to) {
        metadata.setTo(to);
        getInvocation().ifPresent(i -> i.getArguments().
                set(1, Argument.invArg( getTo())));
    }

    /**
     * @param inc the inc to set
     */
    public void setInc(Invocation inc) {
        metadata.setInc(inc);
        getInvocation().ifPresent(i -> i.getArguments().
                set(2, Argument.invArg(getInc())));
    }

}

class ForDeclarationMetaData {

    private Invocation from;
    private Invocation to;
    private Invocation inc;

    public ForDeclarationMetaData(Invocation from, Invocation to, Invocation inc) {
        this.from = from;
        this.to = to;
        this.inc = inc;
    }

    /**
     * @return the from
     */
    public Invocation getFrom() {
        return from;
    }

    /**
     * @return the to
     */
    public Invocation getTo() {
        return to;
    }

    /**
     * @return the inc
     */
    public Invocation getInc() {
        return inc;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Invocation from) {
        this.from = from;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Invocation to) {
        this.to = to;
    }

    /**
     * @param inc the inc to set
     */
    public void setInc(Invocation inc) {
        this.inc = inc;
    }

}
