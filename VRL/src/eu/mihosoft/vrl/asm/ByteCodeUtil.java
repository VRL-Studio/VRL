/* 
 * ByteCodeUtil.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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

package eu.mihosoft.vrl.asm;

import eu.mihosoft.vrl.lang.VLangUtils;
import groovyjarjarasm.asm.*;
//import groovyjarjarasm.asm.commons.EmptyVisitor;
import groovyjarjarasm.asm.commons.Remapper;
import groovyjarjarasm.asm.commons.RemappingClassAdapter;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Util class used to analyze content and dependencies of .class files.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ByteCodeUtil {

    // no instanciation allowed
    private ByteCodeUtil() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Returns the names of all classes that are used by the specified .class
     * file. <b>Note:</b> only .class files are supported. This method will not
     * work for jar archives. </p>
     *
     * @param classFile the file to analyze
     * @param prefix
     * @return the names of all classes that are used by the specified .lass
     * file
     * @throws IOException
     */
    public static Set<String> getClassesUsedBy(
            final File classFile,
            final String prefix // common prefix for all classes
            // that will be retrieved
            ) throws IOException {

        // based on ideas from:
        // http://stackoverflow.com/questions/3734825/find-out-which-classes-of-a-given-api-are-used

        IOException exception = null;

        BufferedInputStream in = null;

        try {
            in = new BufferedInputStream(new FileInputStream(classFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ByteCodeUtil.class.getName()).log(Level.SEVERE, null, ex);
            exception = ex;
        } finally {
            if (exception != null) {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ByteCodeUtil.class.getName()).
                            log(Level.SEVERE, null, ex);
                }


                throw exception;
            }
        }

        ClassReader reader = null;

        try {
            reader = new ClassReader(in);
        } catch (IOException ex) {
            Logger.getLogger(ByteCodeUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {

            if (exception != null) {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ByteCodeUtil.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
                throw exception;
            }
        }

        final Set<String> usedClasses =
                new TreeSet<String>(new Comparator<String>() {

            @Override
            public int compare(final String o1, final String o2) {
                return o1.compareTo(o2);
            }
        });

        final Remapper remapper = new ClassCollector(usedClasses, prefix);
        final VClassVisitor inner = new VClassVisitor();
        final RemappingClassAdapter visitor =
                new RemappingClassAdapter(inner, remapper);

        reader.accept(visitor, 0);

        if (in != null) {
            in.close();
        }

        Collection<String> definedClasses = getClassNames(classFile);

        // we remove this class and inners from its own dependency list
        usedClasses.removeAll(definedClasses);

        // search for usedClasses that are part of the same code file as the
        // defined classes
        // 
        // # Q: why not only remove classes that are defined in the specified
        //   class file?
        //
        //   A: because each nested class is stored in its own class file.
        //      thus, we would have usage-dependencies
        //      (outer-class relations are captured as usage) in the result
        //      of this method
        Collection<String> outerClasses = new ArrayList<String>();
        for (String definedCls : definedClasses) {

            for (String usedCls : usedClasses) {

                // the prefix denotes the outer class, i.e., the one class
                // or interface that contains all nested classes of the file.
                // # note:
                // only one public class may be defined per source file. all
                // other classes must be inner classes of the public class
                String definedClsPrefix = definedCls.split("\\$")[0].trim();
              
                // check whether this used Cls is a class from the same code
                // file
                if (definedCls.equals(usedCls) 
                        || usedCls.startsWith(definedClsPrefix + "$")) {
                    outerClasses.add(usedCls);
                    break;
                }
            }
        }

        // we remove its outer class from its own dependency list
        usedClasses.removeAll(outerClasses);

        return usedClasses;
    }

    /**
     * Default class visitor. Does not modify class.
     */
//    private static class VClassVisitor extends EmptyVisitor {
    private static class VClassVisitor extends ClassVisitor {

        public VClassVisitor() {
            super(Opcodes.ASM4);
        }
        
        

        @Override
        public void visit(int i, int i1, String string,
                String string1, String string2, String[] strings) {
            super.visit(i, i1, string, string1, string2, strings);
        }

        @Override
        public void visitSource(String string, String string1) {
            super.visitSource(string, string1);
        }

        @Override
        public void visitOuterClass(String name, String string1,
                String string2) {
            super.visitOuterClass(name, string1, string2);
        }

        @Override
        public AnnotationVisitor visitAnnotation(
                String string, boolean bln) {

            return super.visitAnnotation(string, bln);
        }

        @Override
        public void visitAttribute(Attribute atrbt) {
            super.visitAttribute(atrbt);
        }

        @Override
        public void visitInnerClass(
                String string, String string1, String string2, int i) {
            super.visitInnerClass(string, string1, string2, i);
        }

        @Override
        public FieldVisitor visitField(
                int i, String string, String string1,
                String string2, Object o) {
            return super.visitField(i, string, string1, string2, o);
        }

        @Override
        public MethodVisitor visitMethod(
                int i, String string, String string1,
                String string2, String[] strings) {
            return super.visitMethod(i, string, string1, string2, strings);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }
    }

    /**
     * Returns the full class names of the classes that are defined 
     * in the specified file. <p>
     * <b>Note:</b> only .class files are supported. This method will not work
     * for jar archives. This method may check .class files of inner classes to
     * fully extract all class names explicitly and implicitly defined in the
     * specified file. </p>
     *
     * @param f file to analyze
     * @return full class names of the classes in the specified file
     * @throws Exception
     */
    public static Collection<String> getClassNames(final File f)
            throws IOException {
        Collection<String> classNames = new ArrayList<String>();
        Collection<File> classFiles = new ArrayList<File>();

        return _getClassNames(f, classNames, classFiles, false);
    }
    
    /**
     * Returns the name of the first outer class defined in the specified file.
     * <p>
     * <b>Note:</b> only .class files are supported. This method will not work
     * for jar archives.</p>
     * @param f file to analyze
     * @return name of the first outer class defined in the specified file or 
     *         <code>null</code> if no class is defined in the specified file
     * @throws IOException 
     */
    public static String getFirstClassNameIn(File f) throws IOException {
        String result = null;
        
        Collection<String> classNames = new ArrayList<String>();
        Collection<File> classFiles = new ArrayList<File>();
        
        Collection<String> outerNames = 
                _getClassNames(f, classNames, classFiles, false);
        
        if (!outerNames.isEmpty()) {
            result = outerNames.iterator().next();
        }
        
        return result;
    }
    
        /**
     * Returns the names of the outer classes defined in the specified file.
     * <p>
     * <b>Note:</b> only .class files are supported. This method will not work
     * for jar archives.</p>
     * @param f file to analyze
     * @return names of the outer classes defined in the specified file
     * @throws IOException 
     */
    public static Collection<String> getOuterClassNamesIn(File f) throws IOException {
        
        Collection<String> classNames = new ArrayList<String>();
        Collection<File> classFiles = new ArrayList<File>();
        
        Collection<String> result = 
                _getClassNames(f, classNames, classFiles, false);

        return result;
    }

    /**
     * Internal method for recursion. See {@link #getClassNames(java.io.File) }
     * for description.
     *
     * @param f file
     * @param classNames class names from previous call
     * @param classFiles class files from previous call
     * @param ignoreInner defines whether to ignore inner classes
     * @return full class names of the classes found in the specified file
     * @throws IOException
     */
    private static Collection<String> _getClassNames(final File f,
            final Collection<String> classNames,
            final Collection<File> classFiles, final boolean ignoreInner) throws IOException {

        if (classFiles.contains(f)) {
            return classNames;
        }

        classFiles.add(f);

        IOException exception = null;

//        class ClassNameVisitor extends EmptyVisitor {
            class ClassNameVisitor extends ClassVisitor {

        public  ClassNameVisitor() {
            super(Opcodes.ASM4);
        }

            @Override
            public void visit(
                    int version, int access, String name, String signature,
                    String superName, String[] interfaces) {

                name = name.replace("/", ".");

                if (classNames.contains(name)) {
                    return;
                }

                classNames.add(name);
            }

            @Override
            public void visitInnerClass(String name, String outer,
                    String inner, int access) {
                
                // we do nothing if we ignore inner classes
                if (ignoreInner) {
                    return;
                }

                name = name.replace("/", ".");

                String[] parts = name.split("\\$");

                boolean isAnonymousClass = false;

                for (String part : parts) {
                    if (part.trim().matches("^\\d.*")) {
                        isAnonymousClass = true;
                        break;
                    }
                }

                // we ignore anonymous classes as they are not part of of the 
                // public api of this class
                if (isAnonymousClass) {
                    return;
                }

                if (classNames.contains(name)) {
                    return;
                }

                classNames.add(name);

                File innerFile =
                        new File(f.getParent(),
                        VLangUtils.shortNameFromFullClassName(name) + ".class");
                try {
                    _getClassNames(innerFile, classNames, classFiles, false);
                } catch (IOException ex) {
                    Logger.getLogger(ByteCodeUtil.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        }

        ClassNameVisitor visitor = null;

        InputStream binary = null;
        try {
            binary = new FileInputStream(f);
            ClassReader reader = new ClassReader(binary);
            visitor = new ClassNameVisitor();
            reader.accept(visitor, -1);
        } catch (IOException ex) {
            exception = ex;
        } finally {

            if (binary != null) {
                try {
                    binary.close();
                } catch (Exception ex) {
                }
            }

            if (exception != null) {
                throw exception;
            }
        }

        return classNames;
    }

    /**
     * Internal class used to gather names of classes used in specified bytecode
     */
    private static class ClassCollector extends Remapper {

        private final Set<String> classNames;
        private final String prefix;

        public ClassCollector(final Set<String> classNames, final String prefix) {
            this.classNames = classNames;
            this.prefix = prefix;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String mapDesc(final String desc) {
            if (desc.startsWith("L")) {
                this.addType(desc.substring(1, desc.length() - 1));
            }
            return super.mapDesc(desc);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String[] mapTypes(final String[] types) {
            for (final String type : types) {
                this.addType(type);
            }
            return super.mapTypes(types);
        }

        private void addType(final String type) {
            final String className = type.replace('/', '.');
            if (className.startsWith(this.prefix)) {
                this.classNames.add(className);
            }
        }

        @Override
        public String mapType(final String type) {
            this.addType(type);
            return type;
        }
    }

    /**
     * Default class visitor. Does not modify class.
     */
//    private static class VPrintClassVisitor extends EmptyVisitor {
    private static class VPrintClassVisitor extends ClassVisitor {

        public VPrintClassVisitor() {
            super(Opcodes.ASM4);
        }

        @Override
        public void visit(int i, int i1, String string,
                String string1, String string2, String[] strings) {
            System.out.println("VSIT: " + string);
            super.visit(i, i1, string, string1, string2, strings);
        }

        @Override
        public void visitSource(String string, String string1) {
            super.visitSource(string, string1);
        }

        @Override
        public void visitOuterClass(String string, String string1,
                String string2) {
            super.visitOuterClass(string, string1, string2);;
        }

        @Override
        public AnnotationVisitor visitAnnotation(
                String string, boolean bln) {

            return super.visitAnnotation(string, bln);
        }

        @Override
        public void visitAttribute(Attribute atrbt) {
            super.visitAttribute(atrbt);
        }

        @Override
        public void visitInnerClass(
                String string, String string1, String string2, int i) {
            System.out.println("VSIT-INNER: " + string);
            super.visitInnerClass(string, string1, string2, i);
        }

        @Override
        public FieldVisitor visitField(
                int i, String string, String string1,
                String string2, Object o) {
            return super.visitField(i, string, string1, string2, o);
        }

        @Override
        public MethodVisitor visitMethod(
                int i, String string, String string1,
                String string2, String[] strings) {
            return super.visitMethod(i, string, string1, string2, strings);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }
    }
}
//    public static String getClassName(File f) throws IOException {
//
//        // solution based on code from here:
//        // http://stackoverflow.com/questions/1649674/resolve-class-name-from-bytecode
//
//        IOException exception = null;
//
//        BufferedInputStream in = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(f));
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(ByteCodeUtil.class.getName()).log(Level.SEVERE, null, ex);
//            exception = ex;
//        } finally {
//            if (exception != null) {
//                try {
//                    if (in != null) {
//                        in.close();
//                    }
//                } catch (IOException ex) {
//                    Logger.getLogger(ByteCodeUtil.class.getName()).
//                            log(Level.SEVERE, null, ex);
//                }
//                throw exception;
//            }
//        }
//
//        DataInputStream dis = new DataInputStream(in);
//        dis.readLong(); // skip header and class version
//        int cpcnt = (dis.readShort() & 0xffff) - 1;
//        int[] classes = new int[cpcnt];
//        String[] strings = new String[cpcnt];
//        for (int i = 0; i < cpcnt; i++) {
//            int t = dis.read();
//            if (t == 7) {
//                classes[i] = dis.readShort() & 0xffff;
//            } else if (t == 1) {
//                strings[i] = dis.readUTF();
//            } else if (t == 5 || t == 6) {
//                dis.readLong();
//                i++;
//            } else if (t == 8) {
//                dis.readShort();
//            } else {
//                dis.readInt();
//            }
//        }
//        dis.readShort(); // skip access flags
//
//        String name = strings[classes[(dis.readShort() & 0xffff) - 1] - 1].replace('/', '.');
//
//        if (in != null) {
//            in.close();
//        }
//
//        return name;
//    }
