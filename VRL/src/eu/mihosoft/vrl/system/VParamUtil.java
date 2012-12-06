/* 
 * VParamUtil.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2012 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2012 by Michael Hoffer
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.vrl.system;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * <p>
 * This utility class allows parameter validation.
 * </p>
 * <p><b>Example (Java code):</b></p>
 * <p>Validate that all parameters are not null:</p>
 * <code>
 * <pre>
 * class SampleClass {
 *   
 *   public SampleClass(Object a, Object b, Object c) {
 *       VParamUtil.throwIfNotNull(a,b,c);
 *   }
 * }
 * </pre>
 * </code>
 * <p>Validate that all parameters are instances of Integer:</p>
 * <code>
 * <pre>
 * class SampleClass {
 *   
 *   public SampleClass(Object a, Object b, Object c) {
 *       VParamUtil.throwIfNotValid(VParamUtil.VALIDATOR_INSTANCEOF, Integer.class, a, b, c);
 *   }
 * }
 * </pre>
 * </code>
 * 
 * <b>Note:</b> By providing custom implementations of {@link ParameterValidator} this
 * framework can be easily extended.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VParamUtil {

    /**
     * Validates that the specified parameter is not null.
     */
    public static final ParameterValidator VALIDATOR_NOT_NULL =
            new NotNullValidator();
    /**
     * Validates that the specified parameter is a file object.
     */
    public static final ParameterValidator VALIDATOR_FILE =
            new FileValidator();
    /**
     * Validates that the specified parameter is a file object that represents
     * an existing file.
     */
    public static final ParameterValidator VALIDATOR_EXISTING_FILE =
            new ExistingFileValidator();
    /**
     * Validates that the specified parameter is a file object that represents 
     * an existing folder.
     */
    public static final ParameterValidator VALIDATOR_EXISTING_FOLDER =
            new ExistingFolderValidator();
    
    
    /**
     * Validates that the specified parameter is a class object
     */
    public static final ParameterValidator VALIDATOR_CLASS =
            new ClassValidator();
    
    /**
     * Validates that the specified parameter is an instance of the class
     * that has been specified as validationArg
     */
    public static final ParameterValidator VALIDATOR_INSTANCEOF =
            new InstanceOfValidator();

    /**
     * Validates that the specified objects are not null. If one or more objects 
     * are null an {@link IllegalArgumentException} will be thrown.
     * @param params parameters to validate
     */
    public static void throwIfNull(Object... params) {
        throwIfNotValid(VALIDATOR_NOT_NULL, null, params);
    }

    /**
     * Validates that the specified objects meet the requested conditions.
     * If the given validator invalidates one or more of the objects to validate
     * an {@link IllegalArgumentException} will be thrown.
     * @param v validator
     * @param validationArg validation argument 
     * (may be null, usage depends on the validator implementation)
     * @param params objects to validate
     */
    public static void throwIfNotValid(
            ParameterValidator v,
            Object validationArg,
            Object... params) {

        for (Object o : params) {
            ValidationResult r = v.validate(o, validationArg);

            if (!r.isValid()) {
                throw new IllegalArgumentException(r.getMessage());
            }
        }
    }

    /**
     * Validates that the specified objects meet the requested conditions.
     * If the given validator invalidates one or more of the objects to validate
     * an {@link IllegalArgumentException} will be thrown.
     * @param v validator
     * @param validationArg validation argument 
     * (may be null, usage depends on the validator implementation)
     * @param params objects to validate
     */
    public static ValidationResult validate(
            ParameterValidator v,
            Object validationArg,
            Object... params) {

        for (Object o : params) {
            ValidationResult r = v.validate(o, validationArg);

            if (!r.isValid()) {
                return r;
            }
        }

        return ValidationResult.VALID;
    }

    /**
     * Validates that the specified objects meet the requested conditions.
     * @param v validator
     * @param validationArg validation argument (may be null, usage depends on the validator implementation)
     * @param params objects to validate
     * @return validation result
     */
    public static ValidationResult validate(
            ParameterValidator v,
            Object validationArg,
            Collection<?> params) {

        for (Object o : params) {
            ValidationResult r = v.validate(o, validationArg);

            if (!r.isValid()) {
                return r;
            }
        }

        return ValidationResult.VALID;
    }

    /**
     * Validates that the specified objects meet the requested conditions.
     * @param v validator
     * @param params objects to validate
     * @return validation result
     */
    public static ValidationResult validate(
            ParameterValidator v,
            Collection<?> params) {
        return validate(v, null, params);
    }

    /**
     * Validates that the specified objects meet the requested conditions.
     * If the given validator invalidates one or more of the objects to validate
     * an {@link IllegalArgumentException} will be thrown.
     * @param v validator
     * @param validationArg validation argument 
     * (may be null, usage depends on the validator implementation)
     * @param params objects to validate
     */
    public static void throwIfNotValid(
            ParameterValidator v,
            Object validationArg,
            Collection<?> params) {

        for (Object o : params) {
            ValidationResult r = v.validate(o, validationArg);

            if (!r.isValid()) {
                throw new IllegalArgumentException(r.getMessage());
            }
        }
    }

    /**
     * Validates that the specified objects meet the requested conditions.
     * If the given validator invalidates one or more of the objects to validate
     * an {@link IllegalArgumentException} will be thrown.
     * @param v validator
     * @param params objects to validate
     */
    public static void throwIfNotValid(
            ParameterValidator v,
            Collection<?> params) {
        throwIfNotValid(v, null, params);
    }
}

