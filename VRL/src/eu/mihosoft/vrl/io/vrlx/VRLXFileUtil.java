////Error reading included file Templates/Classes/../Licenses/license-gplv3classpath.txt
//package eu.mihosoft.vrl.io.vrlx;
//
//import java.io.FileNotFoundException;
//
///**
// *
// * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
// */
//public final class VRLXFileUtil {
//
//    private static IOModel model = new DefaultIOModel(new XMLEntryFactory());
//
//    // no instanciation allowed
//    private VRLXFileUtil() {
//        throw new AssertionError(); // not in this class either!
//    }
//
//    /**
//     * Creates and returns a new session file.
//     * @return a new session file
//     */
//    public static SessionFile newFile() {
//        return model.newFile();
//    }
//
//    /**
//     * Creates and returns a new session file.
//     * @param version file version info
//     * @return a new session file
//     */
//    public static SessionFile newFile(FileVersionInfo version) {
//        return model.newFile(version);
//    }
//
//    /**
//     * Returns the content of a file that is content of the specified folder.
//     * @param folder folder
//     * @param name fila name
//     * @param clazz class of the content object
//     * @return the content of a file or <code>null</code> if no file or
//     *         content object with the specified class object exists
//     */
//    public static Object getFileContent(
//            SessionEntryFolder file, String name, Class<?> clazz) {
//        return model.getFileContent(file, name, clazz);
//    }
//
//    /**
//     * Returns the content of a file.
//     * @param entry file entry
//     * @param clazz the class object of the content object
//     * @return the content of a file or <code>null</code> if content object
//     *         with the specified class object exists
//     */
//    public static Object getFileContent(
//            SessionEntry entry, String name, Class<?> clazz) {
//        return model.getFileContent(null, name, clazz);
//    }
//
//    /**
//     * Returns a subfolder of a given folder.
//     * @param folder folder
//     * @param name subfolder name
//     * @return a subfolder of a given folder or <code>null</code> if no folder
//     *         with the specified name exists
//     */
//    public static SessionEntryFolder getSubFolder(
//            SessionEntryFolder folder, String name) {
//        return model.getSubFolder(folder, name);
//    }
//
//    /**
//     * Returns a subfile of a given folder.
//     * @param folder folder
//     * @param name name of the file to return
//     * @return a subfile of a given folder or <code>null</code> if no file
//     *         with the specified name exists
//     */
//    public static SessionEntryFile getSubFile(
//            SessionEntryFolder folder, String name) {
//        return model.getSubFile(folder, name);
//    }
//
//    /**
//     * Returns a session entry as file.
//     * @param entry session entry
//     * @return the session entry as file or <code>null</code> if the entry is no
//     *         file
//     */
//    public static SessionEntryFile asFile(SessionEntry entry) {
//        return model.asFile(entry);
//    }
//
//    /**
//     * Creates a new entry file and returns it.
//     * @param parent parent folder
//     * @param path file path (relative to parent folder,
//     *             folders will be created if necessary)
//     * @return a new entry file
//     * @throws FileNotFoundException if the file already exists this exception
//     *                               will be thrown
//     */
//    public static SessionEntryFile createFile(
//            SessionEntryFolder parent, String path)
//            throws FileNotFoundException {
//
//        return model.createFile(parent, path);
//    }
//
//    /**
//     * Creates a new entry folder and returns it.
//     * @param parent parent folder
//     * @param path folder path (relative to parent folder,
//     *             folders will be created if necessary)
//     * @return a new entry folder
//     * @throws FileNotFoundException if the folder already exists this exception
//     *                               will be thrown
//     */
//    public static SessionEntryFolder createFolder(
//            SessionEntryFolder parent, String path)
//            throws FileNotFoundException {
//        return model.createFolder(parent, path);
//    }
//
//    /**
//     * Returns a folder specifed by path (relative to parent folder).
//     * @param parent parent folder
//     * @param path folder path
//     * @return the specified folder
//     * @throws FileNotFoundException if the specified folder cannot be found
//     *                               this exception will be thrown
//     */
//    public static SessionEntryFolder getFolder(
//            SessionEntryFolder parent, String path)
//            throws FileNotFoundException {
//        return model.getFolder(parent, path);
//    }
//
//    /**
//     * Returns a file specifed by path (relative to parent folder).
//     * @param parent parent folder
//     * @param path file path
//     * @return the specified file
//     * @throws FileNotFoundException if the specified file cannot be found
//     *                               this exception will be thrown
//     */
//    public static SessionEntryFile getFile(
//            SessionEntryFolder parent, String path)
//            throws FileNotFoundException {
//        return model.getFile(parent, path);
//    }
//
//    /**
//     * Indicates whether the specified entry exists.
//     * @param parent parent folder
//     * @param path entry path
//     * @return <code>true</code> if the specified entry exists;
//     *         <code>false</code> otherwise
//     */
//    public static boolean exists(SessionEntryFolder parent, String path) {
//        return model.exists(parent, path);
//    }
//
//    /**
//     * Returns a session entry specified by path (relative to parent folder).
//     * @param parent parent folder
//     * @param path entry path
//     * @return the specified session entry
//     * @throws FileNotFoundException
//     */
//    public static SessionEntry getEntry(
//            SessionEntryFolder parent, String path) {
//        return getEntry(parent, path);
//    }
//
//    /**
//     * @param model the model to set
//     */
//    public static void setModel(IOModel model) {
//        VRLXFileUtil.model = model;
//    }
//}
