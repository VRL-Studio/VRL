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
public class CodeEventType implements ICodeEventType {

    private final String name;
    private final CodeEventType superType;
    
    public static final CodeEventType ANY = new CodeEventType("CodeEventType:ANY");
    public static final CodeEventType ROOT = ANY;
    public static final CodeEventType CHANGE = new CodeEventType(ANY, "CodeEventType:CHANGE");

    public CodeEventType(CodeEventType superType, String name) {
        this.name = name;
        this.superType = superType;
    }
    
    private CodeEventType(String name) {
        this.name = name;
        this.superType = null;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final CodeEventType getSuperType() {
        return superType;
    }

}
