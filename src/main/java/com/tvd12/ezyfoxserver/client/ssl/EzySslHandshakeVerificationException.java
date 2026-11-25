package com.tvd12.ezyfoxserver.client.ssl;

public class EzySslHandshakeVerificationException extends IllegalStateException {
    private static final long serialVersionUID = 4417593164306470821L;

    public EzySslHandshakeVerificationException(String message) {
        super(message);
    }

    public EzySslHandshakeVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
