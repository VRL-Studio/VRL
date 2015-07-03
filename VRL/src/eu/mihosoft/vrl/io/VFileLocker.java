//// Error reading included file Templates/Classes/../Licenses/license-gplv3classpath.txt
//package eu.mihosoft.vrl.io;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.nio.channels.FileChannel;
//import java.nio.channels.FileLock;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
// */
//public class VFileLocker {
//
//    private File f;
//    private FileChannel fileChannel;
//    private FileLock fileLock;
//
//    public VFileLocker(File f) {
//        this.f = f;
//    }
//
//    public void lock() throws FileNotFoundException, IOException {
//
//        if (fileChannel != null) {
//            unlock();
//        }
//
//        if (f.exists()) {
//            // Try to get the lock
//            fileChannel =
//                    new RandomAccessFile(f, "rw").getChannel();
//
//            
//        }
//    }
//
//    private void lock(FileChannel channel,
//            FileLock lock) throws FileNotFoundException, IOException {
//
//        int counter = 0;
//
//        int retryDelay = 1000;
//        int maxRetries = 3;
//
//        while ((lock = channel.tryLock()) == null && counter < maxRetries) {
//            // File is locked by other application
//            System.out.println(
//                    "Resource unavailable. Trying again in "
//                    + (retryDelay / 1000) + " sec.");
//            try {
//                Thread.sleep(retryDelay);
//            } catch (InterruptedException e) {
//                //
//            }
//
//            counter++;
//        }
//
//    }
//
//    private void unlock(FileChannel channel, FileLock lock) throws IOException {
//
//        IOException lockEx = null;
//        IOException channelEx = null;
//
//        if (lock != null) {
//            try {
//                lock.release();
//            } catch (IOException ex) {
//                lockEx = ex;
//            }
//        }
//
//        if (channel != null) {
//            try {
//                channel.close();
//            } catch (IOException ex) {
//                channelEx = ex;
//            }
//        }
//
//        if (lockEx != null) {
//            throw lockEx;
//        }
//
//        if (channelEx != null) {
//            throw channelEx;
//        }
//    }
//
//    public void unlock() {
//        try {
//            unlock(fileChannel, fileLock);
//            fileChannel = null;
//            fileLock = null;
//        } catch (IOException ex) {
//            Logger.getLogger(VFileLocker.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public boolean isLocked(File f) {
//    }
//}
