/* 
 * ScopeInvocationImpl.java
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

import eu.mihosoft.vrl.lang.workflow.WorkflowUtil;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import java.util.Objects;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ScopeInvocationImpl extends InvocationImpl implements ScopeInvocation {

    private final Scope scope;
    private ObservableCodeImpl observableCode;

    public ScopeInvocationImpl(Scope s) {
        super(s, "", null, "scope", Type.VOID, false, true, true);
        this.scope = s;

        VNode node = scope.getNode();

        node.getValueObject().setValue(this);

        node.setMainInput(node.addInput(WorkflowUtil.CONTROL_FLOW)).
                getVisualizationRequest().set(
                        VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);
        node.setMainOutput(node.addOutput(WorkflowUtil.CONTROL_FLOW)).
                getVisualizationRequest().set(
                        VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);

        if (s instanceof ControlFlowStatement) {
            ControlFlowStatement controlFlowStatement = (ControlFlowStatement) s;
            controlFlowStatement.defineParameters(this);
        }

        int argIndex = 0;
        for (IArgument arg : getArguments()) {
            node.addInput(WorkflowUtil.DATA_FLOW).getValueObject().
                    setValue(new ArgumentValue(argIndex, arg));
            argIndex++;
        }

        if (!Objects.equals(getReturnType(), Type.VOID)) {
            Connector output = node.addOutput(WorkflowUtil.DATA_FLOW);
            output.getValueObject().setValue(getReturnType());
            node.setMainOutput(output);
        }
    }

    /**
     * @return the scope
     */
    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public boolean isScope() {
        return true;
    }

    @Override
    public ICodeRange getRange() {
        return scope.getRange();
    }

    @Override
    public VNode getNode() {
        return this.scope.getNode();
    }

    // TODO 19.07.2014: think about relation between scope invocation
    //                  and scope in terms of event handling.
}