class ParameterValidatorImpl implements ParameterValidator {

    private HashSet<ParameterValidator> dependencies =
            new HashSet<ParameterValidator>();

    public ParameterValidatorImpl() {
        // 
    }

    

    public ParameterValidatorImpl(ParameterValidator... validators) {
        VParamUtil.validate(
                VParamUtil.VALIDATOR_NOT_NULL, Arrays.asList(validators));
        
        dependencies.addAll(Arrays.asList(validators));
    }

    @Override
    public final ValidationResult validate(Object p) {
        return validate(p, null);
    }

    @Override
    public ValidationResult validate(Object p, Object validationArg) {
        for (ParameterValidator v : dependencies) {

            if (v == null) {
                throw new IllegalArgumentException("***");
            }
            
            ValidationResult r = v.validate(p, validationArg);

            if (!r.isValid()) {
                return r;
            }
        }

        return ValidationResult.VALID;
    }
}

class NotNullValidator extends ParameterValidatorImpl {

    public NotNullValidator() {
        // no dependencies
    }

    @Override
    public ValidationResult validate(Object p, Object validationArg) {
        
        ValidationResult r = super.validate(p,validationArg);

        if (!r.isValid()) {
            return r;
        }
        
        if (p == null) {
            return new ValidationResult(
                    false, p, "Argument \"null\" is not supported.");
        }

        return ValidationResult.VALID;
    }
}

class ClassValidator extends ParameterValidatorImpl {
    public ClassValidator() {
        super(VParamUtil.VALIDATOR_NOT_NULL);
    }

    @Override
    public ValidationResult validate(Object p, Object validationArg) {
        
        ValidationResult r = super.validate(p,validationArg);

        if (!r.isValid()) {
            return r;
        }

        if (!(p instanceof Class<?>)) {
            return new ValidationResult(
                    false, p, "Argument is no class object.");
        }
        
        return ValidationResult.VALID;
    }
}

class InstanceOfValidator extends ParameterValidatorImpl {

    public InstanceOfValidator() {
        super(VParamUtil.VALIDATOR_NOT_NULL);
    }

    @Override
    public ValidationResult validate(Object p, Object validationArg) {

        ValidationResult r = VParamUtil.validate(
                VParamUtil.VALIDATOR_CLASS, null, validationArg);
                
        if (!r.isValid()) {
            return r;
        }

        r = super.validate(p,validationArg);

        if (!r.isValid()) {
            return r;
        }

        Class<?> clazz = (Class<?>)validationArg;
        
        if (!clazz.isAssignableFrom(p.getClass())) {
            return new ValidationResult(
                    false, p, 
                    "Argument is no instance of \"" + clazz.getName() + "\".");
        }
        
        return ValidationResult.VALID;
    }
}

class ExistingFolderValidator extends ParameterValidatorImpl {

    public ExistingFolderValidator() {
        super(VParamUtil.VALIDATOR_FILE);
    }

    @Override
    public ValidationResult validate(Object p, Object validationArg) {

        ValidationResult r = super.validate(p,validationArg);

        if (!r.isValid()) {
            return r;
        }

        if (!((File) p).isDirectory()) {
            return new ValidationResult(false, p, "Argument is no directory: " + p);
        }

        return ValidationResult.VALID;
    }
}

class ExistingFileValidator extends ParameterValidatorImpl {

    public ExistingFileValidator() {
        super(VParamUtil.VALIDATOR_FILE);
    }

    @Override
    public ValidationResult validate(Object p, Object validationArg) {

        ValidationResult r = super.validate(p, validationArg);

        if (!r.isValid()) {
            return r;
        }

        if (!((File) p).exists() || ((File) p).isDirectory()) {
            return new ValidationResult(
                    false, p, "Argument is no existing file: " + p);
        }

        return ValidationResult.VALID;
    }
}

class FileValidator extends ParameterValidatorImpl {

    public FileValidator() {
        super(VParamUtil.VALIDATOR_NOT_NULL);
    }

    @Override
    public ValidationResult validate(Object p, Object validationArg) {

        ValidationResult r = super.validate(p, validationArg);

        if (!r.isValid()) {
            return r;
        }

        if (!(p instanceof File)) {
            return new ValidationResult(
                    false, p, "Argument is no file object.");
        }

        return ValidationResult.VALID;
    }
}
