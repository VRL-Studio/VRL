/* 
 * VRLShell.java
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

package eu.mihosoft.vrl.lang;

import eu.mihosoft.vrl.dialogs.FileDialogManager;
import eu.mihosoft.vrl.io.TextLoader;
import eu.mihosoft.vrl.io.TextSaver;
import eu.mihosoft.vrl.reflection.ObjectInspector;
import eu.mihosoft.vrl.reflection.VisualObject;
import eu.mihosoft.vrl.system.Constants;
import eu.mihosoft.vrl.system.VSysUtil;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.InvokerInvocationException;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VRLShell {

    private SourceUnit parser;
    private Exception error;
    private int indentCounter = 0;
    private HashSet<String> imports = new HashSet<String>();
    private Map<String, Object> context;
    private Map<String, Object> variables;
    private Binding binding;
    private GroovyShell shell;
    private PrintStream shellOutStream;
    private PrintStream shellErrStream;
    private InputStream shellInputStream;
    private String prompt = "VRL:> ";
    private ClassLoader loader;
    private boolean allowQuit = false;
    private ShellThread thread;
    private HashSet<String> constants = new HashSet<String>();
    private boolean initialized = false;
    private Object lastResult;
    private String buffer = "";
    private String workingDirectory=".";

    public VRLShell() {
        init(null);
    }

    public VRLShell(ClassLoader loader) {
        init(loader);
    }

    public String getScript() {
        buffer = getImportString() + buffer;
        return buffer;
    }

    public final void init(ClassLoader loader) {
        context = new HashMap<String, Object>();
        variables = new HashMap<String, Object>();
        variables.putAll(context);
        variables.put("context", context);
        binding = new Binding(variables);
        this.loader = loader;
        if (loader != null) {
            shell = new GroovyShell(loader, binding);
        } else {
            shell = new GroovyShell(binding);
        }
        initialized = true;
//        this.loader = loader;

    }
    
    public boolean isRunning() {
        return thread != null;
    }

    public void addConstant(String s, Object o) {

        if (o != null && s != null && !s.isEmpty()) {
            constants.add(s);
            variables.put(s, o);
        }
    }

    public void removeConstant(String s) {
        constants.remove(s);
        variables.remove(s);
    }

    /**
     * <p>
     * Attempts to parse the specified code with the specified tolerance.
     * Updates the <code>parser</code> and <code>error</code> members
     * appropriately.  Returns true if the text parsed, false otherwise.
     * The attempts to identify and suppress errors resulting from the
     * unfinished source text.
     * </p>
     * <p>
     * <b>Note:</b> taken from {@link groovy.ui.InteractiveShell}.
     * </p>
     */
    private boolean parse(final String code, final int tolerance) {
        assert code != null;

        boolean parsed = false;
        parser = null;
        error = null;

        // Create the parser and attempt to parse the text as a top-level statement.
        try {
            parser = SourceUnit.create("vrl-script", code, tolerance);
            parser.parse();
            parsed = true;
        } // We report errors other than unexpected EOF to the user.
        catch (CompilationFailedException e) {
            if (parser.getErrorCollector().getErrorCount() > 1
                    || !parser.failedWithUnexpectedEOF()) {
                error = e;
            }
        } catch (Exception e) {
            error = e;
        }

        boolean illegalAssignment = assignmentToConstant(code);

        if (illegalAssignment) {
            shellErrStream.println("Illegal assignment to shell constant.");
        }

        return !illegalAssignment && parsed;
    }

    private boolean parse(final String code) {
        return parse(code, 1);
    }

    private String getIndentString() {
        String result = "";
        String indent = "    ";


        for (int i = 0; i
                < indentCounter; i++) {
            result += indent;


        }
        return result;
    }

    private int numBraceLeft(String input) {
        int result = 0;

        for (int i = 0; i
                < input.length(); i++) {
            if (input.charAt(i) == '{') {
                result++;


            }
        }
        return result;
    }

    private int numBraceRight(String input) {
        int result = 0;

        for (int i = 0; i
                < input.length(); i++) {
            if (input.charAt(i) == '}') {
                result++;
            }
        }
        return result;
    }

    private String read() {
        BufferedReader br =
                new BufferedReader(
                new InputStreamReader(shellInputStream));
        return read(br);
    }

    private String read(BufferedReader br) {

        String input = "";

        while (true) {
            String line = null;
            try {

                indentCounter = Math.max(
                        numBraceLeft(input) - numBraceRight(input), 0);

                shellOutStream.print(prompt);

                line = br.readLine().trim();

                boolean shellCmd = false;

                // exit shell
                if (line.equals("exit") || line.equals("quit")) {
                    shellCmd = true;
                    break;
                }

                // execute shell command (not part of the history)
                if (line.startsWith("sh:")) {
                    input = "";
                    shellCmd = true;

                    if (!(input.contains("(") && input.contains(")"))) {
                        line += "()";
                    }

                    shell.evaluate(line.replace(":", "."));
                }


                // detecting import
                if (line.startsWith("import ")) {

                    if (line.matches("import\\s+(\\w+\\.)*(\\w+|\\*)")) {
                        imports.add(line);
                    } else {
                        shellErrStream.println("Error: illegal import command");
                        return "";
                    }
                    shellCmd = true;
                }

                // normal input
                if (!shellCmd) {
                    input += "\n" + line;
                }

                // start system shell command
                if (line.startsWith(":")) {

                    String shellBinaryName = "/bin/sh";
                    String shellBinaryArgs = "-c";

                    if (VSysUtil.getOS().contains(VSysUtil.OS_WINDOWS)) {

                        shellBinaryName = "cmd.exe";
                        shellBinaryArgs = "/c";
                    }

                    input = "println([\"" + shellBinaryName + "\", \"" 
                            + shellBinaryArgs + "\", \""
                            + VLangUtils.addEscapeCharsToCode("cd " 
                            + getWorkingDirectory() + ";" + line.substring(1))
                            + "\"].execute().text)";
                    buffer += input + "\n";
                } else // start xterm command
                if (line.startsWith("#")) {
                    input = "println([\"/bin/sh\", \"-c\", \"xterm -e \\\""
                            + "cd " + getWorkingDirectory() + ";" + 
                            VLangUtils.addEscapeCharsToCode(line.substring(1))
                            + "\\\"\"].execute().text)";
                    buffer += input + "\n";
                } else if (!shellCmd) {
                    buffer += line + "\n";
                }

            } catch (Exception ex) {
                shellErrStream.println(ex.getMessage());
            }

            if (parse(input)) {
                return input;
            } else {
                if (error != null) {

                    int braceBalance =
                            numBraceLeft(input) - numBraceRight(input);

                    if (braceBalance == 0) {
                        input = "";

                        String msg = error.getMessage();

                        msg = msg.trim().replaceFirst("vrl-script:\\s\\d+:", "");

                        shellErrStream.println(msg);

                    } else if (braceBalance < 0) {
                        shellErrStream.println(
                                "To many \"}\". Deleted last line of input."
                                + " Please type \"sh:clear\" if you want to clear"
                                + " the current buffer.");
                        input = input.substring(0, input.length() - line.length());
                    }
                }
            }
        }

        return null;
    }

    void reset() {
        // imports.clear();

        Map<String, Object> tmpVars = variables;

        init(loader);

        for (String name : constants) {
            variables.put(name, tmpVars.get(name));
        }

        shell.resetLoadedClasses();
        updateConstants();
        buffer = "";
    }

    public Object executeScript(String s) {
        String bufferTmp = buffer;
        reset();
        buffer = bufferTmp;

        return shell.evaluate(s);
    }

    private String assignmentCheck(String s) {
        String result = "";
        Matcher m1 = Patterns.VARIABLE_ASSIGNMENT_PATTERN.matcher(s);
        if (m1.find()) {
            Matcher m2 = Patterns.IDENTIFIER.matcher(m1.group());
            if (m2.find()) {
                result = m2.group();
            }
        }
        return result;
    }

    private boolean assignmentToConstant(String s) {
        return constants.contains(assignmentCheck(s));
    }

    public void addImport(String i) {
        imports.add(i);
    }

    public void addImports(Collection<String> imports) {
        this.imports.addAll(imports);
    }

    public void clearImports() {
        imports.clear();
    }

    private void quit() {
        if (thread != null) {
            thread.quit();
//            thread.interrupt();
            //thread = null;

            initialized = false;
        }
    }

    void printVariables() {
        shellOutStream.println("------------------------ variables ------------------------");

        int maxNameLength = 0;
        int maxClassNameLength = 0;

        for (String name : variables.keySet()) {

            if (constants.contains(name) || name.equals("context")) {
                continue;
            }

            maxNameLength = Math.max(maxNameLength, name.length());

            Object var = variables.get(name);

            if (var == null) {
                continue;
            }

            maxClassNameLength = Math.max(
                    maxClassNameLength, var.getClass().getName().length());
        }

        for (String name : variables.keySet()) {

            if (constants.contains(name) || name.equals("context")) {
                continue;
            }

            Object var = variables.get(name);

            if (var == null) {
                continue;
            }

            String formatString =
                    "%s %-" + maxNameLength + "s %s %-" + maxClassNameLength + "s\n";

            shellOutStream.printf(formatString,
                    "->", name, "type =", var.getClass().getName());
        }
    }

    void printConstants() {
        shellOutStream.println("------------------------ constants ------------------------");
        if (constants.isEmpty()) {
            return;
        }

        int maxNameLength = 0;
        int maxClassNameLength = 0;

        for (String name : constants) {
            maxNameLength = Math.max(maxNameLength, name.length());

            Object var = variables.get(name);

            if (var == null) {
                continue;
            }

            maxClassNameLength = Math.max(
                    maxClassNameLength, var.getClass().getName().length());
        }

        for (String name : constants) {

            Object var = variables.get(name);

            if (var == null) {
                continue;
            }

            String formatString =
                    "%s %-" + maxNameLength + "s %s %-" + maxClassNameLength + "s\n";

            shellOutStream.printf(formatString,
                    "->", name, "type =", var.getClass().getName());
        }
    }

    private void printWelcome() {
        shellOutStream.println("*******************************************************************");
        shellOutStream.println("                             VRL-Shell                             ");
        shellOutStream.println("-------------------------------------------------------------------");
        shellOutStream.println(" => VRL-"
                + eu.mihosoft.vrl.system.Constants.VERSION);
        shellOutStream.println(" => Java-" + System.getProperty("java.version")
                + " (" + System.getProperty("java.vendor") + ")");
        shellOutStream.println(" => Groovy-" + groovy.lang.GroovySystem.getVersion());
        shellOutStream.println(" => OS: " + VSysUtil.getPlatformInfo());
        shellOutStream.println(" => pid: " + VSysUtil.getPID());
        shellOutStream.println("*******************************************************************");
        shellOutStream.println();
        shellOutStream.println("Welcome, user! Type \"sh:help\" to get help.\n");
    }

    public void clear() {
        shellOutStream.println("------------------------ clear ------------------------");
    }

    public String getImportString() {
        String result = "";

        for (String s : imports) {
            result += s + "\n";
        }

        return result;
    }

    public void run() {

        quit();

        thread = new ShellThread() {

            @Override
            public void run() {

                if (!initialized) {
                    init(loader);
                }

                if (getShellOutStream() == null
                        && getShellErrStream() == null
                        && getShellInputStream() == null) {
                    setShellOutStream(System.out);
                    setShellErrStream(System.err);
                    setShellInputStream(System.in);
                }

                updateConstants();
                printWelcome();

                String input = "";

                while (!isQuit()) {
                    try {
                        input = read();

                        if (input == null && isAllowQuit()) {
                            shellOutStream.println(
                                    "------------------------ quit ------------------------");
                            break;
                        } else if (input == null) {
                            shellOutStream.println(
                                    "shell: quit not allowed."
                                    + " Close the current VRL Session"
                                    + " to exit this shell.");
                            input = "";
                        }

                        String importString = getImportString();

                        lastResult = null;

                        if (input.trim().startsWith("class ")) {
                            shell.getClassLoader().parseClass(importString + input);
                        } else {
                            lastResult = Evaluator.evaluate(
                                    shell, shellOutStream, shellErrStream,
                                    importString + input);
                        }

                        if (lastResult != null) {
                            shellOutStream.println("out:> " + lastResult);
                        }

                        addConstant("lastResult", lastResult);

                    } catch (Exception ex) {
                        shellErrStream.println("-- FATAL ERROR --");

                        shellErrStream.println(
                                ">> Please "
                                + Constants.WRITE_VRL_BUG_REPORT_PLAIN 
                                + " including the following error message:\n");

                        shellErrStream.println(ex.toString());
                    }
                }
                
                System.out.println(">> VRLShell.quit()");
                
                thread = null;
            }
        };

        thread.start();
    }

    private void updateConstants() {
        addConstant("out", shellOutStream);
        addConstant("err", shellErrStream);
        addConstant("input", shellInputStream);
        addConstant("sh", new ShellControl(this));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        VRLShell sh = new VRLShell();
        sh.setAllowQuit(true);
        sh.run();
    }

    /**
     * @return the shellOutStream
     */
    public PrintStream getShellOutStream() {
        return shellOutStream;
    }

    /**
     * @param shellOutStream the shellOutStream to set
     */
    public void setShellOutStream(PrintStream shellOutStream) {
        this.shellOutStream = shellOutStream;
    }

    /**
     * @return the shellErrStream
     */
    public PrintStream getShellErrStream() {
        return shellErrStream;
    }

    /**
     * @param shellErrStream the shellErrStream to set
     */
    public void setShellErrStream(PrintStream shellErrStream) {
        this.shellErrStream = shellErrStream;
    }

    /**
     * @return the shellInputStream
     */
    public InputStream getShellInputStream() {
        return shellInputStream;
    }

    /**
     * @param shellInputStream the shellInputStream to set
     */
    public void setShellInputStream(InputStream shellInputStream) {
        this.shellInputStream = shellInputStream;
    }

    /**
     * @return the prompt
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * @param prompt the prompt to set
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * @return the allowQuit
     */
    public boolean isAllowQuit() {
        return allowQuit;
    }

    /**
     * @param allowQuit the allowQuit to set
     */
    public void setAllowQuit(boolean allowQuit) {
        this.allowQuit = allowQuit;
    }

    /**
     * @return the workingDirectory
     */
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * @param workingDirectory the workingDirectory to set
     */
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
}

