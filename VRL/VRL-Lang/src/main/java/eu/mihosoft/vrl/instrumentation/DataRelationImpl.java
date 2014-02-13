/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.DataRelation;
import eu.mihosoft.vrl.lang.model.Invocation;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
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
