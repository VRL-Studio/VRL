/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface CompilationUnitDeclaration extends Scope{
//    public String getLocation();
    
    public String getFileName();
    
    public List<ClassDeclaration> getDeclaredClasses();
    
    public String getPackageName();
}
