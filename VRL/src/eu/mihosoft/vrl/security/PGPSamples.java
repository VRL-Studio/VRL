/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.security;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class PGPSamples {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Security.addProvider(new BouncyCastleProvider());
        try {
            RSAKeyPairGenerator.createKeyPair("test.eu", "", true, new File("lib/key.pub.asc"), new File("lib/key.priv.asc"));
            DetachedSignatureProcessor.signFile(new File("lib/key.priv.asc"), "", new File("sample.txt"), new File("sample.txt.asc123"),true);
            DetachedSignatureProcessor.verifyFile(new File("lib/key.pub.asc"), new File("sample.txt"), new File("sample.txt.asc123"));
        } catch (Exception ex) {
            Logger.getLogger(PGPSamples.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    public static PrivateKey readPrivateKey(File f, String passwd)
//            throws FileNotFoundException, IOException,
//            NoSuchAlgorithmException, NoSuchPaddingException,
//            InvalidKeySpecException, InvalidKeyException,
//            InvalidAlgorithmParameterException {
//
//        FileInputStream fis = new FileInputStream(f);
//        DataInputStream dis = new DataInputStream(fis);
//        byte[] keyBytes = new byte[(int) f.length()];
//        dis.readFully(keyBytes);
//        dis.close();
//        EncryptedPrivateKeyInfo encryptPKInfo = new EncryptedPrivateKeyInfo(keyBytes);
//        Cipher cipher = Cipher.getInstance(encryptPKInfo.getAlgName());
//        PBEKeySpec pbeKeySpec = new PBEKeySpec(passwd.toCharArray());
//        SecretKeyFactory secFac = SecretKeyFactory.getInstance(encryptPKInfo.getAlgName());
//        Key pbeKey = secFac.generateSecret(pbeKeySpec);
//        AlgorithmParameters algParams = encryptPKInfo.getAlgParameters();
//        cipher.init(Cipher.DECRYPT_MODE, pbeKey, algParams);
//        KeySpec pkcs8KeySpec = encryptPKInfo.getKeySpec(cipher);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        return kf.generatePrivate(pkcs8KeySpec);
//
//    }
}
