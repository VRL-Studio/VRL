//
//
//// Error reading included file Templates/Classes/../Licenses/license-gplv3classpath.txt
//
//
//import eu.mihosoft.vrl.devel.*;
//import eu.mihosoft.vrl.annotation.ComponentInfo;
//import eu.mihosoft.vrl.annotation.MethodInfo;
//import eu.mihosoft.vrl.annotation.ObjectInfo;
//import eu.mihosoft.vrl.annotation.ParamInfo;
//import eu.mihosoft.vrl.io.IOUtil;
//import eu.mihosoft.vrl.reflection.VisualCanvas;
//import eu.mihosoft.vrl.types.CanvasRequest;
//import eu.mihosoft.vrl.visual.Message;
//import eu.mihosoft.vrl.visual.MessageType;
//import eu.mihosoft.vrl.visual.VDialog;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//@ComponentInfo(name = "Plugin Content Creator", category = "VRL/Development",
//description = "Creates content files that can be added to a VRL-Plugin")
//@ObjectInfo(controlFlowIn = true, controlFlowOut = true)
//public class PluginContentCreator implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @MethodInfo(name = "", valueName = "Destination",
//    interactive = true, hide = false)
//    public File addContent(
//            CanvasRequest cReq,
//            @ParamInfo(name = "Source Folder", style = "load-folder-dialog",
//            options = "description=\"Content Folder\"") final File src,
//            @ParamInfo(name = "Destination", style = "save-dialog",
//            options = "endings=[\".zip\"];description=\"Plugin Content Archive - *.zip\"") final File dest,
//            @ParamInfo(name = "Content Name (e.g. help)", style = "default") final String contentName,
//            @ParamInfo(name="Ask if File exists", options="value=true") boolean ask) {
//
//        if (cReq != null) {
//            addNatives(
//                    src,
//                    dest,
//                    contentName,
//                    ask,
//                    new AddLibraryPluginActionImpl(cReq.getCanvas()));
//        } else {
//            addNatives(
//                    src,
//                    dest,
//                    contentName,
//                    ask,
//                    null);
//        }
//
//        return dest;
//    }
//
//    private static class AddLibraryPluginActionImpl
//            implements eu.mihosoft.vrl.devel.AddNativeLibrariesAction {
//
//        private VisualCanvas canvas;
//
//        public AddLibraryPluginActionImpl(VisualCanvas canvas) {
//            this.canvas = canvas;
//        }
//
//        @Override
//        public boolean overwrite(File destFile) {
//            return VDialog.showConfirmDialog(canvas,
//                    "Overwrite file?",
//                    "<html><div align=center>Shall the file "
//                    + Message.EMPHASIZE_BEGIN
//                    + destFile
//                    + Message.EMPHASIZE_END
//                    + " be overwritten?.</div></html>",
//                    VDialog.DialogType.YES_NO)
//                    == VDialog.YES;
//        }
//
//        @Override
//        public void cannotAdd(Exception ex) {
//            canvas.getMessageBox().addUniqueMessage(
//                    "Cannot Create Content",
//                    ex.toString(), null, MessageType.ERROR);
//        }
//
//        @Override
//        public void added(File f) {
//            canvas.getMessageBox().addMessage(
//                    "Created plugin content:",
//                    ">> the archive "
//                    + f.getName() + " has been created.",
//                    MessageType.INFO);
//        }
//
//        @Override
//        public void illegalDest(File destFile) {
//            canvas.getMessageBox().addUniqueMessage(
//                    "Cannot Create Content", ">> File "
//                    + Message.EMPHASIZE_BEGIN
//                    + destFile
//                    + Message.EMPHASIZE_END
//                    + " does not specify a valid archive file."
//                    + " Archive files must end with <b>.zip</b>",
//                    null, MessageType.ERROR);
//        }
//
//        @Override
//        public void nativesDontExist(File content) {
//            canvas.getMessageBox().addUniqueMessage(
//                    "Cannot Create Content", ">> File "
//                    + Message.EMPHASIZE_BEGIN
//                    + content
//                    + Message.EMPHASIZE_END
//                    + " does not exist.",
//                    null, MessageType.ERROR);
//        }
//    }; // end action
//
////    @MethodInfo(noGUI = true)
////    private static void addNatives(
////            final File src,
////            final File dest,
////            final File natives,
////            final String osArchFolder,
////            final AddNativeLibrariesAction action,
////            final boolean multiThreaded) {
////
////        Runnable r = new Runnable() {
////
////            @Override
////            public void run() {
////                addNatives(src, action);
////            }
////        };
////
////
////        if (multiThreaded) {
////            Thread t = new Thread(r);
////            t.start();
////        } else {
////            r.run();
////        }
////    }
//    private static void addNatives(
//            final File src,
//            File dest,
//            final String contentName,
//            boolean ask,
//            AddNativeLibrariesAction action) {
//
//        if (!dest.getName().endsWith(".jar")) {
//            if (action != null) {
//                action.illegalDest(dest);
//                return;
//            } else {
//                throw new IllegalArgumentException("Library must end with .jar");
//            }
//        }
//
//        if (dest.exists() && ask) {
//            if (action != null) {
//                if (!action.overwrite(dest)) {
//                    return;
//                }
//            }
//        }
//
//        File tmpFolder = null;
//
//        try {
//            tmpFolder = IOUtil.createTempDir();
//        } catch (IOException ex) {
//            Logger.getLogger(NativePluginCreator.class.getName()).
//                    log(Level.SEVERE, null, ex);
//
//            if (action != null) {
//                action.cannotAdd(ex);
//            }
//
//            return;
//        }
//
//        // copy native libraries
//
//        IOException exception = null;
//
//        File contentFolder = new File(tmpFolder,
//                "eu/mihosoft/vrl/plugin/content/"+ contentName);
//        File contentDest = new File(contentFolder, content.getName());
//
//        try {
//
//            // unzip VRL plugin
//            IOUtil.unzip(src, tmpFolder);
//
//            // unzip content
//            contentFolder.mkdirs();
//            IOUtil.copyFile(content, contentDest);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(NativePluginCreator.class.getName()).
//                    log(Level.SEVERE, null, ex);
//            exception = ex;
//        } catch (IOException ex) {
//            Logger.getLogger(NativePluginCreator.class.getName()).
//                    log(Level.SEVERE, null, ex);
//            exception = ex;
//        }
//
//        if (exception != null) {
//            if (action != null) {
//                action.cannotAdd(exception);
//            }
//        }
//
//        try {
//            
//            IOUtil.zipContentOfFolder(
//                    tmpFolder, dest);
//        } catch (IOException ex) {
//            Logger.getLogger(NativePluginCreator.class.getName()).
//                    log(Level.SEVERE, null, ex);
//
//            if (action != null) {
//                action.cannotAdd(ex);
//            }
//        }
//
//        if (action != null) {
//            action.added(dest);
//        }
//    }
//}
