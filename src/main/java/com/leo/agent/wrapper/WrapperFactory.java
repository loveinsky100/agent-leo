package com.leo.agent.wrapper;

import com.leo.agent.protocol.request.FakedHttpRequestResolver;
import com.leo.agent.protocol.request.ShadowsocksRequestResolver;
import com.leo.agent.protocol.request.XRequestResolver;
import com.leo.agent.security.AesCipher;
import com.leo.agent.security.BlowfishCipher;
import com.leo.agent.security.Cipher;
import com.leo.agent.server.Configuration;

public class WrapperFactory {

    private WrapperFactory() {
    }

    public static Wrapper getInstance(Configuration config, String id) throws Exception {
        return getInstance(config.getEncryption(), config.getPassword(), id);
    }

    /**
     * Notice:
     * The frame-based processes below cannot be configured simultaneously in this version of implementation
     * <p>
     * compress, zero-padding, random-padding
     */
    public static Wrapper getInstance(String encryption, String password, String id) throws Exception {
        switch (id) {
            case "raw":
                return new RawWrapper();
            case "encrypt":
                switch (encryption) {
                    case "aes-256-cfb":
                        return new CipherWrapper(
                                new AesCipher(password, AesCipher.AES_256_CFB),
                                new AesCipher(password, AesCipher.AES_256_CFB)
                        );
                    case "aes-192-cfb":
                        return new CipherWrapper(
                                new AesCipher(password, AesCipher.AES_192_CFB),
                                new AesCipher(password, AesCipher.AES_192_CFB)
                        );
                    case "aes-128-cfb":
                        return new CipherWrapper(
                                new AesCipher(password, AesCipher.AES_128_CFB),
                                new AesCipher(password, AesCipher.AES_128_CFB)
                        );
                    case "aes-256-ofb":
                        return new CipherWrapper(
                                new AesCipher(password, AesCipher.AES_256_OFB),
                                new AesCipher(password, AesCipher.AES_256_OFB)
                        );
                    case "aes-192-ofb":
                        return new CipherWrapper(
                                new AesCipher(password, AesCipher.AES_192_OFB),
                                new AesCipher(password, AesCipher.AES_192_OFB)
                        );
                    case "aes-128-ofb":
                        return new CipherWrapper(
                                new AesCipher(password, AesCipher.AES_128_OFB),
                                new AesCipher(password, AesCipher.AES_128_OFB)
                        );
                    case "bf-cfb":
                        return new CipherWrapper(
                                new BlowfishCipher(password, BlowfishCipher.BLOWFISH_CFB),
                                new BlowfishCipher(password, BlowfishCipher.BLOWFISH_CFB)
                        );
                    default:
                        throw new Exception("unknown encryption");
                }
            case "compress":
                return new FrameWrapper(262144, new CompressWrapper());
            case "zero-padding":
                return new FrameWrapper(262144, new ZeroPaddingWrapper(200, 56));
            case "random-padding":
                return new FrameWrapper(262144, new RandomPaddingWrapper(200, 56));
            default:
                throw new Exception("unknown process function");
        }
    }

    public static boolean exists(Configuration config, String id) {
        return exists(config.getEncryption(), id);
    }

    public static boolean exists(String encryption, String id) {
        switch (id) {
            case "raw":
                return true;
            case "encrypt":
                switch (encryption) {
                    case "aes-256-cfb":
                    case "aes-192-cfb":
                    case "aes-128-cfb":
                    case "aes-256-ofb":
                    case "aes-192-ofb":
                    case "aes-128-ofb":
                    case "bf-cfb":
                        return true;
                    default:
                        return false;
                }
            case "compress":
            case "zero-padding":
            case "random-padding":
                return true;
            default:
                return false;
        }
    }

    public static Wrapper getInstance(Wrapper... wrapper) {
        return new MultiWrapper(wrapper);
    }

    public static Wrapper newRawWrapperInstance() {
        return new RawWrapper();
    }

    public static Wrapper newZeroPaddingWrapper(int threshold, int range) {
        return new ZeroPaddingWrapper(threshold, range);
    }

    public static Wrapper newRandomPaddingWrapper(int threshold, int range) {
        return new RandomPaddingWrapper(threshold, range);
    }

    public static Wrapper newCompressWrapperInstance() {
        return new CompressWrapper();
    }

    public static Wrapper newHttpWrapperInstance(boolean requestMode) {
        return new FakedHttpWrapper(requestMode);
    }

    public static Wrapper newMultiWrapperInstance(Wrapper... wrappers) {
        return new MultiWrapper(wrappers);
    }

    public static Wrapper newCipherWrapperInstance(Cipher encipher, Cipher decipher) {
        return new CipherWrapper(encipher, decipher);
    }

    public static Wrapper newFrameWrapperInstance(int fixedFrameLength) {
        return new FrameWrapper(fixedFrameLength);
    }

    public static Wrapper newFrameWrapperInstance(int fixedFrameLength, Wrapper frameHandler) {
        return new FrameWrapper(fixedFrameLength, frameHandler);
    }

    public static XRequestResolver newShadowsocksRequestWrapperInstance() {
        return new ShadowsocksRequestResolver();
    }

    public static XRequestResolver newFakedHttpRequestWrapperInstance() {
        return new FakedHttpRequestResolver();
    }

}
