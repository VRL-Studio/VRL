///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package eu.mihosoft.vrl.devel;
//
//import eu.mihosoft.vrl.annotation.ComponentInfo;
//import eu.mihosoft.vrl.annotation.MethodInfo;
//import eu.mihosoft.vrl.annotation.ParamInfo;
//import eu.mihosoft.vrl.system.Messaging;
//import eu.mihosoft.vrl.system.VMessage;
//import java.io.File;
//import java.io.Serializable;
//
///**
// *
// * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
// */
//@ComponentInfo(name = "Run System Command", category = "Custom")
//class RunSystemCommand implements Serializable {
//
//    private static final long serialVersionUID = 1;
//    private transient Process proc;
//
//    @MethodInfo(hide = false)
//    public void run(
//            @ParamInfo(name = "Terminal", style = "selection",
//            options = "value=[\"none\", \"xterm\", \"cmd\"]") String terminal,
//            @ParamInfo(name = "Command", style = "default", options = "") String cmd,
//            @ParamInfo(name = "Arguments", style = "array", options = "") String... arguments) {
//
//        if (proc == null) {
//            for (String arg : arguments) {
//                cmd += " " + arg;
//            }
//
//
//            if (terminal.equals("xterm")) {
//                // unix
//                cmd = "xterm -e " + cmd;
//            } else if (terminal.equals("cmd")) {
//                // windows
//                cmd = "cmd /c " + cmd;
//            }
//
//            Messaging.getStream(Messaging.MSG_OUT).println(">> Running Command: " + cmd);
//
//            proc = new ProcessBuilder(addShellPrefix(cmd))
//                    .directory(new File(System.getProperty("user.dir"))).
//                    redirectErrorStream(true).start();
//      
//      proc.consumeProcessOutput(
//                    Messaging.getStream(Messaging.MSG_OUT),
//                    Messaging.getStream(Messaging.MSG_OUT));
//
//            proc.waitFor();
//            proc = null;
//        } else {
//            VMessage.error("Cannot Run Command:",
//                    ">> stop the current command before running another command.");
//        }
//    }
//
//    private void addShellPrefix(String command) {
//        String[] commandArray = new String[3];
//        commandArray[0] = "sh";
//        commandArray[1] = "-c";
//        commandArray[2] = command;
//        return commandArray;
//    }
//
//    @MethodInfo(hide = false)
//    public void stop() {
//        if (proc != null) {
//            proc.destroy();
//            Messaging.getStream(Messaging.MSG_OUT).println(">> Stopped Command");
//        }
//    }
//}
