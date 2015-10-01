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

package eu.mihosoft.vrl.lang.model;

import eu.mihosoft.vrl.workflow.VFlow;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Scope extends CodeEntity {

    public ScopeType getType();

    public String getName();

    public Object[] getScopeArgs();

    public Collection<Variable> getVariables();

    public Variable getVariable(String name);

    public Variable createVariable(IType type, String varName);

//    public Variable createStaticVariable(IType type);
    
    public BinaryOperatorInvocation assignInvocationResult(String id, String varName, Invocation invocation);

    public BinaryOperatorInvocation assignConstant(String id, String varName, Object constant);

    public BinaryOperatorInvocation assignVariable(String id, String varNameDest, String varNameSrc);

    public ControlFlow getControlFlow();

    public ObservableList<Scope> getScopes();

    public Scope getScopeById(String id);

//    public Variable createVariable(IType type);
//    public Variable createVariable(Invocation invocation);

    public DataFlow getDataFlow();

    @Deprecated
    public void generateDataFlow();

    public Scope createScope(String id, ScopeType type, String name, Object[] args);

    public List<Comment> getComments();

    public void createComment(String id, ICodeRange range, String comment);
    
    public boolean removeScope(Scope s);
    
//    public VFlow getFlow();

    public DeclarationInvocation declareVariable(String id, IType type, String varName);
    
    public Optional<ScopeInvocation> getInvocation();
    
    public VFlow getFlow();
    
    public void visitScopeAndAllSubElements(Consumer<CodeEntity> consumer);
    
    public List<CodeEntity> collectScopeAndAllsubElements();

    public DeclareAndAssignInvocation declareAndAssignVariable(String request, IType type, String varName, Argument assignmentArg);

    /**
     * Used to prevent update collisions in transformation/instrumentation phase.
     * @deprecated don't use this in external transformations (as of 29.09.2015)
     * this method needs to be removed (fix bugs in setParent(), init inside InvocationImpl)
     */
    @Deprecated
    public void disableParentUpdate();
    
    /**
     * Picks the smallest code entity at the specified location.
     * @param loc location
     * @return 
     */
    public Optional<CodeEntity> pick(ICodeLocation loc);
}

