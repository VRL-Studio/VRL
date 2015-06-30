/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.system;

/**
 * Plugin failure exception. Should only be used internally.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class PluginInitFailedException extends Exception {

    public PluginInitFailedException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