class Evaluator {

    private static Object result;

    public synchronized static Object evaluate(final GroovyShell shell,
            PrintStream stdStream, PrintStream errorStream, final String input) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    result = shell.evaluate(input);
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Throwable cause = ex;
            if (ex instanceof InvocationTargetException && ex.getCause() != null) {
                cause = ex.getCause();
            }
            String msg = cause.toString();

            if (cause.getMessage() != null) {
                msg = cause.getMessage().replaceFirst("for class: Script\\d*", "");
                msg = msg.replaceFirst("Script\\d+.groovy:\\s\\d+:", "");
            }

            errorStream.println(msg);
        }

        return result;
    }
}

class ShellThread extends Thread {

    private boolean stop = false;

    /**
     * @return the stop
     */
    public boolean isQuit() {
        return stop;
    }

    /**
     * @param stop the stop to set
     */
    public void quit() {
        this.stop = true;
    }
}

class ShellControl {

    private VRLShell shell;

    public ShellControl(VRLShell shell) {
        this.shell = shell;
    }

    public BufferedReader getIn() {
        return new BufferedReader(
                new InputStreamReader(shell.getShellInputStream()));
    }
    
    public void cd(String wd) {
        shell.setWorkingDirectory(wd);
    }
    
    public String pwd() {
        return shell.getWorkingDirectory();
    }

