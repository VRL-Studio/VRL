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
class InstrumentationEventTypeImpl implements InstrumentationEventType {

    private final String name;
    private final InstrumentationEventType superType;

    public InstrumentationEventTypeImpl(InstrumentationEventType superType, String name) {
        this.name = name;
        this.superType = superType;
    }

    InstrumentationEventTypeImpl(String name) {
        this.name = name;
        this.superType = null;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final InstrumentationEventType getSuperType() {
        return superType;
    }

}
