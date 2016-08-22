package com.puke.buildergenerator.api;

/**
 * @author zijiao
 * @version 16/8/5
 */
public class Exceptions {

    public static void apt(String format, Object... args) {
        throw new AptException(String.format(format, args));
    }


    public static void runtime(String format, Object... args) {
        throw new RuntimeException(String.format(format, args));
    }

    public static class AptException extends RuntimeException {
        public AptException() {
        }

        public AptException(String message) {
            super(message);
        }

        public AptException(String message, Throwable cause) {
            super(message, cause);
        }

        public AptException(Throwable cause) {
            super(cause);
        }
    }
}
