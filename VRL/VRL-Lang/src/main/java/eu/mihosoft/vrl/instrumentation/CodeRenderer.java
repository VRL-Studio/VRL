/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 * @param <T> 
 */
public interface CodeRenderer <T extends CodeEntity> {
    public String render(T e);
    public void render(T e, CodeBuilder cb);
}
