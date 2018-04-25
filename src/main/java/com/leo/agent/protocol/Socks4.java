package com.leo.agent.protocol;

public interface Socks4 {
    int VERSION = 4;
    byte[] reject = {0, 0x5B};
}
