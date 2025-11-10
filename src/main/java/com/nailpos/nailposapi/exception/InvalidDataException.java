package com.nailpos.nailposapi.exception;

// Thêm Exception này để xử lý lỗi logic 400
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}