    public PrintStream getOut() {
        return shell.getShellOutStream();
    }

    public PrintStream getErr() {
        return shell.getShellErrStream();
    }

    public String getScript() {
        return shell.getScript();
    }

    public void saveScript() throws IOException {
        saveText(null, getScript());
    }

    public void save() throws IOException {
        saveText(null, getScript());
    }

    public void saveScript(File file) throws IOException {
        saveText(file, getScript());
    }

    public void saveScript(String fileName) throws IOException {

        File file = null;

        if (fileName != null && !fileName.isEmpty()) {
            file = new File(fileName);
        }

        saveText(file, getScript());
    }
    
    public Object getObject(VisualObject vobj) {
        
        ObjectInspector inspector = 
                vobj.getObjectRepresentation().getInspector();
        
        int objID = vobj.getObjectRepresentation().getObjectID();
        
        return inspector.getObject(objID);
    } 

    public void saveText(String text) throws IOException {
        saveText(null, text);
    }

    public void saveText(File file, String text) throws IOException {

        if (file == null) {
            FileDialogManager dialogManager = new FileDialogManager();

            dialogManager.saveFile(
                    null, text, new TextSaver(), new FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.getName().toLowerCase().endsWith(".groovy")
                            || f.getName().toLowerCase().endsWith(".txt")
                            || f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "Text/Script Files (groovy, txt)";
                }
            });

        } else {

            boolean overwrite = true;

            if (file.exists()) {
                shell.getShellOutStream().print(
                        "The file already exists. Overwrite? (Y/n): ");
                String answer = getIn().readLine();
                overwrite = answer.isEmpty()
                        || answer.toLowerCase().trim().equals("y");
            }

            if (overwrite) {
                BufferedWriter writer =
                        new BufferedWriter(new FileWriter(file));
                writer.write(text);
                writer.flush();
                writer.close();
            }
        }
    }

    public String loadText(String fileName) throws IOException {

        String result = "";

        if (fileName == null || fileName.isEmpty()) {
            FileDialogManager dialogManager = new FileDialogManager();

            result = (String) dialogManager.loadFile(null, new TextLoader(),
                    new FileFilter() {

                        @Override
                        public boolean accept(File f) {
                            return f.getName().toLowerCase().endsWith(".groovy")
                                    || f.getName().toLowerCase().endsWith(".txt")
                                    || f.isDirectory();
                        }

                        @Override
                        public String getDescription() {
                            return "Text/Script Files (groovy, txt)";
                        }
                    });

        } else {

            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            while (reader.ready()) {
                result += reader.readLine() + "\n";
            }

            reader.close();
        }

        return result;
    }

    public Object execute(String s) {
        return shell.executeScript(s);
    }

    public Object execute() throws IOException {
        return shell.executeScript(loadText(null));
    }

    public Object execute(File f) throws IOException {
        return shell.executeScript(loadText(f.getPath()));
    }

    public void restart() {
        PrintStream shellOutStream = shell.getShellOutStream();
        shell.reset();
        shellOutStream.println("------------------------ restart ------------------------");
    }

    public void constants() {
        shell.printConstants();
    }

    public void variables() {
        shell.printVariables();
    }

    public void clear() {
        shell.clear();
    }

    public void help() {
        PrintStream shellOutStream = shell.getShellOutStream();
        shellOutStream.println("------------------------ help ------------------------");
        shellOutStream.println("valid shell commands:");
        shellOutStream.println("- sh:clear\t clears current input buffer");
        shellOutStream.println("- sh:variables\t shows defined variables");
        shellOutStream.println("- sh:constants\t shows defined constants");
        shellOutStream.println("- sh:restart\t restarts shell (local variables and classes will be lost)");

        shellOutStream.println("- sh:save\t saves current shell session as script");
        shellOutStream.println("- sh:execute\t executes groovy script");
        shellOutStream.println("- :CMD\t\t executes CMD on system shell (replace CMD with the desired command)");
        shellOutStream.println("  \t\t Note: a) CMD must not be interactive or require terminal emulation!");
        shellOutStream.println("  \t\t       b) $ character must be escaped, e.g., $ becomes \\$");
        shellOutStream.println("  \t\t       c) \\ character must be escaped, e.g., C:\\ becomes C:\\\\");
        shellOutStream.println("- #CMD\t\t same as :CMD, but runs CMD in xterm.");
        shellOutStream.println("  \t\t Note: a) xterm must be installed on your system!");
        shellOutStream.println("  \t\t       b) Use this for interactive shell commands such as 'view' or 'vim'.");

        shellOutStream.println("- quit \t\t quits this shell (if allowed)");
        shellOutStream.println("- exit \t\t quits this shell (if allowed)");
        shellOutStream.println("- sh:help\t shows this help message");
    }
}
