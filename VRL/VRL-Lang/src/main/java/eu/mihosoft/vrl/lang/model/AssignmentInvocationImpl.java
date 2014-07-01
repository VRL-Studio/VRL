/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.lang.model;

/**
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@Deprecated
public class AssignmentInvocationImpl extends InvocationImpl implements AssignmentInvocation {
    
    private final Variable assignmentVariable;
    private final IArgument assignmentArgument;

    public AssignmentInvocationImpl(Scope parent, Variable assignmentVariable, IArgument arg) {
        super(parent, "", null, "assign " + assignmentVariable.getName(), assignmentVariable.getType(), false, false, true, new IArgument[0]);
        this.assignmentVariable = assignmentVariable;
        this.assignmentArgument = arg;
        
        getArguments().add(Argument.varArg(assignmentVariable));
        getArguments().add(arg);
    }

    @Override
    public Variable getAssignmentVariable() {
        return this.assignmentVariable;
    }

    @Override
    public IArgument getAssignmentArgument() {
        return this.assignmentArgument;
    }
    
}
