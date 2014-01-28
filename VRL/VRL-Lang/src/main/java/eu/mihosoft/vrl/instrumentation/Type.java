/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.Patterns;
import eu.mihosoft.vrl.lang.VLangUtils;
import java.util.Objects;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class Type implements IType {

    private final String packageName;
    private final String shortName;
    private final boolean isReturnOrParamType;

    public Type(String packageName, String shortName, boolean isReturnOrParamType) {
        this.packageName = packageName;
        this.shortName = shortName;
        this.isReturnOrParamType = isReturnOrParamType;

        validate();
    }

    public Type(String packageName, String shortName) {
        this(packageName, shortName, false);

        validate();
    }

    public Type(String fullName) {
        this(fullName, false);
    }

    public Type(String fullName, boolean isReturnOrParamType) {

        if (!VLangUtils.isShortName(fullName)) {
            this.packageName = VLangUtils.slashToDot(
                    VLangUtils.packageNameFromFullClassName(fullName));
            this.shortName = VLangUtils.shortNameFromFullClassName(fullName);
        } else {
            this.packageName = "";
            this.shortName = fullName;
        }

        this.isReturnOrParamType = isReturnOrParamType;

        validate();
    }

    private void validate() {
        if (!VLangUtils.isPackageNameValid(VLangUtils.slashToDot(packageName))) {
            throw new IllegalArgumentException("Specified package is invalid: " + getPackageName());
        }
        if (!isReturnOrParamType) {
            if (!VLangUtils.isClassNameValid(shortName)) {
                throw new IllegalArgumentException("Specified classname is invalid: " + getShortName());
            }
        } else {
            if (!VLangUtils.isIdentifierValid(shortName, true)) {
                throw new IllegalArgumentException("Specified classname is invalid: " + getShortName());
            }
        }
    }

    /**
     * @return the packageName
     */
    @Override
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return the shortName
     */
    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public String getFullClassName() {
        if (packageName.isEmpty()) {
            return shortName;
        } else {
            return packageName + "." + shortName;
        }
    }

    @Override
    public String toString() {
        return "[pck: " + packageName + ", name: " + shortName + "]";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.packageName);
        hash = 71 * hash + Objects.hashCode(this.shortName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Type other = (Type) obj;
        if (!Objects.equals(this.packageName, other.packageName)) {
            return false;
        }
        if (!Objects.equals(this.shortName, other.shortName)) {
            return false;
        }
        return true;
    }

}
