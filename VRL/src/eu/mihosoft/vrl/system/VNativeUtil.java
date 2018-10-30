/* 
 * VNativeUtil.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
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
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.system;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simplifies handling of native libraries, i.e., returning a list of libraries
 * that are currently loaded.
 * 
 * @see http://stackoverflow.com/questions/1007861/how-do-i-get-a-list-of-jni-libraries-which-are-loaded/1008631#1008631
 *
 * @author Michael Hoffer
 */
public class VNativeUtil {

    private static Field loadedLibraryNames;
    private static Field systemNativeLibraries;
    private static Field nativeLibraries;
    private static Field nativeLibraryFromClass;
    private static Field nativeLibraryName;
    
    private static boolean valid;


    static {
        try {
            loadedLibraryNames = ClassLoader.class.getDeclaredField("loadedLibraryNames");
            loadedLibraryNames.setAccessible(true);

            systemNativeLibraries = ClassLoader.class.getDeclaredField("systemNativeLibraries");
            systemNativeLibraries.setAccessible(true);

            nativeLibraries = ClassLoader.class.getDeclaredField("nativeLibraries");
            nativeLibraries.setAccessible(true);

            Class<?> nativeLibrary = null;
            for (Class<?> nested : ClassLoader.class.getDeclaredClasses()) {
                if (nested.getSimpleName().equals("NativeLibrary")) {
                    nativeLibrary = nested;
                    break;
                }
            }
            nativeLibraryFromClass = nativeLibrary.getDeclaredField("fromClass");
            nativeLibraryFromClass.setAccessible(true);

            nativeLibraryName = nativeLibrary.getDeclaredField("name");
            nativeLibraryName.setAccessible(true);
            
            valid = true;
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(VNativeUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(VNativeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Indicates whether native util class has been correclty initialized.
     * @return the valid
     */
    public static boolean isValid() {
        return valid;
    }

    /**
     * Returns the names of native libraries loaded across all class loaders.
     * <p/>
     * @return a list of native libraries loaded
     */
    public static List<String> getLoadedLibraries() {
        try {
            @SuppressWarnings("unchecked")
            final List<String> result = (List<String>) loadedLibraryNames.get(null);
            return result;
        } catch (IllegalAccessException ex) {
            throw new AssertionError(ex);
        } catch (IllegalArgumentException ex) {
            throw new AssertionError(ex);
        }
    }

    /**
     * Returns the native libraries loaded by the system class loader.
     * <p/>
     * @return a Map from the names of native libraries to the classes that
     * loaded them
     */
    public static Map<String, Class<?>> getSystemNativeLibraries() {
        try {
            Map<String, Class<?>> result = new HashMap<String, Class<?>>();
            @SuppressWarnings("unchecked")
            final List<Object> libraries = (List<Object>) systemNativeLibraries.get(null);
            for (Object nativeLibrary : libraries) {
                String libraryName = (String) nativeLibraryName.get(nativeLibrary);
                Class<?> fromClass = (Class<?>) nativeLibraryFromClass.get(nativeLibrary);
                result.put(libraryName, fromClass);
            }
            return result;
        } catch (IllegalAccessException ex) {
            throw new AssertionError(ex);
        } catch (IllegalArgumentException ex) {
            throw new AssertionError(ex);
        }
    }

    /**
     * Returns a Map from the names of native libraries to the classes that
     * loaded them.
     * <p/>
     * @param loader the ClassLoader that loaded the libraries
     * @return an empty Map if no native libraries were loaded
     */
    public static Map<String, Class<?>> getNativeLibraries(final ClassLoader loader) {
        try {
            Map<String, Class<?>> result = new HashMap<String, Class<?>>();
            @SuppressWarnings("unchecked")
            final List<Object> libraries = (List<Object>) nativeLibraries.get(loader);
            for (Object nativeLibrary : libraries) {
                String libraryName = (String) nativeLibraryName.get(nativeLibrary);
                Class<?> fromClass = (Class<?>) nativeLibraryFromClass.get(nativeLibrary);
                result.put(libraryName, fromClass);
            }
            return result;
        } catch (IllegalAccessException ex) {
            throw new AssertionError(ex);
        } catch (IllegalArgumentException ex) {
            throw new AssertionError(ex);
        }
    }

    /**
     * The same as {@link #getNativeLibraries()} except that all ancestor
     * classloaders are processed as well.
     * <p/>
     * @param loader the ClassLoader that loaded (or whose ancestors loaded) the
     * libraries
     * @return an empty Map if no native libraries were loaded
     */
    public static Map<String, Class<?>> getTransitiveNativeLibraries(final ClassLoader loader) {
        Map<String, Class<?>> result = new HashMap<String,Class<?>>();
        ClassLoader parent = loader.getParent();
        while (parent != null) {
            result.putAll(getTransitiveNativeLibraries(parent));
            parent = parent.getParent();
        }
        result.putAll(getNativeLibraries(loader));
        return result;
    }

    /**
     * Converts a map of library names to the classes that loaded them to a map
     * of library names to the classloaders that loaded them.
     * <p/>
     * @param libraryToClass a map of library names to the classes that loaded
     * them
     * @return a map of library names to the classloaders that loaded them
     */
    public static Map<String, ClassLoader> getLibraryClassLoaders(Map<String, Class<?>> libraryToClass) {
        Map<String, ClassLoader> result = new HashMap<String,ClassLoader>();
        for (Entry<String, Class<?>> entry : libraryToClass.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getClassLoader());
        }
        return result;
    }

    /**
     * Returns a list containing the classloader and its ancestors.
     * <p/>
     * @param loader the classloader
     * @return a list containing the classloader, its parent, and so on
     */
    public static List<ClassLoader> getTransitiveClassLoaders(ClassLoader loader) {
        List<ClassLoader> result = new ArrayList<ClassLoader>();
        ClassLoader parent = loader.getParent();
        result.add(loader);
        while (parent != null) {
            result.add(parent);
            parent = parent.getParent();
        }
        return result;
    }
}
