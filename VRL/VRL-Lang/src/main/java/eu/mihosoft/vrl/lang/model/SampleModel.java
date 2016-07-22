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
public class SampleModel {

    public static void main(String[] args) {
        
        // create a new model builder instance
        VisualCodeBuilder builder = VisualCodeBuilder.newInstance();

        // define a compilation unit, i.e., a source file
        CompilationUnitDeclaration cu
                = builder.declareCompilationUnit(
                        "MyFile.java", "eu.vrl.example");
        
        // add a class definition to the compilation unit
        ClassDeclaration mainClass
                = builder.declareClass(cu, new Type("eu.vrl.example.Main"));
        
        // add a static main method to the class
        MethodDeclaration mainMethod
                = builder.declareMethod(mainClass,
                        new Modifiers(Modifier.PUBLIC, Modifier.STATIC),
                        Type.VOID, "main", new Parameters(
                                new Parameter(Type.fromClass(String[].class),
                                        "args")));
        
        // invoke println() inside the main method
        Invocation printSomething = builder.invokeMethod(mainMethod,
                ObjectProvider.fromVariable("this", mainClass.getClassType()), "println",
                Type.VOID,
                Argument.constArg(Type.STRING, "Hello, World!"));
        
        // render compilation unit to code
        String code = Scope2Code.getCode(cu);
        
        // print the code
        System.out.println(code);
    }
}
