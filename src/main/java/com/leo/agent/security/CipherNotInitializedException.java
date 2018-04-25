package com.leo.agent.security;

public class CipherNotInitializedException extends RuntimeException {
    public CipherNotInitializedException() {
        super();
    }

    public CipherNotInitializedException(String s) {
        super(s);
    }
}
