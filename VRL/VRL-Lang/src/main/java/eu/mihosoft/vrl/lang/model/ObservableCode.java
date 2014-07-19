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
public interface ObservableCode {
    public void addEventHandler(ICodeEventType type, CodeEventHandler eventHandler);
    public void removeEventHandler(ICodeEventType type, CodeEventHandler eventHandler);
}
