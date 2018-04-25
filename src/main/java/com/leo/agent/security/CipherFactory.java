package com.leo.agent.security;

public class CipherFactory {
    private CipherFactory() {
    }

    public static Cipher newAesCipherInstance(String password, int mode) {
        return new AesCipher(password, mode);
    }

    public static Cipher newBlowfishCipherInstance(String password, int mode) {
        return new BlowfishCipher(password, mode);
    }

    public static Cipher newRawCipherInstance() {
        return new RawCipher();
    }
}
