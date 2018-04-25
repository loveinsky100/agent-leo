package com.leo.agent.wrapper;

import com.leo.agent.util.KeyHelper;

import java.util.Arrays;

public class ZeroPaddingWrapper extends PaddingWrapper {

    // +-----------------------+--------------------+------+
    // | header (padding size) | padding (nullable) | data |
    // +-----------------------+--------------------+------+
    public ZeroPaddingWrapper(int paddingThreshold, int paddingRange) {
        super(paddingThreshold, paddingRange);
        if (paddingThreshold + paddingRange < 0xFF - 1)
            headerLength = 1;
        else if (paddingThreshold + paddingRange < 0xFFFF - 2)
            headerLength = 2;
        else if (paddingThreshold + paddingRange < 0xFFFFFF - 3)
            headerLength = 3;
    }

    @Override
    public byte[] wrap(byte[] bytes) {
        if (bytes.length < paddingThreshold + paddingRange) {
            int randomLength = KeyHelper.generateRandomInteger(
                    Math.max(paddingThreshold, bytes.length)
                    , paddingThreshold + paddingRange
            ) + headerLength;
            byte[] wrapBytes = new byte[randomLength];
            byte[] headerBytes = KeyHelper.getBytes(headerLength, randomLength - bytes.length);
            System.arraycopy(bytes, 0, wrapBytes, wrapBytes.length - bytes.length, bytes.length);
            System.arraycopy(headerBytes, 0, wrapBytes, 0, headerBytes.length);
            return wrapBytes;
        } else {
            byte[] wrapBytes = new byte[headerLength + bytes.length];
            System.arraycopy(bytes, 0, wrapBytes, headerLength, bytes.length);
            System.arraycopy(KeyHelper.getBytes(headerLength, headerLength), 0, wrapBytes, 0, headerLength);
            return wrapBytes;
        }
    }

    @Override
    public byte[] unwrap(byte[] bytes) {
        int paddingSize = KeyHelper.toBigEndianInteger(Arrays.copyOfRange(bytes, 0, headerLength));
        return Arrays.copyOfRange(bytes, paddingSize, bytes.length);
    }
}
