/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface DataRelation {

    public void setSender(Invocation invocation);

    public Invocation getSender();

    public void setReceiver(Invocation invocation);

    public Invocation getReceiver();
    
//    public String getInputVariable();
//    public void setInputVariable(String name);
}

class DataRelationImpl implements DataRelation {
    
    private Invocation sender;
    private Invocation receiver;

    public DataRelationImpl(Invocation sender, Invocation receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }


    @Override
    public void setSender(Invocation invocation) {
        this.sender = invocation;
    }

    @Override
    public Invocation getSender() {
        return sender;
    }

    @Override
    public void setReceiver(Invocation invocation) {
        this.receiver = invocation;
    }

    @Override
    public Invocation getReceiver() {
        return receiver;
    }

//    @Override
//    public String getInputVariable() {
//        return 
//    }
//
//    @Override
//    public void setInputVariable(String name) {
//        throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
//    }
    
}
