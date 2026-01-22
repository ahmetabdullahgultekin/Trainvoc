package com.rollingcatsoftware.trainvocmultiplayerapplication.exception;

public class RoomPasswordException extends RuntimeException {
    private final String error;

    public RoomPasswordException(String error, String message) {
        super(message);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}

