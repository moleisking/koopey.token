package com.koopey.common;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class SecurityHelper {

    private final static String LOG_HEADER = "SecurityHelper";

    public static String hash(String text) {
        try {
            // Create SHA-512 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-512");
            digest.update(text.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return "";
        }
    }

    public static String encrypt(String text, String password) {
        try {
            // Generating IV.
            int ivSize = 16;
            byte[] iv = new byte[ivSize];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Hashing key.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(password.getBytes("UTF-8"));
            byte[] keyBytes = new byte[16];
            System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Encrypt.
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(text.getBytes());

            // Combine IV and encrypted part.
            byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
            System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);

            return Base64.encodeToString(encryptedIVAndText, Base64.DEFAULT);
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return "";
        }
    }

    public static String encrypt(String text, PrivateKey prvKey) {
        try {
            // Generating IV.
            int ivSize = 16;
            byte[] iv = new byte[ivSize];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Encrypt.
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, prvKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());

            // Combine IV and encrypted part.
            byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
            System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);

            return Base64.encodeToString(encryptedIVAndText, Base64.DEFAULT);
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return "";
        }
    }

    public static String encrypt(String text, PublicKey pubKey) {
        try {
            // Generating IV.
            int ivSize = 16;
            byte[] iv = new byte[ivSize];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Encrypt.
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());

            // Combine IV and encrypted part.
            byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
            System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);

            return Base64.encodeToString(encryptedIVAndText, Base64.DEFAULT);
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return "";
        }
    }

    public static String decrypt(String text, String password) {
        try {
            byte[] encryptedIvTextBytes = Base64.decode(text, Base64.DEFAULT);
            int ivSize = 16;
            int keySize = 16;

            // Extract IV.
            byte[] iv = new byte[ivSize];
            System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Extract encrypted part.
            int encryptedSize = encryptedIvTextBytes.length - ivSize;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

            // Hash key.
            byte[] keyBytes = new byte[keySize];
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Decrypt.
            Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

            return new String(decrypted);
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return "";
        }
    }

    public static String decrypt(String text, PrivateKey prvKey) {
        try {
            byte[] encryptedIvTextBytes = Base64.decode(text, Base64.DEFAULT);
            int ivSize = 16;
            int keySize = 16;

            // Extract IV.
            byte[] iv = new byte[ivSize];
            System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Extract encrypted part.
            int encryptedSize = encryptedIvTextBytes.length - ivSize;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

            // Decrypt.
            Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, prvKey);
            byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

            return new String(decrypted);
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return "";
        }
    }

    public static String decrypt(String text, PublicKey pubKey) {
        try {
            byte[] encryptedIvTextBytes = Base64.decode(text, Base64.DEFAULT);
            int ivSize = 16;
            int keySize = 16;

            // Extract IV.
            byte[] iv = new byte[ivSize];
            System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Extract encrypted part.
            int encryptedSize = encryptedIvTextBytes.length - ivSize;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

            // Decrypt.
            Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, pubKey);
            byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

            return new String(decrypted);
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return "";
        }
    }

    public static KeyPair generatePrivateAndPublicKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("AES_256/ECB/NoPadding");
            keyPairGenerator.initialize(4096);
            return keyPairGenerator.genKeyPair();
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return null;
        }
    }

    public static String generateSignature(String text, String privateKey) {
        try {
            // Get private key from String
            PrivateKey publicKey = stringToPrivateKey(privateKey);

            // signature
            Signature signature = Signature.getInstance("SHA512withRSA");
            signature.initSign(publicKey);
            signature.update(text.getBytes());
            byte[] signatureBytes = signature.sign();

            return signatureBytes.toString();
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return "";
        }
    }

    public static boolean verifySignature(String original, String signature, String publicKey) {
        try {
            PublicKey privateKey = stringToPublicKey(publicKey);
            Signature sig = Signature.getInstance("SHA512withRSA");
            sig.initVerify(privateKey);
            sig.update(original.getBytes());
            return sig.verify(signature.getBytes());
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return false;
        }
    }

    public static boolean comparePrivateAndPublicKey(String privateKey, String publicKey) {
        PrivateKey privateKeyObject = stringToPrivateKey(privateKey);
        PublicKey publicKeyObject = stringToPublicKey(publicKey);
        return false;
    }

    private static PrivateKey stringToPrivateKey(String key64) {
        try {
            byte[] clear = Base64.decode(key64, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey priv = fact.generatePrivate(keySpec);
            Arrays.fill(clear, (byte) 0);
            return priv;
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return null;
        }
    }

    private static PublicKey stringToPublicKey(String key64) {
        try {
            byte[] data = key64.getBytes();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            return fact.generatePublic(spec);
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
            return null;
        }
    }

    /*public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }*/
}
