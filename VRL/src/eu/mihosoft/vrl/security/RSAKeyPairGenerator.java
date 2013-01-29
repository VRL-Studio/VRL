/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

/**
 * A simple utility class that generates a RSA PGPPublicKey/PGPSecretKey pair.
 * <p> usage: RSAKeyPairGenerator [-a] identity passPhrase <p> Where identity is
 * the name to be associated with the public key. The keys are placed in the
 * files pub.[asc|bpg] and secret.[asc|bpg].
 */
class RSAKeyPairGenerator {

    // this example comes from the library developer and the compiler shall
    // not blame us for that
    @SuppressWarnings("deprecation")
    private static void exportKeyPair(
            OutputStream secretOut,
            OutputStream publicOut,
            PublicKey publicKey,
            PrivateKey privateKey,
            String identity,
            char[] passPhrase,
            boolean armor)
            throws IOException, InvalidKeyException, NoSuchProviderException, SignatureException, PGPException {
        if (armor) {
            secretOut = new ArmoredOutputStream(secretOut);
        }

        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyPair keyPair = new PGPKeyPair(PGPPublicKey.RSA_GENERAL, publicKey, privateKey, new Date());
        PGPSecretKey secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, keyPair, identity, sha1Calc, null, null, new JcaPGPContentSignerBuilder(keyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1), new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.CAST5, sha1Calc).setProvider("BC").build(passPhrase));

        secretKey.encode(secretOut);

        secretOut.close();

        if (armor) {
            publicOut = new ArmoredOutputStream(publicOut);
        }

        PGPPublicKey key = secretKey.getPublicKey();

        key.encode(publicOut);

        publicOut.close();
    }

    public static void main(
            String[] args)
            throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");

        kpg.initialize(1024);

        KeyPair kp = kpg.generateKeyPair();

        if (args.length < 2) {
            System.out.println("RSAKeyPairGenerator [-a] identity passPhrase");
            System.exit(0);
        }

        if (args[0].equals("-a")) {
            if (args.length < 3) {
                System.out.println("RSAKeyPairGenerator [-a] identity passPhrase");
                System.exit(0);
            }

            FileOutputStream out1 = new FileOutputStream("secret.asc");
            FileOutputStream out2 = new FileOutputStream("pub.asc");

            exportKeyPair(out1, out2, kp.getPublic(), kp.getPrivate(), args[1], args[2].toCharArray(), true);
        } else {
            FileOutputStream out1 = new FileOutputStream("secret.bpg");
            FileOutputStream out2 = new FileOutputStream("pub.bpg");

            exportKeyPair(out1, out2, kp.getPublic(), kp.getPrivate(), args[0], args[1].toCharArray(), false);
        }
    }

    public static void createKeyPair(
            String identity, String password, boolean ascii, File pubFile, File privFile) throws IOException
             {
        try {
            Security.addProvider(new BouncyCastleProvider());

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");

            kpg.initialize(1024);

            KeyPair kp = kpg.generateKeyPair();

            if (ascii) {

                FileOutputStream out1 = new FileOutputStream(privFile);
                FileOutputStream out2 = new FileOutputStream(pubFile);

                exportKeyPair(out1, out2, kp.getPublic(), kp.getPrivate(), identity, password.toCharArray(), true);
            } else {
                FileOutputStream out1 = new FileOutputStream(privFile);
                FileOutputStream out2 = new FileOutputStream(pubFile);

                exportKeyPair(out1, out2, kp.getPublic(), kp.getPrivate(), identity, password.toCharArray(), false);
            }
        } catch (InvalidKeyException ex) {
            Logger.getLogger(RSAKeyPairGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(RSAKeyPairGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PGPException ex) {
            Logger.getLogger(RSAKeyPairGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSAKeyPairGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(RSAKeyPairGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}