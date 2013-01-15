/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.io;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Network utility class. This class provides methods for checking for host
 * reachability, downloads etc.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class NetUtil {
    
    private NetUtil() {
        throw new AssertionError();
    }

    /**
     * Indicates whether the specified host is reachable on the specified host.
     * 
     * <p><b>Note:</b> this is a replacement implementation for {@link InetAddress#isReachable(int) }
     * which does not allow a port specification and does not reliably work on Windows
     * (the port which is checked is blocked for security reasons).</p>
     *
     * @param host host (e.g. google.com)
     * @param port 80
     * @return <code>true</code> if the specified host is reachable;
     * <code>false</code> otherwise
     */
    public static boolean isHostReachable(String host, int port, int timeout) {
        return _isHostReachable(host, port, timeout) > 0;
    }

    private static int _isHostReachable(String host, int port, int timeout) {
        // see following discussion
        // http://www.java-forum.org/netzwerkprogrammierung/45203-inetadress-isreachable-unzuverlaessig.html

        long start = System.currentTimeMillis();

        try {

            Socket socket = new Socket(host, port);
            socket.setSoTimeout(timeout);
            socket.close();

        } catch (ConnectException e) {

            String ex = e.toString();

            if (ex.contains("Connection refused")) {

                long end = System.currentTimeMillis() - start;
//	    		if (DEBUG)System.out.println("online, indirekt ermittelt");
                return (int) end;

            } else {

//	    		if (DEBUG) System.out.println("offline");
                return -1;

            }

        } catch (UnknownHostException e) {

//	    	if (DEBUG) System.out.println("offline");
            return -1;

        } catch (IOException e) {

//			if (DEBUG) System.out.println("offline");
            return -1;

        } catch (Throwable t){
            t.printStackTrace(System.err);
        }

        long end = System.currentTimeMillis() - start;
//		if (DEBUG)System.out.println("online");
        return (int) end;
    }
}
