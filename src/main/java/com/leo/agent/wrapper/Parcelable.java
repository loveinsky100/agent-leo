package com.leo.agent.wrapper;

public interface Parcelable {
    byte[] wrap(final byte[] bytes);

    byte[] unwrap(final byte[] bytes);
}
