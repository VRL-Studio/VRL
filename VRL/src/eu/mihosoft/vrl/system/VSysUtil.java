/* 
 * VSysUtil.java
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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import eu.mihosoft.vrl.io.IOUtil;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides methods for handling platform specific paths,
 * compatibility information and other system functionality such as access to
 * the system clipboard or loading native libraries.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VSysUtil {

    public static final String OS_LINUX = "Linux";
    public static final String OS_MAC = "Mac OS X";
    public static final String OS_WINDOWS = "Windows";
    public static final String OS_OTHER = "Other";
    public static final String[] SUPPORTED_OPERATING_SYSTEMS = {OS_LINUX, OS_MAC, OS_WINDOWS};
    public static final String[] SUPPORTED_ARCHITECTURES = {
        "x86", "i386", "i686", // 32 bit (equivalent)
        "x86_64", "amd64"};    // 64 bit (equivalent)
    
    private static boolean fakeLinux;
    private static boolean fakeMacOS;
    private static boolean fakeWindows;
    
    @Deprecated
    public static void setFakeLinux(boolean value) {
        fakeLinux = value;
    }
    
    @Deprecated
    public static void setFakeWindows(boolean value) {
        fakeWindows = value;
    }
    
    @Deprecated
    public static void setFakeMacOS(boolean value) {
        fakeMacOS = value;
    }
    
    // no instanciation allowed
    private VSysUtil() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Returns the architecture name, i.e.,
     * <code>x64</code> or
     * <code>x86</code> or
     * <code>generic</code>.
     *
     * <p><b>Note:</b> names returned by this method are compatible with native
     * library and resource locations for VRL and VRL plugins.</p>
     *
     * @return architecture name
     */
    public static String getArchName() {

        String osArch = System.getProperty("os.arch");

        if (!isArchSupported()) {
            return "generic";
        }

        String archName = "x86";

        if (osArch.contains("64")) {
            archName = "x64";
        }

        return archName;
    }

    /**
     * Returns the name of the OS, i.e.,
     * <code>linux</code> or
     * <code>osx</code> or
     * <code>windows</code> or
     * <code>generic</code>.
     *
     * <p><b>Note:</b> names returned by this method are compatible with native
     * library and resource locations for VRL and VRL plugins.</p>
     *
     * @return architecture name
     */
    public static String getOSName() {

        String osName = System.getProperty("os.name");

        if (osName.contains("Linux")) {
            return "linux";
        } else if (osName.contains("Mac OS X")) {
            return "osx";
        } else if (osName.contains("Windows")) {
            return "windows";
        }

        return "generic";
    }

    /**
     * Returns the platform and architecture specific path prefix, e.g.,
     * <code>linux/x64</code> or
     * <code>windows/x86</code>.
     *
     * @return the platform and architecture specific path prefix
     */
    public static String getPlatformSpecificPath() {
        String result = "";

        String osName = System.getProperty("os.name");

        String archFolder = getArchName() + "/";


        if (osName.contains("Linux")) {
            result += "linux/" + archFolder;
        } else if (osName.contains("Mac OS X")) {
            result += "osx/";
        } else if (osName.contains("Windows")) {
            result += "windows/" + archFolder;
        } else {
            result += "generic/";
        }

        return result;
    }

    public static boolean isWindows() {
        if(fakeWindows) {
            return getOS().equals(OS_WINDOWS);
        } else {
            return true;
        }
    }

    public static boolean isMacOSX() {
        if(fakeMacOS) {
            return getOS().equals(OS_MAC);
        } else {
            return true;
        }
    }

    public static boolean isLinux() {
        if(fakeLinux) {
            return getOS().equals(OS_LINUX);
        } else {
            return true;
        }
    }

    /**
     * Returns the platform specific ending for native dynamic libraries.
     *
     * @param os operatin system
     * @return <code>so</code> on Linux/Unix, <code>dll</code> on Windows,
     * <code>dylib</code> on Mac OS X and <code>so</code> for other operating
     * system (unsupported)
     */
    public static String getPlatformSpecificLibraryEnding(String os) {

        VParamUtil.throwIfNull(os);

        if (os.equals(OS_MAC)) {
            return "dylib";
        } else if (os.equals(OS_LINUX)) {
            return "so";
        } else if (os.equals(OS_WINDOWS)) {
            return "dll";
        }

        // for other assuming posix complient
        return "so";
    }

    /**
     * Returns the platform specific ending for native dynamic libraries.
     *
     * @return <code>so</code> on Linux/Unix, <code>dll</code> on Windows,
     * <code>dylib</code> on Mac OS X and <code>so</code> for other operating
     * system (unsupported)
     */
    public static String getPlatformSpecificLibraryEnding() {
        return getPlatformSpecificLibraryEnding(getOS());
    }

    /**
     * Returns the platform name and architecture.
     *
     * @return he platform name and architecture
     */
    public static String getPlatformInfo() {

        String arch = getArchName();

        return System.getProperty("os.name")
                + " (" + arch + ")";
    }

    /**
     * Loads all native libraries in the specified folder and optionally all of
     * its subfolders. Please ensure that all libraries in the folder are
     * compatible with the current os. The folder must contain all library
     * dependencies.
     *
     * @param folder library folder
     * @param recursive defines whether recursively load libraries from sub
     * folders
     *
     * @return <code>true</code> if all native libraries could be loaded;
     * <code>false</code> otherwise
     */
    public static boolean loadNativeLibrariesInFolder(File folder, boolean recursive) {
        VParamUtil.throwIfNotValid(
                VParamUtil.VALIDATOR_EXISTING_FOLDER, folder);

        final String dylibEnding = "." + VSysUtil.getPlatformSpecificLibraryEnding();

        Collection<File> dynamicLibraries = new ArrayList<File>();

        if (recursive) {
            dynamicLibraries.addAll(
                    IOUtil.listFiles(folder, new String[]{dylibEnding}));
        } else {
            File[] libFiles = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(dylibEnding);
                }
            });
            dynamicLibraries.addAll(Arrays.asList(libFiles));
        }

        System.out.println(">> loading native libraries:");

        ArrayList<String> loadedLibraries = new ArrayList<String>();
        ArrayList<String> errorLibraries = new ArrayList<String>();

        int lastSize = -1;

        while (loadedLibraries.size() > lastSize) {

            lastSize = loadedLibraries.size();

            for (File f : dynamicLibraries) {

                String libName = f.getAbsolutePath();

                if (!loadedLibraries.contains(libName)) {
//                    System.out.println(" --> " + f.getName());
                    try {
                        System.load(libName);
                        loadedLibraries.add(libName);
                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    } catch (UnsatisfiedLinkError ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }

        boolean errors = loadedLibraries.size() != dynamicLibraries.size();

        for (File f : dynamicLibraries) {
            if (!loadedLibraries.contains(f.getAbsolutePath())) {
                errorLibraries.add(f.getName());
            }
        }

        System.out.println(" --> done.");

        if (errors) {
            System.err.println(">> Not Loaded:");

            for (String loadedLib : errorLibraries) {
                System.err.println("--> " + loadedLib);
            }
        }


        return !errors;
    }

    /**
     * Loads all native librarties in the specified folder and all of its
     * subfolders. Please ensure that all libraries in the folder are compatible
     * with the current os.
     *
     * @param folder library folder
     *
     * @return <code>true</code> if all native libraries could be loaded;
     * <code>false</code> otherwise
     */
    public static boolean loadNativeLibrariesInFolder(File folder) {
        return loadNativeLibrariesInFolder(folder, true);
    }

    /**
     * Returns the binary path to system executables. The path depends on the OS
     * and architecture.
     *
     * @return the binary path to system executables
     */
    public static String getSystemBinaryPath() {
        return "bin/" + getPlatformSpecificPath();
    }

    /**
     * Returns the binary path to custom executables. The path depends on the OS
     * and architecture.
     *
     * @return the binary path to custom executables
     */
    public static String getCustomBinaryPath() {
        return "custom-bin/" + getPlatformSpecificPath();
    }

    /**
     * Indicates whether the current OS is officially supported by VRL.
     *
     * @return <code>true</code> if the current OS is officially supported;
     * <code>false</code> otherwise
     */
    public static boolean isOsSupported() {
        boolean result = false;

        String osName = System.getProperty("os.name");

        for (String s : SUPPORTED_OPERATING_SYSTEMS) {
            if (osName.contains(s)) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * <p> Returns the OS name. If the OS is not supported, "Other" will be
     * returned. </p> <p> <b>Note:</b> in contrary to
     * <code>System.getProperty()</code> only the base name will be returned.
     * See {@link #SUPPORTED_OPERATING_SYSTEMS}. </p>
     *
     * @return the OS name
     */
    public static String getOS() {
        String result = OS_OTHER;

        String osName = System.getProperty("os.name");

        for (String s : SUPPORTED_OPERATING_SYSTEMS) {
            if (osName.contains(s)) {
                result = s;
                break;
            }
        }

        return result;
    }

    /**
     * Indicates whether the current architecture is officially supported by
     * VRL.
     *
     * @return <code>true</code> if the current architecture is officially
     * supported; <code>false</code> otherwise
     */
    public static boolean isArchSupported() {
        boolean result = false;

        String osArch = System.getProperty("os.arch");

        for (String s : SUPPORTED_ARCHITECTURES) {
            if (s.equals(osArch)) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * Copies a string to the system clipboard.
     *
     * @param s string to copy
     */
    public static void copyToClipboard(String s) {
        TextTransfer textTransfer = new TextTransfer();
        textTransfer.setClipboardContents(s);
    }

    /**
     * Copies a string from the system clipboard.
     *
     * @return a copy of the string from the sytem clipboard
     */
    public static String copyFromClipboard() {
        TextTransfer textTransfer = new TextTransfer();
        return textTransfer.getClipboardContents();
    }

    /**
     * Clipboard class. Based on a forum entry that I don't remember.
     */
    private static class TextTransfer implements ClipboardOwner {

        /**
         * Empty implementation of the ClipboardOwner interface.
         */
        @Override
        public void lostOwnership(Clipboard aClipboard,
                Transferable aContents) {
            //do nothing
        }

        /**
         * Places a string on the clipboard and makes this class the owner of
         * the Clipboard's contents.
         */
        public void setClipboardContents(String aString) {
            StringSelection stringSelection = new StringSelection(aString);
            Clipboard clipboard =
                    Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        }

        /**
         * Returns the current system clipboard string.
         *
         * @return any text found on the Clipboard; if none found, return an
         * empty String.
         */
        public String getClipboardContents() {
            String result = "";
            Clipboard clipboard =
                    Toolkit.getDefaultToolkit().getSystemClipboard();
            //odd: the Object param of getContents is not currently used
            Transferable contents = clipboard.getContents(null);
            boolean hasTransferableText =
                    (contents != null)
                    && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
            if (hasTransferableText) {
                try {
                    result = (String) contents.getTransferData(
                            DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(VSysUtil.class.getName()).
                            log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(VSysUtil.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
            return result;
        }
    }

    /**
     * Adds a folder path to the native library path.
     *
     * @param path path to add
     * @throws IOException
     */
    public static void addNativeLibraryPath(String path) throws IOException {
        try {
            // This enables the java.library.path to be modified at runtime
            // Idea comes from a Sun engineer at
            // http://forums.sun.com/thread.jspa?threadID=707176
            //
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (path.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = path;
            field.set(null, tmp);
            System.setProperty("java.library.path",
                    System.getProperty("java.library.path")
                    + File.pathSeparator + path);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }

    /**
     * Gives access to native UNIX/POSIX functionality.
     */
    private interface CLibrary extends Library {

        CLibrary INSTANCE = (CLibrary) Native.loadLibrary("c", CLibrary.class);

        int getpid();

        int kill(int pid, int signal);
    }

    /**
     * Returns the current process id.
     *
     * @return the current process id
     */
    public static int getPID() {

        int result;

        if (isWindows()) {
            result = Kernel32.INSTANCE.GetCurrentProcessId();
        } else if (isLinux() || isMacOSX()) {
            return CLibrary.INSTANCE.getpid();
        } else {
            System.out.println(">> Runing on unsupported OS."
                    + " Assuming POSIX compliant OS.");
            return CLibrary.INSTANCE.getpid();
        }

        return result;
    }

    /**
     * Dermines whether the specified process is running.
     *
     * @param pid process id
     * @return <code>true</code> if the specified process is running;
     * <code>false</code> otherwise
     */
    public static boolean isRunning(int pid) {
        if (isWindows()) {
            // TODO why passing 1 works, 0 does not? the documentation clearly 
            //      states that this value should be set to zero.
            return Kernel32.INSTANCE.OpenProcess(1, false, pid) != null;
        } else if (isLinux() || isMacOSX()) {
            return CLibrary.INSTANCE.kill(pid, 0) == 0;
        } else {
            System.out.println(">> Runing on unsupported OS."
                    + " Assuming POSIX compliant OS.");

            return CLibrary.INSTANCE.kill(pid, 0) == 0;
        }
    }

    /**
     * Determines whether desktop integration is supported.
     */
    public static boolean isDesktopSupported() {
        return java.awt.Desktop.isDesktopSupported();
    }

    /**
     * Determines whether desktop integration supports browser actions.
     */
    public static boolean isBrowserActionSupported() {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        return desktop.isSupported(java.awt.Desktop.Action.BROWSE);
    }

    /**
     * Opens the specified URI in the default browser of the operating system.
     *
     * @param uri URI to open
     */
    public static boolean openURI(URI uri) {
        try {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            desktop.browse(uri);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(VSysUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Indicates whether the specified program is installed and present in the
     * execution path.
     *
     * <p><b>Note:</b> this method is currently not supported on Windows. It
     * requires the Unix program
     * <code>which</code></p>.
     *
     * @param program program to check
     * @return <code>true</code> if the program could be found;
     * <code>false</code> otherwise
     */
    public static boolean isProgramInstalledOnUnix(String program) {
        if (!isWindows()) {

            try {

                String msg = "";

                Process p = new ProcessBuilder("which", program).start();

                p.waitFor();

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(p.getErrorStream()));

                String line = null;

                while ((line = input.readLine()) != null) {
                    msg += line + "\n";
                }

                return msg.isEmpty();

            } catch (InterruptedException ex) {
                Logger.getLogger(VSysUtil.class.getName()).
                        log(Level.SEVERE, null, ex);
                return false;
            } catch (IOException ex) {
                return false;
            }

        } else {
            throw new IllegalStateException("This command does not support Windows OS!");
        }
    }

    /**
     * Opens the specified file in the default file broswer of the operating
     * system.
     *
     * If the default browser cannot be determined the application associated
     * with the file type will be opened.
     *
     * @param f the file to open
     */
    public static boolean openFileInDefaultFileBrowser(File f) {
        try {
            if (VSysUtil.isWindows()) {
                Process p = new ProcessBuilder("explorer.exe", "/select," + f.getAbsolutePath()).start();
            } else if (VSysUtil.isMacOSX()) {
                Process p = new ProcessBuilder("open", "-R", f.getAbsolutePath()).start();
            } else if (VSysUtil.isLinux()) {
                if (isKDERunning()) {
                    Process p = new ProcessBuilder("dolphin", "--select", f.getAbsolutePath()).start();
                } else if (isProgramInstalledOnUnix("nautilus")) {
                    Process p = new ProcessBuilder("nautilus", "--browser", f.getAbsolutePath()).start();
                } else if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(f);
                }
            } else if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(f);
            }
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    /**
     * Indicates whether KDE 4 is running.
     *
     * <p><b>Note:</b> on Windows this method will return
     * <code>false</code> even though KDE running (e.g. through
     * Cygwin). </p>
     *
     * @return <code>true</code> if KDE 4 is
     * running;<code>false</code> otherwise
     */
    public static boolean isKDERunning() {
        if (isWindows()) {
            return false;
        }

        Runtime rt = Runtime.getRuntime();

        try {

            String msg = "";

            Process pr = rt.exec("sh -c ps aux");

            pr.waitFor();

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(pr.getInputStream()));

            String line = null;

            while ((line = input.readLine()) != null) {
                msg += line + "\n";
            }

            return msg.contains("kdeinit4");

        } catch (InterruptedException ex) {
            Logger.getLogger(VSysUtil.class.getName()).
                    log(Level.SEVERE, null, ex);

            return false;
        } catch (IOException ex) {
            Logger.getLogger(VSysUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Indicates whether Gnome or Unity is running.
     *
     * <p><b>Note:</b> on Windows this method will return
     * <code>false</code> even though Gnome or Unity is running (e.g. through
     * Cygwin). </p>
     *
     * @return <code>true</code> if Gnome or Unity is
     * running;<code>false</code> otherwise
     */
    public static boolean isGnomeOrUnityRunning() {
        if (isWindows()) {
            return false;
        }

        Runtime rt = Runtime.getRuntime();

        try {

            String msg = "";

            Process pr = rt.exec("sh -c ps aux");

            pr.waitFor();

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(pr.getErrorStream()));

            String line = null;

            while ((line = input.readLine()) != null) {
                msg += line + "\n";
            }

            return msg.contains("gnome-session");

        } catch (InterruptedException ex) {
            Logger.getLogger(VSysUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Runs the specified command with administrator privileges.
     *
     * <p><b>Note:</b> be careful. Using this method can do serious damage to
     * the users data! Before considering the use of this method be sure you
     * really need it. </p>
     *
     * @param cmd the full command to execute (including arguments)
     * @return <code>true</code> if the command could be executed;
     * <code>false</code> otherwise
     */
    public static boolean runWithAdminPrivileges(String cmd) {

        throw new UnsupportedOperationException(
                "Unfortunately, this is feature not implemented yet!");


//        Runtime rt = Runtime.getRuntime();
//
//        try {
//
//            String msg = "";
//
//            Process pr = rt.exec("sh -c ps aux");
//
//            pr.waitFor();
//
//            BufferedReader input = new BufferedReader(
//                    new InputStreamReader(pr.getErrorStream()));
//
//            String line = null;
//
//            while ((line = input.readLine()) != null) {
//                msg += line + "\n";
//            }
//
//            return msg.contains("gnome-session");
//
//        } catch (InterruptedException ex) {
//            Logger.getLogger(VSysUtil.class.getName()).
//                    log(Level.SEVERE, null, ex);
//            return false;
//        } catch (IOException ex) {
//            return false;
//        }
    }
}
