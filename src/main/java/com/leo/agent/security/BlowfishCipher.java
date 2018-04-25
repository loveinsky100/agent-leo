package com.leo.agent.security;

import com.leo.agent.util.KeyHelper;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import javax.crypto.spec.SecretKeySpec;

public class BlowfishCipher extends Cipher {
    // encryption mode
    public static final int BLOWFISH_CFB = 16;
    private final int keyLength;
    private final StreamBlockCipher cipher;

    /**
     * <b>Notice: </b><br>
     * 1. in <code>new CFBBlockCipher(engine, <b>8</b> * 8);</code> the IV length (8) is
     * reference to the shadowsocks's design.
     *
     * @see <a href="https://shadowsocks.org/en/spec/cipher.html">
     * https://shadowsocks.org/en/spec/cipher.html</a>#Cipher
     */
    public BlowfishCipher(String password, int mode) {
        key = new SecretKeySpec(password.getBytes(), "BF");
        keyLength = mode;
        BlowfishEngine engine = new BlowfishEngine();
        cipher = new CFBBlockCipher(engine, 8 * 8);
    }

    public static boolean isValidMode(int mode) {
        return mode == 16;
    }

    @Override
    protected void _init(boolean isEncrypt, byte[] iv) {
        String keyStr = new String(key.getEncoded());
        ParametersWithIV params = new ParametersWithIV(
                new KeyParameter(KeyHelper.generateKeyDigest(keyLength, keyStr)), iv
        );
        cipher.init(isEncrypt, params);
    }

    @Override
    protected byte[] _encrypt(byte[] originData) {
        byte[] encryptedData = new byte[originData.length];
        cipher.processBytes(originData, 0, originData.length, encryptedData, 0);
        return encryptedData;
    }

    @Override
    protected byte[] _decrypt(byte[] encryptedData) {
        byte[] originData = new byte[encryptedData.length];
        cipher.processBytes(encryptedData, 0, encryptedData.length, originData, 0);
        return originData;
    }

    @Override
    public int getIVLength() {
        return 8;
    }
}
