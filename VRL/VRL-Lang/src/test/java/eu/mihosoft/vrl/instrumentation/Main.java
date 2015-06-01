package eu.mihosoft.vrl.instrumentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@GroovyASTTransformationClass("eu.mihosoft.vrl.instrumentation.TestTransform")
public @interface Main {

}
