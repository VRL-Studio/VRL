/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.security;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;

/**
 * Utility class for PGP key generation, signing and verification. This class is
 * only functional if the bcpg-*.jar files are on the classpath. As these
 * Jar-Files are signed they cannot be included in the VRL.jar.
 * <p>
 * These methods use the PGP implementation from http://www.bouncycastle.org/ .
 * </p>
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class PGPUtil {

    private PGPUtil() {
        throw new AssertionError();
    }

    /**
     * Creates an RSA key pair for PGP signing and verification.
     *
     * @param identity the identity, e.g., "mycompany.com"
     * @param password the password for the private key
     * @param pubKeyFile public key file (will be created/overwritten)
     * @param privKeyFile private key file (will be created/overwritten)
     * @param ascii defines whether to use ASCII format
     * @throws IOException if key files cannot be writen
     * @throws PGPExceptionif PGP algorithms failed or security provider hasn't
     * been accepted
     */
    public static void createKeyPair(
            String identity, String password,
            File pubKeyFile, File privKeyFile, boolean ascii)
            throws IOException, PGPException {

        addProviderIfNecessary();

        RSAKeyPairGenerator.createKeyPair(
                identity, password, ascii, pubKeyFile, privKeyFile);
    }

    /**
     * Signs a file. This method creates a signature file for the specified
     * file.
     *
     * @param privKeyFile the private key
     * @param password the password for the private key
     * @param file the file to sign
     * @param signatureFile the signature file (will be created/overwritten)
     * @param ascii defines whether to use ASCII format
     * @throws IOException if files cannot be found or the signature cannot be
     * written
     * @throws PGPExceptionif PGP algorithms failed or security provider hasn't
     * been accepted
     */
    public static void signFile(File privKeyFile, String password,
            File file, File signatureFile, boolean ascii)
            throws IOException, PGPException {

        addProviderIfNecessary();

        DetachedSignatureProcessor.signFile(privKeyFile,
                password, file,
                signatureFile, ascii);
    }

    /**
     * Verifies a file. This method uses a signature file and a public key to
     * verify the specified file.
     *
     * @param pubKeyFile public key file
     * @param file file to verify
     * @param signatureFile signature file
     * @return <code>true</code> if verification was successful;
     * <code>false</code> otherwise
     * @throws IOException if files cannot be read
     * @throws PGPExceptionif PGP algorithms failed or security provider hasn't
     * been accepted
     */
    public static boolean verifyFile(
            File pubKeyFile, File file, File signatureFile)
            throws IOException, PGPException {

        addProviderIfNecessary();

        return DetachedSignatureProcessor.verifyFile(
                pubKeyFile, file, signatureFile);
    }

    /**
     * Adds the BC provider if necessary, i.e., if the provider hasn't already
     * been added.
     */
    private static void addProviderIfNecessary() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
}
